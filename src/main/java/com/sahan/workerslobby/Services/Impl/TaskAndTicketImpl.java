package com.sahan.workerslobby.Services.Impl;

import com.sahan.workerslobby.Entities.TaskAndTicket;
import com.sahan.workerslobby.Exceptions.TaskNotFoundException;
import com.sahan.workerslobby.Repositories.TaskAndTicketRepository;
import com.sahan.workerslobby.Services.TaskAndTicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskAndTicketImpl implements TaskAndTicketService
{
    private TaskAndTicketRepository taskAndTicketRepository;

    @Autowired
    public TaskAndTicketImpl(TaskAndTicketRepository taskAndTicketRepository) {
        this.taskAndTicketRepository = taskAndTicketRepository;
    }

    @Override
    public void createTaskAndTicketForClient(long ticketId, String description)
    {
        TaskAndTicket taskAndTicket = new TaskAndTicket();
        taskAndTicket.setTicketId(ticketId);
        taskAndTicket.setDescription(description);
        taskAndTicketRepository.save(taskAndTicket);
    }

    @Override
    public void createTaskAndTicketForEngineer(long taskId, long ticketId) throws TaskNotFoundException {
        TaskAndTicket taskAndTicket = taskAndTicketRepository.findTaskAndTicketByTicketId(ticketId);
        if (taskAndTicket == null)
            throw new TaskNotFoundException("Invalid ticket Id");
        taskAndTicket.setTaskId(taskId);
        taskAndTicketRepository.save(taskAndTicket);
    }
}
