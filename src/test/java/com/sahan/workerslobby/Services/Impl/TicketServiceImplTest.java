package com.sahan.workerslobby.Services.Impl;

import com.sahan.workerslobby.Exceptions.UserNotFoundException;
import com.sahan.workerslobby.Services.TicketService;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TicketServiceImplTest
{
    private TicketService ticketService;

    @Autowired
    public TicketServiceImplTest(TicketService ticketService)
    {
        this.ticketService = ticketService;
    }

    @Test
    void createTicket() throws UserNotFoundException {
        ticketService.createTicket("rebel fleet noticed ", "seen a rebel fleet by moff tarkin", 1);
    }
}