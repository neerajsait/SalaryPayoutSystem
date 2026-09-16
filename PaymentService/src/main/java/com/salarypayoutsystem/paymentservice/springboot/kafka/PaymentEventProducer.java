package com.salarypayoutsystem.paymentservice.springboot.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.salarypayoutsystem.paymentservice.springboot.event.PaymentResultEvent;
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
}
