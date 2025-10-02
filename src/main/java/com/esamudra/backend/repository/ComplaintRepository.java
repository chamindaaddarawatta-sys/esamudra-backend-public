package com.esamudra.backend.repository;

import com.esamudra.backend.model.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    // Find complaints by category
    List<Complaint> findByCategory(String category);

    // Find complaints by status
    List<Complaint> findByStatus(String status);

    // Find complaints by category and status
    List<Complaint> findByCategoryAndStatus(String category, String status);

    // REMOVED: Old staff assignment methods (no longer valid)
    // List<Complaint> findByAssignedStaffStaffId(Long staffId);
    // List<Complaint> findByAssignedStaffStaffIdAndStatus(Long staffId, String status);

    // NEW: Find complaints by staff via ticket relationship
    @Query("SELECT c FROM Complaint c WHERE c.ticket.staff.staffId = ?1")
    List<Complaint> findByTicketStaffStaffId(Long staffId);

    // NEW: Find complaints by staff and status via ticket
    @Query("SELECT c FROM Complaint c WHERE c.ticket.staff.staffId = ?1 AND c.status = ?2")
    List<Complaint> findByTicketStaffStaffIdAndStatus(Long staffId, String status);

    // NEW: Find complaints without tickets
    @Query("SELECT c FROM Complaint c WHERE c.ticket IS NULL")
    List<Complaint> findComplaintsWithoutTickets();

    // NEW: Find complaints with tickets but no staff assigned
    @Query("SELECT c FROM Complaint c WHERE c.ticket IS NOT NULL AND c.ticket.staff IS NULL")
    List<Complaint> findComplaintsWithTicketsButNoStaff();

    // Count complaints by category
    long countByCategory(String category);

    // Count complaints by status
    long countByStatus(String status);

    // Count unresolved complaints (not RESOLVED)
    long countByStatusNot(String status);

    // NEW: Count complaints without tickets
    @Query("SELECT COUNT(c) FROM Complaint c WHERE c.ticket IS NULL")
    long countComplaintsWithoutTickets();

    // NEW: Count complaints with tickets but no staff
    @Query("SELECT COUNT(c) FROM Complaint c WHERE c.ticket IS NOT NULL AND c.ticket.staff IS NULL")
    long countComplaintsWithTicketsButNoStaff();

    // Get complaint statistics for dashboard
    @Query("SELECT c.category, COUNT(c) FROM Complaint c GROUP BY c.category")
    List<Object[]> getComplaintStatisticsByCategory();

    // Get complaints created within date range
    @Query("SELECT c FROM Complaint c WHERE c.creationDate BETWEEN ?1 AND ?2")
    List<Complaint> findComplaintsByDateRange(java.time.LocalDateTime startDate, java.time.LocalDateTime endDate);

    // Find high priority complaints (based on category or other criteria)
    @Query("SELECT c FROM Complaint c WHERE c.category IN ('BILLING', 'SERVICE') AND c.status NOT IN ('RESOLVED')")
    List<Complaint> findHighPriorityComplaints();

    // Get average resolution time by category
    @Query("SELECT c.category, AVG(TIMESTAMPDIFF(HOUR, c.creationDate, c.resolutionDate)) " +
            "FROM Complaint c WHERE c.resolutionDate IS NOT NULL GROUP BY c.category")
    List<Object[]> getAverageResolutionTimeByCategory();

    // NEW: Get complaints with their ticket information
    @Query("SELECT c FROM Complaint c LEFT JOIN FETCH c.ticket t WHERE c.ticket IS NOT NULL")
    List<Complaint> findComplaintsWithTickets();

    // NEW: Get recent complaints with ticket and staff info
    @Query("SELECT c FROM Complaint c LEFT JOIN FETCH c.ticket t LEFT JOIN FETCH t.staff ORDER BY c.creationDate DESC")
    List<Complaint> findRecentComplaintsWithDetails();
}