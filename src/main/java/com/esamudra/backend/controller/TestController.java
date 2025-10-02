package com.esamudra.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/")
    public String home() {
        return "Spring Boot is working! Connected to MySQL successfully! 🎉";
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello from Spring Boot! Database connection: OK!";
    }
}