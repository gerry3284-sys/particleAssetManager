package com.particle.asset.manager.repositories;

import com.particle.asset.manager.models.BusinessUnit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BusinessUnitRepository extends JpaRepository<BusinessUnit, Long>
{
    // Ritorna Optional<> se il nome combacia con quello della tabella business_unit
    Optional<BusinessUnit> findByName(String name);

    // TODO: Da togliere e usare findByName ?
    boolean existsByName(String name);

    // Trova la BusinessUnit più recente. Restituisce NULL se non trova nulla
    BusinessUnit findTopByOrderByIdDesc();
}
