package com.salarypayoutsystem.employeeservice.springboot.service;

import com.salarypayoutsystem.employeeservice.springboot.model.Employee;
import com.salarypayoutsystem.employeeservice.springboot.model.SalaryRecord;
import com.salarypayoutsystem.employeeservice.springboot.repository.SalaryRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.web.client.RestTemplate;

@Service
public class SalaryRecordServiceImpl implements SalaryRecordService {

    @Autowired
    private SalaryRecordRepository salaryRecordRepository;

    @Autowired
    private EmployeeService employeeService;
    
    private RestTemplate restTemplate = new RestTemplate();
    private final String PAYMENT_SERVICE_URL = "http://localhost:8083/api/payments/create-order";

    @Override
    public SalaryRecord generateSalary(Long employeeId, String month, Integer year) {
        // Fetch employee details directly from EmployeeService
        Optional<Employee> employeeOpt = employeeService.getEmployeeById(employeeId);
        
        if (employeeOpt.isEmpty()) {
            throw new RuntimeException("Employee not found with ID: " + employeeId);
        }

        Employee employee = employeeOpt.get();
        
        if (!"ACTIVE".equalsIgnoreCase(employee.getStatus())) {
            throw new RuntimeException("Cannot generate salary for inactive employee.");
        }
        
        if (salaryRecordRepository.existsByEmployeeIdAndMonthAndYear(employeeId, month, year)) {
            throw new RuntimeException("Salary record already exists for " + month + " " + year);
        }

        // Extract basic salary
        Double basicSalary = employee.getBasicSalary();
        if (basicSalary == null) {
            basicSalary = 0.0;
        }

        // Create the salary record
        SalaryRecord record = new SalaryRecord();
        record.setEmployeeId(employeeId);
        record.setMonth(month);
        record.setYear(year);
        record.setAmount(basicSalary);
        record.setStatus("PENDING");

        SalaryRecord savedRecord = salaryRecordRepository.save(record);
        
        // Automate payment creation via PaymentService
        try {
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
            Map<String, Object> payload = Map.of("salaryRecordId", savedRecord.getId());
            org.springframework.http.HttpEntity<Map<String, Object>> requestEntity = new org.springframework.http.HttpEntity<>(payload, headers);
            
            restTemplate.postForObject(PAYMENT_SERVICE_URL, requestEntity, Object.class);
        } catch (Exception e) {
            System.err.println("Failed to automate payment for SalaryRecord " + savedRecord.getId() + ": " + e.getMessage());
        }
        
        return savedRecord;
    }

    @Override
    public List<SalaryRecord> getAllSalaryRecords() {
        return salaryRecordRepository.findAll();
    }

    @Override
    public SalaryRecord getSalaryRecordById(Long id) {
        return salaryRecordRepository.findById(id).orElseThrow(() -> new RuntimeException("Salary record not found"));
    }

    @Override
    public SalaryRecord updateSalaryStatus(Long id, String status) {
        SalaryRecord record = getSalaryRecordById(id);
        record.setStatus(status);
        return salaryRecordRepository.save(record);
    }

    @org.springframework.scheduling.annotation.Scheduled(cron = "0 0 0 1 * ?") // 1st of every month
    public void generateMonthlySalaries() {
        System.out.println("Running scheduled job: generateMonthlySalaries");
        java.time.LocalDate now = java.time.LocalDate.now();
        String month = now.getMonth().getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.ENGLISH);
        Integer year = now.getYear();

        List<Employee> allEmployees = employeeService.getAllEmployees();
        for (Employee emp : allEmployees) {
            if ("ACTIVE".equalsIgnoreCase(emp.getStatus())) {
                try {
                    // Check if already exists to prevent duplicate generation during cron
                    if (!salaryRecordRepository.existsByEmployeeIdAndMonthAndYear(emp.getId(), month, year)) {
                        generateSalary(emp.getId(), month, year);
                        System.out.println("Auto-generated salary for: " + emp.getName());
                    }
                } catch (Exception e) {
                    System.err.println("Failed to auto-generate salary for " + emp.getName() + ": " + e.getMessage());
                }
            }
        }
    }

    @Override
    public List<SalaryRecord> generateSalariesForAllActive(String month, Integer year) {
        List<Employee> allEmployees = employeeService.getAllEmployees();
        List<SalaryRecord> generatedRecords = new ArrayList<>();
        
        for (Employee emp : allEmployees) {
            if ("ACTIVE".equalsIgnoreCase(emp.getStatus())) {
                try {
                    if (!salaryRecordRepository.existsByEmployeeIdAndMonthAndYear(emp.getId(), month, year)) {
                        SalaryRecord record = generateSalary(emp.getId(), month, year);
                        generatedRecords.add(record);
                    }
                } catch (Exception e) {
                    System.err.println("Failed to batch generate salary for " + emp.getName() + ": " + e.getMessage());
                }
            }
        }
        return generatedRecords;
    }
}
