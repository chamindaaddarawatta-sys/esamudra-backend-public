package com.esamudra.backend.repository;

import com.esamudra.backend.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Find all notifications ordered by date (newest first)
    List<Notification> findAllByOrderByCreatedDateDesc();

    // Find unread notifications ordered by date
    List<Notification> findByIsReadFalseOrderByCreatedDateDesc();

    // Find notifications by type
    List<Notification> findByTypeOrderByCreatedDateDesc(String type);

    // Count unread notifications
    Long countByIsReadFalse();

    // Count notifications by type
    Long countByType(String type);

    // Find recent notifications (last 24 hours)
    List<Notification> findByCreatedDateAfter(java.time.LocalDateTime date);
}
