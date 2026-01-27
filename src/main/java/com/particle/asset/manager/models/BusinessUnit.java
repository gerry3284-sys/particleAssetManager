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
public class BusinessUnit
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

    @Column(nullable = false)
    private LocalDateTime updateDate = LocalDateTime.now();

    // "mappedBy = *nome*" --> nome del lato di chi passa la chiave
    // "cascade = CascadeType.ALL" --> salva/aggiorna/cancella anche i figli
    // "orphanRemoval = true" --> se rimuovo una bu, cancella anche il record ad esso collegato
    /*@OneToMany(mappedBy = "businessUnit", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<User> user = new ArrayList<>();*/

//    @OneToMany(mappedBy = "businessUnit", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Asset> asset = new ArrayList<>();

    /*@JsonIgnore
    public List<User> getUsers() {
        return user;
    }

    public void setUsers(List<User> users) {
        this.user = users;
    }*/

    /*@JsonIgnore
    public List<Asset> getAssets() {
        return asset;
    }

    public void setAssets(List<Asset> assets) {
        this.asset = assets;
    }*/
}
