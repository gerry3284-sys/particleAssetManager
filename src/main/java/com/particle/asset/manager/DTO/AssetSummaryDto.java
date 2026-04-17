package com.particle.asset.manager.DTO;

import lombok.Data;

@Data
public class AssetSummaryDto
{
    private Short ram;
    private String brand, model, serialNumber, storage, code, statusCode;

    public AssetSummaryDto(String brand, String model,
                           String serialNumber, Short ram, String storage, String code, String statusCode)
    {
        this.ram = ram;
        this.storage = storage;
        this.brand = brand;
        this.model = model;
        this.serialNumber = serialNumber;
        this.code = code;
        this.statusCode = statusCode;
    }

    public AssetSummaryDto() {

    }

    
}
