package com.particle.asset.manager.services;

import com.particle.asset.manager.DTO.AssetTypeBusinessUnitAssetStatusTypeActiveDeactiveBodyDTO;
import com.particle.asset.manager.DTO.AssetTypeBusinessUnitAssetStatusTypeBodyDTO;
import com.particle.asset.manager.enumerations.StatusForControllerOperations;
import com.particle.asset.manager.models.AssetStatusType;
import com.particle.asset.manager.repositories.AssetStatusTypeRepository;
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
public class AssetStatusTypeService
{
    private final AssetStatusTypeRepository repository;
    private final CacheManager cacheManager;

    public AssetStatusTypeService(AssetStatusTypeRepository repository, CacheManager cacheManager)
    {
        this.repository = repository;
        this.cacheManager = cacheManager;
    }

    // Mostra tutti gli AssetStatusType (con cache)
    // @Chacheable → Quando si chiama "getAllTypes()" la prima volta, i dati vengono recuperati
    //               dal database e salvati i cache con la chiave "all". Le chiamata successive
    //               leggono direttamente dalla cache per 8 ore.
    @Cacheable(value = "assetStatusTypes", key = "'all'")
    public List<AssetStatusType> getAllAssetStatusType()
    {
        System.out.println(">>> Fetching ALL AssetStatusTypes from database...");

        List<AssetStatusType> types = repository.findAll();

        // Popola anche le cache per singoli ID
        Cache cache = cacheManager.getCache("assetStatusTypes");
        if (cache != null)
            types.forEach(type -> cache.put("id::" + type.getId(), type));

        return types;
    }

    public AssetStatusType getAssetStatusTypeById(Long id)
    {
        Cache cache = cacheManager.getCache("assetStatusTypes");

        // 1. Cerca prima nella cache del singolo ID
        Cache.ValueWrapper idWrapper = cache != null ? cache.get("id::" + id) : null;
        if (idWrapper != null) {
            System.out.println(">>> getAssetStatusTypeById(" + id + ") - CACHE (singolo ID)");
            return (AssetStatusType) idWrapper.get();
        }

        // 2. Se non c'è, cerca nella cache "all"
        Cache.ValueWrapper allWrapper = cache != null ? cache.get("all") : null;
        if (allWrapper != null) {
            System.out.println(">>> getAssetStatusTypeById(" + id + ") - CACHE (filtrato da 'all')");
            @SuppressWarnings("unchecked")
            List<AssetStatusType> allAssetStatusTypes = (List<AssetStatusType>) allWrapper.get();
            return allAssetStatusTypes.stream()
                    .filter(type -> type.getId().equals(id))
                    .findFirst()
                    .orElse(null);
        }

        // 3. Cache vuota - va al DB e salva SOLO questo ID
        System.out.println(">>> getAssetStatusTypeById(" + id + ") - DATABASE (salvando singolo ID in cache)");
        AssetStatusType assetStatusType = repository.findById(id).orElse(null);
        if (cache != null && assetStatusType != null)
            cache.put("id::" + id, assetStatusType);


        return assetStatusType;
    }

    // Crea un AssetStatusType (reset cache)
    // @CacheEvict → Quando si crea/aggiorna/disattiva un record, la cache viene
    //               completamente svuotata (clear). Alla prossima chiamata GET,
    //               i dati verrano caricati direttamente dal database.
    @CacheEvict(value = "assetStatusTypes", allEntries = true)
    public AssetTypeBusinessUnitAssetStatusTypeBodyDTO createAssetStatusType(
            AssetTypeBusinessUnitAssetStatusTypeBodyDTO assetStatusTypeDTO)
    {
        if(assetStatusTypeDTO == null || assetStatusTypeDTO.getName() == null ||
                assetStatusTypeDTO.getName().trim().isEmpty())
            return null;

        // Normalizzazione del nome (se necessario)
        assetStatusTypeDTO.setName(normalizeName(assetStatusTypeDTO.getName()));

        if(repository.existsByName(assetStatusTypeDTO.getName()))
            return null;

        AssetStatusType assetStatusType = new AssetStatusType();
        assetStatusType.setName(assetStatusTypeDTO.getName());

        String nameWithoutSpaces = assetStatusType.getName().replaceAll("\\s+", "");
        assetStatusType.setCode(nameWithoutSpaces.toUpperCase()
                .substring(0, Math.min(2, nameWithoutSpaces.length())) + (repository.count()+1));

        repository.save(assetStatusType);

        return assetStatusTypeDTO;
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

    // Aggiorna un AssetStatusType (reset cache)
    @CacheEvict(value = "assetStatusTypes", allEntries = true)
    public Result.AssetTypeBusinessUnitAssetStatusTypeBodyDTOPatchResult updateAssetStatusType(Long id,
                                             AssetTypeBusinessUnitAssetStatusTypeBodyDTO assetStatusTypeDTO)
    {
        if(assetStatusTypeDTO == null || assetStatusTypeDTO.getName() == null)
            return new Result.AssetTypeBusinessUnitAssetStatusTypeBodyDTOPatchResult(StatusForControllerOperations.BAD_REQUEST, null);

        // Normalizzazione del nome (se necessario)
        assetStatusTypeDTO.setName(normalizeName(assetStatusTypeDTO.getName()));
        if(repository.existsByName(assetStatusTypeDTO.getName()))
            return new Result.AssetTypeBusinessUnitAssetStatusTypeBodyDTOPatchResult(StatusForControllerOperations.BAD_REQUEST, null);

        Optional<AssetStatusType> assetStatusTypeById = repository.findById(id);

        if(assetStatusTypeById.isEmpty())
            return new Result.AssetTypeBusinessUnitAssetStatusTypeBodyDTOPatchResult(StatusForControllerOperations.NOT_FOUND, null);

        AssetStatusType updatedAssetStatusType = assetStatusTypeById.get();

        if(!(updatedAssetStatusType.getName().equals(assetStatusTypeDTO.getName())) &&
                repository.existsByName(assetStatusTypeDTO.getName()))
            return new Result.AssetTypeBusinessUnitAssetStatusTypeBodyDTOPatchResult(StatusForControllerOperations.BAD_REQUEST, null);

        updatedAssetStatusType.setName(assetStatusTypeDTO.getName());
        updatedAssetStatusType.setUpdateDate(LocalDateTime.now());
        repository.save(updatedAssetStatusType);

        return new Result.AssetTypeBusinessUnitAssetStatusTypeBodyDTOPatchResult(StatusForControllerOperations.OK, assetStatusTypeDTO);
    }

    // Attiva o Disattiva un AssetStatusType (reset cache)
    @CacheEvict(value = "assetStatusTypes", allEntries = true)
    public AssetTypeBusinessUnitAssetStatusTypeActiveDeactiveBodyDTO activateDeactivateAssetStatusType(Long id)
    {
        Optional<AssetStatusType> assetStatusTypeById = repository.findById(id);

        if(assetStatusTypeById.isEmpty())
            return null;

        AssetStatusType activatedDeactivatedAssetStatusType = assetStatusTypeById.get();

        activatedDeactivatedAssetStatusType.setActive(!activatedDeactivatedAssetStatusType.isActive());
        activatedDeactivatedAssetStatusType.setUpdateDate(LocalDateTime.now());
        repository.save(activatedDeactivatedAssetStatusType);

        return new AssetTypeBusinessUnitAssetStatusTypeActiveDeactiveBodyDTO(
                activatedDeactivatedAssetStatusType.getName(), activatedDeactivatedAssetStatusType.isActive());
    }
}
