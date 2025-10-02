package com.esamudra.backend.service;

import org.springframework.transaction.annotation.Transactional;
import com.esamudra.backend.model.Complaint;
import com.esamudra.backend.model.Feedback;
import com.esamudra.backend.model.Staff;
import com.esamudra.backend.model.Ticket;
import com.esamudra.backend.repository.ComplaintRepository;
import com.esamudra.backend.repository.FeedbackRepository;
import com.esamudra.backend.repository.StaffRepository;
import com.esamudra.backend.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ComplaintService {

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private TicketRepository ticketRepository;

    public Complaint createComplaint(Complaint complaint) {
        Complaint savedComplaint = complaintRepository.save(complaint);
        createTicketForComplaint(savedComplaint);
        return savedComplaint;
    }

    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAll();
    }

    public Complaint updateComplaint(Long id, Complaint complaintDetails) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Complaint not found with id: " + id));

        complaint.setStatus(complaintDetails.getStatus());
        complaint.setDescription(complaintDetails.getDescription());
        complaint.setCategory(complaintDetails.getCategory());

        if ("RESOLVED".equals(complaintDetails.getStatus())) {
            complaint.setResolutionDate(LocalDateTime.now());
        }

        return complaintRepository.save(complaint);
    }

    public Complaint updateComplaintStatus(Long id, String status) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Complaint not found with id: " + id));

        complaint.setStatus(status);

        if ("RESOLVED".equals(status)) {
            complaint.setResolutionDate(LocalDateTime.now());
        }

        return complaintRepository.save(complaint);
    }

    // FIXED METHOD: Now assigns staff via TICKET instead of directly to complaint
    public Complaint assignComplaintToStaff(Long complaintId, Long staffId) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found with id: " + complaintId));

        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found with id: " + staffId));

        // FIX: Assign staff via ticket instead of directly to complaint
        Ticket ticket = complaint.getTicket();
        if (ticket != null) {
            ticket.setStaff(staff);
            ticket.setStatus("IN_PROGRESS");
            ticketRepository.save(ticket);

            // Also update complaint status
            complaint.setStatus("IN_PROGRESS");
            System.out.println("=== DEBUG: Staff " + staff.getName() + " assigned to ticket " + ticket.getTicketId());
        } else {
            System.out.println("=== DEBUG: No ticket found for complaint " + complaintId + ", creating one...");
            // Create a ticket if none exists
            createTicketForComplaintWithStaff(complaint, staff);
        }

        return complaintRepository.save(complaint);
    }

    public Complaint getComplaintById(Long id) {
        return complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Complaint not found with id: " + id));
    }

    public List<Staff> getAllStaff() {
        return staffRepository.findAll();
    }

    // NEW METHOD: Auto-create complaint from negative feedback
    public Complaint createComplaintFromFeedback(Feedback feedback) {
        Complaint complaint = new Complaint();
        complaint.setFeedback(feedback);
        complaint.setDescription("Auto-generated from negative feedback: " +
                (feedback.getComments() != null ? feedback.getComments() : "No comments"));

        complaint.setCategory(detectComplaintCategory(feedback.getComments()));
        complaint.setStatus("RECEIVED");
        complaint.setCreationDate(LocalDateTime.now());

        Complaint savedComplaint = complaintRepository.save(complaint);
        createTicketForComplaint(savedComplaint);
        return savedComplaint;
    }

    // NEW METHOD: Create ticket for complaint (WITH EXTENSIVE DEBUGGING)
    private void createTicketForComplaint(Complaint complaint) {
        try {
            System.out.println("=== DEBUG: Starting ticket creation for complaint ID: " + complaint.getComplaintId());

            // DEBUG: Check staff repository
            List<Staff> allStaff = staffRepository.findAll();
            System.out.println("=== DEBUG: Total staff found: " + allStaff.size());

            if (allStaff.isEmpty()) {
                System.out.println("=== DEBUG: No staff available, creating ticket without staff assignment");
                // Create ticket without staff (staff_id will be NULL)
                createTicketWithoutStaff(complaint);
                return;
            }

            // DEBUG: List all staff
            for (Staff staff : allStaff) {
                System.out.println("=== DEBUG: Available staff - ID: " + staff.getStaffId() + ", Name: " + staff.getName());
            }

            // Try to get staff with ID 1, if not use first available
            Staff defaultStaff = staffRepository.findById(1L)
                    .orElse(allStaff.get(0));

            System.out.println("=== DEBUG: Selected staff: " + defaultStaff.getName() + " (ID: " + defaultStaff.getStaffId() + ")");

            // Create ticket WITH staff assignment
            createTicketWithStaff(complaint, defaultStaff);

        } catch (Exception e) {
            System.out.println("=== DEBUG: Error creating ticket with staff, trying without staff...");
            e.printStackTrace();
            // Fallback: create ticket without staff
            createTicketWithoutStaff(complaint);
        }
    }
    // NEW METHOD: Fix ticket-complaint relationships for existing data
    @Transactional
    public String fixTicketComplaintRelationships() {
        System.out.println("=== DEBUG: Fixing ticket-complaint relationships ===");

        List<Complaint> complaints = complaintRepository.findAll();
        List<Ticket> tickets = ticketRepository.findAll();

        int fixedCount = 0;

        for (Complaint complaint : complaints) {
            // Find ticket for this complaint
            Ticket matchingTicket = tickets.stream()
                    .filter(ticket -> ticket.getComplaint() != null &&
                            ticket.getComplaint().getComplaintId().equals(complaint.getComplaintId()))
                    .findFirst()
                    .orElse(null);

            if (matchingTicket != null && complaint.getTicket() == null) {
                // Link the complaint to the ticket
                complaint.setTicket(matchingTicket);
                complaintRepository.save(complaint);
                fixedCount++;
                System.out.println("=== DEBUG: Linked complaint " + complaint.getComplaintId() + " to ticket " + matchingTicket.getTicketId());
            }
        }

        return "Fixed " + fixedCount + " ticket-complaint relationships";
    }
    // HELPER: Create ticket with staff assignment
    private void createTicketWithStaff(Complaint complaint, Staff staff) {
        Ticket ticket = new Ticket();
        ticket.setComplaint(complaint);
        ticket.setStaff(staff); // Set the staff
        ticket.setStatus("OPEN");

        // Safe category and description handling
        String category = complaint.getCategory() != null ? complaint.getCategory() : "OTHER";
        String description = complaint.getDescription() != null ? complaint.getDescription() : "";
        ticket.setPriority(determinePriority(category, description));

        ticket.setCreatedDate(LocalDateTime.now());

        // DEBUG: Check ticket object before saving
        System.out.println("=== DEBUG: Ticket staff ID: " + (ticket.getStaff() != null ? ticket.getStaff().getStaffId() : "NULL"));
        System.out.println("=== DEBUG: Ticket complaint ID: " + (ticket.getComplaint() != null ? ticket.getComplaint().getComplaintId() : "NULL"));
        System.out.println("=== DEBUG: About to save ticket...");

        Ticket savedTicket = ticketRepository.save(ticket);
        System.out.println("=== DEBUG: Ticket created successfully with ID: " + savedTicket.getTicketId());

        // Update complaint with ticket reference
        complaint.setTicket(savedTicket);
        complaintRepository.save(complaint);
    }

    // HELPER: Create ticket without staff assignment (staff_id will be NULL)
    private void createTicketWithoutStaff(Complaint complaint) {
        try {
            System.out.println("=== DEBUG: Creating ticket without staff assignment...");

            Ticket ticket = new Ticket();
            ticket.setComplaint(complaint);
            ticket.setStaff(null); // Explicitly set to null
            ticket.setStatus("OPEN");

            String category = complaint.getCategory() != null ? complaint.getCategory() : "OTHER";
            String description = complaint.getDescription() != null ? complaint.getDescription() : "";
            ticket.setPriority(determinePriority(category, description));

            ticket.setCreatedDate(LocalDateTime.now());

            System.out.println("=== DEBUG: Ticket without staff - ready to save...");
            Ticket savedTicket = ticketRepository.save(ticket);
            System.out.println("=== DEBUG: Ticket created successfully without staff, ID: " + savedTicket.getTicketId());

            // Update complaint with ticket reference
            complaint.setTicket(savedTicket);
            complaintRepository.save(complaint);

        } catch (Exception e) {
            System.out.println("=== DEBUG: ERROR creating ticket without staff: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // HELPER: Create ticket with specific staff (for assignment)
    private void createTicketForComplaintWithStaff(Complaint complaint, Staff staff) {
        createTicketWithStaff(complaint, staff);
    }

    // NEW METHOD: Determine ticket priority
    private String determinePriority(String category, String description) {
        if (description == null) return "MEDIUM";

        String lowerDesc = description.toLowerCase();

        if (lowerDesc.contains("urgent") || lowerDesc.contains("emergency") ||
                lowerDesc.contains("immediate") || lowerDesc.contains("critical")) {
            return "HIGH";
        }

        if ("SERVICE".equals(category) || "BILLING".equals(category)) {
            return "MEDIUM";
        }

        if ("FACILITIES".equals(category) || "OTHER".equals(category)) {
            return "LOW";
        }

        return "MEDIUM";
    }

    // NEW METHOD: Detect complaint category
    private String detectComplaintCategory(String comments) {
        if (comments == null || comments.trim().isEmpty()) {
            return "OTHER";
        }

        String lowerComments = comments.toLowerCase();

        if (lowerComments.contains("room") || lowerComments.contains("clean") ||
                lowerComments.contains("bed") || lowerComments.contains("toilet") ||
                lowerComments.contains("bathroom") || lowerComments.contains("ac") ||
                lowerComments.contains("air conditioner") || lowerComments.contains("linen") ||
                lowerComments.contains("towel") || lowerComments.contains("housekeeping")) {
            return "ROOM";
        } else if (lowerComments.contains("food") || lowerComments.contains("restaurant") ||
                lowerComments.contains("meal") || lowerComments.contains("breakfast") ||
                lowerComments.contains("dinner") || lowerComments.contains("lunch") ||
                lowerComments.contains("taste") || lowerComments.contains("menu") ||
                lowerComments.contains("buffet") || lowerComments.contains("cuisine") ||
                lowerComments.contains("restaurant") || lowerComments.contains("bar")) {
            return "FOOD";
        } else if (lowerComments.contains("staff") || lowerComments.contains("service") ||
                lowerComments.contains("rude") || lowerComments.contains("friendly") ||
                lowerComments.contains("helpful") || lowerComments.contains("attitude") ||
                lowerComments.contains("reception") || lowerComments.contains("manager") ||
                lowerComments.contains("waiter") || lowerComments.contains("front desk") ||
                lowerComments.contains("unprofessional") || lowerComments.contains("ignored")) {
            return "SERVICE";
        } else if (lowerComments.contains("facility") || lowerComments.contains("pool") ||
                lowerComments.contains("gym") || lowerComments.contains("spa") ||
                lowerComments.contains("wi-fi") || lowerComments.contains("wifi") ||
                lowerComments.contains("internet") || lowerComments.contains("parking") ||
                lowerComments.contains("elevator") || lowerComments.contains("lift") ||
                lowerComments.contains("swimming") || lowerComments.contains("maintenance") ||
                lowerComments.contains("equipment") || lowerComments.contains("lobby")) {
            return "FACILITIES";
        } else if (lowerComments.contains("bill") || lowerComments.contains("price") ||
                lowerComments.contains("charge") || lowerComments.contains("payment") ||
                lowerComments.contains("cost") || lowerComments.contains("expensive") ||
                lowerComments.contains("overcharge") || lowerComments.contains("invoice") ||
                lowerComments.contains("rate") || lowerComments.contains("fee") ||
                lowerComments.contains("billing") || lowerComments.contains("refund")) {
            return "BILLING";
        } else {
            return "OTHER";
        }
    }

    // REST OF YOUR EXISTING METHODS...
    public List<Complaint> getComplaintsByCategory(String category) {
        return complaintRepository.findByCategory(category);
    }

    public long getComplaintCountByCategory(String category) {
        return complaintRepository.countByCategory(category);
    }
}