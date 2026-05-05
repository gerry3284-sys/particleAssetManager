package com.particle.asset.manager.DTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TicketReplyResponseDto
{
    private String ticket, user, message, status;
    private LocalDateTime date;
}