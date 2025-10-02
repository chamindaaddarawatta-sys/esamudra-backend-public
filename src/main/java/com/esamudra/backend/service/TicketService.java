package com.esamudra.backend.service;

import com.esamudra.backend.model.Ticket;
import com.esamudra.backend.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    public Ticket createTicket(Ticket ticket) {
        // VALIDATION: Ticket must have staff assigned
        if (ticket.getStaff() == null) {
            throw new IllegalArgumentException("Ticket must have staff assigned. Staff cannot be null.");
        }

        // VALIDATION: Ticket must have complaint assigned
        if (ticket.getComplaint() == null) {
            throw new IllegalArgumentException("Ticket must be associated with a complaint. Complaint cannot be null.");
        }

        // Set default values if not provided
        if (ticket.getStatus() == null) {
            ticket.setStatus("OPEN");
        }
        if (ticket.getPriority() == null) {
            ticket.setPriority("MEDIUM");
        }
        if (ticket.getCreatedDate() == null) {
            ticket.setCreatedDate(LocalDateTime.now());
        }

        System.out.println("=== DEBUG TICKETSERVICE: Creating ticket with staff ID: " +
                ticket.getStaff().getStaffId() + ", complaint ID: " +
                ticket.getComplaint().getComplaintId());

        return ticketRepository.save(ticket);
    }

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    public Optional<Ticket> getTicketById(Long id) {
        return ticketRepository.findById(id);
    }

    public List<Ticket> getTicketsByComplaint(Long complaintId) {
        return ticketRepository.findByComplaintComplaintId(complaintId);
    }

    public List<Ticket> getTicketsByStaff(Long staffId) {
        return ticketRepository.findByStaffStaffId(staffId);
    }

    public List<Ticket> getTicketsByStatus(String status) {
        return ticketRepository.findByStatus(status);
    }

    public List<Ticket> getOpenTickets() {
        return ticketRepository.findByStatusNotIn(List.of("RESOLVED", "CLOSED"));
    }

    public List<Ticket> getUrgentTickets() {
        return ticketRepository.findUrgentTickets();
    }

    public Ticket updateTicketStatus(Long ticketId, String status, String resolutionNotes) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found with id: " + ticketId));

        ticket.setStatus(status);
        ticket.setResolutionNotes(resolutionNotes);

        if ("RESOLVED".equals(status) || "CLOSED".equals(status)) {
            ticket.setResolvedDate(LocalDateTime.now());
        }

        return ticketRepository.save(ticket);
    }

    public Ticket assignTicketToStaff(Long ticketId, Long staffId) {
        // This would require StaffService injection
        // For now, just update status
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        ticket.setStatus("IN_PROGRESS");
        return ticketRepository.save(ticket);
    }

    public void deleteTicket(Long id) {
        ticketRepository.deleteById(id);
    }

    public List<Object[]> getTicketStatusSummary() {
        return ticketRepository.countTicketsByStatus();
    }
}