package com.particle.asset.manager.repositories;

import com.particle.asset.manager.models.AssetType;
import com.particle.asset.manager.models.BusinessUnit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AssetTypeRepository extends JpaRepository<AssetType, Long>
{
    // Ritorna Optional<> se il nome combacia con quello della tabella type
    Optional<AssetType> findByName(String name);

    // TODO: Da togliere e usare findByName ?
    boolean existsByName(String name);

    // Trova l'AssetType più recente. Restituisce NULL se non trova nulla
    AssetType findTopByOrderByIdDesc();

    Optional<AssetType> findByCode(String code);
}
