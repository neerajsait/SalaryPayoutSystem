package com.salarypayoutsystem.employeeservice.springboot.event;

/**
 * Consumed from the "notification.send" Kafka topic.
 * Published by PaymentService after a payment succeeds.
 * EmployeeService uses this to send a PDF payslip email via EmailServiceImpl.
 */
public record NotificationEvent(
    String employeeEmail,
    String employeeName,
    String month,
    Integer year,
    Double amount
) {}
