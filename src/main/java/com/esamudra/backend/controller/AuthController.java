package com.esamudra.backend.controller;

import com.esamudra.backend.dto.LoginRequest;
import com.esamudra.backend.model.Staff;
import com.esamudra.backend.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    @Autowired
    private StaffRepository staffRepository;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        Optional<Staff> staffOptional = staffRepository.findByEmail(loginRequest.getEmail());

        if (staffOptional.isPresent()) {
            Staff staff = staffOptional.get();
            if (staff.getPassword().equals(loginRequest.getPassword())) {
                return ResponseEntity.ok("Login successful");
            }
        }

        return ResponseEntity.status(401).body("Invalid email or password");
    }
}
