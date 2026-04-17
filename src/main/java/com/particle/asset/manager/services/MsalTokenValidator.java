package com.particle.asset.manager.services;

import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.particle.asset.manager.security.DecodedToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.Date;

@Service
public class MsalTokenValidator {

    @Value("${azure.activedirectory.client-id}")
    private String clientId;

    @Value("${azure.activedirectory.jwks-uri}")
    private String jwksUri;

    public DecodedToken validateAndDecode(String token) {
        try {
            // Scarica le chiavi pubbliche di Microsoft
            JWKSet jwkSet = JWKSet.load(new URL(jwksUri));

            // Decodifica il token
            SignedJWT signedJWT = SignedJWT.parse(token);

            // Prende il kid (key id) dall'header del token
            String kid = signedJWT.getHeader().getKeyID();

            // Trova la chiave corrispondente
            JWK jwk = jwkSet.getKeyByKeyId(kid);
            if (jwk == null) throw new RuntimeException("Chiave non trovata");

            // Verifica la firma
            RSASSAVerifier verifier = new RSASSAVerifier((RSAKey) jwk);
            if (!signedJWT.verify(verifier)) {
                throw new RuntimeException("Firma token non valida");
            }

            // Legge le claims
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

            // Verifica audience
            if (!claims.getAudience().contains(clientId)) {
                throw new RuntimeException("Token audience non valido");
            }

            // Verifica scadenza
            if (claims.getExpirationTime().before(new Date())) {
                throw new RuntimeException("Token scaduto");
            }

            return new DecodedToken(
                    claims.getStringClaim("oid"),
                    claims.getStringClaim("preferred_username"),
                    claims.getStringClaim("name")
            );

        } catch (Exception e) {
            throw new RuntimeException("Token non valido: " + e.getMessage());
        }
    }
}