package com.particle.asset.manager.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AssetMaintenanceListRowResponseDto
{
    private String code, brand, model, serialNumber, assetCode, assetType, businessUnit;
    private boolean inProgress;
    private LocalDateTime returnedDate, endMaintenanceDate;
}
