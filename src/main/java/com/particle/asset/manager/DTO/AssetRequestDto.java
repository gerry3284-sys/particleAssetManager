package com.particle.asset.manager.DTO;

import lombok.Data;

@Data
public class AssetRequestDto
{
    private String brand, model, serialNumber, note, storage,
            businessUnitCode, assetTypeCode;
    private Short ram;
}
