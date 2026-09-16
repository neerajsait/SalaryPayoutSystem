package com.salarypayoutsystem.employeeservice.springboot.event;

/**
 * Published to the "employee.updated" Kafka topic on any employee write operation.
 * No consumer exists yet — this is a future hook for services like:
 *   - AuditService (track changes)
 *   - ReportsService (recalculate aggregates)
 *   - NotificationService (welcome email on CREATED)
 */
public record EmployeeEvent(
    Long employeeId,
    String eventType,      // "CREATED", "UPDATED", "DEACTIVATED"
    String employeeName,
    String email,
    Double basicSalary,
    String status
) {}
