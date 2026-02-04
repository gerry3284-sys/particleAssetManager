package com.particle.asset.manager.models;

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
    private String assignedUser;
    private String businessUnit;
    private LocalDateTime assignmentDate;
}
