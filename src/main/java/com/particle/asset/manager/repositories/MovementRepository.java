package com.particle.asset.manager.repositories;

import com.particle.asset.manager.models.Movement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MovementRepository extends JpaRepository<Movement, Long>
{
    // Ritorna una List <> se lo userId combacia con quello della tabella users
    List<Movement> findByUsersId(Long userId);

    // Ritorna una List<> se l'assetId combacia con quello della tabella movement
    //List<Movement> findByAssetId(Long assetId);
    List<Movement> findByAssetCode(String code);

    boolean existsByAssetCode(String assetCode);

    // "findFirst" --> solo il primo risultato
    // "ByAssetId" --> filtra dove asset.id = assetId
    // "OrderByDateDesc" --> ordina per date in ordine decrescente
    // Ritorna Optional<> se l'assetId combacia con quello del primo risultato della tabella movement
    //Optional<Movement> findFirstByAssetIdOrderByDateDesc(Long assetId);

    Optional<Movement> findFirstByAssetCodeOrderByDateDesc(String assetCode);
}
