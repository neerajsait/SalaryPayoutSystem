package com.salarypayoutsystem.employeeservice.springboot.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.salarypayoutsystem.employeeservice.springboot.event.EmployeeEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class EmployeeEventProducer {

    private static final String TOPIC = "employee.updated";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void publishEmployeeEvent(EmployeeEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(TOPIC, String.valueOf(event.employeeId()), message);
            System.out.println("Published employee.updated event: employeeId=" + event.employeeId() + " type=" + event.eventType());
        } catch (JsonProcessingException e) {
            System.err.println("Failed to serialize EmployeeEvent: " + e.getMessage());
        }
    }
}
