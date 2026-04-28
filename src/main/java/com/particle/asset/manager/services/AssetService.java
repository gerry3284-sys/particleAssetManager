package com.particle.asset.manager.services;

import com.particle.asset.manager.DTO.*;
import com.particle.asset.manager.enums.*;
import com.particle.asset.manager.models.*;
import com.particle.asset.manager.repositories.*;
import com.particle.asset.manager.results.Result;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import jakarta.annotation.PostConstruct;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Arrays;

@Service
public class AssetService
{
    @Value("${receipts.dir}")
    private String receiptsDir;

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

    // ============================================
    // ASSET METHODS
    // ============================================

    @Cacheable(value = "assets", key = "'all'")
    public List<FetchAssetResponseDto> getAllAssets()
    {
        System.out.println(">>> Fetching ALL Assets from database...");
        List<Asset> assets = assetRepository.findAll();

        List<FetchAssetResponseDto> assetsDto = assets.stream()
                .map(this::toResponseDto)
                .toList();

        // Popola anche le cache per singoli ID
        Cache cache = cacheManager.getCache("assets");
        if (cache != null)
            assetsDto.forEach(dto -> cache.put("id::" + dto.getCode(), dto));

        return assetsDto;
    }

    public List<AssetListRowResponseDto> getAssetList()
    {
        List<Asset> assets = assetRepository.findAll();

        return assets.stream()
                .map(asset -> {
                    Optional<Movement> lastMovement =
                            movementRepository.findFirstByAssetCodeOrderByDateDesc(asset.getCode());

                    String assignedUser = "-";
                    LocalDateTime assignmentDate = null;

                    if (lastMovement.isPresent()
                            && BasicAssetStatuses.ASSIGNED.name().equals(lastMovement.get().getMovementType().name()))
                    {
                        User u = lastMovement.get().getUsers();
                        assignedUser = u.getName() + " " + u.getSurname();
                        assignmentDate = lastMovement.get().getDate();
                    }

                    return new AssetListRowResponseDto(
                            asset.getAssetStatusType().getName(),
                            asset.getBrand(),
                            asset.getModel(),
                            asset.getSerialNumber(),
                            asset.getCode(),
                            asset.getAssetType().getName(),
                            assignedUser,
                            asset.getBusinessUnit().getName(),
                            assignmentDate
                    );
                })
                .toList();
    }

    private FetchAssetResponseDto toResponseDto(Asset asset)
    {
        FetchAssetResponseDto assetDto = new FetchAssetResponseDto();
        AssetTypeResponseDto assetTypeDto = new AssetTypeResponseDto();
        BusinessUnitResponseDto businessUnitDto = new BusinessUnitResponseDto();
        AssetStatusTypeStatusResponseDto assetStatusTypeDto = new AssetStatusTypeStatusResponseDto();

        assetDto.setCode(asset.getCode());
        assetDto.setBrand(asset.getBrand());
        assetDto.setModel(asset.getModel());
        assetDto.setSerialNumber(asset.getSerialNumber());
        assetDto.setNote(asset.getNote());
        assetDto.setRam(asset.getRam());
        assetDto.setStorage(asset.getStorage());
        assetDto.setEndMaintenance(asset.getEndMaintenanceDate());

        assetTypeDto.setCode(asset.getAssetType().getCode());
        assetTypeDto.setName(asset.getAssetType().getName());
        assetTypeDto.setRam(asset.getAssetType().isRam());
        assetTypeDto.setStorage(asset.getAssetType().isStorage());
        assetTypeDto.setActive(asset.getAssetType().isActive());
        assetDto.setAssetType(assetTypeDto);

        businessUnitDto.setName(asset.getBusinessUnit().getName());
        businessUnitDto.setCode(asset.getBusinessUnit().getCode());
        businessUnitDto.setActive(asset.getBusinessUnit().isActive());
        assetDto.setBusinessUnit(businessUnitDto);

        assetStatusTypeDto.setName(asset.getAssetStatusType().getName());
        assetStatusTypeDto.setCode(asset.getAssetStatusType().getCode());
        assetDto.setAssetStatusType(assetStatusTypeDto);

        return assetDto;
    }

