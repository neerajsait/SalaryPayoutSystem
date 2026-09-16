package com.salarypayoutsystem.paymentservice.springboot.service;

import com.salarypayoutsystem.paymentservice.springboot.event.SalaryGeneratedEvent;
import com.salarypayoutsystem.paymentservice.springboot.model.Payment;

public interface PaymentService {
    /**
     * Processes a payment from a Kafka SalaryGeneratedEvent.
     * All required data (amount, employeeId, etc.) is contained in the event.
     */
    Payment createPaymentOrder(SalaryGeneratedEvent event);
}
