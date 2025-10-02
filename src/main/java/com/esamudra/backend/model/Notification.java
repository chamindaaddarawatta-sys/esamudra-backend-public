package com.esamudra.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    private String title;
    private String message;
    private String type; // INFO, WARNING, ALERT, SUCCESS

    @Column(name = "is_read")
    private Boolean isRead = false;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    // Constructors
    public Notification() {
        this.createdDate = LocalDateTime.now();
        this.isRead = false;
    }

    public Notification(String title, String message, String type) {
        this();
        this.title = title;
        this.message = message;
        this.type = type;
    }

    // Getters and Setters
    public Long getNotificationId() { return notificationId; }
    public void setNotificationId(Long notificationId) { this.notificationId = notificationId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }
}