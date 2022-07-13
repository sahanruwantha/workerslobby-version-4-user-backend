package com.sahan.workerslobby.Services;

import com.sahan.workerslobby.Entities.Ticket;
import com.sahan.workerslobby.Exceptions.UserNotFoundException;

import java.io.IOException;
import java.util.List;

public interface TicketService
{
    Ticket createTicket(String ticketName, String ticketDescription, long clientId) throws UserNotFoundException;

    List<Ticket> getPendingTickets(long clientId) throws UserNotFoundException;

    List<Ticket> getDoneTickets(long clientId) throws UserNotFoundException;
}
