package com.particle.asset.manager.DTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MovementSummaryDTO
{
    private Long id;
    private LocalDateTime date;
    private String movementType, note;
    private AssetSummaryDTO asset;
    private UserSummaryDTO user;

    public MovementSummaryDTO(Long id, LocalDateTime date, String movementType, String note, AssetSummaryDTO asset, UserSummaryDTO user) {
        this.id = id;
        this.date = date;
        this.movementType = movementType;
        this.note = note;
        this.asset = asset;
        this.user = user;
    }

    public MovementSummaryDTO() {

    }
}
