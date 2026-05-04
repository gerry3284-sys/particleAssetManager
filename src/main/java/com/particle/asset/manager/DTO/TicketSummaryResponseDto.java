package com.particle.asset.manager.DTO;

import com.particle.asset.manager.enums.MovementTypes;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TicketSummaryResponseDto
{
    private String ticketCode, user;
    @Enumerated(EnumType.STRING)
    private MovementTypes operation;
    private String assetTypeCode, assetCode;
    private LocalDate date;
}
