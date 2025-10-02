package com.esamudra.backend.repository;

import com.esamudra.backend.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // Existing methods
    Optional<Customer> findByEmail(String email);

    // NEW METHODS FOR UPDATED FIELDS
    Optional<Customer> findByPhoneNumber(String phoneNumber);

    // Find customers by loyalty tier
    List<Customer> findByLoyaltyTier(String loyaltyTier);

    // Search customers by name
    List<Customer> findByNameContainingIgnoreCase(String name);

    // Find premium customers (GOLD/PLATINUM)
    @Query("SELECT c FROM Customer c WHERE c.loyaltyTier IN ('GOLD', 'PLATINUM')")
    List<Customer> findPremiumCustomers();
}