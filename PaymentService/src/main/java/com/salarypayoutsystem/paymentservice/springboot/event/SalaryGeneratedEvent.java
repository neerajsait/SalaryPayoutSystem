package com.salarypayoutsystem.paymentservice.springboot.event;

/**
 * Consumed from the "salary.generated" Kafka topic.
 * Contains all data needed to process payment — no HTTP callback to EmployeeService required.
 */
public record SalaryGeneratedEvent(
    Long salaryRecordId,
    Long employeeId,
    Double amount,
    String month,
    Integer year,
    String employeeEmail,
    String employeeName
) {}
