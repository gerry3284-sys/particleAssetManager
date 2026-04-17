package com.particle.asset.manager.DTO;

import lombok.Data;

@Data
public class AssetTypeResponseDto
{
    private String name;
    private boolean ram, storage;
}
