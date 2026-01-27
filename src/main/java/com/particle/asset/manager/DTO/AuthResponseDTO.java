package com.particle.asset.manager.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponseDTO
{
    private String accessToken, /*refreshToken,*/ tokenType = "Bearer";
    private long expiresIn; // Millisecondi
}