    @CacheEvict(value = "assets", allEntries = true)
    public Result.AssetDtoResult createAsset(AssetRequestDto assetDTO)
    {
        if(assetDTO == null || assetDTO.getAssetTypeCode() == null || assetDTO.getBrand() == null ||
                assetDTO.getModel() == null || assetDTO.getBusinessUnitCode() == null ||
                assetDTO.getSerialNumber() == null /*||
                assetDTO.getRam() == null || assetDTO.getStorage() == null ||
                assetRepository.existsBySerialNumber(assetDTO.getSerialNumber().trim())*/)
            return new Result.AssetDtoResult(AssetOperations.BAD_REQUEST, null);

        if(assetRepository.existsBySerialNumber(assetDTO.getSerialNumber().trim()))
            return new Result.AssetDtoResult(AssetOperations.ALREADY_EXISTS, null);

        Optional<AssetType> assetTypeByCode = assetTypeRepository.findByCode(assetDTO.getAssetTypeCode());

        if(assetTypeByCode.isPresent())
        {
            if((assetTypeByCode.get().isRam()) && (assetDTO.getRam() == null || (assetDTO.getRam() <= 0)))
                return new Result.AssetDtoResult(AssetOperations.INVALID_RAM, null);

            if((assetTypeByCode.get().isStorage()) &&
                    (assetDTO.getStorage() == null || !isValidStorageFormat(assetDTO.getStorage().trim())))
                return new Result.AssetDtoResult(AssetOperations.INVALID_STORAGE, null);
        }
        else
            return new Result.AssetDtoResult(AssetOperations.BAD_REQUEST, null);

        Optional<BusinessUnit> businessUnitByCode = businessUnitRepository.findByCode(assetDTO.getBusinessUnitCode());
        // ↓ Cerco e inserisco il lo status "Disponibile" alla creazione dell'Asset
        Optional<AssetStatusType> assetStatusTypeByCode =
                assetStatusTypeRepository.findByName(BasicAssetStatuses.AVAILABLE.name());

        if(businessUnitByCode.isEmpty())
            return new Result.AssetDtoResult(AssetOperations.BAD_REQUEST, null);

        Asset asset = new Asset();
        asset.setAssetType(assetTypeByCode.get());
        asset.setBrand(assetDTO.getBrand().trim());
        asset.setModel(assetDTO.getModel().trim());
        asset.setBusinessUnit(businessUnitByCode.get());
        asset.setSerialNumber(assetDTO.getSerialNumber().trim());
        asset.setRam(assetTypeByCode.get().isRam() ?assetDTO.getRam() :null);
        asset.setStorage(assetTypeByCode.get().isStorage()
                ?assetDTO.getStorage().trim().replaceAll("\\s+", " ")
                :null);
        asset.setAssetStatusType(assetStatusTypeByCode.get());
        asset.setNote(assetDTO.getNote().trim());

        String nameWithoutSpaces = asset.getSerialNumber().replaceAll("\\s+", "");
        asset.setCode(nameWithoutSpaces.toUpperCase()
                .substring(0, Math.min(2, nameWithoutSpaces.length())) + (assetRepository.count()+1));

        assetRepository.save(asset);

        return new Result.AssetDtoResult(AssetOperations.OK, getAssetResponseDto(asset));
    }

    public boolean isValidStorageFormat(String storage) {
        // Costruisce il pattern dagli enum: (SSD|HDD|NVMe|...)
        String storagePattern = Arrays.stream(StorageTypes.values())
                .map(s -> s.name())
                .collect(Collectors.joining("|"));

        // Costruisce il pattern dagli enum: (GB|TB|MB|...)
        String unitPattern = Arrays.stream(DataSizeUnits.values())
                .map(d -> d.name())
                .collect(Collectors.joining("|"));

        // Pattern finale: "SSD 500 GB" oppure "HDD 1 TB" ecc.
        String regex = "(?i)(" + storagePattern + ")\\s+[1-9]\\d*\\s+(" + unitPattern + ")";

        return storage != null && storage.matches(regex);
    }

