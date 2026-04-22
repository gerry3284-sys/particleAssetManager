package com.particle.asset.manager.models;

import com.particle.asset.manager.enums.MovementTypes;
import com.particle.asset.manager.enums.TicketStatuses;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Ticket
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_code", referencedColumnName = "oid", nullable = false)
    private User users;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MovementTypes operation;

    @ManyToOne
    @JoinColumn(name = "type_code", referencedColumnName = "code")
    private AssetType assetType;

    @ManyToOne
    @JoinColumn(name = "asset_code", referencedColumnName = "code")
    private Asset asset;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TicketStatuses status = TicketStatuses.OPEN;

    @Column(nullable = false)
    private LocalDateTime date = LocalDateTime.now();
}
