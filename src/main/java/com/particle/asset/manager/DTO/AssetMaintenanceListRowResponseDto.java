package com.particle.asset.manager.DTO;

import com.particle.asset.manager.enums.TicketsAssetsPriorities;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AssetMaintenanceListRowResponseDto
{
    private String code, brand, model, serialNumber, assetCode, assetType, businessUnit;
    private boolean inProgress;
    private TicketsAssetsPriorities priority;
    private LocalDateTime startMaintenanceDate, endMaintenanceDate;
}
