/*package com.particle.asset.manager.services;

import com.particle.asset.manager.models.RefreshToken;
import com.particle.asset.manager.models.User;
import com.particle.asset.manager.repositories.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    @Transactional
    public RefreshToken createRefreshToken(User user) {
        // Revoca eventuali refresh token precedenti
        refreshTokenRepository.findByUserAndRevokedFalse(user)
                .ifPresent(token -> {
                    token.setRevoked(true);
                    refreshTokenRepository.save(token);
                });

        // Crea nuovo refresh token
        String tokenString = jwtService.generateRefreshToken(user.getEmail());

        // Usa LocalDateTime con fuso orario italiano
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Europe/Rome"));
        long expirationMillis = jwtService.getRefreshExpirationMs();
        LocalDateTime expiry = now.plusNanos(expirationMillis * 1_000_000); // converti ms in nanosecondi

        /*System.out.println("\n=== DEBUG REFRESH TOKEN ===");
        System.out.println("Ora attuale (Italia): " + now);
        System.out.println("Millisecondi da aggiungere: " + expirationMillis);
        System.out.println("Data di scadenza (Italia): " + expiry);
        System.out.println("Differenza in minuti: " +
                java.time.Duration.between(now, expiry).toMinutes());*/
/*
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(tokenString)
                .expiryDate(expiry)
                .revoked(false)
                .build();

        RefreshToken saved = refreshTokenRepository.save(refreshToken);
        /*System.out.println("Salvato nel DB con expiryDate: " + saved.getExpiryDate());
        System.out.println("===========================\n");*/
/*
        return saved;
    }

    @Transactional
    public RefreshToken renewRefreshToken(RefreshToken oldToken) {
        // Calcola nuova scadenza
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Europe/Rome"));
        long expirationMillis = jwtService.getRefreshExpirationMs();
        LocalDateTime newExpiry = now.plusNanos(expirationMillis * 1_000_000);

        // Aggiorna la scadenza del token esistente
        oldToken.setExpiryDate(newExpiry);

        return refreshTokenRepository.save(oldToken);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public boolean isValid(RefreshToken refreshToken) {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Europe/Rome"));
        return !refreshToken.isRevoked()
                && refreshToken.getExpiryDate().isAfter(now)
                && jwtService.isRefreshTokenValid(refreshToken.getToken());
    }

    @Transactional
    public void revokeToken(String token) {
        refreshTokenRepository.findByToken(token)
                .ifPresent(refreshToken -> {
                    refreshToken.setRevoked(true);
                    refreshTokenRepository.save(refreshToken);
                });
    }

    @Transactional
    public void revokeAllUserTokens(User user) {
        refreshTokenRepository.deleteByUser(user);
    }
}*/