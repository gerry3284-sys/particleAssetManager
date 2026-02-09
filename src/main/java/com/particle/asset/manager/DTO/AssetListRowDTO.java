package com.particle.asset.manager.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AssetListRowDTO {
    private String status;
    private String brand;
    private String model;
    private String serialNumber;
    private String assetCode;
    private String assetType;
    private String assignedUser;
    private String businessUnit;
    private LocalDateTime assignmentDate;
}
