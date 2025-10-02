package com.esamudra.backend.service;

import com.esamudra.backend.model.Customer;
import com.esamudra.backend.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public Customer createCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Optional<Customer> getCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    public Optional<Customer> getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    // NEW METHODS FOR UPDATED FIELDS
    public Optional<Customer> getCustomerByPhoneNumber(String phoneNumber) {
        return customerRepository.findByPhoneNumber(phoneNumber);
    }

    public List<Customer> getCustomersByLoyaltyTier(String loyaltyTier) {
        return customerRepository.findByLoyaltyTier(loyaltyTier);
    }

    public List<Customer> searchCustomersByName(String name) {
        return customerRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Customer> getPremiumCustomers() {
        return customerRepository.findPremiumCustomers();
    }

    public Customer updateCustomer(Long id, Customer customerDetails) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));

        // Update all fields
        customer.setName(customerDetails.getName());
        customer.setEmail(customerDetails.getEmail());
        customer.setPhoneNumber(customerDetails.getPhoneNumber());
        customer.setAddress(customerDetails.getAddress());
        customer.setLoyaltyTier(customerDetails.getLoyaltyTier());
        customer.setPreferences(customerDetails.getPreferences());
        customer.setContactInfo(customerDetails.getContactInfo());

        return customerRepository.save(customer);
    }

    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }

    public Customer upgradeLoyaltyTier(Long customerId, String newTier) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        if (isValidLoyaltyTier(newTier)) {
            customer.setLoyaltyTier(newTier.toUpperCase());
            return customerRepository.save(customer);
        } else {
            throw new RuntimeException("Invalid loyalty tier: " + newTier);
        }
    }

    private boolean isValidLoyaltyTier(String tier) {
        return List.of("BRONZE", "SILVER", "GOLD", "PLATINUM").contains(tier.toUpperCase());
    }
}