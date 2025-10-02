package com.esamudra.backend.controller;

import com.esamudra.backend.model.Stay;
import com.esamudra.backend.service.StayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/stays")
@CrossOrigin(origins = "http://localhost:3000")
public class StayController {

    @Autowired
    private StayService stayService;

    @GetMapping
    public List<Stay> getAllStays() {
        return stayService.getAllStays();
    }

    @GetMapping("/{id}")
    public Optional<Stay> getStayById(@PathVariable Long id) {
        return stayService.getStayById(id);
    }

    @GetMapping("/customer/{customerId}")
    public List<Stay> getStaysByCustomer(@PathVariable Long customerId) {
        return stayService.getStaysByCustomer(customerId);
    }

    @PostMapping
    public Stay createStay(@RequestBody Stay stay) {
        return stayService.createStay(stay);
    }

    @PutMapping("/{id}")
    public Stay updateStay(@PathVariable Long id, @RequestBody Stay stayDetails) {
        return stayService.updateStay(id, stayDetails);
    }

    @DeleteMapping("/{id}")
    public void deleteStay(@PathVariable Long id) {
        stayService.deleteStay(id);
    }
}