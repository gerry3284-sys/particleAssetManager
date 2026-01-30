package com.particle.asset.manager.services;

import com.particle.asset.manager.DTO.AssetTypeBusinessUnitAssetStatusTypeActiveDeactiveBodyDTO;
import com.particle.asset.manager.DTO.AssetTypeBusinessUnitAssetStatusTypeBodyDTO;
import com.particle.asset.manager.enumerations.StatusForControllerOperations;
import com.particle.asset.manager.models.AssetType;
import com.particle.asset.manager.repositories.AssetTypeRepository;
import com.particle.asset.manager.results.Result;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AssetTypeService
{
    private final AssetTypeRepository repository;
    private final CacheManager cacheManager;

    public AssetTypeService(AssetTypeRepository repository, CacheManager cacheManager)
    {
        this.repository = repository;
        this.cacheManager = cacheManager;
    }

    // Mostra tutti i Type (con cache)
    // @Chacheable → Quando si chiama "getAllTypes()" la prima volta, i dati vengono recuperati
    //               dal database e salvati i cache con la chiave "all". Le chiamata successive
    //               leggono direttamente dalla cache per 8 ore.
    @Cacheable(value = "assetTypes", key = "'all'")
    public List<AssetType> getAllTypes() {
        System.out.println(">>> Fetching ALL AssetTypes from database...");
        List<AssetType> types = repository.findAll();

        // Popola anche le cache per singoli ID
        Cache cache = cacheManager.getCache("assetTypes");
        if (cache != null)
            types.forEach(type -> cache.put("id::" + type.getId(), type));

        return types;
    }

    public AssetType getAssetTypeById(Long id) {
        Cache cache = cacheManager.getCache("assetTypes");

        // 1. Cerca prima nella cache del singolo ID
        Cache.ValueWrapper idWrapper = cache != null ? cache.get("id::" + id) : null;
        if (idWrapper != null) {
            System.out.println(">>> getAssetTypeById(" + id + ") - CACHE (singolo ID)");
            return (AssetType) idWrapper.get();
        }

        // 2. Se non c'è, cerca nella cache "all"
        Cache.ValueWrapper allWrapper = cache != null ? cache.get("all") : null;
        if (allWrapper != null) {
            System.out.println(">>> getAssetTypeById(" + id + ") - CACHE (filtrato da 'all')");
            @SuppressWarnings("unchecked")
            List<AssetType> allAssetTypes = (List<AssetType>) allWrapper.get();
            return allAssetTypes.stream()
                    .filter(type -> type.getId().equals(id))
                    .findFirst()
                    .orElse(null);
        }

        // 3. Cache vuota - va al DB e salva SOLO questo ID
        System.out.println(">>> getAssetTypeById(" + id + ") - DATABASE (salvando singolo ID in cache)");
        AssetType assetType = repository.findById(id).orElse(null);
        if (cache != null && assetType != null)
            cache.put("id::" + id, assetType);

        return assetType;
    }

    // Crea un Type (reset cache)
    // @CacheEvict → Quando si crea/aggiorna/disattiva un record, la cache viene
    //               completamente svuotata (clear). Alla prossima chiamata GET,
    //               i dati verrano caricati direttamente dal database.
    @CacheEvict(value = "assetTypes", allEntries = true)
    public AssetTypeBusinessUnitAssetStatusTypeBodyDTO createType(
            AssetTypeBusinessUnitAssetStatusTypeBodyDTO assetTypeDTO)
    {
        if(assetTypeDTO == null || assetTypeDTO.getName() == null ||
                assetTypeDTO.getName().trim().isEmpty())
            return null;

        // Normalizzazione del nome (se necessario)
        assetTypeDTO.setName(normalizeName(assetTypeDTO.getName()));

        if(repository.existsByName(assetTypeDTO.getName()))
            return null;

        AssetType assetType = new AssetType();
        assetType.setName(assetTypeDTO.getName());

        String nameWithoutSpaces = assetType.getName().replaceAll("\\s+", "");
        assetType.setCode(nameWithoutSpaces.toUpperCase()
                .substring(0, Math.min(2, nameWithoutSpaces.length())) + (repository.count()+1));

        repository.save(assetType);

        return assetTypeDTO;
    }

    private String normalizeName(String name)
    {
        if (name == null || name.trim().isEmpty())
            return name;


        String trimmed = name.trim();

        // Verifica se è già nel formato corretto
        if (trimmed.length() > 0 &&
                Character.isUpperCase(trimmed.charAt(0)) &&
                trimmed.substring(1).equals(trimmed.substring(1).toLowerCase()))
            return trimmed; // Già corretto, restituisce così com'è

        // Altrimenti normalizza
        return trimmed.substring(0, 1).toUpperCase() +
                trimmed.substring(1).toLowerCase();
    }

    // Aggiorna un Type (reset cache)
    @CacheEvict(value = "assetTypes", allEntries = true)
    public Result.AssetTypeBusinessUnitAssetStatusTypeBodyDTOPatchResult updateTypeById(Long id,
                                                                                        AssetTypeBusinessUnitAssetStatusTypeBodyDTO assetTypeDTO)
    {
        if(assetTypeDTO == null || assetTypeDTO.getName() == null)
            return new Result.AssetTypeBusinessUnitAssetStatusTypeBodyDTOPatchResult(StatusForControllerOperations.BAD_REQUEST, null);

        // Normalizzazione del nome (se necessario)
        assetTypeDTO.setName(normalizeName(assetTypeDTO.getName()));
        if(repository.existsByName(assetTypeDTO.getName()))
            return new Result.AssetTypeBusinessUnitAssetStatusTypeBodyDTOPatchResult(StatusForControllerOperations.BAD_REQUEST, null);

        Optional<AssetType> typeById = repository.findById(id);

        if(typeById.isEmpty())
            return new Result.AssetTypeBusinessUnitAssetStatusTypeBodyDTOPatchResult(StatusForControllerOperations.NOT_FOUND, null);

        AssetType updatedAssetType = typeById.get();

        if(!(updatedAssetType.getName().equals(assetTypeDTO.getName())) &&
                repository.existsByName(assetTypeDTO.getName()))
            return new Result.AssetTypeBusinessUnitAssetStatusTypeBodyDTOPatchResult(StatusForControllerOperations.BAD_REQUEST, null);

        updatedAssetType.setName(assetTypeDTO.getName());
        updatedAssetType.setUpdateDate(LocalDateTime.now());
        repository.save(updatedAssetType);

        return new Result.AssetTypeBusinessUnitAssetStatusTypeBodyDTOPatchResult(StatusForControllerOperations.OK, assetTypeDTO);
    }


    // Attiva o Disattiva un Type (resetCache)
    @CacheEvict(value = "assetTypes", allEntries = true)
    public AssetTypeBusinessUnitAssetStatusTypeActiveDeactiveBodyDTO activateDeactivateTypeById(Long id)
    {
        Optional<AssetType> typeById = repository.findById(id);

        if(typeById.isEmpty())
            return null;

        AssetType activatedDeactivatedAssetType = typeById.get();

        activatedDeactivatedAssetType.setActive(!activatedDeactivatedAssetType.isActive());
        activatedDeactivatedAssetType.setUpdateDate(LocalDateTime.now());
        repository.save(activatedDeactivatedAssetType);

        return new AssetTypeBusinessUnitAssetStatusTypeActiveDeactiveBodyDTO(
                activatedDeactivatedAssetType.getName(), activatedDeactivatedAssetType.isActive());
    }
}
