package com.particle.asset.manager.DTO;

import lombok.Data;

@Data
public class AssetRequestBodyDTO
{
    private Short ram;
    private String brand, model, serialNumber, note, hardDisk;
    private Long businessUnit, assetType, assetStatusType;
}
