package com.esamudra.backend.config;

import com.esamudra.backend.model.Staff;
import com.esamudra.backend.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private StaffRepository staffRepository;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== DATA INITIALIZER STARTING ===");
        System.out.println("Total staff in database: " + staffRepository.count());

        // Create default admin account if it doesn't exist
        if (staffRepository.findByEmail("admin@samudra.com").isEmpty()) {
            Staff admin = new Staff();
            admin.setName("System Administrator");
            admin.setRole("ADMIN");
            admin.setEmail("admin@samudra.com");
            admin.setContactInfo("+94 71 234 5678");

            staffRepository.save(admin);
            System.out.println("Default admin account created");
        } else {
            System.out.println("Admin account already exists");
        }

        // Create operational staff members if none exist
        long staffCount = staffRepository.count();
        System.out.println("Current staff count: " + staffCount);

        if (true) { // ALWAYS CREATE STAFF FOR NOW
            Staff manager = new Staff();
            manager.setName("Hotel Manager");
            manager.setEmail("manager@samudra.com");
            manager.setRole("MANAGER");
            manager.setContactInfo("+94 11 234 5678");
            staffRepository.save(manager);

            Staff reception = new Staff();
            reception.setName("Front Desk Staff");
            reception.setEmail("reception@samudra.com");
            reception.setRole("RECEPTION");
            reception.setContactInfo("+94 11 234 5679");
            staffRepository.save(reception);

            System.out.println("Default operational staff members created successfully!");
        } else {
            System.out.println("Staff members already exist, skipping creation");
        }

        System.out.println("=== DATA INITIALIZER COMPLETED ===");
    }
}