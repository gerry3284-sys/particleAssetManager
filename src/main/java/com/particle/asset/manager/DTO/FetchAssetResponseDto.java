package com.particle.asset.manager.DTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class FetchAssetResponseDto
{
    private String code, brand, model, serialNumber, note, storage;
    AssetTypeResponseDto assetType;
    BusinessUnitResponseDto businessUnit;
    AssetStatusTypeStatusResponseDto assetStatusType;
    private Short ram;
    private LocalDate endMaintenance;
}
