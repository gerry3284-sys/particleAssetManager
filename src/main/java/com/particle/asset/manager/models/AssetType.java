package com.particle.asset.manager.models;

//import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

//import java.util.ArrayList;
//import java.util.List;

@Entity
@Data
public class AssetType
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY) // Nasconde l'id nel RequestBody dello Swagger
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private LocalDateTime creationDate = LocalDateTime.now();

    private LocalDateTime updateDate;

    @Column(nullable = false, unique = true)
    private String code; // Codice univoco per ogni record "primi due caratteri + id"

    /*@OneToMany(mappedBy = "type", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Asset> asset = new ArrayList<>();*/
}
