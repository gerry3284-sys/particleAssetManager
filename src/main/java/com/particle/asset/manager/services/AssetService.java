package com.particle.asset.manager.services;

import com.particle.asset.manager.DTO.*;
import com.particle.asset.manager.enumerations.StatusForControllerOperations;
import com.particle.asset.manager.models.*;
import com.particle.asset.manager.repositories.*;
import com.particle.asset.manager.results.Result;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
    private final CacheManager cacheManager;

    public AssetService(AssetRepository assetRepository, MovementRepository movementRepository,
                        UserRepository userRepository, AssetTypeRepository assetTypeRepository,
                        BusinessUnitRepository businessUnitRepository,
                        AssetStatusTypeRepository assetStatusTypeRepository,
                        CacheManager cacheManager)
    {
        this.assetRepository = assetRepository;
        this.movementRepository = movementRepository;
        this.userRepository = userRepository;
        this.assetTypeRepository = assetTypeRepository;
        this.businessUnitRepository = businessUnitRepository;
        this.assetStatusTypeRepository = assetStatusTypeRepository;
        this.cacheManager = cacheManager;
    }

    // Mostra tutti gli asset (con cache)
    // @Cacheable → Quando si chiama "getAllAssets()" la prima volta, i dati vengono recuperati
    //              dal database e salvati in cache con la chiave "all". Le chiamate successive
    //              leggono direttamente dalla cache per 8 ore.
    @Cacheable(value = "assets", key = "'all'")
    public List<Asset> getAllAssets()
    {
        System.out.println(">>> Fetching ALL Assets from database...");

        List<Asset> assets = assetRepository.findAll();

        // Popola anche le cache per singoli ID
        Cache cache = cacheManager.getCache("assets");
        if (cache != null)
            assets.forEach(asset -> cache.put("id::" + asset.getCode(), asset));

        return assets;
    }

    // Crea un asset (reset cache)
    // @CacheEvict → Quando si crea/aggiorna un record, la cache viene
    //               completamente svuotata (clear). Alla prossima chiamata GET,
    //               i dati verranno caricati direttamente dal database.
    @CacheEvict(value = "assets", allEntries = true)
    public AssetBodyDTO createAsset(AssetBodyDTO assetDTO)
    {
        // Un Asset è automaticamente di una BU ?
        if(assetDTO == null || assetDTO.getAssetTypeCode() == null || assetDTO.getBrand() == null ||
                assetDTO.getModel() == null || assetDTO.getBusinessUnitCode() == null ||
                assetDTO.getSerialNumber() == null || assetDTO.getAssetStatusTypeCode() == null ||
                assetDTO.getRam() == null || assetDTO.getHardDisk() == null ||
                assetRepository.existsBySerialNumber(assetDTO.getSerialNumber()))
            return null;

        Optional<AssetType> assetTypeById = assetTypeRepository.findByCode(assetDTO.getAssetTypeCode());
        Optional<BusinessUnit> businessUnitById = businessUnitRepository.findByCode(assetDTO.getBusinessUnitCode());
        Optional<AssetStatusType> assetStatusTypeById = assetStatusTypeRepository.findByCode(assetDTO.getAssetStatusTypeCode());

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

        String nameWithoutSpaces = asset.getSerialNumber().replaceAll("\\s+", "");
        asset.setCode(nameWithoutSpaces.toUpperCase()
                .substring(0, Math.min(2, nameWithoutSpaces.length())) + (assetRepository.count()+1));

        assetRepository.save(asset);

        return assetDTO;
    }

    // Ottiene un asset tramite il code
    public Asset getAssetById(String code)
    {
        Cache cache = cacheManager.getCache("assets");

        // 1. Cerca prima nella cache del singolo ID
        Cache.ValueWrapper idWrapper = cache != null ? cache.get("id::" + code) : null;
        if (idWrapper != null) {
            System.out.println(">>> getAssetById(" + code + ") - CACHE (singolo ID)");
            return (Asset) idWrapper.get();
        }

        // 2. Se non c'è, cerca nella cache "all"
        Cache.ValueWrapper allWrapper = cache != null ? cache.get("all") : null;
        if (allWrapper != null) {
            System.out.println(">>> getAssetById(" + code + ") - CACHE (filtrato da 'all')");
            @SuppressWarnings("unchecked")
            List<Asset> allAssets = (List<Asset>) allWrapper.get();
            return allAssets.stream()
                    .filter(asset -> asset.getCode().equals(code))
                    .findFirst()
                    .orElse(null);
        }

        // 3. Cache vuota - va al DB e salva SOLO questo ID
        System.out.println(">>> getAssetById(" + code + ") - DATABASE (salvando singolo ID in cache)");
        Asset asset = assetRepository.findByCode(code).orElse(null);
        if (cache != null && asset != null)
            cache.put("id::" + code, asset);

        return asset;
    }

    // Aggiorna l'asset, tramite il code (reset cache)
    @CacheEvict(value = "assets", allEntries = true)
    public Result.AssetBodyDTOResult updateAssetById(String code, AssetBodyDTO assetDTO)
    {
        if(assetDTO == null || assetDTO.getAssetTypeCode() == null || assetDTO.getBrand() == null ||
                assetDTO.getModel() == null || assetDTO.getBusinessUnitCode() == null ||
                assetDTO.getAssetStatusTypeCode() == null || assetDTO.getSerialNumber() == null)
            return new Result.AssetBodyDTOResult(StatusForControllerOperations.BAD_REQUEST, null);

        Optional<Asset> assetById = assetRepository.findByCode(code);
        Optional<AssetType> assetTypeById = assetTypeRepository.findByCode(assetDTO.getAssetTypeCode());
        Optional<BusinessUnit> businessUnitById = businessUnitRepository.findByCode(assetDTO.getBusinessUnitCode());
        Optional<AssetStatusType> assetStatusTypeById = assetStatusTypeRepository.findByCode(assetDTO.getAssetStatusTypeCode());

        if(assetById.isEmpty())
            return new Result.AssetBodyDTOResult(StatusForControllerOperations.NOT_FOUND, null);

        if(assetTypeById.isEmpty() || businessUnitById.isEmpty() || assetStatusTypeById.isEmpty())
            return new Result.AssetBodyDTOResult(StatusForControllerOperations.BAD_REQUEST, null);

        Asset updatedAsset = assetById.get();

        if(!(updatedAsset.getSerialNumber().equals(assetDTO.getSerialNumber())) &&
                assetRepository.existsBySerialNumber(assetDTO.getSerialNumber()))
            return new Result.AssetBodyDTOResult(StatusForControllerOperations.BAD_REQUEST, null);

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
        assetRepository.save(updatedAsset);

        return new Result.AssetBodyDTOResult(StatusForControllerOperations.OK, assetDTO);
    }

    // Ottiene tutti i movimenti di un certo asset
    public List<MovementSummaryDTO> getAssetMovementDTO(String assetCode)
    {
        if(!(assetRepository.existsByCode(assetCode) && movementRepository.existsByAssetCode(assetCode)))
            return null;

        List<Movement> movements = movementRepository.findByAssetCode(assetCode);

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

    // Assegna/Restituisce/Dismette un asset (reset cache)
    @CacheEvict(value = "assets", allEntries = true)
    public Result.MovementBodyDTOResult assignReturnedDismissAsset(String assetCode, MovementRequestBodyDTO movementDTO)
    {
        // Se l'id della path e quello dell'input sono diversi --> null
        if(!(assetRepository.existsByCode(assetCode)) ||
                !(movementDTO.getMovementType().equals("Returned") ||
                        movementDTO.getMovementType().equals("Assigned") ||
                        movementDTO.getMovementType().equals("Dismissed")))
            return new Result.MovementBodyDTOResult(StatusForControllerOperations.BAD_REQUEST, null);

        // assetId == movement.getAsset().getId()
        Optional<Asset> assetOpt = assetRepository.findByCode(assetCode);
        if(assetOpt.isEmpty())
            return new Result.MovementBodyDTOResult(StatusForControllerOperations.NOT_FOUND, null);

        Optional<User> userOpt = userRepository.findById(movementDTO.getUser());
        if(userOpt.isEmpty())
            return new Result.MovementBodyDTOResult(StatusForControllerOperations.NOT_FOUND, null);

        Optional<Movement> lastMovement = movementRepository.findFirstByAssetCodeOrderByDateDesc(assetCode);

        // Se sto facendo "Assigned"
        if(movementDTO.getMovementType().equals("Assigned"))
        {
            // Blocca se l'ultimo movimento è già "Assigned"
            if(lastMovement.isPresent() &&
                    lastMovement.get().getMovementType().equals("Assigned"))
                return new Result.MovementBodyDTOResult(StatusForControllerOperations.BAD_REQUEST, null); // Asset già assegnato!

            if(lastMovement.isPresent() &&
                    lastMovement.get().getMovementType().equals("Dismissed"))
                return new Result.MovementBodyDTOResult(StatusForControllerOperations.BAD_REQUEST, null); // Asset già dismesso
        }

        // Se sto facendo "Returned" (riconsegna)
        if(movementDTO.getMovementType().equals("Returned"))
        {
            // Blocca se NON c'è nessun movimento precedente
            if(lastMovement.isEmpty())
                return new Result.MovementBodyDTOResult(StatusForControllerOperations.BAD_REQUEST, null); // Non puoi restituire se non è mai stato assegnato

            if(lastMovement.get().getMovementType().equals("Dismissed"))
                return new Result.MovementBodyDTOResult(StatusForControllerOperations.BAD_REQUEST, null); // Asset già Dismesso

            // Blocca se l'ultimo movimento NON è "Assigned"
            /*if (!lastMovement.get().getMovementType().equals("Assigned"))
                return null; // L'asset non è assegnato, quindi non puoi restituirlo*/

        }

        if(movementDTO.getMovementType().equals("Dismissed") && lastMovement.isPresent())
        {
            if(lastMovement.get().getMovementType().equals("Dismissed"))
                return null; // Non si può effettuare la dismissione un asset già dismesso

            if(lastMovement.get().getMovementType().equals("Assigned"))
                return null; // L'asset può essere dismesso solo se non è assegnato
        }

        Movement addedMovement = new Movement();
        addedMovement.setMovementType(movementDTO.getMovementType());
        addedMovement.setAsset(assetOpt.get());
        addedMovement.setUsers(userOpt.get());
        addedMovement.setNote(movementDTO.getNote());
        movementRepository.save(addedMovement);

        return new Result.MovementBodyDTOResult(StatusForControllerOperations.OK,
                new MovementResponseBodyDTO(assetCode, movementDTO.getUser(),
                        movementDTO.getMovementType(), movementDTO.getNote()));
    }
}