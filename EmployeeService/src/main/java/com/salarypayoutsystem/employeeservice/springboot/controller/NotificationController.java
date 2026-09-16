package com.salarypayoutsystem.employeeservice.springboot.controller;

import com.salarypayoutsystem.employeeservice.springboot.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin("http://localhost:8080")
public class NotificationController {

    @Autowired
    private EmailService emailService;

    // Payload: { "email": "test@test.com", "name": "John Doe", "month": "September", "year": 2026, "amount": 50000.0 }
    @PostMapping("/salary-email")
    public ResponseEntity<String> sendSalaryEmail(@RequestBody Map<String, Object> request) {
        try {
            String email = request.get("email").toString();
            String name = request.get("name").toString();
            String month = request.get("month").toString();
            Integer year = Integer.valueOf(request.get("year").toString());
            Double amount = Double.valueOf(request.get("amount").toString());

            emailService.sendSalaryCreditEmail(email, name, month, year, amount);
            
            return new ResponseEntity<>("Email sent successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error sending email: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
