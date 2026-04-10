package com.particle.asset.manager.services;

import com.particle.asset.manager.DTO.AssetSummaryDto;
import com.particle.asset.manager.DTO.MovementSummaryResponseDto;
import com.particle.asset.manager.DTO.UserSummaryDto;
import com.particle.asset.manager.models.Movement;
import com.particle.asset.manager.models.User;
import com.particle.asset.manager.repositories.MovementRepository;
import com.particle.asset.manager.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService
{
    private final UserRepository userRepository;
    private final MovementRepository movementRepository;

    public UserService(UserRepository repository, MovementRepository movementRepository)
    {
        this.userRepository = repository;
        this.movementRepository = movementRepository;
    }

    // Mostra tutti gli utenti
    public List<User> getAllUsers() { return userRepository.findAll(); }

    // Ottiene uno specifico utente tramite l'id
    public User getUserById(Long id)
    {
        Optional<User> findUser = userRepository.findById(id);

        // Restituisco l'utente se Optional contiene uno User, null altrimenti
        return findUser.orElse(null);
    }

    // Ottiene tutti i movimenti degli utenti
    public List<MovementSummaryResponseDto> getUserMovements(Long userId)
    {
        if(!userRepository.existsById(userId))
           return null;

        List<Movement> movements = movementRepository.findByUsersId(userId);

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
        assetSummaryDTO.setHardDisk(movement.getAsset().getHardDisk());
        assetSummaryDTO.setStatusCode(movement.getAsset().getAssetStatusType().getCode());
        dto.setAsset(assetSummaryDTO);

        UserSummaryDto userSummaryDTO = new UserSummaryDto();
        userSummaryDTO.setId(movement.getUsers().getId());
        userSummaryDTO.setName(movement.getUsers().getName());
        userSummaryDTO.setSurname(movement.getUsers().getSurname());
        userSummaryDTO.setEmail(movement.getUsers().getEmail());
        dto.setUser(userSummaryDTO);

        return dto;
    }
}
