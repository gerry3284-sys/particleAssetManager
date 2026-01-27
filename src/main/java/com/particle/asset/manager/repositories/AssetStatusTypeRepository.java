package com.particle.asset.manager.repositories;

import com.particle.asset.manager.models.AssetStatusType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssetStatusTypeRepository extends JpaRepository<AssetStatusType, Long>
{
    boolean existsByName(String name);
}
