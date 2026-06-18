package com.particle.asset.manager.DTO;

import lombok.Data;

@Data
public class TicketRequestDto
{
    private String userCode;
    private String operation;
    private String assetTypeCode, assetCode, message;
    private boolean clientProject;
    /*@Enumerated(EnumType.STRING)
    private TicketsAssetsPriorities priority;*/
}