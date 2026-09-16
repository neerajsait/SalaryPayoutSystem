package com.salarypayoutsystem.employeeservice.springboot.event;

/**
 * Published to the "salary.generated" Kafka topic when a salary record is created.
 * Contains all data PaymentService needs — no HTTP callback required.
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
