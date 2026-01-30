package com.particle.asset.manager.models;

//import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Data
public class Movement
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY) // Nasconde l'id nel RequestBody dello Swagger
    private Long id;

    @Column(nullable = false)
    private LocalDateTime date = LocalDateTime.now();

    @Column(name = "movement_type", nullable = false)
    private String movementType;

    private String note;

    @ManyToOne
    @JoinColumn(name = "asset_code", referencedColumnName = "code", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Asset asset;

    // Quando l'asset viene assegnato, lo user_id indica l'utente a cui è stato assegnato
    // Quando l'asset viene riconsegnato, lo user_id indica l'utente che lo ha riconsegnato
    // Quando l'asset viene dismesso, lo user_id indica l'utente che ha effettuato questa operazione
    @ManyToOne
    @JoinColumn(name = "users_id", nullable = false)
    private User users;
}
