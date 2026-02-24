package com.particle.asset.manager.DTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MovementSummaryResponseDto
{
    private Long id;
    private LocalDateTime date;
    private String movementType, note;
    private AssetSummaryDto asset;
    private UserSummaryDto user;

    public MovementSummaryResponseDto(Long id, LocalDateTime date, String movementType, String note, AssetSummaryDto asset, UserSummaryDto user) {
        this.id = id;
        this.date = date;
        this.movementType = movementType;
        this.note = note;
        this.asset = asset;
        this.user = user;
    }

    public MovementSummaryResponseDto() {

    }
}
