package com.salarypayoutsystem.employeeservice.springboot.repository;

import com.salarypayoutsystem.employeeservice.springboot.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    boolean existsByBankAccountUpi(String bankAccountUpi);
}
