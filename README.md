# Salary Payout System

A microservices-based enterprise payroll management application built with Spring Boot 3, Java 21, and JSP.

## Architecture

This project is divided into three distinct Spring Boot microservices:

1. **FrontendService** (Port: `8080`)
   - Handles the user interface (UI) and views using JSP.
   - Secures the application via Spring Security.
   - Acts as an API gateway of sorts for the UI to consume backend services.

2. **EmployeeService** (Port: `8081`)
   - Manages Employee data (CRUD, Soft Deletes).
   - Manages Salary Records (Duplicate prevention, batch generation).
   - Contains a scheduled cron job to auto-generate salaries on the 1st of every month.

3. **PaymentService** (Port: `8083`)
   - Integrates with Stripe to handle payment processing.
   - Listens for Stripe webhooks to automatically mark salaries as PAID when a transaction succeeds.

## Features
- **Batch Salary Generation**: Generate salaries and automatically trigger Stripe payments for all active employees with a single click.
- **Duplicate Prevention**: Safeguards prevent generating the same salary twice for a given month.
- **Soft Deletes**: Employees can be marked as inactive without deleting historical financial records.

## Requirements
- Java 21+
- Maven
- MySQL Server

## Setup Instructions

This project is a ready-to-deploy template. You can easily configure it for your own business without touching the Java code.

1. Ensure MySQL is running on port 3306 with a root user and password `root`.
2. **Configure Admin Credentials**: 
   - Open `FrontendService/src/main/resources/application.properties`.
   - Update `app.admin.username` and `app.admin.password` with your desired secure credentials.
3. **Configure Stripe API**: 
   - Set up your Stripe keys in the `PaymentService` `.env` or `application.properties` file.
4. Start the services via your IDE or Maven:
   - `EmployeeService`
   - `PaymentService`
   - `FrontendService`
5. Access the application at `http://localhost:8080/`.
