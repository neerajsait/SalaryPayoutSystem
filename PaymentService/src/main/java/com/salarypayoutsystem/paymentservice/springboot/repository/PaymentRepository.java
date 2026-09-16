package com.salarypayoutsystem.paymentservice.springboot.repository;

import com.salarypayoutsystem.paymentservice.springboot.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByTransactionId(String transactionId);
    boolean existsBySalaryRecordId(Long salaryRecordId);
}