    public FetchAssetResponseDto getAssetByCode(String code)
    {
        Cache cache = cacheManager.getCache("assets");

        // 1. Cerca prima nella cache del singolo ID
        Cache.ValueWrapper idWrapper = cache != null ? cache.get("id::" + code) : null;
        if (idWrapper != null) {
            System.out.println(">>> getAssetById(" + code + ") - CACHE (singolo ID)");
            return (FetchAssetResponseDto) idWrapper.get();
        }

        // 2. Se non c'è, cerca nella cache "all"
        Cache.ValueWrapper allWrapper = cache != null ? cache.get("all") : null;
        if (allWrapper != null) {
            System.out.println(">>> getAssetById(" + code + ") - CACHE (filtrato da 'all')");
            @SuppressWarnings("unchecked")
            List<FetchAssetResponseDto> allAssets = (List<FetchAssetResponseDto>) allWrapper.get();
            return allAssets.stream()
                    .filter(asset -> asset.getCode().equals(code))
                    .findFirst()
                    .orElse(null);
        }

        // 3. Cache vuota - va al DB e salva SOLO questo ID
        System.out.println(">>> getAssetById(" + code + ") - DATABASE (salvando singolo ID in cache)");
        Asset asset = assetRepository.findByCode(code).orElse(null);
        if(asset == null) return null;

        FetchAssetResponseDto dto = toResponseDto(asset);

        if (cache != null)
            cache.put("id::" + code, dto);

        return dto;
    }

    public List<AssetMaintenanceListRowResponseDto> getUnderMaintenanceAssets()
    {
        List<Asset> assets = assetRepository.findByAssetStatusType_Code("MA4");

        return assets.stream()
                .map(asset -> {
                    Optional<Movement> lastMovement =
                            movementRepository.findFirstByAssetCodeOrderByDateDesc(asset.getCode());

                    LocalDateTime returnedDate = null;

                    if (lastMovement.isPresent()
                            && lastMovement.get().getAsset().getAssetStatusType().getCode().equals("MA4"))
                        returnedDate = lastMovement.get().getDate();

                    return new AssetMaintenanceListRowResponseDto(
                            asset.getCode(),
                            asset.getBrand(),
                            asset.getModel(),
                            asset.getSerialNumber(),
                            asset.getCode(),
                            asset.getAssetType().getName(),
                            asset.getBusinessUnit().getName(),
                            returnedDate,
                            null
                    );
                })
                .toList();
    }

