package com.salarypayoutsystem.employeeservice.springboot.service;

public interface EmailService {
    void sendSalaryCreditEmail(String toEmail, String employeeName, String month, Integer year, Double amount);
}
