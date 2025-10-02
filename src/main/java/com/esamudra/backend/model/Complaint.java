package com.esamudra.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "complaint")
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long complaintId;

    @OneToOne
    @JoinColumn(name = "feedback_id", referencedColumnName = "feedbackId")
    private Feedback feedback;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String category; // ROOM, SERVICE, FOOD, FACILITIES, BILLING, OTHER

    private String status; // RECEIVED, IN_PROGRESS, RESOLVED

    private LocalDateTime creationDate;

    private LocalDateTime resolutionDate;

    // FIXED: Use JsonIgnoreProperties to break circular reference but still include ticket
    @OneToOne(mappedBy = "complaint", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("complaint") // This ignores the complaint field in Ticket, not the entire Ticket
    private Ticket ticket;

    // Constructors
    public Complaint() {
        this.creationDate = LocalDateTime.now();
        this.status = "RECEIVED";
    }

    public Complaint(Feedback feedback, String description, String category) {
        this();
        this.feedback = feedback;
        this.description = description;
        this.category = category;
    }

    // Getters and Setters
    public Long getComplaintId() { return complaintId; }
    public void setComplaintId(Long complaintId) { this.complaintId = complaintId; }

    public Feedback getFeedback() { return feedback; }
    public void setFeedback(Feedback feedback) { this.feedback = feedback; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreationDate() { return creationDate; }
    public void setCreationDate(LocalDateTime creationDate) { this.creationDate = creationDate; }

    public LocalDateTime getResolutionDate() { return resolutionDate; }
    public void setResolutionDate(LocalDateTime resolutionDate) { this.resolutionDate = resolutionDate; }

    public Ticket getTicket() { return ticket; }
    public void setTicket(Ticket ticket) { this.ticket = ticket; }
}