    @CacheEvict(value = "assets", allEntries = true)
    public Result.AssetDtoResult updateAssetByCode(String code, AssetRequestDto assetDTO)
    {
        if(assetDTO == null || assetDTO.getAssetTypeCode() == null ||
                assetDTO.getBrand() == null || assetDTO.getModel() == null ||
                assetDTO.getBusinessUnitCode() == null || assetDTO.getSerialNumber() == null)
            return new Result.AssetDtoResult(AssetOperations.BAD_REQUEST, null);

        Optional<Asset> assetByCode = assetRepository.findByCode(code);
        Optional<AssetType> assetTypeByCode = assetTypeRepository.findByCode(assetDTO.getAssetTypeCode());
        Optional<BusinessUnit> businessUnitByCode = businessUnitRepository.findByCode(assetDTO.getBusinessUnitCode());

        if(assetTypeByCode.isPresent())
        {
            if((assetTypeByCode.get().isRam()) && (assetDTO.getRam() == null || (assetDTO.getRam() <= 0)))
                return new Result.AssetDtoResult(AssetOperations.INVALID_RAM, null);

            if((assetTypeByCode.get().isStorage()) &&
                    (assetDTO.getStorage() == null || !isValidStorageFormat(assetDTO.getStorage().trim())))
                return new Result.AssetDtoResult(AssetOperations.INVALID_STORAGE, null);
        }
        else
            return new Result.AssetDtoResult(AssetOperations.BAD_REQUEST, null);

        if(assetByCode.isEmpty())
            return new Result.AssetDtoResult(AssetOperations.NOT_FOUND, null);

        Asset updatedAsset = assetByCode.get();

        if(assetByCode.get().getAssetStatusType().getName().equals(BasicAssetStatuses.ASSIGNED.name()) ||
                assetByCode.get().getAssetStatusType().getName().equals(BasicAssetStatuses.DISMISSED.name()))
            return new Result.AssetDtoResult(AssetOperations.CANNOT_UPDATE, null);

        if(!(updatedAsset.getSerialNumber().trim().equals(assetDTO.getSerialNumber().trim())) &&
                assetRepository.existsBySerialNumber(assetDTO.getSerialNumber().trim()))
            return new Result.AssetDtoResult(AssetOperations.ALREADY_EXISTS, null);

        updatedAsset.setModel(assetDTO.getModel().trim());
        updatedAsset.setBrand(assetDTO.getBrand().trim());
        updatedAsset.setSerialNumber(assetDTO.getSerialNumber().trim());
        updatedAsset.setAssetType(assetTypeByCode.get());
        updatedAsset.setBusinessUnit(businessUnitByCode.get());
        updatedAsset.setNote(assetDTO.getNote().trim());
        updatedAsset.setRam(assetTypeByCode.get().isRam() ?assetDTO.getRam() :null);
        updatedAsset.setStorage(assetTypeByCode.get().isStorage()
                ?assetDTO.getStorage().trim().replaceAll("\\s+", " ")
                :null);
        updatedAsset.setUpdateDate(LocalDateTime.now());
        assetRepository.save(updatedAsset);

        AssetResponseDto response = getAssetResponseDto(updatedAsset);

        return new Result.AssetDtoResult(AssetOperations.OK, response);
    }

    public Result.AssetDtoResult updateEndMaintenance(String code, EndMaintenanceRequestDto date)
    {
        if(code == null || (date != null && date.getEndMaintenance() != null &&
                (date.getEndMaintenance().isBefore(LocalDate.now()) ||
                        date.getEndMaintenance().isEqual(LocalDate.now()))))
            return new Result.AssetDtoResult(AssetOperations.INVALID_DATE, null);

        Optional<Asset> assetOpt = assetRepository.findByCode(code);
        if(assetOpt.isEmpty())
            return new Result.AssetDtoResult(AssetOperations.NOT_FOUND, null);

        Asset updatedAssetMaintenanceDate = assetOpt.get();
        updatedAssetMaintenanceDate.setEndMaintenanceDate(date.getEndMaintenance());
        updatedAssetMaintenanceDate.setUpdateDate(LocalDateTime.now());
        assetRepository.save(updatedAssetMaintenanceDate);

        return new Result.AssetDtoResult(AssetOperations.OK, getAssetResponseDto(updatedAssetMaintenanceDate));
    }

    // Metodo di conversione per convertire da Asset ad AssetResponse
    private static AssetResponseDto getAssetResponseDto(Asset updatedAsset)
    {
        AssetResponseDto response = new AssetResponseDto();
        response.setModel(updatedAsset.getModel());
        response.setBrand(updatedAsset.getBrand());
        response.setSerialNumber(updatedAsset.getSerialNumber());
        response.setAssetTypeCode(updatedAsset.getAssetType().getCode());
        response.setBusinessUnitCode(updatedAsset.getBusinessUnit().getCode());
        response.setNote(updatedAsset.getNote());
        response.setRam(updatedAsset.getRam());
        response.setStorage(updatedAsset.getStorage());
        response.setAssetStatusTypeCode(updatedAsset.getAssetStatusType().getCode());
        response.setEndMaintenance(updatedAsset.getEndMaintenanceDate());
        return response;
    }

