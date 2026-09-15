package com.salarypayoutsystem.paymentservice.springboot.service;

import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import com.salarypayoutsystem.paymentservice.springboot.model.Payment;
import com.salarypayoutsystem.paymentservice.springboot.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    private RestTemplate restTemplate = new RestTemplate();
    private final String SALARY_SERVICE_URL = "http://localhost:8081/api/salaries/";

    @Value("${stripe.api.key:YOUR_STRIPE_SECRET_KEY}")
    private String stripeApiKey;

    @Override
    public Payment createPaymentOrder(Long salaryRecordId) {
        // 1. Fetch Salary Record
        String url = SALARY_SERVICE_URL + salaryRecordId;
        Map<String, Object> salaryRecord;
        try {
            salaryRecord = restTemplate.getForObject(url, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching salary record: " + e.getMessage());
        }

        if (salaryRecord == null) {
            throw new RuntimeException("Salary record not found");
        }

        Double amount = Double.valueOf(salaryRecord.get("amount").toString());

        try {
            Stripe.apiKey = stripeApiKey;

            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount((long) (amount * 100)) // amount in paise
                .setCurrency("inr")
                .putMetadata("salaryRecordId", salaryRecordId.toString())
                .build();

            PaymentIntent intent = PaymentIntent.create(params);

            // 4. Save Payment Record
            Payment payment = new Payment();
            payment.setSalaryRecordId(salaryRecordId);
            payment.setAmount(amount);
            payment.setTransactionId(intent.getId());
            payment.setStatus("CREATED");
            
            Payment savedPayment = paymentRepository.save(payment);
            
            // 5. Fetch Employee Details and Send Email
            try {
                String empUrl = "http://localhost:8081/api/employees/" + salaryRecord.get("employeeId");
                Map<String, Object> employee = restTemplate.getForObject(empUrl, Map.class);
                
                if (employee != null) {
                    Map<String, Object> emailPayload = Map.of(
                        "email", employee.get("email"),
                        "name", employee.get("name"),
                        "month", salaryRecord.get("month"),
                        "year", salaryRecord.get("year"),
                        "amount", amount
                    );
                    restTemplate.postForObject("http://localhost:8081/api/notifications/salary-email", emailPayload, String.class);
                }
            } catch (Exception e) {
                System.out.println("Payment succeeded, but failed to send email: " + e.getMessage());
            }
            
            return savedPayment;

        } catch (Exception e) {
            throw new RuntimeException("Error creating Stripe PaymentIntent: " + e.getMessage());
        }
    }
}
