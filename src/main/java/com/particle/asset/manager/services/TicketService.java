package com.particle.asset.manager.services;

import com.particle.asset.manager.DTO.TicketRequestDto;
import com.particle.asset.manager.DTO.TicketResponseBodyDto;
import com.particle.asset.manager.enums.MovementTypes;
import com.particle.asset.manager.enums.TicketOperations;
import com.particle.asset.manager.enums.TicketStatuses;
import com.particle.asset.manager.enums.UserTypes;
import com.particle.asset.manager.models.Asset;
import com.particle.asset.manager.models.AssetType;
import com.particle.asset.manager.models.Ticket;
import com.particle.asset.manager.models.User;
import com.particle.asset.manager.repositories.AssetRepository;
import com.particle.asset.manager.repositories.AssetTypeRepository;
import com.particle.asset.manager.repositories.TicketRepository;
import com.particle.asset.manager.repositories.UserRepository;
import com.particle.asset.manager.results.Result;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TicketService
{
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final AssetRepository assetRepository;
    private final AssetTypeRepository assetTypeRepository;
    private final CacheManager cacheManager;

    public TicketService(TicketRepository ticketRepository, UserRepository userRepository,
                         AssetRepository assetRepository, AssetTypeRepository assetTypeRepository, CacheManager cacheManager)
    {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.assetRepository = assetRepository;
        this.assetTypeRepository = assetTypeRepository;
        this.cacheManager = cacheManager;
    }

    // Mostra tutti i Type (con cache)
    // @Chacheable → Quando si chiama "getAllTypes()" la prima volta, i dati vengono recuperati
    //               dal database e salvati i cache con la chiave "all". Le chiamata successive
    //               leggono direttamente dalla cache per 8 ore.
    @Cacheable(value = "tickets", key = "'all'")
    public List<TicketResponseBodyDto> getAllTickets()
    {
        System.out.println(">>> Fetching ALL Tickets from database...");
        List<Ticket> tickets = ticketRepository.findAll();

        List<TicketResponseBodyDto> ticketsDto = tickets.stream()
                .map(this::toResponseDto)
                .toList();

        // Popola anche le cache per i singoli ID
        Cache cache = cacheManager.getCache("tickets");
        if(cache != null)
            tickets.forEach(ticket -> cache.put("id::" + ticket.getId(), toResponseDto(ticket)));

        return ticketsDto;
    }

    public TicketResponseBodyDto getTicketByCode(String code)
    {
        Cache cache = cacheManager.getCache("tickets");

        // 1. Cerca prima nella cache del singolo ID
        Cache.ValueWrapper idWrapper = cache != null ? cache.get("id::" + code) : null;
        if(idWrapper != null)
        {
            System.out.println(">>> getTicketById(" + code + ") - CACHE (singolo ID)");
            return (TicketResponseBodyDto) idWrapper.get();
        }

        // 2. Se non c'è, cerca nella cache "all"
        Cache.ValueWrapper allWrapper = cache != null ? cache.get("all") : null;
        if(allWrapper != null)
        {
            System.out.println(">>> getTicketById(" + code + ") - CACHE (filtrato da 'all')");
            @SuppressWarnings("unchecked")
            List<TicketResponseBodyDto> allTickets = (List<TicketResponseBodyDto>) allWrapper.get();
            return allTickets.stream()
                    .filter(ticket -> ticket.getTicketCode().equals(code))
                    .findFirst()
                    .orElse(null);
        }

        // 3. Cache vuota - va al DB e salva SOLO questo ID
        System.out.println(">>> getTicketById(" + code + ") - DATABASE (salvando singolo ID in cache)");
        Ticket ticket = ticketRepository.findByCode(code).orElse(null);
        if (cache != null && ticket != null)
            cache.put("id::" + code, toResponseDto(ticket));

        return ticket != null ?toResponseDto(ticket) :null; // NOT_FOUND
    }

    // Crea un Type (reset cache)
    // @CacheEvict → Quando si crea/aggiorna/disattiva un record, la cache viene
    //               completamente svuotata (clear). Alla prossima chiamata GET,
    //               i dati verrano caricati direttamente dal database.
    @CacheEvict(value = "tickets", allEntries = true)
    public Result.TicketResult createTicket(TicketRequestDto ticket)
    {
        if(ticket.getUserCode() == null || ticket.getOperation() == null ||
                (ticket.getAssetTypeCode() == null && ticket.getAssetCode() == null) ||
                (ticket.getAssetTypeCode() != null && ticket.getAssetCode() != null) ||
                ticket.getMessage() == null || ticket.getStatus() == null)
            return new Result.TicketResult(TicketOperations.BAD_REQUEST, null);

        if(ticket.getOperation().name().equals(MovementTypes.ASSIGNED.name()) && ticket.getAssetCode() != null ||
            ticket.getOperation().name().equals(MovementTypes.RETURNED.name()) && ticket.getAssetTypeCode() != null ||
                ticket.getOperation().name().equals(MovementTypes.DISMISSED.name()) && ticket.getAssetTypeCode() != null)
            return new Result.TicketResult(TicketOperations.OPERATION_ERROR, null);

        Optional<User> userOpt = userRepository.findByOid(ticket.getUserCode());
        Optional<AssetType> assetTypeOpt = assetTypeRepository.findByCode(ticket.getAssetTypeCode());
        Optional<Asset> assetOpt = assetRepository.findByCode(ticket.getAssetCode());

        if(userOpt.isEmpty())
            return new Result.TicketResult(TicketOperations.USER_NOT_FOUND, null);
        else if(assetOpt.isEmpty() && ticket.getAssetCode() != null)
            return new Result.TicketResult(TicketOperations.ASSET_NOT_FOUND, null);
        else if(assetTypeOpt.isEmpty() && ticket.getAssetTypeCode() != null)
            return new Result.TicketResult(TicketOperations.ASSET_TYPE_NOT_FOUND, null);

        Ticket createdTicket = new Ticket();
        createdTicket.setUsers(userOpt.get());
        createdTicket.setAssetType(assetTypeOpt.orElse(null));
        createdTicket.setAsset(assetOpt.orElse(null));
        createdTicket.setOperation(ticket.getOperation());
        createdTicket.setMessage(ticket.getMessage());
        createdTicket.setStatus(ticket.getStatus());
        String userName = userOpt.get().getName().toUpperCase().substring(0, Math.min(2, userOpt.get().getName().length()));
        if(userOpt.get().getUserType().name().equals(UserTypes.ADMIN.name()))
        {
            String role = userOpt.get().getUserType().name().toUpperCase()
                    .substring(0, 2);
            createdTicket.setCode(userName + role + (ticketRepository.count()+1));
        }
        else
        {
            String businessUnit = userOpt.get().getBusinessUnit().getName().toUpperCase()
                    .substring(0, Math.min(2, userOpt.get().getBusinessUnit().getName().length()));
            createdTicket.setCode(userName + businessUnit + (ticketRepository.count()+1));
        }
        ticketRepository.save(createdTicket);

        return new Result.TicketResult(TicketOperations.OK, toResponseDto(createdTicket));
    }

    private TicketResponseBodyDto toResponseDto(Ticket ticket)
    {
        TicketResponseBodyDto dto = new TicketResponseBodyDto();
        dto.setTicketCode(ticket.getCode());
        dto.setUserCode(ticket.getUsers().getOid());
        dto.setOperation(ticket.getOperation());
        dto.setAssetTypeCode(ticket.getAssetType() != null ? ticket.getAssetType().getCode() : null);
        dto.setAssetCode(ticket.getAsset() != null ? ticket.getAsset().getCode() : null);
        dto.setMessage(ticket.getMessage());
        dto.setStatus(ticket.getStatus());
        dto.setDate(ticket.getDate());
        return dto;
    }
}
