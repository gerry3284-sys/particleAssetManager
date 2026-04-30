package com.particle.asset.manager.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MovementResponseBodyDto
{
    private String asset;
    private String userCode, movementType, note;
}
