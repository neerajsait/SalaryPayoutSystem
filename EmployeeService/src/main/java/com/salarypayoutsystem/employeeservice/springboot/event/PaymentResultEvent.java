package com.salarypayoutsystem.employeeservice.springboot.event;

/**
 * Consumed from "payment.succeeded" and "payment.failed" topics.
 * Published by PaymentService after Stripe processing completes.
 */
public record PaymentResultEvent(
    Long salaryRecordId,
    String status,          // "PAID" or "FAILED"
    String transactionId
) {}
