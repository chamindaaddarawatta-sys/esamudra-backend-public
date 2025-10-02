package com.esamudra.backend.controller;

import com.esamudra.backend.model.Complaint;
import com.esamudra.backend.service.ComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/complaints")
@CrossOrigin(origins = "http://localhost:3000")
public class ComplaintController {

    @Autowired
    private ComplaintService complaintService;

    @PostMapping
    public Complaint createComplaint(@RequestBody Complaint complaint) {
        return complaintService.createComplaint(complaint);
    }

    @GetMapping
    public List<Complaint> getAllComplaints() {
        return complaintService.getAllComplaints();
    }

    @GetMapping("/{id}")
    public Complaint getComplaintById(@PathVariable Long id) {
        return complaintService.getComplaintById(id);
    }

    // UPDATED: Use the correct method that includes category
    @PutMapping("/{id}")
    public Complaint updateComplaint(@PathVariable Long id, @RequestBody Complaint complaintDetails) {
        return complaintService.updateComplaint(id, complaintDetails);
    }

    @PutMapping("/{id}/status")
    public Complaint updateComplaintStatus(@PathVariable Long id, @RequestBody String status) {
        return complaintService.updateComplaintStatus(id, status);
    }

    @PutMapping("/{complaintId}/assign/{staffId}")
    public Complaint assignComplaintToStaff(@PathVariable Long complaintId, @PathVariable Long staffId) {
        return complaintService.assignComplaintToStaff(complaintId, staffId);
    }

    // NEW ENDPOINTS FOR CATEGORY SUPPORT

    @GetMapping("/category/{category}")
    public List<Complaint> getComplaintsByCategory(@PathVariable String category) {
        return complaintService.getComplaintsByCategory(category);
    }

    @GetMapping("/category/{category}/count")
    public long getComplaintCountByCategory(@PathVariable String category) {
        return complaintService.getComplaintCountByCategory(category);
    }

    @GetMapping("/status/{status}")
    public List<Complaint> getComplaintsByStatus(@PathVariable String status) {
        return complaintService.getAllComplaints().stream()
                .filter(c -> c.getStatus().equalsIgnoreCase(status))
                .toList();
    }

    // FIXED: Now checks staff assignment via TICKET instead of direct assignment
    @GetMapping("/staff/{staffId}")
    public List<Complaint> getComplaintsByStaff(@PathVariable Long staffId) {
        return complaintService.getAllComplaints().stream()
                .filter(c -> c.getTicket() != null &&
                        c.getTicket().getStaff() != null &&
                        c.getTicket().getStaff().getStaffId().equals(staffId))
                .toList();
    }

    @GetMapping("/unresolved")
    public List<Complaint> getUnresolvedComplaints() {
        return complaintService.getAllComplaints().stream()
                .filter(c -> !"RESOLVED".equals(c.getStatus()))
                .toList();
    }

    @PostMapping("/from-feedback/{feedbackId}")
    public Complaint createComplaintFromFeedback(@PathVariable Long feedbackId) {
        // You'll need to implement this method in ComplaintService
        // This would fetch the feedback and auto-create a complaint
        return complaintService.createComplaint(new Complaint());
    }

    @GetMapping("/stats/category")
    public List<Object[]> getComplaintStatsByCategory() {
        // You might need to add this method to ComplaintService
        return List.of(); // Placeholder - implement based on your needs
    }

    // FIXED: Advanced search with TICKET-based staff filtering
    @GetMapping("/search")
    public List<Complaint> searchComplaints(@RequestParam(required = false) String category,
                                            @RequestParam(required = false) String status,
                                            @RequestParam(required = false) Long staffId) {
        return complaintService.getAllComplaints().stream()
                .filter(c -> category == null || (c.getCategory() != null && c.getCategory().equalsIgnoreCase(category)))
                .filter(c -> status == null || c.getStatus().equalsIgnoreCase(status))
                .filter(c -> staffId == null ||
                        (c.getTicket() != null &&
                                c.getTicket().getStaff() != null &&
                                c.getTicket().getStaff().getStaffId().equals(staffId)))
                .toList();
    }

    // NEW ENDPOINT: Get complaints without assigned staff (no ticket or ticket without staff)
    @GetMapping("/unassigned")
    public List<Complaint> getUnassignedComplaints() {
        return complaintService.getAllComplaints().stream()
                .filter(c -> c.getTicket() == null ||
                        c.getTicket().getStaff() == null)
                .toList();
    }

    // NEW ENDPOINT: Get complaints with tickets
    @GetMapping("/with-tickets")
    public List<Complaint> getComplaintsWithTickets() {
        return complaintService.getAllComplaints().stream()
                .filter(c -> c.getTicket() != null)
                .toList();
    }
    // NEW ENDPOINT: Fix ticket relationships
    @PostMapping("/fix-relationships")
    public String fixRelationships() {
        return complaintService.fixTicketComplaintRelationships();
    }
    // NEW ENDPOINT: Get complaints without tickets
    @GetMapping("/without-tickets")
    public List<Complaint> getComplaintsWithoutTickets() {
        return complaintService.getAllComplaints().stream()
                .filter(c -> c.getTicket() == null)
                .toList();
    }
}