package com.particle.asset.manager.DTO;

import com.particle.asset.manager.enums.MovementTypes;
import com.particle.asset.manager.enums.TicketStatuses;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class TicketRequestDto
{
    private String userCode;
    @Enumerated(EnumType.STRING)
    private MovementTypes operation;
    private String assetTypeCode, assetCode, message;
    @Enumerated(EnumType.STRING)
    private TicketStatuses status;
}
