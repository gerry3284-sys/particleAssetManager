package com.particle.asset.manager.services;

import com.particle.asset.manager.DTO.AssetTypeBusinessUnitAssetStatusTypeActiveDeactiveBodyDTO;
import com.particle.asset.manager.DTO.AssetTypeBusinessUnitAssetStatusTypeBodyDTO;
import com.particle.asset.manager.enumerations.StatusForControllerOperations;
import com.particle.asset.manager.models.BusinessUnit;
import com.particle.asset.manager.repositories.BusinessUnitRepository;
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
public class BusinessUnitService
{
    private final BusinessUnitRepository repository;
    private final CacheManager cacheManager;

    public BusinessUnitService(BusinessUnitRepository repository, CacheManager cacheManager)
    {
        this.repository = repository;
        this.cacheManager = cacheManager;
    }

    // Mostra tutte le BusinessUnit (con cache)
    // @Chacheable → Quando si chiama "getAllTypes()" la prima volta, i dati vengono recuperati
    //               dal database e salvati i cache con la chiave "all". Le chiamata successive
    //               leggono direttamente dalla cache per 8 ore.
    @Cacheable(value = "businessUnits", key = "'all'")
    public List<BusinessUnit> getAllBusinessUnits()
    {
        System.out.println(">>> Fetching ALL BusinessUnits from database...");
        List<BusinessUnit> businessUnits = repository.findAll();


        // Popola anche le cache per singoli ID
        Cache cache = cacheManager.getCache("businessUnits");
        if (cache != null)
            businessUnits.forEach(type -> cache.put("id::" + type.getId(), type));

        return businessUnits;
    }

    public BusinessUnit getBusinessUnitById(String code)
    {
        Cache cache = cacheManager.getCache("businessUnits");

        // 1. Cerca prima nella cache del singolo ID
        Cache.ValueWrapper idWrapper = cache != null ? cache.get("id::" + code) : null;
        if (idWrapper != null) {
            System.out.println(">>> getBusinessUnitById(" + code + ") - CACHE (singolo ID)");
            return (BusinessUnit) idWrapper.get();
        }

        // 2. Se non c'è, cerca nella cache "all"
        Cache.ValueWrapper allWrapper = cache != null ? cache.get("all") : null;
        if (allWrapper != null) {
            System.out.println(">>> getBusinessUnitById(" + code + ") - CACHE (filtrato da 'all')");
            @SuppressWarnings("unchecked")
            List<BusinessUnit> allBusinessUnits = (List<BusinessUnit>) allWrapper.get();
            return allBusinessUnits.stream()
                    .filter(type -> type.getCode().equals(code))
                    .findFirst()
                    .orElse(null);
        }

        // 3. Cache vuota - va al DB e salva SOLO questo ID
        System.out.println(">>> getBusinessUnitById(" + code + ") - DATABASE (salvando singolo ID in cache)");
        BusinessUnit businessUnit = repository.findByCode(code).orElse(null);
        if (cache != null && businessUnit != null)
            cache.put("id::" + code, businessUnit);


        return businessUnit;
    }

    // Crea una businessUnit (reset cache)
    // @CacheEvict → Quando si crea/aggiorna/disattiva un record, la cache viene
    //               completamente svuotata (clear). Alla prossima chiamata GET,
    //               i dati verranno caricati direttamente dal database.
    @CacheEvict(value = "businessUnits", allEntries = true)
    public AssetTypeBusinessUnitAssetStatusTypeBodyDTO createBusinessUnit(
            AssetTypeBusinessUnitAssetStatusTypeBodyDTO businessUnitDTO)
    {
        if(businessUnitDTO == null || businessUnitDTO.getName() == null ||
                businessUnitDTO.getName().trim().isEmpty())
            return null;

        // Normalizzazione del nome (se necessario)
        businessUnitDTO.setName(normalizeName(businessUnitDTO.getName()));

        if(repository.existsByName(businessUnitDTO.getName()))
            return null;

        BusinessUnit businessUnit = new BusinessUnit();
        businessUnit.setName(businessUnitDTO.getName());

        String nameWithoutSpaces = businessUnit.getName().replaceAll("\\s+", "");
        businessUnit.setCode(nameWithoutSpaces.toUpperCase()
                .substring(0, Math.min(2, nameWithoutSpaces.length())) + (repository.count()+1));

        repository.save(businessUnit);

        return businessUnitDTO;
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

    // Aggiorna un BusinessUnit (reset cache)
    @CacheEvict(value = "businessUnits", allEntries = true)
    public Result.AssetTypeBusinessUnitAssetStatusTypeBodyDTOPatchResult updateBusinessUnitById(String code,
                                                                                                AssetTypeBusinessUnitAssetStatusTypeBodyDTO businessUnitDTO)
    {
        if(businessUnitDTO == null || businessUnitDTO.getName() == null)
            return new Result.AssetTypeBusinessUnitAssetStatusTypeBodyDTOPatchResult(StatusForControllerOperations.BAD_REQUEST, null);

        // Normalizzazione del nome (se necessario)
        businessUnitDTO.setName(normalizeName(businessUnitDTO.getName()));
        if(repository.existsByName(businessUnitDTO.getName()))
            return new Result.AssetTypeBusinessUnitAssetStatusTypeBodyDTOPatchResult(StatusForControllerOperations.BAD_REQUEST, null);

        Optional<BusinessUnit> businessUnitById = repository.findByCode(code);

        if(businessUnitById.isEmpty())
            return new Result.AssetTypeBusinessUnitAssetStatusTypeBodyDTOPatchResult(StatusForControllerOperations.NOT_FOUND, null);

        BusinessUnit updatedBusinessUnit = businessUnitById.get();

        if(!(updatedBusinessUnit.getName().equals(businessUnitDTO.getName())) &&
                repository.existsByName(businessUnitDTO.getName()))
            return new Result.AssetTypeBusinessUnitAssetStatusTypeBodyDTOPatchResult(StatusForControllerOperations.BAD_REQUEST, null);

        updatedBusinessUnit.setName(businessUnitDTO.getName());
        updatedBusinessUnit.setUpdateDate(LocalDateTime.now());
        repository.save(updatedBusinessUnit);

        return new Result.AssetTypeBusinessUnitAssetStatusTypeBodyDTOPatchResult(StatusForControllerOperations.OK, businessUnitDTO);
    }


    // Attiva o Disattiva un Type (reset Cache)
    @CacheEvict(value = "businessUnits", allEntries = true)
    public AssetTypeBusinessUnitAssetStatusTypeActiveDeactiveBodyDTO activateDeactivateBusinessUnitById(String code)
    {
        Optional<BusinessUnit> businessUnitById = repository.findByCode(code);

        if(businessUnitById.isEmpty())
            return null;

        BusinessUnit activatedDeactivatedBusinessUnit = businessUnitById.get();

        activatedDeactivatedBusinessUnit.setActive(!activatedDeactivatedBusinessUnit.isActive());
        activatedDeactivatedBusinessUnit.setUpdateDate(LocalDateTime.now());
        repository.save(activatedDeactivatedBusinessUnit);

        return new AssetTypeBusinessUnitAssetStatusTypeActiveDeactiveBodyDTO(
                activatedDeactivatedBusinessUnit.getName(), activatedDeactivatedBusinessUnit.isActive());
    }
}
