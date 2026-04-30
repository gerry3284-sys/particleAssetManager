package com.particle.asset.manager.DTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TicketReplyResponseDto
{
    private String ticket, user, message, status;
    private LocalDate date;
}