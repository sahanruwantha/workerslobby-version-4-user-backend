package com.sahan.workerslobby.Services.Impl;


import com.sahan.workerslobby.Entities.Task;
import com.sahan.workerslobby.Entities.Ticket;
import com.sahan.workerslobby.Entities.User;
import com.sahan.workerslobby.Exceptions.TicketNotFoundException;
import com.sahan.workerslobby.Exceptions.UserNotFoundException;
import com.sahan.workerslobby.Repositories.TaskRepository;
import com.sahan.workerslobby.Repositories.TicketRepository;
import com.sahan.workerslobby.Services.TaskAndTicketService;
import com.sahan.workerslobby.Services.TaskService;
import com.sahan.workerslobby.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskServiceImpl implements TaskService
{
    private UserService userService;
    private TaskRepository taskRepository;

    private TicketRepository ticketRepository;

    @Autowired
    public TaskServiceImpl(UserService userService, TaskRepository taskRepository, TicketRepository ticketRepository) {
        this.userService = userService;
        this.taskRepository = taskRepository;
        this.ticketRepository = ticketRepository;
    }



    @Override
    public Task createTask(long engineerId,
                           String description,
                           long ticketId) throws UserNotFoundException, TicketNotFoundException {
        User user = userService.validateUserById(engineerId);
        validateTicket(ticketId);
        Task task = new Task();
        task.setEngineerId(engineerId);
        task.setDescription(description);
        task.setState(true);
        //task.setTicketId(ticketId);
        Task newTask = taskRepository.save(task);
        return newTask;
    }

    @Override
    public List<Task> getPendingTasks(long engineerId) throws UserNotFoundException
    {
        userService.validateUserById(engineerId);
        return taskRepository.findTasksByEngineerIdAndState(engineerId, true);
    }

    @Override
    public List<Task> getDoneTasks(long engineerId) throws UserNotFoundException
    {
        userService.validateUserById(engineerId);
        return taskRepository.findTasksByEngineerIdAndState(engineerId, false);
    }

    private void validateTicket(long ticketId) throws TicketNotFoundException {
        Ticket ticket = ticketRepository.findById(ticketId).orElse(null);
        if (ticket == null)
            throw new TicketNotFoundException("ivalid Ticket Id");
    }
}
