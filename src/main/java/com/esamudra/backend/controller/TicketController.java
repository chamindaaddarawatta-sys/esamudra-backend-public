package com.esamudra.backend.controller;

import com.esamudra.backend.model.Ticket;
import com.esamudra.backend.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tickets")
@CrossOrigin(origins = "http://localhost:3000")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @GetMapping
    public List<Ticket> getAllTickets() {
        return ticketService.getAllTickets();
    }

    @GetMapping("/{id}")
    public Optional<Ticket> getTicketById(@PathVariable Long id) {
        return ticketService.getTicketById(id);
    }

    @GetMapping("/complaint/{complaintId}")
    public List<Ticket> getTicketsByComplaint(@PathVariable Long complaintId) {
        return ticketService.getTicketsByComplaint(complaintId);
    }

    @GetMapping("/staff/{staffId}")
    public List<Ticket> getTicketsByStaff(@PathVariable Long staffId) {
        return ticketService.getTicketsByStaff(staffId);
    }

    @GetMapping("/status/{status}")
    public List<Ticket> getTicketsByStatus(@PathVariable String status) {
        return ticketService.getTicketsByStatus(status);
    }

    @GetMapping("/open")
    public List<Ticket> getOpenTickets() {
        return ticketService.getOpenTickets();
    }

    @GetMapping("/urgent")
    public List<Ticket> getUrgentTickets() {
        return ticketService.getUrgentTickets();
    }

    @GetMapping("/summary")
    public List<Object[]> getTicketStatusSummary() {
        return ticketService.getTicketStatusSummary();
    }

    @PostMapping
    public Ticket createTicket(@RequestBody Ticket ticket) {
        return ticketService.createTicket(ticket);
    }

    @PutMapping("/{id}/status")
    public Ticket updateTicketStatus(@PathVariable Long id,
                                     @RequestParam String status,
                                     @RequestParam(required = false) String resolutionNotes) {
        return ticketService.updateTicketStatus(id, status, resolutionNotes);
    }

    @PutMapping("/{id}/assign/{staffId}")
    public Ticket assignTicketToStaff(@PathVariable Long id, @PathVariable Long staffId) {
        return ticketService.assignTicketToStaff(id, staffId);
    }

    @DeleteMapping("/{id}")
    public void deleteTicket(@PathVariable Long id) {
        ticketService.deleteTicket(id);
    }
}
