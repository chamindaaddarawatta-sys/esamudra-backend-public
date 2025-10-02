package com.esamudra.backend.repository;

import com.esamudra.backend.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    // Find tickets by complaint
    List<Ticket> findByComplaintComplaintId(Long complaintId);

    // Find tickets by assigned staff
    List<Ticket> findByStaffStaffId(Long staffId);

    // Find tickets by status
    List<Ticket> findByStatus(String status);

    // Find tickets by priority
    List<Ticket> findByPriority(String priority);

    // Find open tickets (not resolved/closed)
    List<Ticket> findByStatusNotIn(List<String> statuses);

    // Find tickets by staff and status
    List<Ticket> findByStaffStaffIdAndStatus(Long staffId, String status);

    // Count tickets by status for dashboard
    @Query("SELECT t.status, COUNT(t) FROM Ticket t GROUP BY t.status")
    List<Object[]> countTicketsByStatus();

    // Find high priority open tickets
    @Query("SELECT t FROM Ticket t WHERE t.priority IN ('HIGH', 'CRITICAL') AND t.status NOT IN ('RESOLVED', 'CLOSED')")
    List<Ticket> findUrgentTickets();
}