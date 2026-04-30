package com.particle.asset.manager.DTO;

import lombok.Data;

@Data
public class TicketReplyRequestDto
{
    String message, oid;
    boolean closed;
}