    @CacheEvict(value = "assets", allEntries = true)
    public Result.AssetDtoResult updateStatusByCode(String assetCode/*, String statusCode*/)
    {
        if(assetCode == null)
            return new Result.AssetDtoResult(AssetOperations.INVALID_ASSET_OR_TYPE_VALUE, null);

        Optional<Asset> assetByCode = assetRepository.findByCode(assetCode);
//        Optional<AssetStatusType> assetStatusByCode =
//                assetStatusTypeRepository.findByCode(statusCode);
//
//        if(assetByCode.isEmpty() || assetStatusByCode.isEmpty())
//            return new Result.AssetDtoResult(AssetOperations.NO_ASSET_OR_TYPE_FOUND, null);

        /*if(assetStatusByCode.get().getCode().equals(assetByCode.get().getAssetStatusType().getCode()))
            return new Result.AssetDtoResult(AssetOperations.BAD_REQUEST, null);*/

        if(assetByCode.get().getAssetStatusType().getName().equals(BasicAssetStatuses.ASSIGNED.name()) ||
            assetByCode.get().getAssetStatusType().getName().equals(BasicAssetStatuses.DISMISSED.name()))
            return new Result.AssetDtoResult(AssetOperations.STATUS_ERROR, null);

        // ↓ Non è possibile effettuare questa operazione perchè sono dei Movement
        // Da un qualcosa come "Under Maintenance" ad "Available" va bene perchè
        // non viene effettuato nessun Movement effettivo.
//        if(assetStatusByCode.get().getName().equals(BasicAssetStatuses.ASSIGNED.name()) ||
//            assetStatusByCode.get().getName().equals(BasicAssetStatuses.DISMISSED.name()))
//            return new Result.AssetDtoResult(AssetOperations.STATUS_ERROR, null);

        Asset updatedAssetStatus = assetByCode.get();
//        updatedAssetStatus.setAssetStatusType(assetStatusByCode.get());
        // TODO: Da Modificare
        if(updatedAssetStatus.getAssetStatusType().getName().equals(BasicAssetStatuses.MAINTENANCE.name()))
            updatedAssetStatus.setAssetStatusType(assetStatusTypeRepository.findByCode("AV1").get());
        else
            updatedAssetStatus.setAssetStatusType(assetStatusTypeRepository.findByCode("MA4").get());

        assetRepository.save(updatedAssetStatus);
        AssetResponseDto response = getAssetResponseDto(assetByCode.get());

        return new Result.AssetDtoResult(AssetOperations.OK, response);
    }

    // Ottiene tutti i movimenti (con cache)
    @Cacheable(value = "movements", key = "'all'")
    public List<Movement> getAllMovements()
    {
        System.out.println(">>> Fetching ALL Movements from database...");

        List<Movement> movements = movementRepository.findAll();

        // Popola anche le cache per singoli asset code
        Cache cache = cacheManager.getCache("movements");
        if (cache != null) {
            movements.forEach(movement -> {
                String assetCode = movement.getAsset().getCode();
                cache.put("assetCode::" + assetCode,
                        movementRepository.findByAssetCode(assetCode));
            });
        }

        return movements;
    }

    // Ottiene tutti i movimenti di un certo asset (con cache)
    @Cacheable(value = "movements", key = "'assetCode::' + #assetCode")
    public List<MovementSummaryResponseDto> getAssetMovementDTO(String assetCode)
    {
        System.out.println(">>> Fetching Movements for Asset " + assetCode + " from database...");

        if(!(assetRepository.existsByCode(assetCode)))
            return null;

        // ↓ Non necessario. In caso, si restituisce una lista vuota
       /* if(!(movementRepository.existsByAssetCode(assetCode)))
            return null;*/

        List<Movement> movements = movementRepository.findByAssetCode(assetCode);

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
        assetSummaryDTO.setRam(movement.getAsset().getRam());
        assetSummaryDTO.setStorage(movement.getAsset().getStorage());
        assetSummaryDTO.setCode(movement.getAsset().getCode());
        assetSummaryDTO.setStatusCode(movement.getAsset().getAssetStatusType().getCode());
        dto.setAsset(assetSummaryDTO);

        UserSummaryDto userSummaryDTO = new UserSummaryDto();
        if(movement.getUsers() != null)
        {
            userSummaryDTO.setId(movement.getUsers().getId());
            userSummaryDTO.setName(movement.getUsers().getName());
            userSummaryDTO.setSurname(movement.getUsers().getSurname());
            userSummaryDTO.setEmail(movement.getUsers().getEmail());
            dto.setUser(userSummaryDTO);
        }
        else
            dto.setUser(null);

        return dto;
    }

