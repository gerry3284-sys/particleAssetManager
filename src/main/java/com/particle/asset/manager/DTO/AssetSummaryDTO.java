package com.particle.asset.manager.DTO;

import lombok.Data;

@Data
public class AssetSummaryDTO
{
    private Long id;
    private Short ram;
    private String brand, model, serialNumber, hardDisk;

    public AssetSummaryDTO(Long id, String brand, String model, String serialNumber, Short ram, String hardDisk)
    {
        this.id = id;
        this.ram = ram;
        this.hardDisk = hardDisk;
        this.brand = brand;
        this.model = model;
        this.serialNumber = serialNumber;
    }

    public AssetSummaryDTO() {

    }

    
}
