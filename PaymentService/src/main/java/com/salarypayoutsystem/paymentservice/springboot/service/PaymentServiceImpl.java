package com.salarypayoutsystem.paymentservice.springboot.service;

import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import com.salarypayoutsystem.paymentservice.springboot.event.SalaryGeneratedEvent;
import com.salarypayoutsystem.paymentservice.springboot.kafka.PaymentEventProducer;
import com.salarypayoutsystem.paymentservice.springboot.model.Payment;
import com.salarypayoutsystem.paymentservice.springboot.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentEventProducer paymentEventProducer;

    @Value("${stripe.api.key:YOUR_STRIPE_SECRET_KEY}")
    private String stripeApiKey;

    /**
     * Creates a Stripe PaymentIntent from a Kafka SalaryGeneratedEvent.
     * All data comes from the event — no HTTP calls to EmployeeService needed.
     * Payment result (PAID/FAILED) is published back via Kafka.
     */
    @Override
    public Payment createPaymentOrder(SalaryGeneratedEvent event) {
        try {
            Stripe.apiKey = stripeApiKey.trim();

            // 1. Create the PaymentIntent
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount((long) (event.amount() * 100)) // amount in paise
                .setCurrency("inr")
                .putMetadata("salaryRecordId", event.salaryRecordId().toString())
                .putMetadata("employeeId", event.employeeId().toString())
                .build();
            PaymentIntent intent = PaymentIntent.create(params);

            // 2. Save the Payment record
            Payment payment = new Payment();
            payment.setSalaryRecordId(event.salaryRecordId());
            payment.setAmount(event.amount());
            payment.setTransactionId(intent.getId());
            payment.setStatus("CREATED");
            Payment savedPayment = paymentRepository.save(payment);

            // 3. Confirm the PaymentIntent (demo: using test payment method)
            com.stripe.param.PaymentIntentConfirmParams confirmParams =
                com.stripe.param.PaymentIntentConfirmParams.builder()
                    .setPaymentMethod("pm_card_visa")
                    .setReturnUrl("http://localhost:8080/salaries")
                    .build();
            intent.confirm(confirmParams);

            // 4. Mark payment as PAID locally
            payment.setStatus("PAID");
            paymentRepository.save(payment);

            // 5. Notify EmployeeService via Kafka — no RestTemplate call
            paymentEventProducer.publishPaymentResult(event.salaryRecordId(), "PAID", intent.getId());
            System.out.println("Payment processed and published payment.succeeded for salaryRecordId=" + event.salaryRecordId());

            // 6. TODO Phase 4: publish notification.send event for email
            //    paymentEventProducer.publishNotification(event.employeeEmail(), event.employeeName(),
            //        event.month(), event.year(), event.amount());

            return savedPayment;

        } catch (Exception e) {
            // Publish failure event so EmployeeService can mark salary as FAILED
            paymentEventProducer.publishPaymentResult(event.salaryRecordId(), "FAILED", "N/A");
            throw new RuntimeException("Error creating Stripe PaymentIntent: " + e.getMessage());
        }
    }
}
