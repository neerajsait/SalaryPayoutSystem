package com.salarypayoutsystem.paymentservice.springboot.controller;

import com.salarypayoutsystem.paymentservice.springboot.model.Payment;
import com.salarypayoutsystem.paymentservice.springboot.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin("http://localhost:8080")
public class PaymentController {

    @Autowired
    private PaymentRepository paymentRepository;

    /**
     * Payment creation is now event-driven via Kafka (salary.generated topic).
     * This endpoint returns all payment records for monitoring/admin purposes.
     */
    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        return new ResponseEntity<>(paymentRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPaymentById(@PathVariable Long id) {
        return paymentRepository.findById(id)
            .<ResponseEntity<?>>map(p -> new ResponseEntity<>(p, HttpStatus.OK))
            .orElse(new ResponseEntity<>("Payment not found", HttpStatus.NOT_FOUND));
    }
}
