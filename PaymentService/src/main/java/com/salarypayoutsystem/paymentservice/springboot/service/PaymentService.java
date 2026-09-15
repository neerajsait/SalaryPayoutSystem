package com.salarypayoutsystem.paymentservice.springboot.service;

import com.salarypayoutsystem.paymentservice.springboot.model.Payment;

public interface PaymentService {
    Payment createPaymentOrder(Long salaryRecordId);
}
