package com.sahan.workerslobby.Repositories;

import com.sahan.workerslobby.Entities.TaskAndTicket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskAndTicketRepository extends JpaRepository<TaskAndTicket, Long>
{
    TaskAndTicket findTaskAndTicketByTicketId(long ticketId);
}
