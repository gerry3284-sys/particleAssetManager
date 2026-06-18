package com.particle.asset.manager.DTO;

import com.particle.asset.manager.enums.MovementTypes;
import com.particle.asset.manager.enums.TicketsAssetsPriorities;
import lombok.Data;

@Data
public class MovementRequestBodyDto
{
    private String note;
    private MovementTypes movementType;
    private String userCode, receiptBase64/*, priority*/;
}