package com.salarypayoutsystem.employeeservice.springboot.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.salarypayoutsystem.employeeservice.springboot.event.SalaryGeneratedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class SalaryEventProducer {

    private static final String TOPIC = "salary.generated";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void publishSalaryGenerated(SalaryGeneratedEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(TOPIC, String.valueOf(event.salaryRecordId()), message);
            System.out.println("Published salary.generated event for salaryRecordId=" + event.salaryRecordId());
        } catch (JsonProcessingException e) {
            System.err.println("Failed to serialize SalaryGeneratedEvent: " + e.getMessage());
        }
    }
}