    // Assegna/Restituisce/Dismette un asset (reset cache)
    @CacheEvict(value = {"movements", "assets"}, allEntries = true)
    public Result.MovementDtoResult assignReturnedDismissAsset(String assetCode, MovementRequestBodyDto movementDTO)
    {
        // Validazione base
        if(movementDTO.getMovementType() == null /*|| movementDTO.getUser() == null*/ || assetCode == null
            /*|| movementDTO.getReceiptBase64()*/ || (movementDTO.getUser() == null &&
                !movementDTO.getMovementType().name().equals(MovementTypes.DISMISSED.name())))
            return new Result.MovementDtoResult(MovementOperations.BAD_REQUEST, null);

        // Controllo base per assetCode e corretto MovementType
        if (!(assetRepository.existsByCode(assetCode)) ||
                !(movementDTO.getMovementType().name().equals(MovementTypes.RETURNED.name()) ||
                        movementDTO.getMovementType().name().equals(MovementTypes.ASSIGNED.name()) ||
                        movementDTO.getMovementType().name().equals(MovementTypes.DISMISSED.name())))
            return new Result.MovementDtoResult(MovementOperations.INVALID_MOVEMENT_TYPE, null);

        // Validazione ricevuta - Per il momento così perchè null
        /*if (movementDTO.getReceiptBase64() == null || movementDTO.getReceiptBase64().isBlank())
            return new Result.MovementDtoResult(StatusForControllerOperations.BAD_REQUEST, null);*/

        // Ricerca Asset
        Optional<Asset> assetOpt = assetRepository.findByCode(assetCode);
        if (assetOpt.isEmpty())
            return new Result.MovementDtoResult(MovementOperations.ASSET_NOT_FOUND, null);

        // Controllo temporaneo per lo status type corrente. Da modificare perchè statico ?
        if(!(assetOpt.get().getAssetStatusType().getName().equals(BasicAssetStatuses.AVAILABLE.name()) ||
                assetOpt.get().getAssetStatusType().getName().equals(BasicAssetStatuses.ASSIGNED.name()) ||
                assetOpt.get().getAssetStatusType().getName().equals(BasicAssetStatuses.DISMISSED.name())))
            return new Result.MovementDtoResult(MovementOperations.ASSET_STATE_BLOCKS_OPERATION, null);

        // Ricerca Utente
        Optional<User> userOpt = Optional.empty();

        if(movementDTO.getUser() != null)
        {
            userOpt = userRepository.findById(movementDTO.getUser());
            if (userOpt.isEmpty())
                return new Result.MovementDtoResult(MovementOperations.USER_NOT_FOUND, null);
        }

        Optional<Movement> lastMovement = movementRepository.findFirstByAssetCodeOrderByDateDesc(assetCode);

        // Regole di business sui tipi di movimento
        if (movementDTO.getMovementType().name().equals(MovementTypes.ASSIGNED.name()))
        {
            if (lastMovement.isPresent() &&
                    lastMovement.get().getMovementType().name().equals(MovementTypes.ASSIGNED.name()))
                return new Result.MovementDtoResult(MovementOperations.INVALID_MOVEMENT_TYPE, null);

            if (lastMovement.isPresent() &&
                    lastMovement.get().getMovementType().name().equals(MovementTypes.DISMISSED.name()))
                return new Result.MovementDtoResult(MovementOperations.INVALID_MOVEMENT_TYPE, null);

            if(userOpt.get().getBusinessUnit() == null)
                return new Result.MovementDtoResult(MovementOperations.INVALID_TARGET_ROLE, null);

            if(!(userOpt.get().getBusinessUnit().getCode().equals(assetOpt.get().getBusinessUnit().getCode())))
                return new Result.MovementDtoResult(MovementOperations.BUSINESS_UNIT_MISMATCH, null);
        }

        if (movementDTO.getMovementType().name().equals(MovementTypes.RETURNED.name()))
        {
            if (lastMovement.isEmpty())
                return new Result.MovementDtoResult(MovementOperations.INVALID_MOVEMENT_TYPE, null);

            if (lastMovement.get().getMovementType().name().equals(MovementTypes.DISMISSED.name()))
                return new Result.MovementDtoResult(MovementOperations.INVALID_MOVEMENT_TYPE, null);

            if (lastMovement.get().getMovementType().name().equals(MovementTypes.RETURNED.name()))
                return new Result.MovementDtoResult(MovementOperations.INVALID_MOVEMENT_TYPE, null);

            if(!Objects.equals(lastMovement.get().getUsers().getId(), movementDTO.getUser()) &&
                    lastMovement.get().getMovementType().name().equals(BasicAssetStatuses.ASSIGNED.name()))
                return new Result.MovementDtoResult(MovementOperations.INVALID_RETURN_USER, null);
        }

        if (movementDTO.getMovementType().name().equals(MovementTypes.DISMISSED.name()) && lastMovement.isPresent())
        {
            if (lastMovement.get().getMovementType().name().equals(MovementTypes.DISMISSED.name()))
                return new Result.MovementDtoResult(MovementOperations.INVALID_MOVEMENT_TYPE, null);

            if (lastMovement.get().getMovementType().name().equals(MovementTypes.ASSIGNED.name()))
                return new Result.MovementDtoResult(MovementOperations.INVALID_MOVEMENT_TYPE, null);
        }

        // Costruzione nome file: {assetCode}_{surname}_{movementType}_{rowCount}.pdf
        long rowCount = movementRepository.count()+1;
        String fileName;
        if(!movementDTO.getMovementType().name().equals(MovementTypes.DISMISSED.name()))
            fileName = assetCode.toUpperCase() + "_"
                + userOpt.get().getSurname() + "_"
                + movementDTO.getMovementType() + "_"
                + rowCount + ".pdf";
        else
            fileName = assetCode.toUpperCase() + "_"
                    + movementDTO.getMovementType() + "_"
                    + rowCount + ".pdf";

        // Salvataggio file
        // TODO: Modificare dopo che il FE finisce
        String savedFileName = null;
        if(movementDTO.getReceiptBase64() != null  && !movementDTO.getReceiptBase64().isEmpty())
        {
            savedFileName = saveReceiptFile(movementDTO.getReceiptBase64(), fileName);
            if (savedFileName == null)
                return new Result.MovementDtoResult(MovementOperations.INVALID_FILE_NAME, null);
        }

        // Creazione e salvataggio movimento
        Movement addedMovement = new Movement();
        addedMovement.setMovementType(movementDTO.getMovementType());
        addedMovement.setAsset(assetOpt.get());
        addedMovement.setUsers(!(movementDTO.getMovementType().name().equals(MovementTypes.DISMISSED.name()))
                ?userOpt.get() :null);
        addedMovement.setNote(movementDTO.getNote());
        addedMovement.setReceiptFileName(savedFileName);
        if(!movementDTO.getMovementType().name().equals(MovementTypes.DISMISSED.name()))
            addedMovement.setCode(movementDTO.getMovementType().name()
                    .substring(0, 2) + movementDTO.getUser() + assetCode.toUpperCase() +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + (movementRepository.count()+1));
        else
            addedMovement.setCode(movementDTO.getMovementType().name()
                    .substring(0, 2) + assetCode.toUpperCase() +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + (movementRepository.count()+1));

        // Trovare un modo per inserire "isPresent()" ?
        Asset updatedAssetStatus = assetOpt.get();
        if(movementDTO.getMovementType().name().equals(MovementTypes.RETURNED.name()))
            updatedAssetStatus.setAssetStatusType
                    (assetStatusTypeRepository.findByName(BasicAssetStatuses.MAINTENANCE.name()).get());
        else if(movementDTO.getMovementType().name().equals(MovementTypes.ASSIGNED.name()))
            updatedAssetStatus.setAssetStatusType
                    (assetStatusTypeRepository.findByName(MovementTypes.ASSIGNED.name()).get());
        else // "DISMISSED"
            updatedAssetStatus.setAssetStatusType
                    (assetStatusTypeRepository.findByName(BasicAssetStatuses.DISMISSED.name()).get());

        movementRepository.save(addedMovement);
        assetRepository.save(updatedAssetStatus);

        if(!(movementDTO.getMovementType().name().equals(MovementTypes.DISMISSED.name())))
        return new Result.MovementDtoResult(MovementOperations.OK,
                new MovementResponseBodyDto(assetCode, movementDTO.getUser(),
                        movementDTO.getMovementType().name(), movementDTO.getNote()));
        else
            return new Result.MovementDtoResult(MovementOperations.OK,
                    new MovementResponseBodyDto(assetCode, null,
                        movementDTO.getMovementType().name(), movementDTO.getNote()));
    }

