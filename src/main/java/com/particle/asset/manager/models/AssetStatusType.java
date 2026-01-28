package com.particle.asset.manager.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class AssetStatusType
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private LocalDateTime creationDate = LocalDateTime.now();

    private LocalDateTime updateDate;

    @Column(nullable = false, unique = true)
    private String code; // Codice univoco per ogni record "primi due caratteri + id"
}
