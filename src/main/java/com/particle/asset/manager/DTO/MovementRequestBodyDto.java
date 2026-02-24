package com.particle.asset.manager.DTO;

import lombok.Data;

@Data
public class MovementRequestBodyDto
{
    private String note, movementType;
    private Long user;
    private String receiptBase64;
}