package com.esamudra.backend.repository;

import com.esamudra.backend.model.Stay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface StayRepository extends JpaRepository<Stay, Long> {

    // Find stays by customer
    List<Stay> findByCustomerId(Long customerId);

    // Find active stays (check-out date is in future)
    List<Stay> findByCheckOutDateAfter(LocalDate date);

    // Find stays within date range
    @Query("SELECT s FROM Stay s WHERE s.checkInDate BETWEEN ?1 AND ?2 OR s.checkOutDate BETWEEN ?1 AND ?2")
    List<Stay> findStaysInDateRange(LocalDate startDate, LocalDate endDate);

    // Find stay by booking reference
    Optional<Stay> findByBookingReference(String bookingReference);
}