    private String saveReceiptFile(String base64Pdf, String fileName)
    {
        try
        {
            Path dirPath = Paths.get(receiptsDir);

            if (!Files.exists(dirPath))
                Files.createDirectories(dirPath);

            byte[] pdfBytes = Base64.getDecoder().decode(base64Pdf);
            Path filePath = dirPath.resolve(fileName);
            Files.write(filePath, pdfBytes);

            return fileName; // Solo il nome, non il path completo
        }
        catch (IOException e)
        {
            System.err.println("Errore nel salvataggio della ricevuta: " + e.getMessage());
            return null;
        }
    }

    public Result.ReceiptResult getMovementReceipt(String assetCode, String movementCode)
    {

        Optional<Asset> assetOtp = assetRepository.findByCode(assetCode);
        if(assetOtp.isEmpty())
            return new Result.ReceiptResult(MovementOperations.ASSET_NOT_FOUND, null, null);

        Optional<Movement> movementOpt = movementRepository.findByCode(movementCode);

        if (movementOpt.isEmpty())
            return new Result.ReceiptResult(MovementOperations.MOVEMENT_NOT_FOUND, null, null);

        if(!movementOpt.get().getAsset().getCode().equals(assetOtp.get().getCode()))
            return new Result.ReceiptResult(MovementOperations.DIFFERENT_ASSET_CODE, null, null);

        if(movementOpt.get().getReceiptFileName() == null)
            return new Result.ReceiptResult(MovementOperations.FILE_IS_MISSING, null, null);
        try
        {
            String fileName = movementOpt.get().getReceiptFileName();
            Path filePath = Paths.get(receiptsDir).resolve(fileName);
            byte[] pdfBytes = Files.readAllBytes(filePath);

            return new Result.ReceiptResult(MovementOperations.OK, pdfBytes, fileName);
        }
        catch (IOException e)
        {
            System.err.println("Errore nella lettura della ricevuta: " + e.getMessage());
            return new Result.ReceiptResult(MovementOperations.INVALID_FILE_NAME, null, null);
        }
    }

