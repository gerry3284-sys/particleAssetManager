package com.particle.asset.manager.services;

import com.particle.asset.manager.DTO.AssetTypeBusinessUnitAssetStatusTypeRequestBodyDTO;
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
    public AssetStatusType createAssetStatusType(AssetTypeBusinessUnitAssetStatusTypeRequestBodyDTO assetStatusTypeDTO)
    {
        if(assetStatusTypeDTO == null || assetStatusTypeDTO.getName() == null ||
                repository.existsByName(assetStatusTypeDTO.getName()))
            return null;

        AssetStatusType assetStatusType = new AssetStatusType();
        assetStatusType.setName(assetStatusTypeDTO.getName());

        return repository.save(assetStatusType);
    }

    // Aggiorna un AssetStatusType (reset cache)
    @CacheEvict(value = "assetStatusTypes", allEntries = true)
    public Result.AssetStatusTypeResult updateAssetStatusType(Long id,
                                             AssetTypeBusinessUnitAssetStatusTypeRequestBodyDTO assetStatusTypeDTO)
    {
        if(assetStatusTypeDTO == null || assetStatusTypeDTO.getName() == null ||
                repository.existsByName(assetStatusTypeDTO.getName()))
            return new Result.AssetStatusTypeResult(StatusForControllerOperations.BAD_REQUEST, null);

        Optional<AssetStatusType> assetStatusTypeById = repository.findById(id);

        if(assetStatusTypeById.isEmpty())
            return new Result.AssetStatusTypeResult(StatusForControllerOperations.NOT_FOUND, null);

        AssetStatusType updatedAssetStatusType = assetStatusTypeById.get();

        if(!(updatedAssetStatusType.getName().equals(assetStatusTypeDTO.getName())) &&
                repository.existsByName(assetStatusTypeDTO.getName()))
            return new Result.AssetStatusTypeResult(StatusForControllerOperations.BAD_REQUEST, null);

        updatedAssetStatusType.setName(assetStatusTypeDTO.getName());
        updatedAssetStatusType.setUpdateDate(LocalDateTime.now());

        return new Result.AssetStatusTypeResult(StatusForControllerOperations.OK, repository.save(updatedAssetStatusType));
    }

    // Attiva o Disattiva un AssetStatusType (reset cache)
    @CacheEvict(value = "assetStatusTypes", allEntries = true)
    public AssetStatusType activateDeactivateAssetStatusType(Long id)
    {
        Optional<AssetStatusType> assetStatusTypeById = repository.findById(id);

        if(assetStatusTypeById.isEmpty())
            return null;

        AssetStatusType activatedDeactivatedAssetStatusType = assetStatusTypeById.get();

        activatedDeactivatedAssetStatusType.setActive(!activatedDeactivatedAssetStatusType.isActive());
        activatedDeactivatedAssetStatusType.setUpdateDate(LocalDateTime.now());

        return repository.save(activatedDeactivatedAssetStatusType);
    }
}
