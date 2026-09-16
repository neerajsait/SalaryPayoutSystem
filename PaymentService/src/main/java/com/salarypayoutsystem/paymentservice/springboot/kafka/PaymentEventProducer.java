package com.salarypayoutsystem.paymentservice.springboot.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.salarypayoutsystem.paymentservice.springboot.event.NotificationEvent;
import com.salarypayoutsystem.paymentservice.springboot.event.PaymentResultEvent;
import com.salarypayoutsystem.paymentservice.springboot.event.SalaryGeneratedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PaymentEventProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void publishPaymentResult(Long salaryRecordId, String status, String transactionId) {
        String topic = "PAID".equals(status) ? "payment.succeeded" : "payment.failed";
        try {
            PaymentResultEvent event = new PaymentResultEvent(salaryRecordId, status, transactionId);
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topic, String.valueOf(salaryRecordId), message);
            System.out.println("Published " + topic + " event for salaryRecordId=" + salaryRecordId);
        } catch (JsonProcessingException e) {
            System.err.println("Failed to serialize PaymentResultEvent: " + e.getMessage());
        }
    }

    /**
     * Publishes a notification.send event so EmployeeService can send the PDF payslip email.
     * Decoupled: email failures never affect payment processing.
     */
    public void publishNotification(SalaryGeneratedEvent event) {
        try {
            NotificationEvent notif = new NotificationEvent(
                event.employeeEmail(),
                event.employeeName(),
                event.month(),
                event.year(),
                event.amount()
            );
            String message = objectMapper.writeValueAsString(notif);
            kafkaTemplate.send("notification.send", String.valueOf(event.salaryRecordId()), message);
            System.out.println("Published notification.send event for: " + event.employeeEmail());
        } catch (JsonProcessingException e) {
            System.err.println("Failed to serialize NotificationEvent: " + e.getMessage());
        }
    }
}

