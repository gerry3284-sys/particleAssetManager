package com.particle.asset.manager.services;

import com.particle.asset.manager.DTO.AssetSummaryDTO;
import com.particle.asset.manager.DTO.MovementSummaryDTO;
import com.particle.asset.manager.DTO.UserSummaryDTO;
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
    public List<MovementSummaryDTO> getUserMovements(Long userId)
    {
        if(!userRepository.existsById(userId))
           return null;

        List<Movement> movements = movementRepository.findByUsersId(userId);

        return movements.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Converte in DTO il movimento per restituire il dato corretto
    public MovementSummaryDTO convertToDTO(Movement movement)
    {
        MovementSummaryDTO dto = new MovementSummaryDTO();
        dto.setId(movement.getId());
        dto.setDate(movement.getDate());
        dto.setMovementType(movement.getMovementType());
        dto.setNote(movement.getNote());

        AssetSummaryDTO assetSummaryDTO = new AssetSummaryDTO();
        assetSummaryDTO.setId(movement.getAsset().getId());
        assetSummaryDTO.setBrand(movement.getAsset().getBrand());
        assetSummaryDTO.setModel(movement.getAsset().getModel());
        assetSummaryDTO.setSerialNumber(movement.getAsset().getSerialNumber());
        dto.setAsset(assetSummaryDTO);

        UserSummaryDTO userSummaryDTO = new UserSummaryDTO();
        userSummaryDTO.setId(movement.getUsers().getId());
        userSummaryDTO.setName(movement.getUsers().getName());
        userSummaryDTO.setSurname(movement.getUsers().getSurname());
        userSummaryDTO.setEmail(movement.getUsers().getEmail());
        dto.setUser(userSummaryDTO);

        return dto;
    }
}
