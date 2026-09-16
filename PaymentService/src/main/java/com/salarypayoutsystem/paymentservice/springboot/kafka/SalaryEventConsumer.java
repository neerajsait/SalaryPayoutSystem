package com.salarypayoutsystem.paymentservice.springboot.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.salarypayoutsystem.paymentservice.springboot.event.SalaryGeneratedEvent;
import com.salarypayoutsystem.paymentservice.springboot.repository.PaymentRepository;
import com.salarypayoutsystem.paymentservice.springboot.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class SalaryEventConsumer {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "salary.generated", groupId = "payment-service")
    public void consume(String message) {
        try {
            SalaryGeneratedEvent event = objectMapper.readValue(message, SalaryGeneratedEvent.class);
            System.out.println("Received salary.generated event: salaryRecordId=" + event.salaryRecordId());

            // Idempotency check: skip if a payment already exists for this salary record
            if (paymentRepository.existsBySalaryRecordId(event.salaryRecordId())) {
                System.out.println("Payment already exists for salaryRecordId=" + event.salaryRecordId() + ". Skipping (idempotent).");
                return;
            }

            paymentService.createPaymentOrder(event);

        } catch (Exception e) {
            System.err.println("Error processing SalaryGeneratedEvent: " + e.getMessage());
        }
    }
}
