package com.particle.asset.manager.services;

import com.particle.asset.manager.DTO.*;
import com.particle.asset.manager.enums.MovementTypes;
import com.particle.asset.manager.enums.TicketOperations;
import com.particle.asset.manager.enums.TicketStatuses;
import com.particle.asset.manager.enums.UserTypes;
import com.particle.asset.manager.models.*;
import com.particle.asset.manager.repositories.*;
import com.particle.asset.manager.results.Result;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TicketService
{
    private final TicketRepository ticketRepository;
    private final TicketReplyRepository ticketReplyRepository;
    private final UserRepository userRepository;
    private final AssetRepository assetRepository;
    private final AssetTypeRepository assetTypeRepository;
    private final CacheManager cacheManager;

    public TicketService(TicketRepository ticketRepository, UserRepository userRepository,
                         AssetRepository assetRepository, AssetTypeRepository assetTypeRepository,
                         CacheManager cacheManager, TicketReplyRepository ticketReplyRepository)
    {
        this.ticketRepository = ticketRepository;
        this.ticketReplyRepository = ticketReplyRepository;
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
    public List<FetchTicketResponseBodyDto> getAllTickets()
    {
        System.out.println(">>> Fetching ALL Tickets from database...");
        List<Ticket> tickets = ticketRepository.findAll();

        List<FetchTicketResponseBodyDto> ticketsDto = tickets.stream()
                .map(this::fetchToResponseDto)
                .toList();

        // Popola anche le cache per i singoli ID
        Cache cache = cacheManager.getCache("tickets");
        if(cache != null)
            tickets.forEach(ticket -> cache.put("id::" + ticket.getId(), fetchToResponseDto(ticket)));

        return ticketsDto;
    }

    public FetchTicketResponseBodyDto getTicketByCode(String code)
    {
        Cache cache = cacheManager.getCache("tickets");

        // 1. Cerca prima nella cache del singolo ID
        Cache.ValueWrapper idWrapper = cache != null ? cache.get("id::" + code) : null;
        if(idWrapper != null)
        {
            System.out.println(">>> getTicketById(" + code + ") - CACHE (singolo ID)");
            return (FetchTicketResponseBodyDto) idWrapper.get();
        }

        // 2. Se non c'è, cerca nella cache "all"
        Cache.ValueWrapper allWrapper = cache != null ? cache.get("all") : null;
        if(allWrapper != null)
        {
            System.out.println(">>> getTicketById(" + code + ") - CACHE (filtrato da 'all')");
            @SuppressWarnings("unchecked")
            List<FetchTicketResponseBodyDto> allTickets = (List<FetchTicketResponseBodyDto>) allWrapper.get();
            return allTickets.stream()
                    .filter(ticket -> ticket.getTicketCode().equals(code))
                    .findFirst()
                    .orElse(null);
        }

        // 3. Cache vuota - va al DB e salva SOLO questo ID
        System.out.println(">>> getTicketById(" + code + ") - DATABASE (salvando singolo ID in cache)");
        Ticket ticket = ticketRepository.findByCode(code).orElse(null);
        if (cache != null && ticket != null)
            cache.put("id::" + code, fetchToResponseDto(ticket));

        return ticket != null ?fetchToResponseDto(ticket) :null; // NOT_FOUND
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
                ticket.getMessage() == null)
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

        if(userOpt.get().getUserType() == UserTypes.ADMIN)
            return new Result.TicketResult(TicketOperations.INVALID_USER_TYPE, null);

        Ticket createdTicket = new Ticket();
        createdTicket.setUsers(userOpt.get());
        createdTicket.setAssetType(assetTypeOpt.orElse(null));
        createdTicket.setAsset(assetOpt.orElse(null));
        createdTicket.setOperation(ticket.getOperation());
        //createdTicket.setMessage(ticket.getMessage());
        //createdTicket.setStatus(ticket.getStatus());
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
        addTicketReply(createdTicket, ticket.getMessage());

        return new Result.TicketResult(TicketOperations.OK, toResponseDto(createdTicket, ticket.getMessage()));
    }

    private void addTicketReply(Ticket ticket, String message)
    {
        TicketReply tr = new TicketReply();
        tr.setTickets(ticket);
        tr.setMessage(message);
        tr.setUsers(ticket.getUsers());
        tr.setCode(ticket.getCode() + (ticketReplyRepository.count()+1));
        ticketReplyRepository.save(tr);
    }

    private FetchTicketResponseBodyDto fetchToResponseDto(Ticket ticket)
    {
        FetchTicketResponseBodyDto dto = new FetchTicketResponseBodyDto();
        dto.setTicketCode(ticket.getCode());
        dto.setUserCode(ticket.getUsers().getOid());
        dto.setOperation(ticket.getOperation());
        dto.setAssetTypeCode(ticket.getAssetType() != null ? ticket.getAssetType().getCode() : null);
        dto.setAssetCode(ticket.getAsset() != null ? ticket.getAsset().getCode() : null);
        //dto.setMessage(ticket.getMessage());
        dto.setStatus(ticket.getStatus());
        dto.setDate(ticket.getDate());
        return dto;
    }

    private CreateTicketResponseBodyDto toResponseDto(Ticket ticket, String message)
    {
        CreateTicketResponseBodyDto dto = new CreateTicketResponseBodyDto();
        dto.setTicketCode(ticket.getCode());
        dto.setUserCode(ticket.getUsers().getOid());
        dto.setOperation(ticket.getOperation());
        dto.setAssetTypeCode(ticket.getAssetType() != null ? ticket.getAssetType().getCode() : null);
        dto.setAssetCode(ticket.getAsset() != null ? ticket.getAsset().getCode() : null);
        dto.setMessage(message);
        dto.setStatus(ticket.getStatus());
        dto.setDate(ticket.getDate());
        return dto;
    }

    public List<TicketReplyResponseDto> getAllTicketReplies(String ticketCode)
    {
        Cache cache = cacheManager.getCache("ticketReplies");

        // 1. Cerca prima nella cache del singolo ticketCode
        Cache.ValueWrapper idWrapper = cache != null ? cache.get("ticketCode::" + ticketCode) : null;
        if(idWrapper != null)
        {
            System.out.println(">>> getAllTicketReplies(" + ticketCode + ") - CACHE (singolo ticketCode)");
            @SuppressWarnings("unchecked")
            List<TicketReplyResponseDto> cached = (List<TicketReplyResponseDto>) idWrapper.get();
            return cached;
        }

        // 2. Cache vuota - va al DB e salva
        System.out.println(">>> getAllTicketReplies(" + ticketCode + ") - DATABASE (salvando in cache)");
        List<TicketReply> ticketReplies = ticketReplyRepository.findByTicketsCode(ticketCode);

        if(ticketReplies.isEmpty())
            return null;

        List<TicketReplyResponseDto> dtos = ticketReplies.stream()
                .map(this::toReplyResponseDto)
                .toList();

        if(cache != null)
            cache.put("ticketCode::" + ticketCode, dtos);

        return dtos;
    }

    @CacheEvict(value = "ticketReplies", allEntries = true)
    public Result.TicketReplyResult reply(String ticketCode, TicketReplyRequestDto reply)
    {
        if(reply.getMessage() == null || reply.getMessage().isEmpty() ||
            reply.getOid() == null || reply.getOid().isEmpty())
            return new Result.TicketReplyResult(TicketOperations.BAD_REQUEST, null);

        Optional<Ticket> ticketOpt = ticketRepository.findByCode(ticketCode);
        if(ticketOpt.isEmpty())
            return new Result.TicketReplyResult(TicketOperations.TICKET_NOT_FOUND, null);

        if(ticketOpt.get().getStatus() == TicketStatuses.CLOSED)
            return new Result.TicketReplyResult(TicketOperations.CANNOT_REPLY, null);

        Optional<TicketReply> checkRepliability = ticketReplyRepository.findFirstByTicketsCodeOrderByCreationDateDesc(ticketCode);

        if(checkRepliability.isPresent() && checkRepliability.get().getUsers().getOid().equals(reply.getOid()))
            return new Result.TicketReplyResult(TicketOperations.ALREADY_REPLIED, null);

        Optional<User> userOpt = userRepository.findByOid(reply.getOid());
        if(userOpt.isEmpty())
            return new Result.TicketReplyResult(TicketOperations.USER_NOT_FOUND, null);

        if(reply.isClosed() && userOpt.get().getUserType().equals(UserTypes.USER))
            return new Result.TicketReplyResult(TicketOperations.CANNOT_CLOSE, null);

        TicketReply ticketReply = new TicketReply();
        ticketReply.setTickets(ticketOpt.get());
        ticketReply.setUsers(userOpt.get());
        ticketReply.setMessage(reply.getMessage());
        ticketReply.setCode(ticketOpt.get().getCode() + (ticketReplyRepository.count()+1));
        ticketReplyRepository.save(ticketReply);

        if(ticketOpt.get().getStatus().name().equals(TicketStatuses.OPEN.name()) && !reply.isClosed())
        {
            Ticket changeStatus = ticketOpt.get();
            changeStatus.setStatus(TicketStatuses.WORKING);
            ticketRepository.save(changeStatus);
        }
        else if(reply.isClosed())
        {
            Ticket changeStatus = ticketOpt.get();
            changeStatus.setStatus(TicketStatuses.CLOSED);
            ticketRepository.save(changeStatus);
        }

        return new Result.TicketReplyResult(TicketOperations.OK, toReplyResponseDto(ticketReply));
    }

    private TicketReplyResponseDto toReplyResponseDto(TicketReply ticketReply)
    {
        TicketReplyResponseDto dto = new TicketReplyResponseDto();
        dto.setTicket(ticketReply.getTickets().getCode());
        if(ticketReply.getUsers().getUserType().name().equals(UserTypes.ADMIN.name()))
            dto.setUser("ADMIN");
        else
            dto.setUser(ticketReply.getUsers().getName() + ticketReply.getUsers().getSurname());
        dto.setMessage(ticketReply.getMessage());
        dto.setStatus(ticketReply.getTickets().getStatus().name());
        dto.setDate(ticketReply.getCreationDate().toLocalDate());

        return dto;
    }


}
