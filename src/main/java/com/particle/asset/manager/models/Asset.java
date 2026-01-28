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
public class Asset
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY) // Nasconde l'id nel RequestBody dello Swagger
    private Long id;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String model;

    @Column(unique = true, nullable = false)
    private String serialNumber;

    private String note;

    @Column(nullable = false)
    private LocalDateTime creationDate = LocalDateTime.now();

    private LocalDateTime updateDate;

    private Short ram; // RAM in Giga

    private String hardDisk; // *tipo Hard Disk* + spazio

    @ManyToOne
    @JoinColumn(name = "business_unit_id", nullable = false)
    private BusinessUnit businessUnit;

    @ManyToOne
    @JoinColumn(name = "type_id", nullable = false)
    private AssetType assetType;

    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private AssetStatusType assetStatusType;

    @Column(nullable = false, unique = true)
    private String code; // Codice univoco per ogni record "primi due caratteri + id"

    // "mappedBy = *nome*" --> nome del lato di chi passa la chiave
    // "cascade = CascadeType.ALL" --> salva/aggiorna/cancella anche i figli
    // "orphanRemoval = true" --> se rimuovo una bu, cancella anche il record ad esso collegato
    /*@OneToMany(mappedBy = "asset", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Movement> movement = new ArrayList<>();*/

    /*@JsonIgnore
    public List<Movement> getMovements() {
        return movement;
    }

    public void setMovements(List<Movement> movements) {
        this.movement = movements;
    }*/
}
