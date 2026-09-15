package com.salarypayoutsystem.paymentservice.springboot.controller;

import com.salarypayoutsystem.paymentservice.springboot.model.Payment;
import com.salarypayoutsystem.paymentservice.springboot.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin("*")
public class WebhookController {

    @Autowired
    private PaymentRepository paymentRepository;

    private RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(@RequestBody Map<String, Object> payload) {
        try {
            String type = (String) payload.get("type");
            
            if ("payment_intent.succeeded".equals(type)) {
                Map<String, Object> data = (Map<String, Object>) payload.get("data");
                Map<String, Object> object = (Map<String, Object>) data.get("object");
                
                String transactionId = (String) object.get("id");
                
                // 1. Update Payment Record to PAID
                Optional<Payment> paymentOpt = paymentRepository.findByTransactionId(transactionId);
                if (paymentOpt.isPresent()) {
                    Payment payment = paymentOpt.get();
                    payment.setStatus("PAID");
                    paymentRepository.save(payment);
                    
                    // 2. Update Salary Record to PAID
                    Long salaryRecordId = payment.getSalaryRecordId();
                    String url = "http://localhost:8081/api/salaries/" + salaryRecordId + "/status";
                    restTemplate.put(url, Map.of("status", "PAID"));
                    
                    System.out.println("Payment Success! Updated salary record " + salaryRecordId + " to PAID.");
                }
            } else if ("payment_intent.payment_failed".equals(type)) {
                Map<String, Object> data = (Map<String, Object>) payload.get("data");
                Map<String, Object> object = (Map<String, Object>) data.get("object");
                String transactionId = (String) object.get("id");
                
                Optional<Payment> paymentOpt = paymentRepository.findByTransactionId(transactionId);
                if (paymentOpt.isPresent()) {
                    Payment payment = paymentOpt.get();
                    payment.setStatus("FAILED");
                    paymentRepository.save(payment);
                    
                    Long salaryRecordId = payment.getSalaryRecordId();
                    String url = "http://localhost:8081/api/salaries/" + salaryRecordId + "/status";
                    restTemplate.put(url, Map.of("status", "FAILED"));
                }
            }
            
            return new ResponseEntity<>("Webhook processed", HttpStatus.OK);
            
        } catch (Exception e) {
            System.err.println("Webhook error: " + e.getMessage());
            return new ResponseEntity<>("Webhook error", HttpStatus.BAD_REQUEST);
        }
    }
}
