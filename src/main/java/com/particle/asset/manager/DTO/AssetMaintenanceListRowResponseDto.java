package com.particle.asset.manager.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AssetMaintenanceListRowResponseDto
{
    private String brand;
    private String model;
    private String serialNumber;
    private String assetCode;
    private String assetType;
    private String businessUnit;
    private LocalDateTime returnedDate, endMaintenanceDate;
}
