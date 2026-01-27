package com.particle.asset.manager.repositories;

import com.particle.asset.manager.models.Asset;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssetRepository extends JpaRepository<Asset, Long>
{
    // Ritorna true o false, se trova il SerialNumber nella tabella asset
    boolean existsBySerialNumber(String serialNumber);
}
