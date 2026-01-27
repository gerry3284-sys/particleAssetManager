package com.particle.asset.manager.DTO;

import lombok.Data;

@Data
public class MovementRequestBodyDTO
{
    private String note;
    private Long asset, user, movementType;
}
