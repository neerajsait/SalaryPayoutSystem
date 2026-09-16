package com.salarypayoutsystem.paymentservice.springboot.event;

/**
 * Published to "payment.succeeded" or "payment.failed" Kafka topics.
 * Consumed by EmployeeService to update salary status.
 */
public record PaymentResultEvent(
    Long salaryRecordId,
    String status,          // "PAID" or "FAILED"
    String transactionId
) {}
