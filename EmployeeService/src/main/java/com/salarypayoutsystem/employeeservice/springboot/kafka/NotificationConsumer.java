package com.salarypayoutsystem.employeeservice.springboot.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.salarypayoutsystem.employeeservice.springboot.event.NotificationEvent;
import com.salarypayoutsystem.employeeservice.springboot.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationConsumer {

    @Autowired
    private EmailService emailService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Listens for payment success events and sends a PDF payslip email.
     * Decoupled from the payment flow — email failures never affect payment processing.
     */
    @KafkaListener(topics = "notification.send", groupId = "employee-service")
    public void consume(String message) {
        try {
            NotificationEvent event = objectMapper.readValue(message, NotificationEvent.class);
            System.out.println("Received notification.send event for: " + event.employeeEmail());

            emailService.sendSalaryCreditEmail(
                event.employeeEmail(),
                event.employeeName(),
                event.month(),
                event.year(),
                event.amount()
            );

            System.out.println("Payslip email sent successfully to: " + event.employeeEmail());
        } catch (Exception e) {
            // Email failure is logged but does NOT affect salary/payment status
            System.err.println("Failed to send payslip email: " + e.getMessage());
        }
    }
}
