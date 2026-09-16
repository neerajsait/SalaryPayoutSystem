package com.salarypayoutsystem.auditservice.springboot.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class AuditConsumer {

    private static final Logger logger = LoggerFactory.getLogger(AuditConsumer.class);

    @KafkaListener(topics = "employee.updated", groupId = "audit-service")
    public void consumeEmployeeUpdated(String eventPayload) {
        // In a production system, this would be written to a dedicated audit_logs database table
        // For this template, we log it to standard output which can be collected by ELK/Splunk
        logger.info("AUDIT LOG: Employee lifecycle event received - payload: {}", eventPayload);
    }
    
    @KafkaListener(topics = "salary.generated", groupId = "audit-service")
    public void consumeSalaryGenerated(String eventPayload) {
        logger.info("AUDIT LOG: Salary generation triggered - payload: {}", eventPayload);
    }
}