    // Cancella i pdf orfani
    @PostConstruct
    public void cleanOrphanedReceipts()
    {
        try
        {
            Path receiptsPath = Paths.get(receiptsDir);

            if (!Files.exists(receiptsPath))
                return;

            // Ottieni tutti i nomi dei file salvati nel DB
            List<String> dbFileNames = movementRepository.findAll()
                    .stream()
                    .map(Movement::getReceiptFileName)
                    .toList();

            // Scansiona la cartella receipts
            Files.list(receiptsPath)
                    .filter(Files::isRegularFile)
                    .filter(file -> file.toString().endsWith(".pdf"))
                    .forEach(file -> {
                        String fileName = file.getFileName().toString();

                        // Se il file non è nel DB, cancellalo
                        if (!dbFileNames.contains(fileName))
                        {
                            try
                            {
                                Files.delete(file);
                                System.out.println(">>> Deleted orphaned receipt: " + fileName);
                            }
                            catch (IOException e)
                            {
                                System.err.println(">>> Failed to delete orphaned receipt: " + fileName);
                            }
                        }
                    });

            System.out.println(">>> Orphaned receipts cleanup completed");
        }
        catch (IOException e)
        {
            System.err.println(">>> Error during orphaned receipts cleanup: " + e.getMessage());
        }
    }
}