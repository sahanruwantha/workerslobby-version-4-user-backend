package com.sahan.workerslobby.Controllers;

import com.sahan.workerslobby.Entities.Ticket;
import com.sahan.workerslobby.Exceptions.UserNotFoundException;
import com.sahan.workerslobby.Services.TicketService;
import com.sahan.workerslobby.Utils.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RestController
@RequestMapping("/ticket")
public class TicketController
{

    private TicketService ticketService;

    @Autowired
    public TicketController(TicketService ticketService)
    {
        this.ticketService = ticketService;
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('ticket:create')")
    public ResponseEntity<HttpResponse> createTicket(@RequestBody Ticket ticket) throws UserNotFoundException {
        log.info(String.valueOf(ticket));
        ticketService.createTicket(ticket.getTicketName(),
                ticket.getTicketDescription(),
                ticket.getClientId());
        return response(CREATED, "ticketCreated Successfully");
    }

    @GetMapping("/{clientId}/pending")
    public ResponseEntity<List<Ticket>> getMyPendingTickets(@PathVariable("clientId") long clientId) throws UserNotFoundException {
        List<Ticket> pendingTickets = ticketService.getPendingTickets(clientId);
        return new ResponseEntity<>(pendingTickets, OK);
    }

    @GetMapping("/{clientId}/done")
    public ResponseEntity<List<Ticket>> getMyDoneTickets(@PathVariable("clientId") long clientId) throws UserNotFoundException {
        List<Ticket> doneTickets = ticketService.getDoneTickets(clientId);
        return new ResponseEntity<>(doneTickets, OK);
    }


    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message)
    {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(),
                httpStatus, httpStatus.getReasonPhrase().toUpperCase(), message.toUpperCase()), httpStatus);
    }
}
