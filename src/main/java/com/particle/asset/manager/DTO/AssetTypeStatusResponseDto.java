package com.particle.asset.manager.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class AssetTypeStatusResponseDto
{
    private String name;
    private boolean active;
}
