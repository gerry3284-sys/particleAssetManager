package com.particle.asset.manager.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService
{
    private final SecretKey accessKey;
    //private final SecretKey refreshKey;
    private final long accessExpirationMs;
    //private final long refreshExpirationMs;

    public JwtService(
            @Value("${jwt.secret}") String accessSecret,
            //@Value("${jwt.refresh.secret}") String refreshSecret,
            @Value("${jwt.expiration}") long accessExpirationMs)
            //@Value("${jwt.refresh.expiration}") long refreshExpirationMs)
    {
        this.accessKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessSecret));
        //this.refreshKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshSecret));
        this.accessExpirationMs = accessExpirationMs;
        //this.refreshExpirationMs = refreshExpirationMs;
    }

    // Genera Access Token (breve durata)
    public String generateAccessToken(String email, String userType) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userType", userType);
        claims.put("tokenType", "access");

        return Jwts.builder()
                .claims(claims)
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessExpirationMs))
                .signWith(accessKey)
                .compact();
    }

    // Genera Refresh Token (lunga durata)
    /*public String generateRefreshToken(String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("tokenType", "refresh");

        return Jwts.builder()
                .claims(claims)
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshExpirationMs))
                .signWith(refreshKey)
                .compact();
    }*/

    public String extractUsername(String token) {
        return extractClaims(token, accessKey).getSubject();
    }

    /*public String extractUsernameFromRefreshToken(String token) {
        return extractClaims(token, refreshKey).getSubject();
    }*/

    public String extractUserType(String token) {
        return extractClaims(token, accessKey).get("userType", String.class);
    }

    private Claims extractClaims(String token, SecretKey key) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isAccessTokenValid(String token, String user) {
        try {
            var claims = extractClaims(token, accessKey);
            return user.equals(claims.getSubject())
                    && claims.getExpiration().after(new Date())
                    && "access".equals(claims.get("tokenType"));
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /*public boolean isRefreshTokenValid(String token) {
        try {
            var claims = extractClaims(token, refreshKey);
            return claims.getExpiration().after(new Date())
                    && "refresh".equals(claims.get("tokenType"));
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }*/

    /*public long getRefreshExpirationMs() {
        return refreshExpirationMs;
    }*/
}