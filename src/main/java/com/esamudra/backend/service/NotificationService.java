package com.esamudra.backend.service;

import com.esamudra.backend.model.Notification;
import com.esamudra.backend.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    private int negativeFeedbackCount = 0;

    // ✅ CREATE NOTIFICATION METHOD
    public void createNotification(String title, String message, String type) {
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type); // INFO, WARNING, ALERT, SUCCESS
        notification.setIsRead(false);
        notification.setCreatedDate(LocalDateTime.now());

        notificationRepository.save(notification);
    }

    // ✅ INCREMENT NEGATIVE FEEDBACK COUNT (for dashboard)
    public void incrementNegativeFeedback() {
        this.negativeFeedbackCount++;
        createNotification(
                "Negative Feedback Received",
                "A new negative feedback requires attention. Total: " + negativeFeedbackCount,
                "WARNING"
        );
    }

    // ✅ GET NEGATIVE FEEDBACK COUNT
    public int getNegativeFeedbackCount() {
        return this.negativeFeedbackCount;
    }

    // ✅ RESET NEGATIVE FEEDBACK COUNT
    public void resetNegativeFeedbackCount() {
        this.negativeFeedbackCount = 0;
    }

    // ✅ GET ALL NOTIFICATIONS
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAllByOrderByCreatedDateDesc();
    }

    // ✅ GET UNREAD NOTIFICATIONS
    public List<Notification> getUnreadNotifications() {
        return notificationRepository.findByIsReadFalseOrderByCreatedDateDesc();
    }

    // ✅ MARK NOTIFICATION AS READ
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    // ✅ MARK ALL NOTIFICATIONS AS READ
    public void markAllAsRead() {
        List<Notification> unreadNotifications = getUnreadNotifications();
        for (Notification notification : unreadNotifications) {
            notification.setIsRead(true);
        }
        notificationRepository.saveAll(unreadNotifications);
    }

    // ✅ GET NOTIFICATIONS BY TYPE
    public List<Notification> getNotificationsByType(String type) {
        return notificationRepository.findByTypeOrderByCreatedDateDesc(type);
    }

    // ✅ DELETE NOTIFICATION
    public void deleteNotification(Long notificationId) {
        if (notificationRepository.existsById(notificationId)) {
            notificationRepository.deleteById(notificationId);
        }
    }

    // ✅ CLEAR ALL NOTIFICATIONS
    public void clearAllNotifications() {
        notificationRepository.deleteAll();
        this.negativeFeedbackCount = 0;
    }

    // ✅ GET NOTIFICATION STATISTICS
    public java.util.Map<String, Object> getNotificationStats() {
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("totalNotifications", notificationRepository.count());
        stats.put("unreadNotifications", notificationRepository.countByIsReadFalse());
        stats.put("negativeFeedbackCount", this.negativeFeedbackCount);

        // Count by type
        stats.put("alertCount", notificationRepository.countByType("ALERT"));
        stats.put("warningCount", notificationRepository.countByType("WARNING"));
        stats.put("infoCount", notificationRepository.countByType("INFO"));

        return stats;
    }
}