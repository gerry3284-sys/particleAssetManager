package com.particle.asset.manager.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class BusinessUnitResponseDto
{
    private String code, name;
    private boolean active;
}