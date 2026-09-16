package com.salarypayoutsystem.paymentservice.springboot.event;

/**
 * Published to the "notification.send" Kafka topic after a payment succeeds.
 * Consumed by EmployeeService to send the PDF payslip email.
 */
public record NotificationEvent(
    String employeeEmail,
    String employeeName,
    String month,
    Integer year,
    Double amount
) {}
