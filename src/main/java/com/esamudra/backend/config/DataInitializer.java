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
        // Create default admin account if it doesn't exist
        if (staffRepository.findByEmail("admin@samudra.com").isEmpty()) {
            Staff admin = new Staff();
            admin.setName("System Administrator");
            admin.setRole("ADMIN");
            admin.setEmail("admin@samudra.com");
            admin.setPassword("admin123"); // Default password
            admin.setContactInfo("+94 71 234 5678");

            staffRepository.save(admin);
            System.out.println("Default admin account created");
        }
    }
}