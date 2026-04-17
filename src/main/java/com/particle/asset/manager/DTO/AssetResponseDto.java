package com.particle.asset.manager.DTO;

import lombok.Data;

@Data
public class AssetResponseDto
{
    private String brand, model, serialNumber, note, storage,
            businessUnitCode, assetTypeCode, assetStatusTypeCode;
    private Short ram;
}
