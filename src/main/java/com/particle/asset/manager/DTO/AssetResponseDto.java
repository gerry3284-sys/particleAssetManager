package com.particle.asset.manager.DTO;

import lombok.Data;

@Data
public class AssetResponseDto
{
    private String brand, model, serialNumber, note, hardDisk,
            businessUnitCode, assetTypeCode, assetStatusTypeCode;
    private Short ram;
}
