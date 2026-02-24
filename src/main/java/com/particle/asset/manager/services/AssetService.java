package com.particle.asset.manager.services;

import com.particle.asset.manager.DTO.*;
import com.particle.asset.manager.enums.StatusForControllerOperations;
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
    public AssetRequestDto createAsset(AssetRequestDto assetDTO)
    {
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
    public Result.AssetBodyDTOResult updateAssetById(String code, AssetRequestDto assetDTO)
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

        if(!(assetRepository.existsByCode(assetCode) && movementRepository.existsByAssetCode(assetCode)))
            return null;

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
    @CacheEvict(value = "movements", allEntries = true)
    public Result.MovementBodyDTOResult assignReturnedDismissAsset(String assetCode, MovementRequestBodyDto movementDTO)
    {
        // Validazione base
        if (!(assetRepository.existsByCode(assetCode)) ||
                !(movementDTO.getMovementType().equals("Returned") ||
                        movementDTO.getMovementType().equals("Assigned") ||
                        movementDTO.getMovementType().equals("Dismissed")))
            return new Result.MovementBodyDTOResult(StatusForControllerOperations.BAD_REQUEST, null);

        // Validazione ricevuta - Per il momento così perchè null
        /*if (movementDTO.getReceiptBase64() == null || movementDTO.getReceiptBase64().isBlank())
            return new Result.MovementBodyDTOResult(StatusForControllerOperations.BAD_REQUEST, null);*/

        Optional<Asset> assetOpt = assetRepository.findByCode(assetCode);
        if (assetOpt.isEmpty())
            return new Result.MovementBodyDTOResult(StatusForControllerOperations.NOT_FOUND, null);

        Optional<User> userOpt = userRepository.findById(movementDTO.getUser());
        if (userOpt.isEmpty())
            return new Result.MovementBodyDTOResult(StatusForControllerOperations.NOT_FOUND, null);

        Optional<Movement> lastMovement = movementRepository.findFirstByAssetCodeOrderByDateDesc(assetCode);

        // Regole di business sui tipi di movimento
        if (movementDTO.getMovementType().equals("Assigned"))
        {
            if (lastMovement.isPresent() &&
                    lastMovement.get().getMovementType().equals("Assigned"))
                return new Result.MovementBodyDTOResult(StatusForControllerOperations.BAD_REQUEST, null);

            if (lastMovement.isPresent() &&
                    lastMovement.get().getMovementType().equals("Dismissed"))
                return new Result.MovementBodyDTOResult(StatusForControllerOperations.BAD_REQUEST, null);
        }

        if (movementDTO.getMovementType().equals("Returned"))
        {
            if (lastMovement.isEmpty())
                return new Result.MovementBodyDTOResult(StatusForControllerOperations.BAD_REQUEST, null);

            if (lastMovement.get().getMovementType().equals("Dismissed"))
                return new Result.MovementBodyDTOResult(StatusForControllerOperations.BAD_REQUEST, null);

            if (lastMovement.get().getMovementType().equals("Returned"))
                return new Result.MovementBodyDTOResult(StatusForControllerOperations.BAD_REQUEST, null);

            if(!Objects.equals(lastMovement.get().getUsers().getId(), movementDTO.getUser()) && lastMovement.get().getMovementType().equals("Assigned"))
                return new Result.MovementBodyDTOResult(StatusForControllerOperations.BAD_REQUEST, null);
        }

        if (movementDTO.getMovementType().equals("Dismissed") && lastMovement.isPresent())
        {
            if (lastMovement.get().getMovementType().equals("Dismissed"))
                return new Result.MovementBodyDTOResult(StatusForControllerOperations.BAD_REQUEST, null);

            if (lastMovement.get().getMovementType().equals("Assigned"))
                return new Result.MovementBodyDTOResult(StatusForControllerOperations.BAD_REQUEST, null);
        }

        // Costruzione nome file: {assetCode}_{surname}_{movementType}_{rowCount}.pdf
        /*long rowCount = movementRepository.count()+1;
        String fileName = assetCode + "_"
                + userOpt.get().getSurname() + "_"
                + movementDTO.getMovementType() + "_"
                + rowCount + ".pdf";

        // Salvataggio file
        String savedFileName = saveReceiptFile(movementDTO.getReceiptBase64(), fileName);
        if (savedFileName == null)
            return new Result.MovementBodyDTOResult(StatusForControllerOperations.BAD_REQUEST, null);*/

        // Creazione e salvataggio movimento
        Movement addedMovement = new Movement();
        addedMovement.setMovementType(movementDTO.getMovementType());
        addedMovement.setAsset(assetOpt.get());
        addedMovement.setUsers(userOpt.get());
        addedMovement.setNote(movementDTO.getNote());
        //addedMovement.setReceiptFileName(savedFileName);
        movementRepository.save(addedMovement);

        return new Result.MovementBodyDTOResult(StatusForControllerOperations.OK,
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

    public Result.ReceiptResult getMovementReceipt(Long movementId)
    {
        Optional<Movement> movementOpt = movementRepository.findById(movementId);

        if (movementOpt.isEmpty())
            return new Result.ReceiptResult(StatusForControllerOperations.NOT_FOUND, null, null);

        try
        {
            String fileName = movementOpt.get().getReceiptFileName();
            Path filePath = Paths.get(receiptsDir).resolve(fileName);
            byte[] pdfBytes = Files.readAllBytes(filePath);

            return new Result.ReceiptResult(StatusForControllerOperations.OK, pdfBytes, fileName);
        }
        catch (IOException e)
        {
            System.err.println("Errore nella lettura della ricevuta: " + e.getMessage());
            return new Result.ReceiptResult(StatusForControllerOperations.BAD_REQUEST, null, null);
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