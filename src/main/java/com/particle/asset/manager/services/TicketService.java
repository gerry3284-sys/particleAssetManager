package com.particle.asset.manager.services;

import com.particle.asset.manager.DTO.TicketRequestDto;
import com.particle.asset.manager.DTO.TicketResponseBodyDto;
import com.particle.asset.manager.models.Asset;
import com.particle.asset.manager.models.AssetType;
import com.particle.asset.manager.models.Ticket;
import com.particle.asset.manager.models.User;
import com.particle.asset.manager.repositories.AssetRepository;
import com.particle.asset.manager.repositories.AssetTypeRepository;
import com.particle.asset.manager.repositories.TicketRepository;
import com.particle.asset.manager.repositories.UserRepository;
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
    @Cacheable(value = "assetTypes", key = "'all'")
    public List<Ticket> getAllTickets()
    {
        System.out.println(">>> Fetching ALL Tickets from database...");
        List<Ticket> tickets = ticketRepository.findAll();

        // Popola anche le cache per i singoli ID
        Cache cache = cacheManager.getCache("tickets");
        if(cache != null)
            tickets.forEach(ticket -> cache.put("id::" + ticket.getId(), ticket));

        return tickets;
    }

    public Ticket getTicketById(Long id)
    {
        Cache cache = cacheManager.getCache("tickets");

        // 1. Cerca prima nella cache del singolo ID
        Cache.ValueWrapper idWrapper = cache != null ? cache.get("id::" + id) : null;
        if(idWrapper != null)
        {
            System.out.println(">>> getTicketById(" + id + ") - CACHE (singolo ID)");
            return (Ticket) idWrapper.get();
        }

        // 2. Se non c'è, cerca nella cache "all"
        Cache.ValueWrapper allWrapper = cache != null ? cache.get("all") : null;
        if(allWrapper != null)
        {
            // Concludere di risolvere
            System.out.println(">>> getTicketById(" + id + ") - CACHE (filtrato da 'all')");
            @SuppressWarnings("unchecked")
            List<Ticket> allTickets = (List<Ticket>) allWrapper.get();
            return allTickets.stream()
                    .filter(ticket -> ticket.getId().equals(id))
                    .findFirst()
                    .orElse(null);
        }

        // 3. Cache vuota - va al DB e salva SOLO questo ID
        System.out.println(">>> getTicketById(" + id + ") - DATABASE (salvando singolo ID in cache)");
        Ticket ticket = ticketRepository.findById(id).orElse(null);
        if (cache != null && ticket != null)
            cache.put("id::" + id, ticket);

        return ticket;
    }

    // Crea un Type (reset cache)
    // @CacheEvict → Quando si crea/aggiorna/disattiva un record, la cache viene
    //               completamente svuotata (clear). Alla prossima chiamata GET,
    //               i dati verrano caricati direttamente dal database.
    @CacheEvict(value = "tickets", allEntries = true)
    public TicketRequestDto createTicket(TicketRequestDto ticket)
    {
        if(ticket.getUserCode() == null || ticket.getOperation() == null ||
            (ticket.getAssetTypeCode() == null && ticket.getAssetCode() == null) ||
            (ticket.getAssetTypeCode() != null && ticket.getAssetCode() != null) ||
            ticket.getMessage() == null || ticket.getStatus() == null)
            return null;

        //TicketResponseBodyDto createdTicket = new TicketResponseBodyDto();

        Optional<User> userOpt = userRepository.findByOid(ticket.getUserCode());
        Optional<AssetType> assetTypeOpt = assetTypeRepository.findByCode(ticket.getAssetTypeCode());
        Optional<Asset> assetOpt = assetRepository.findByCode(ticket.getAssetCode());

        if(userOpt.isEmpty() || (assetTypeOpt.isEmpty() && ticket.getAssetTypeCode() != null) ||
                (assetOpt.isEmpty() && ticket.getAssetCode() != null))
            return null;

        Ticket createdTicket = new Ticket();
        createdTicket.setUsers(userOpt.get());
        createdTicket.setAssetType(assetTypeOpt.orElse(null));
        createdTicket.setAsset(assetOpt.orElse(null));
        createdTicket.setOperation(ticket.getOperation());
        createdTicket.setMessage(ticket.getMessage());
        createdTicket.setStatus(ticket.getStatus());

        ticketRepository.save(createdTicket);

        return ticket;
    }
}
