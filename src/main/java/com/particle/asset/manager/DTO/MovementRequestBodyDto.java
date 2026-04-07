package com.particle.asset.manager.DTO;

import com.particle.asset.manager.enums.MovementTypes;
import lombok.Data;

@Data
public class MovementRequestBodyDto
{
    private String note;
    private MovementTypes movementType;
    private Long user;
    private String receiptBase64;
}