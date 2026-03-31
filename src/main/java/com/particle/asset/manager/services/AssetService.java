package com.particle.asset.manager.services;

import com.particle.asset.manager.DTO.*;
import com.particle.asset.manager.enums.AssetOperations;
import com.particle.asset.manager.enums.MovementOperations;
import com.particle.asset.manager.enums.GenericOperations;
import com.particle.asset.manager.models.*;
import com.particle.asset.manager.repositories.*;
import com.particle.asset.manager.results.Result;
import com.particle.asset.manager.swaggerResponses.MovementResponses;
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
import java.util.Base64;
import jakarta.annotation.PostConstruct;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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
                            && "Assigned".equals(lastMovement.get().getMovementType()))
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

    @CacheEvict(value = "assets", allEntries = true)
    public Result.AssetDtoResult createAsset(AssetRequestDto assetDTO)
    {
        if(assetDTO == null || assetDTO.getAssetTypeCode() == null || assetDTO.getBrand() == null ||
                assetDTO.getModel() == null || assetDTO.getBusinessUnitCode() == null ||
                assetDTO.getSerialNumber() == null ||
                assetDTO.getRam() == null || assetDTO.getHardDisk() == null ||
                assetRepository.existsBySerialNumber(assetDTO.getSerialNumber()))
            return new Result.AssetDtoResult(AssetOperations.BAD_REQUEST, null);

        Optional<AssetType> assetTypeById = assetTypeRepository.findByCode(assetDTO.getAssetTypeCode());
        Optional<BusinessUnit> businessUnitById = businessUnitRepository.findByCode(assetDTO.getBusinessUnitCode());
        // ↓ Cerco e inserisco il lo status "Disponibile" alla creazione dell'Asset
        Optional<AssetStatusType> assetStatusTypeById = assetStatusTypeRepository.findByName("Available");

        if(assetTypeById.isEmpty() || businessUnitById.isEmpty() || assetStatusTypeById.isEmpty())
            return new Result.AssetDtoResult(AssetOperations.ALREADY_EXISTS, null);

        Asset asset = new Asset();
        asset.setAssetType(assetTypeById.get());
        asset.setBrand(assetDTO.getBrand());
        asset.setModel(assetDTO.getModel());
        asset.setBusinessUnit(businessUnitById.get());
        asset.setSerialNumber(assetDTO.getSerialNumber());
        asset.setRam(assetDTO.getRam());
        asset.setHardDisk(assetDTO.getHardDisk());
        asset.setAssetStatusType(assetStatusTypeById.get());
        asset.setNote(assetDTO.getNote());

        String nameWithoutSpaces = asset.getSerialNumber().replaceAll("\\s+", "");
        asset.setCode(nameWithoutSpaces.toUpperCase()
                .substring(0, Math.min(2, nameWithoutSpaces.length())) + (assetRepository.count()+1));

        assetRepository.save(asset);

        return new Result.AssetDtoResult(AssetOperations.OK, getAssetResponseDto(asset));
    }

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

        if(assetByCode.isEmpty())
            return new Result.AssetDtoResult(AssetOperations.NOT_FOUND, null);

        if(assetTypeByCode.isEmpty() || businessUnitByCode.isEmpty())
            return new Result.AssetDtoResult(AssetOperations.BAD_REQUEST, null);

        Asset updatedAsset = assetByCode.get();

        if(!(updatedAsset.getSerialNumber().equals(assetDTO.getSerialNumber())) &&
                assetRepository.existsBySerialNumber(assetDTO.getSerialNumber()))
            return new Result.AssetDtoResult(AssetOperations.ALREADY_EXISTS, null);

        updatedAsset.setModel(assetDTO.getModel());
        updatedAsset.setBrand(assetDTO.getBrand());
        updatedAsset.setSerialNumber(assetDTO.getSerialNumber());
        updatedAsset.setAssetType(assetTypeByCode.get());
        updatedAsset.setBusinessUnit(businessUnitByCode.get());
        updatedAsset.setNote(assetDTO.getNote());
        updatedAsset.setRam(assetDTO.getRam());
        updatedAsset.setHardDisk(assetDTO.getHardDisk());
        updatedAsset.setUpdateDate(LocalDateTime.now());
        assetRepository.save(updatedAsset);

        AssetResponseDto response = getAssetResponseDto(updatedAsset);

        return new Result.AssetDtoResult(AssetOperations.OK, response);
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
        response.setHardDisk(updatedAsset.getHardDisk());
        response.setAssetStatusTypeCode(updatedAsset.getAssetStatusType().getCode());
        return response;
    }

    @CacheEvict(value = "assets", allEntries = true)
    public Result.AssetDtoResult updateStatusByCode(String code, AssetStatusUpdateRequestDto statusCode)
    {
        if(code == null || statusCode == null || statusCode.getAssetStatusTypeCode() == null)
            return new Result.AssetDtoResult(AssetOperations.BAD_REQUEST, null);

        Optional<Asset> assetByCode = assetRepository.findByCode(code);
        Optional<AssetStatusType> assetStatusByCode = assetStatusTypeRepository.findByCode(statusCode.getAssetStatusTypeCode());

        if(assetByCode.isEmpty() || assetStatusByCode.isEmpty())
            return new Result.AssetDtoResult(AssetOperations.NOT_FOUND, null);

        if(assetStatusByCode.get().getCode().equals(assetByCode.get().getAssetStatusType().getCode()))
            return new Result.AssetDtoResult(AssetOperations.BAD_REQUEST, null);

        if(assetByCode.get().getAssetStatusType().getName().equals("Assigned") ||
            assetByCode.get().getAssetStatusType().getName().equals("Dismissed"))
            return new Result.AssetDtoResult(AssetOperations.STATUS_ERROR, null);

        // ↓ Non è possibile effettuare questa operazione perchè sono dei Movement
        // Da un qualcosa come "Under Maintenance" ad "Available" va bene perchè
        // non viene effettuato nessun Movement effettivo.
        if(assetStatusByCode.get().getName().equals("Assigned") ||
            assetStatusByCode.get().getName().equals("Dismissed"))
            return new Result.AssetDtoResult(AssetOperations.STATUS_ERROR, null);

        Asset updatedAssetStatus = assetByCode.get();
        updatedAssetStatus.setAssetStatusType(assetStatusByCode.get());
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
        dto.setId(movement.getId());
        dto.setDate(movement.getDate());
        dto.setMovementType(movement.getMovementType());
        dto.setNote(movement.getNote());

        AssetSummaryDto assetSummaryDTO = new AssetSummaryDto();
        assetSummaryDTO.setId(movement.getAsset().getId());
        assetSummaryDTO.setBrand(movement.getAsset().getBrand());
        assetSummaryDTO.setModel(movement.getAsset().getModel());
        assetSummaryDTO.setSerialNumber(movement.getAsset().getSerialNumber());
        assetSummaryDTO.setRam(movement.getAsset().getRam());
        assetSummaryDTO.setHardDisk(movement.getAsset().getHardDisk());
        assetSummaryDTO.setCode(movement.getAsset().getCode());
        dto.setAsset(assetSummaryDTO);

        UserSummaryDto userSummaryDTO = new UserSummaryDto();
        userSummaryDTO.setId(movement.getUsers().getId());
        userSummaryDTO.setName(movement.getUsers().getName());
        userSummaryDTO.setSurname(movement.getUsers().getSurname());
        userSummaryDTO.setEmail(movement.getUsers().getEmail());
        dto.setUser(userSummaryDTO);

        return dto;
    }

    // Assegna/Restituisce/Dismette un asset (reset cache)
    @CacheEvict(value = {"movements", "assets"}, allEntries = true)
    public Result.MovementDtoResult assignReturnedDismissAsset(String assetCode, MovementRequestBodyDto movementDTO)
    {
        // Validazione base
        if(movementDTO.getMovementType() == null || movementDTO.getUser() == null || assetCode == null
            /*|| movementDTO.getReceiptBase64()*/)
            return new Result.MovementDtoResult(MovementOperations.BAD_REQUEST, null);

        // Controllo base per assetCode e corretto MovementType
        if (!(assetRepository.existsByCode(assetCode)) ||
                !(movementDTO.getMovementType().equals("Returned") ||
                        movementDTO.getMovementType().equals("Assigned") ||
                        movementDTO.getMovementType().equals("Dismissed")))
            return new Result.MovementDtoResult(MovementOperations.INVALID_MOVEMENT_TYPE, null);

        // Validazione ricevuta - Per il momento così perchè null
        /*if (movementDTO.getReceiptBase64() == null || movementDTO.getReceiptBase64().isBlank())
            return new Result.MovementDtoResult(StatusForControllerOperations.BAD_REQUEST, null);*/

        // Ricerca Asset
        Optional<Asset> assetOpt = assetRepository.findByCode(assetCode);
        if (assetOpt.isEmpty())
            return new Result.MovementDtoResult(MovementOperations.ASSET_NOT_FOUND, null);

        // Controllo temporaneo per lo status type corrente. Da modificare perchè statico ?
        if(!(assetOpt.get().getAssetStatusType().getName().equals("Available") ||
                assetOpt.get().getAssetStatusType().getName().equals("Assigned") ||
                assetOpt.get().getAssetStatusType().getName().equals("Dismissed")))
            return new Result.MovementDtoResult(MovementOperations.ASSET_STATE_BLOCKS_OPERATION, null);

        // Ricerca Utente
        Optional<User> userOpt = userRepository.findById(movementDTO.getUser());
        if (userOpt.isEmpty())
            return new Result.MovementDtoResult(MovementOperations.USER_NOT_FOUND, null);

        Optional<Movement> lastMovement = movementRepository.findFirstByAssetCodeOrderByDateDesc(assetCode);

        // Regole di business sui tipi di movimento
        if (movementDTO.getMovementType().equals("Assigned"))
        {
            if (lastMovement.isPresent() &&
                    lastMovement.get().getMovementType().equals("Assigned"))
                return new Result.MovementDtoResult(MovementOperations.INVALID_MOVEMENT_TYPE, null);

            if (lastMovement.isPresent() &&
                    lastMovement.get().getMovementType().equals("Dismissed"))
                return new Result.MovementDtoResult(MovementOperations.INVALID_MOVEMENT_TYPE, null);
        }

        if (movementDTO.getMovementType().equals("Returned"))
        {
            if (lastMovement.isEmpty())
                return new Result.MovementDtoResult(MovementOperations.INVALID_MOVEMENT_TYPE, null);

            if (lastMovement.get().getMovementType().equals("Dismissed"))
                return new Result.MovementDtoResult(MovementOperations.INVALID_MOVEMENT_TYPE, null);

            if (lastMovement.get().getMovementType().equals("Returned"))
                return new Result.MovementDtoResult(MovementOperations.INVALID_MOVEMENT_TYPE, null);

            if(!Objects.equals(lastMovement.get().getUsers().getId(), movementDTO.getUser()) && lastMovement.get().getMovementType().equals("Assigned"))
                return new Result.MovementDtoResult(MovementOperations.INVALID_MOVEMENT_TYPE, null);
        }

        if (movementDTO.getMovementType().equals("Dismissed") && lastMovement.isPresent())
        {
            if (lastMovement.get().getMovementType().equals("Dismissed"))
                return new Result.MovementDtoResult(MovementOperations.INVALID_MOVEMENT_TYPE, null);

            if (lastMovement.get().getMovementType().equals("Assigned"))
                return new Result.MovementDtoResult(MovementOperations.INVALID_MOVEMENT_TYPE, null);
        }

        // Costruzione nome file: {assetCode}_{surname}_{movementType}_{rowCount}.pdf
        long rowCount = movementRepository.count()+1;
        String fileName = assetCode.toUpperCase() + "_"
                + userOpt.get().getSurname() + "_"
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
        addedMovement.setUsers(userOpt.get());
        addedMovement.setNote(movementDTO.getNote());
        addedMovement.setReceiptFileName(savedFileName);

        // Trovare un modo per inserire "isPresent()" ?
        Asset updatedAssetStatus = assetOpt.get();
        if(movementDTO.getMovementType().equals("Returned"))
            updatedAssetStatus.setAssetStatusType
                    (assetStatusTypeRepository.findByName("Available").get());
        else if(movementDTO.getMovementType().equals("Assigned"))
            updatedAssetStatus.setAssetStatusType
                    (assetStatusTypeRepository.findByName("Assigned").get());
        else // "Dismissed"
            updatedAssetStatus.setAssetStatusType
                    (assetStatusTypeRepository.findByName("Dismissed").get());

        movementRepository.save(addedMovement);
        assetRepository.save(updatedAssetStatus);

        return new Result.MovementDtoResult(MovementOperations.OK,
                new MovementResponseBodyDto(assetCode, movementDTO.getUser(),
                        movementDTO.getMovementType(), movementDTO.getNote()));
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

    public Result.ReceiptResult getMovementReceipt(String code, Long movementId)
    {

        Optional<Asset> assetOtp = assetRepository.findByCode(code);
        if(assetOtp.isEmpty())
            return new Result.ReceiptResult(MovementOperations.ASSET_NOT_FOUND, null, null);

        Optional<Movement> movementOpt = movementRepository.findById(movementId);

        if (movementOpt.isEmpty())
            return new Result.ReceiptResult(MovementOperations.MOVEMENT_NOT_FOUND, null, null);

        if(!movementOpt.get().getAsset().getCode().equals(assetOtp.get().getCode()))
            return new Result.ReceiptResult(MovementOperations.DIFFERENT_ASSET_CODE, null, null);

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