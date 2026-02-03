package com.particle.asset.manager.models;

//import com.fasterxml.jackson.annotation.JsonIgnore;
import com.particle.asset.manager.enumerations.UserType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;

//import java.util.ArrayList;
//import java.util.List;

@Entity
@Table(name = "users")
@Data
public class User
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY) // Nasconde l'id nel RequestBody dello Swagger
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String surname;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserType userType;

    @ManyToOne
    @JoinColumn(name = "business_unit_code", referencedColumnName = "code", nullable = false)
    private BusinessUnit businessUnit;

    // "mappedBy = *nome*" --> nome del lato di chi passa la chiave
    // "cascade = CascadeType.ALL" --> salva/aggiorna/cancella anche i figli
    // "orphanRemoval = true" --> se rimuovo una bu, cancella anche il record ad esso collegato
    /*@OneToMany(mappedBy = "users", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Movement> movements = new ArrayList<>();*/


}
