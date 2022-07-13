package com.sahan.workerslobby.Services;

import com.sahan.workerslobby.Entities.Task;
import com.sahan.workerslobby.Exceptions.*;

import java.io.IOException;
import java.util.List;

public interface TaskService
{
    Task createTask(long engineerId, String description,long ticketId ) throws UserNotFoundException,
            TaskAndTicketNotFoundException, TaskNotFoundException, TicketNotFoundException;

    List<Task> getPendingTasks(long engineerId) throws UserNotFoundException;

    List<Task> getDoneTasks(long engineerId) throws UserNotFoundException;
}
