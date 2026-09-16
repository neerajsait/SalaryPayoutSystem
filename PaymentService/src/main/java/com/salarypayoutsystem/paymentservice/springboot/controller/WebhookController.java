package com.salarypayoutsystem.paymentservice.springboot.controller;

import com.salarypayoutsystem.paymentservice.springboot.kafka.PaymentEventProducer;
import com.salarypayoutsystem.paymentservice.springboot.model.Payment;
import com.salarypayoutsystem.paymentservice.springboot.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin("*")
public class WebhookController {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentEventProducer paymentEventProducer;

    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(@RequestBody Map<String, Object> payload) {
        try {
            String type = (String) payload.get("type");

            if ("payment_intent.succeeded".equals(type)) {
                Map<String, Object> data = (Map<String, Object>) payload.get("data");
                Map<String, Object> object = (Map<String, Object>) data.get("object");
                String transactionId = (String) object.get("id");

                Optional<Payment> paymentOpt = paymentRepository.findByTransactionId(transactionId);
                if (paymentOpt.isPresent()) {
                    Payment payment = paymentOpt.get();
                    payment.setStatus("PAID");
                    paymentRepository.save(payment);

                    // Notify EmployeeService via Kafka — no RestTemplate call
                    paymentEventProducer.publishPaymentResult(payment.getSalaryRecordId(), "PAID", transactionId);
                    System.out.println("Webhook: published payment.succeeded for salaryRecordId=" + payment.getSalaryRecordId());
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

                    // Notify EmployeeService via Kafka — no RestTemplate call
                    paymentEventProducer.publishPaymentResult(payment.getSalaryRecordId(), "FAILED", transactionId);
                    System.out.println("Webhook: published payment.failed for salaryRecordId=" + payment.getSalaryRecordId());
                }
            }

            return new ResponseEntity<>("Webhook processed", HttpStatus.OK);

        } catch (Exception e) {
            System.err.println("Webhook error: " + e.getMessage());
            return new ResponseEntity<>("Webhook error", HttpStatus.BAD_REQUEST);
        }
    }
}
