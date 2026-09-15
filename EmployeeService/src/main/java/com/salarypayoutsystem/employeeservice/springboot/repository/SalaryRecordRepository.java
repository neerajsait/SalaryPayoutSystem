package com.salarypayoutsystem.employeeservice.springboot.repository;

import com.salarypayoutsystem.employeeservice.springboot.model.SalaryRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalaryRecordRepository extends JpaRepository<SalaryRecord, Long> {
    boolean existsByEmployeeIdAndMonthAndYear(Long employeeId, String month, Integer year);
}
