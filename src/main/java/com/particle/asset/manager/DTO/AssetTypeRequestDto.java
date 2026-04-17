package com.particle.asset.manager.DTO;

import lombok.Data;

@Data
public class AssetTypeRequestDto
{
    private String name;
    private boolean ram, storage;
}
