package com.particle.asset.manager.services;

import com.particle.asset.manager.DTO.AssetRequestBodyDTO;
import com.particle.asset.manager.DTO.AssetSummaryDTO;
import com.particle.asset.manager.DTO.MovementSummaryDTO;
import com.particle.asset.manager.DTO.UserSummaryDTO;
import com.particle.asset.manager.enumerations.StatusForControllerOperations;
import com.particle.asset.manager.models.*;
import com.particle.asset.manager.repositories.*;
import com.particle.asset.manager.results.Result;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AssetService
{
    private final AssetRepository assetRepository;
    private final MovementRepository movementRepository;
    private final UserRepository userRepository;
    private final AssetTypeRepository assetTypeRepository;
    private final BusinessUnitRepository businessUnitRepository;
    private final AssetStatusTypeRepository assetStatusTypeRepository;

    public AssetService(AssetRepository assetRepository, MovementRepository movementRepository,
                        UserRepository userRepository, AssetTypeRepository assetTypeRepository,
                        BusinessUnitRepository businessUnitRepository,
                        AssetStatusTypeRepository assetStatusTypeRepository)
    {
        this.assetRepository = assetRepository;
        this.movementRepository = movementRepository;
        this.userRepository = userRepository;
        this.assetTypeRepository = assetTypeRepository;
        this.businessUnitRepository = businessUnitRepository;
        this.assetStatusTypeRepository = assetStatusTypeRepository;
    }

    // Mostra tutti gli asset
    public List<Asset> getAllAssets() { return assetRepository.findAll(); }

    // Crea un asset
    public Asset createAsset(AssetRequestBodyDTO assetDTO)
    {
        // Un Asset è automaticamente di una BU ?
        if(assetDTO == null || assetDTO.getAssetType() == null || assetDTO.getBrand() == null ||
                assetDTO.getModel() == null || assetDTO.getBusinessUnit() == null ||
                assetDTO.getSerialNumber() == null || assetDTO.getAssetStatusType() == null ||
                assetDTO.getRam() == null || assetDTO.getHardDisk() == null ||
                assetRepository.existsBySerialNumber(assetDTO.getSerialNumber()))
            return null;

        Optional<AssetType> assetTypeById = assetTypeRepository.findById(assetDTO.getAssetType());
        Optional<BusinessUnit> businessUnitById = businessUnitRepository.findById(assetDTO.getBusinessUnit());
        Optional<AssetStatusType> assetStatusTypeById = assetStatusTypeRepository.findById(assetDTO.getAssetStatusType());

        if(assetTypeById.isEmpty() || businessUnitById.isEmpty() || assetStatusTypeById.isEmpty())
            return null;

        Asset asset = new Asset();
        asset.setAssetType(assetTypeById.get());
        asset.setBrand(assetDTO.getBrand());
        asset.setModel(assetDTO.getModel());
        asset.setBusinessUnit(businessUnitById.get());
        asset.setSerialNumber(assetDTO.getSerialNumber());
        asset.setRam(assetDTO.getRam());
        asset.setHardDisk(assetDTO.getHardDisk());
        asset.setAssetStatusType(assetStatusTypeById.get());
        Long recentId = assetRepository.findTopByOrderByIdDesc().getId();

        String nameWithoutSpaces = asset.getSerialNumber().replaceAll("\\s+", "");
        asset.setCode(nameWithoutSpaces.toUpperCase()
                .substring(0, Math.min(2, nameWithoutSpaces.length())) + (recentId != null ?recentId+1 :1L));

        return assetRepository.save(asset);
    }

    // Ottiene un asset tramite l'id
    public Asset getAssetById(Long id) { return assetRepository.findById(id).orElse(null); }

    // Aggiorna l'asset, tramite l'id
    public Result.AssetResult updateAssetById(Long id, AssetRequestBodyDTO assetDTO)
    {
        if(assetDTO == null || assetDTO.getAssetType() == null || assetDTO.getBrand() == null ||
                assetDTO.getModel() == null || assetDTO.getBusinessUnit() == null ||
                assetDTO.getAssetStatusType() == null || assetDTO.getSerialNumber() == null)
            return new Result.AssetResult(StatusForControllerOperations.BAD_REQUEST, null);

        Optional<Asset> assetById = assetRepository.findById(id);
        Optional<AssetType> assetTypeById = assetTypeRepository.findById(assetDTO.getAssetType());
        Optional<BusinessUnit> businessUnitById = businessUnitRepository.findById(assetDTO.getBusinessUnit());
        Optional<AssetStatusType> assetStatusTypeById = assetStatusTypeRepository.findById(assetDTO.getAssetStatusType());

        if(assetById.isEmpty() || assetTypeById.isEmpty() ||
                businessUnitById.isEmpty() || assetStatusTypeById.isEmpty())
            return new Result.AssetResult(StatusForControllerOperations.NOT_FOUND, null);

        Asset updatedAsset = assetById.get();

        if(!(updatedAsset.getSerialNumber().equals(assetDTO.getSerialNumber())) &&
                assetRepository.existsBySerialNumber(assetDTO.getSerialNumber()))
            return new Result.AssetResult(StatusForControllerOperations.BAD_REQUEST, null);

        updatedAsset.setModel(assetDTO.getModel());
        updatedAsset.setBrand(assetDTO.getBrand());
        updatedAsset.setSerialNumber(assetDTO.getSerialNumber());
        updatedAsset.setAssetType(assetTypeById.get());
        updatedAsset.setBusinessUnit(businessUnitById.get());
        updatedAsset.setAssetStatusType(assetStatusTypeById.get());
        updatedAsset.setNote(assetDTO.getNote());
        updatedAsset.setRam(assetDTO.getRam());
        updatedAsset.setHardDisk(assetDTO.getHardDisk());
        updatedAsset.setUpdateDate(LocalDateTime.now());

        return new Result.AssetResult(StatusForControllerOperations.OK, assetRepository.save(updatedAsset));
    }

    // Ottiene tutti i movimenti di un certo asset
    public List<MovementSummaryDTO> getAssetMovementDTO(Long assetId)
    {
        if(!assetRepository.existsById(assetId))
            return null;

        List<Movement> movements = movementRepository.findByAssetId(assetId);

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
        assetSummaryDTO.setRam(movement.getAsset().getRam());
        assetSummaryDTO.setHardDisk(movement.getAsset().getHardDisk());
        dto.setAsset(assetSummaryDTO);

        UserSummaryDTO userSummaryDTO = new UserSummaryDTO();
        userSummaryDTO.setId(movement.getUsers().getId());
        userSummaryDTO.setName(movement.getUsers().getName());
        userSummaryDTO.setSurname(movement.getUsers().getSurname());
        userSummaryDTO.setEmail(movement.getUsers().getEmail());
        dto.setUser(userSummaryDTO);

        return dto;
    }

    // Assegna/Restituisce/Dismette un asset
    public Movement assignReturnedDismissAsset(Long assetId, Movement movement)
    {
        // Se l'id della path e quello dell'input sono diversi --> null
        if(!(movement.getAsset().getId().equals(assetId)) ||
            !(movement.getMovementType().equals("Returned") ||
                    movement.getMovementType().equals("Assigned") ||
                movement.getMovementType().equals("Dismissed")))
            return null;

        // assetId == movement.getAsset().getId()
        Optional<Asset> assetOpt = assetRepository.findById(assetId);
        if(assetOpt.isEmpty())
            return null;
        else
            movement.setAsset(assetOpt.get());

        Optional<User> userOpt = userRepository.findById(movement.getUsers().getId());
        if(userOpt.isEmpty())
            return null;
        else
            movement.setUsers(userOpt.get());

        Optional<Movement> lastMovement = movementRepository.findFirstByAssetIdOrderByDateDesc(assetId);

        // Se sto facendo "Assigned"
        if(movement.getMovementType().equals("Assigned"))
        {
            // Blocca se l'ultimo movimento è già "Assigned"
            if(lastMovement.isPresent() &&
                    lastMovement.get().getMovementType().equals("Assigned"))
                return null; // Asset già assegnato!

            if(lastMovement.isPresent() &&
                    lastMovement.get().getMovementType().equals("Dismissed"))
                return null; // Asset già dismesso
        }

        // Se sto facendo "Returned" (riconsegna)
        if(movement.getMovementType().equals("Returned"))
        {
            // Blocca se NON c'è nessun movimento precedente
            if(lastMovement.isEmpty())
                return null; // Non puoi restituire se non è mai stato assegnato

            if(lastMovement.get().getMovementType().equals("Dismissed"))
                return null; // Asset già Dismesso

            // Blocca se l'ultimo movimento NON è "Assigned"
            /*if (!lastMovement.get().getMovementType().equals("Assigned"))
                return null; // L'asset non è assegnato, quindi non puoi restituirlo*/

        }

        if(movement.getMovementType().equals("Dismissed") && lastMovement.isPresent())
        {
            if(lastMovement.get().getMovementType().equals("Dismissed"))
                return null; // Non si può effettuare la dismissione un asset già dismesso

            if(lastMovement.get().getMovementType().equals("Assigned"))
                return null; // L'asset può essere dismesso solo se non è assegnato
        }

        return movementRepository.save(movement);
    }
}
