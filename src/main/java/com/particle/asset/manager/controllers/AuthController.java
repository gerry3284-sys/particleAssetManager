package com.particle.asset.manager.controllers;

import com.particle.asset.manager.DTO.AuthResponseDTO;
//import com.particle.asset.manager.DTO.RefreshTokenRequestDTO;
//import com.particle.asset.manager.models.RefreshToken;
import com.particle.asset.manager.repositories.UserRepository;
import com.particle.asset.manager.services.JwtService;
//import com.particle.asset.manager.services.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController
{
    private final JwtService jwtService;
    //private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;

    /*@PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequestDTO request)
    {
        String refreshTokenString = request.getRefreshToken();

        return refreshTokenService.findByToken(refreshTokenString)
                .filter(refreshTokenService::isValid)
                .map(refreshToken -> {
                    // Rinnova la scadenza del refresh token
                    RefreshToken renewedToken = refreshTokenService.renewRefreshToken(refreshToken);

                    // Genera nuovo access token
                    String accessToken = jwtService.generateAccessToken(
                            renewedToken.getUser().getEmail(),
                            renewedToken.getUser().getUserType().name()
                    );

                    return ResponseEntity.ok(new AuthResponseDTO(
                            accessToken,
                            refreshTokenString, // Mantieni lo stesso refresh token
                            "Bearer",
                            900000L // 15 minuti in ms
                    ));
                }).orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Invalid or expired refresh token"
                ));
    }*/

    /*@PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody RefreshTokenRequestDTO request) {
        refreshTokenService.revokeToken(request.getRefreshToken());
        return ResponseEntity.ok("Logged out successfully");
    }*/
}