package com.esamudra.backend.controller;

import com.esamudra.backend.model.Customer;
import com.esamudra.backend.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "http://localhost:3000")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    // Existing endpoints
    @GetMapping
    public List<Customer> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping("/{id}")
    public Optional<Customer> getCustomerById(@PathVariable Long id) {
        return customerService.getCustomerById(id);
    }

    @GetMapping("/email/{email}")
    public Optional<Customer> getCustomerByEmail(@PathVariable String email) {
        return customerService.getCustomerByEmail(email);
    }

    // NEW ENDPOINTS FOR UPDATED FIELDS
    @GetMapping("/phone/{phoneNumber}")
    public Optional<Customer> getCustomerByPhoneNumber(@PathVariable String phoneNumber) {
        return customerService.getCustomerByPhoneNumber(phoneNumber);
    }

    @GetMapping("/loyalty/{tier}")
    public List<Customer> getCustomersByLoyaltyTier(@PathVariable String tier) {
        return customerService.getCustomersByLoyaltyTier(tier.toUpperCase());
    }

    @GetMapping("/search/{name}")
    public List<Customer> searchCustomersByName(@PathVariable String name) {
        return customerService.searchCustomersByName(name);
    }

    @GetMapping("/premium")
    public List<Customer> getPremiumCustomers() {
        return customerService.getPremiumCustomers();
    }

    @PostMapping
    public Customer createCustomer(@RequestBody Customer customer) {
        return customerService.createCustomer(customer);
    }

    @PutMapping("/{id}")
    public Customer updateCustomer(@PathVariable Long id, @RequestBody Customer customerDetails) {
        return customerService.updateCustomer(id, customerDetails);
    }

    @PutMapping("/{id}/loyalty/{newTier}")
    public Customer upgradeLoyaltyTier(@PathVariable Long id, @PathVariable String newTier) {
        return customerService.upgradeLoyaltyTier(id, newTier);
    }

    @DeleteMapping("/{id}")
    public void deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
    }
}