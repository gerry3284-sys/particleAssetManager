package com.particle.asset.manager.DTO;

import lombok.Data;

@Data
public class AssetSummaryDto
{
    private Long id;
    private Short ram;
    private String brand, model, serialNumber, hardDisk, code;

    public AssetSummaryDto(Long id, String brand, String model,
                           String serialNumber, Short ram, String hardDisk, String code)
    {
        this.id = id;
        this.ram = ram;
        this.hardDisk = hardDisk;
        this.brand = brand;
        this.model = model;
        this.serialNumber = serialNumber;
        this.code = code;
    }

    public AssetSummaryDto() {

    }

    
}
