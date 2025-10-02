package com.esamudra.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore; // ADD THIS IMPORT

@Entity
@Table(name = "ticket")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ticketId;

    @OneToOne
    @JoinColumn(name = "complaint_id", referencedColumnName = "complaintId")
    @JsonIgnore // ADD THIS ANNOTATION to break circular reference
    private Complaint complaint;

    @ManyToOne
    @JoinColumn(name = "staff_id", referencedColumnName = "staffId")
    private Staff staff;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    private String status; // OPEN, IN_PROGRESS, RESOLVED, CLOSED
    private String priority; // LOW, MEDIUM, HIGH, CRITICAL

    @Column(columnDefinition = "TEXT")
    private String resolutionNotes;

    @Column(name = "resolved_date")
    private LocalDateTime resolvedDate;

    // Constructors
    public Ticket() {
        this.createdDate = LocalDateTime.now();
        this.status = "OPEN";
        this.priority = "MEDIUM";
    }

    public Ticket(Complaint complaint, String priority) {
        this();
        this.complaint = complaint;
        this.priority = priority;
        // Staff will be assigned later by admin
    }

    public Ticket(Complaint complaint, Staff staff, String priority) {
        this();
        this.complaint = complaint;
        this.staff = staff;
        this.priority = priority;
    }

    // Getters and Setters
    public Long getTicketId() { return ticketId; }
    public void setTicketId(Long ticketId) { this.ticketId = ticketId; }

    public Complaint getComplaint() { return complaint; }
    public void setComplaint(Complaint complaint) {
        this.complaint = complaint;
        // Also set the complaint's ticket reference to maintain bidirectional relationship
        if (complaint != null) {
            complaint.setTicket(this);
        }
    }

    public Staff getStaff() { return staff; }
    public void setStaff(Staff staff) { this.staff = staff; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getResolutionNotes() { return resolutionNotes; }
    public void setResolutionNotes(String resolutionNotes) { this.resolutionNotes = resolutionNotes; }

    public LocalDateTime getResolvedDate() { return resolvedDate; }
    public void setResolvedDate(LocalDateTime resolvedDate) { this.resolvedDate = resolvedDate; }
}