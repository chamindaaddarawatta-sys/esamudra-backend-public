package com.esamudra.backend.controller;

import com.esamudra.backend.model.Notification;
import com.esamudra.backend.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "http://localhost:3000")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public Map<String, Object> getNotifications() {
        return notificationService.getNotificationStats();
    }

    @GetMapping("/all")
    public List<Notification> getAllNotifications() {
        return notificationService.getAllNotifications();
    }

    @GetMapping("/unread")
    public List<Notification> getUnreadNotifications() {
        return notificationService.getUnreadNotifications();
    }

    @PostMapping("/create")
    public void createNotification(@RequestParam String title,
                                   @RequestParam String message,
                                   @RequestParam String type) {
        notificationService.createNotification(title, message, type);
    }

    @PutMapping("/{id}/read")
    public void markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
    }

    @PostMapping("/read-all")
    public void markAllAsRead() {
        notificationService.markAllAsRead();
    }

    @PostMapping("/reset")
    public void resetNotifications() {
        notificationService.resetNegativeFeedbackCount();
    }

    @DeleteMapping("/{id}")
    public void deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
    }

    @DeleteMapping("/clear")
    public void clearAllNotifications() {
        notificationService.clearAllNotifications();
    }
}