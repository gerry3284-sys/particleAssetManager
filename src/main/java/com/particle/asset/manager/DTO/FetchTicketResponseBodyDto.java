package com.particle.asset.manager.DTO;

import com.particle.asset.manager.enums.MovementTypes;
import com.particle.asset.manager.enums.TicketStatuses;
import com.particle.asset.manager.enums.TicketsAssetsPriorities;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FetchTicketResponseBodyDto
{
    //@JsonIgnore
    //private Long id;

    private String ticketCode, userCode;
    private String operation;
    private String assetTypeCode, assetCode;
    @Enumerated(EnumType.STRING)
    private TicketStatuses status;
    private TicketsAssetsPriorities priority;
    private LocalDateTime date;
    private boolean userCheckReply, adminCheckReply;
}
