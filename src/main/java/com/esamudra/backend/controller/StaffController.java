package com.esamudra.backend.controller;

import com.esamudra.backend.model.Staff;
import com.esamudra.backend.service.ComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/staff")
@CrossOrigin(origins = "http://localhost:3000")
public class StaffController {

    @Autowired
    private ComplaintService complaintService;

    @GetMapping
    public List<Staff> getAllStaff() {
        return complaintService.getAllStaff();
    }
}