package com.particle.asset.manager.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MovementResponseBodyDTO
{
    private String asset;
    private Long user;
    private String movementType, note;
}
