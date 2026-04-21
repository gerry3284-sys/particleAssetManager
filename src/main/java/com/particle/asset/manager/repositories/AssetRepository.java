package com.particle.asset.manager.repositories;

import com.particle.asset.manager.models.Asset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssetRepository extends JpaRepository<Asset, Long>
{
    // Ritorna true o false, se trova il SerialNumber nella tabella asset
    boolean existsBySerialNumber(String serialNumber);

    // Trova l'Asset più recente. Restituisce NULL se non trova nulla
    Asset findTopByOrderByIdDesc();

    Optional<Asset> findByCode(String code);

    boolean existsByCode(String code);

    List<Asset> findByAssetStatusType_Code(String statusCode);
}
