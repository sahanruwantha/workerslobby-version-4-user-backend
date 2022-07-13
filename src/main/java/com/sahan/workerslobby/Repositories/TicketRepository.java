package com.sahan.workerslobby.Repositories;

import com.sahan.workerslobby.Entities.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long>
{
    List<Ticket> findTicketsByClientIdAndAndState(long clientId, boolean state);
}
