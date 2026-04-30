package com.particle.asset.manager.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class TicketReply
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String message;

    @ManyToOne
    @JoinColumn(name = "user_code", referencedColumnName = "oid", nullable = false)
    private User users;

    @ManyToOne
    @JoinColumn(name = "ticket_code", referencedColumnName = "code", nullable = false)
    private Ticket tickets;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private LocalDateTime creationDate = LocalDateTime.now();
}
