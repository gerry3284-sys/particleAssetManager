package com.particle.asset.manager.repositories;

import com.particle.asset.manager.models.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long>
{
    Optional<Ticket> findByCode(String code);
}
