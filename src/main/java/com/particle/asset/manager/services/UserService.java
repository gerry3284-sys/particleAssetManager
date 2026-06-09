package com.particle.asset.manager.services;

import com.particle.asset.manager.DTO.AssetSummaryDto;
import com.particle.asset.manager.DTO.MovementSummaryResponseDto;
import com.particle.asset.manager.DTO.TicketSummaryResponseDto;
import com.particle.asset.manager.DTO.UserSummaryDto;
import com.particle.asset.manager.enums.UserOperations;
import com.particle.asset.manager.enums.UserTypes;
import com.particle.asset.manager.models.Movement;
import com.particle.asset.manager.models.Ticket;
import com.particle.asset.manager.models.User;
import com.particle.asset.manager.repositories.MovementRepository;
import com.particle.asset.manager.repositories.TicketRepository;
import com.particle.asset.manager.repositories.UserRepository;
import com.particle.asset.manager.results.Result;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService
{
    private final UserRepository userRepository;
    private final MovementRepository movementRepository;
    private final TicketRepository ticketRepository;

    public UserService(UserRepository repository, MovementRepository movementRepository, TicketRepository ticketRepository)
    {
        this.userRepository = repository;
        this.movementRepository = movementRepository;
        this.ticketRepository = ticketRepository;
    }

    // Mostra tutti gli utenti
    public List<User> getAllUsers() { return userRepository.findAll(); }

    // Ottiene uno specifico utente tramite l'id
    public User getUserById(String oid)
    {
        Optional<User> findUser = userRepository.findByOid(oid);

        // Restituisco l'utente se Optional contiene uno User, null altrimenti
        return findUser.orElse(null);
    }

    // Ottiene tutti i movimenti degli utenti
    public List<MovementSummaryResponseDto> getUserMovements(String userOid)
    {
        if(!userRepository.existsByOid(userOid))
           return null;

        List<Movement> movements = movementRepository.findByUsersOid(userOid);

        return movements.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Converte in DTO il movimento per restituire il dato corretto
    public MovementSummaryResponseDto convertToDTO(Movement movement)
    {
        MovementSummaryResponseDto dto = new MovementSummaryResponseDto();
        dto.setCode(movement.getCode());
        dto.setDate(movement.getDate());
        dto.setMovementType(String.valueOf(movement.getMovementType()));
        dto.setNote(movement.getNote());

        AssetSummaryDto assetSummaryDTO = new AssetSummaryDto();
        assetSummaryDTO.setBrand(movement.getAsset().getBrand());
        assetSummaryDTO.setModel(movement.getAsset().getModel());
        assetSummaryDTO.setSerialNumber(movement.getAsset().getSerialNumber());
        assetSummaryDTO.setCode(movement.getAsset().getCode());
        assetSummaryDTO.setRam(movement.getAsset().getRam());
        assetSummaryDTO.setStorage(movement.getAsset().getStorage());
        assetSummaryDTO.setStatusCode(movement.getAsset().getAssetStatusType().getCode());
        dto.setAsset(assetSummaryDTO);

        UserSummaryDto userSummaryDTO = new UserSummaryDto();
        userSummaryDTO.setOid(movement.getUsers().getOid());
        userSummaryDTO.setName(movement.getUsers().getName());
        userSummaryDTO.setSurname(movement.getUsers().getSurname());
        userSummaryDTO.setEmail(movement.getUsers().getEmail());
        dto.setUser(userSummaryDTO);

        return dto;
    }

    public List<TicketSummaryResponseDto> getUserTickets(String userOid)
    {
        if(!userRepository.existsByOid(userOid))
            return null;

        List<Ticket> tickets = ticketRepository.findByUsersOid(userOid);

        return tickets.stream().map(this::convertToTicketDto).collect(Collectors.toList());
    }

    private TicketSummaryResponseDto convertToTicketDto(Ticket ticket)
    {
        TicketSummaryResponseDto dto = new TicketSummaryResponseDto();
        dto.setTicketCode(ticket.getCode());
        dto.setOperation(ticket.getOperation());
        dto.setAssetCode(ticket.getAsset() == null ?null :ticket.getAsset().getCode());
        dto.setAssetTypeCode(ticket.getAssetType() == null ?null :ticket.getAssetType().getCode());
        dto.setUser(ticket.getUsers().getName() + " " + ticket.getUsers().getSurname());
        dto.setStatus(ticket.getStatus().name());
        dto.setDate(ticket.getDate());
        dto.setUserCheckReply(ticket.isUserCheckReply());
        dto.setPriority(ticket.getPriority());

        return dto;
    }
}
