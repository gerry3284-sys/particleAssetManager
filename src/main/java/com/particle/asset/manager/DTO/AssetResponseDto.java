package com.particle.asset.manager.DTO;

import com.particle.asset.manager.enums.TicketsAssetsPriorities;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AssetResponseDto
{
    private String brand, model, serialNumber, note, storage,
            businessUnitCode, assetTypeCode, assetStatusTypeCode;
    private Short ram;
    private boolean inProgress;
    private TicketsAssetsPriorities priority;
    private LocalDate endMaintenance;
}
