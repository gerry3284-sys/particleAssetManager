package com.particle.asset.manager.DTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MovementSummaryResponseDto
{
    private String code;
    private LocalDateTime date;
    private String movementType, note;
    private AssetSummaryDto asset;
    private UserSummaryDto user;

    public MovementSummaryResponseDto(LocalDateTime date, String movementType, String note,
                                      AssetSummaryDto asset, UserSummaryDto user, String code) {
        this.code = code;
        this.date = date;
        this.movementType = movementType;
        this.note = note;
        this.asset = asset;
        this.user = user;
    }

    public MovementSummaryResponseDto() {

    }
}
