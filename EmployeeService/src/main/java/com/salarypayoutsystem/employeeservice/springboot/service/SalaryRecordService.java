package com.salarypayoutsystem.employeeservice.springboot.service;

import com.salarypayoutsystem.employeeservice.springboot.model.SalaryRecord;
import java.util.List;

public interface SalaryRecordService {
    SalaryRecord generateSalary(Long employeeId, String month, Integer year);
    List<SalaryRecord> getAllSalaryRecords();
    SalaryRecord getSalaryRecordById(Long id);
    SalaryRecord updateSalaryStatus(Long id, String status);
    List<SalaryRecord> generateSalariesForAllActive(String month, Integer year);
}
