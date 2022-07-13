package com.sahan.workerslobby.Services;

import com.sahan.workerslobby.Exceptions.TaskAndTicketNotFoundException;
import com.sahan.workerslobby.Exceptions.TaskNotFoundException;

public interface TaskAndTicketService
{
    void createTaskAndTicketForClient(long ticketId, String Description);

    void createTaskAndTicketForEngineer(long taskId, long ticketId) throws TaskAndTicketNotFoundException, TaskNotFoundException;
}
