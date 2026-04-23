package com.particle.asset.manager.DTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AssetResponseDto
{
    private String brand, model, serialNumber, note, storage,
            businessUnitCode, assetTypeCode, assetStatusTypeCode;
    private Short ram;
    private LocalDate endMaintenance;
}
