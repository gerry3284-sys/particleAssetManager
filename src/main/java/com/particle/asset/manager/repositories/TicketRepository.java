package com.particle.asset.manager.repositories;

import com.particle.asset.manager.models.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long>
{

}
