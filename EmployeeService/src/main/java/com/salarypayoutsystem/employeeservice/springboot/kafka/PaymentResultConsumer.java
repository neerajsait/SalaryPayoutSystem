package com.salarypayoutsystem.employeeservice.springboot.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.salarypayoutsystem.employeeservice.springboot.event.PaymentResultEvent;
import com.salarypayoutsystem.employeeservice.springboot.model.SalaryRecord;
import com.salarypayoutsystem.employeeservice.springboot.repository.SalaryRecordRepository;
import com.salarypayoutsystem.employeeservice.springboot.service.SalaryRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PaymentResultConsumer {

    @Autowired
    private SalaryRecordRepository salaryRecordRepository;

    @Autowired
    private SalaryRecordService salaryRecordService;

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = {"payment.succeeded", "payment.failed"}, groupId = "employee-service")
    public void consume(String message) {
        try {
            PaymentResultEvent event = objectMapper.readValue(message, PaymentResultEvent.class);
            System.out.println("Received payment result event: salaryRecordId=" + event.salaryRecordId() + " status=" + event.status());

            // Idempotency check: skip if salary is already in the target status
            Optional<SalaryRecord> recordOpt = salaryRecordRepository.findById(event.salaryRecordId());
            if (recordOpt.isEmpty()) {
                System.err.println("No salary record found for id=" + event.salaryRecordId() + ". Skipping.");
                return;
            }

            SalaryRecord record = recordOpt.get();
            if (record.getStatus().equals(event.status())) {
                System.out.println("Salary record " + event.salaryRecordId() + " is already " + event.status() + ". Skipping (idempotent).");
                return;
            }

            salaryRecordService.updateSalaryStatus(event.salaryRecordId(), event.status());
            System.out.println("Updated salary record " + event.salaryRecordId() + " to " + event.status());

        } catch (Exception e) {
            System.err.println("Error processing PaymentResultEvent: " + e.getMessage());
        }
    }
}
