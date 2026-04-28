package com.particle.asset.manager.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class AssetTypeResponseDto
{
    private String code, name;
    private boolean ram, storage, active;
}
