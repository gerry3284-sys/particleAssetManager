/*package com.particle.asset.manager.repositories;

import com.particle.asset.manager.models.RefreshToken;
import com.particle.asset.manager.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long>
{
    Optional<RefreshToken> findByToken(String token);

    void deleteByUser(User user);

    Optional<RefreshToken> findByUserAndRevokedFalse(User user);
}*/
