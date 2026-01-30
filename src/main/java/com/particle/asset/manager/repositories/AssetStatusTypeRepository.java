package com.particle.asset.manager.repositories;

import com.particle.asset.manager.models.AssetStatusType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AssetStatusTypeRepository extends JpaRepository<AssetStatusType, Long>
{
    boolean existsByName(String name);

    // Trova l'AssetStatusType più recente. Restituisce NULL se non trova nulla
    AssetStatusType findTopByOrderByIdDesc();

    Optional<AssetStatusType> findByCode(String code);
}
