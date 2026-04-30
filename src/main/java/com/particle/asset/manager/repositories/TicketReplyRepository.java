package com.particle.asset.manager.repositories;

import com.particle.asset.manager.models.TicketReply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TicketReplyRepository extends JpaRepository <TicketReply, Long>
{
    List<TicketReply> findByTicketsCode(String ticketCode);

    Optional<TicketReply> findFirstByTicketsCodeOrderByCreationDateDesc(String ticketCode);
}
