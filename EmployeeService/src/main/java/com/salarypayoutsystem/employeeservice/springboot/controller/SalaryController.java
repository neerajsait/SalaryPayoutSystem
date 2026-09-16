package com.salarypayoutsystem.employeeservice.springboot.controller;

import com.salarypayoutsystem.employeeservice.springboot.model.SalaryRecord;
import com.salarypayoutsystem.employeeservice.springboot.service.SalaryRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/salaries")
@CrossOrigin("http://localhost:8080")
public class SalaryController {

    @Autowired
    private SalaryRecordService salaryRecordService;

    // Payload: { "employeeId": 1, "month": "September", "year": 2026 }
    @PostMapping("/generate")
    public ResponseEntity<?> generateSalary(@RequestBody Map<String, Object> request) {
        try {
            Long employeeId = Long.valueOf(request.get("employeeId").toString());
            String month = request.get("month").toString();
            Integer year = Integer.valueOf(request.get("year").toString());

            SalaryRecord record = salaryRecordService.generateSalary(employeeId, month, year);
            return new ResponseEntity<>(record, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Payload: { "month": "September", "year": 2026 }
    @PostMapping("/generate-all")
    public ResponseEntity<?> generateAllSalaries(@RequestBody Map<String, Object> request) {
        try {
            String month = request.get("month").toString();
            Integer year = Integer.valueOf(request.get("year").toString());

            List<SalaryRecord> records = salaryRecordService.generateSalariesForAllActive(month, year);
            return new ResponseEntity<>(records, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<List<SalaryRecord>> getAllSalaries() {
        return new ResponseEntity<>(salaryRecordService.getAllSalaryRecords(), HttpStatus.OK);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<SalaryRecord> getSalaryById(@PathVariable Long id) {
        try {
            return new ResponseEntity<>(salaryRecordService.getSalaryRecordById(id), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Payload: { "status": "PAID" }
    @PutMapping("/{id}/status")
    public ResponseEntity<SalaryRecord> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String status = request.get("status");
            return new ResponseEntity<>(salaryRecordService.updateSalaryStatus(id, status), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
