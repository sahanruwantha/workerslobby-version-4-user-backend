package com.sahan.workerslobby.Services.Impl;

import com.sahan.workerslobby.Entities.Ticket;
import com.sahan.workerslobby.Entities.User;
import com.sahan.workerslobby.Exceptions.UserNotFoundException;
import com.sahan.workerslobby.Repositories.TicketRepository;
import com.sahan.workerslobby.Services.TaskAndTicketService;
import com.sahan.workerslobby.Services.TicketService;
import com.sahan.workerslobby.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketServiceImpl implements TicketService
{
    private UserService userService;
    private TicketRepository ticketRepository;

    private TaskAndTicketService taskAndTicketService;


    @Autowired
    public TicketServiceImpl(UserService userService, TicketRepository ticketRepository, TaskAndTicketService taskAndTicketService) {
        this.userService = userService;
        this.ticketRepository = ticketRepository;
        this.taskAndTicketService = taskAndTicketService;
    }

    @Override
    public Ticket createTicket(String ticketName,
                               String ticketDescription,
                               long clientId) throws UserNotFoundException
    {
        User client = userService.validateUserById(clientId);
        Ticket ticket = new Ticket();
        ticket.setTicketName(ticketName);
        ticket.setTicketDescription(ticketDescription);
        ticket.setClientId(clientId);
        ticket.setState(true);
        Ticket newTicket = ticketRepository.save(ticket);
        taskAndTicketService.createTaskAndTicketForClient(newTicket.getTicketID(), ticketDescription);
        return null;
    }

    @Override
    public List<Ticket> getPendingTickets(long clientId) throws UserNotFoundException
    {
        userService.validateUserById(clientId);
        return ticketRepository.findTicketsByClientIdAndAndState(clientId, true);
    }

    @Override
    public List<Ticket> getDoneTickets(long clientId) throws UserNotFoundException {
        userService.validateUserById(clientId);
        return ticketRepository.findTicketsByClientIdAndAndState(clientId, false);
    }


}
