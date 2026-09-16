package com.salarypayoutsystem.employeeservice.springboot.service;

import com.salarypayoutsystem.employeeservice.springboot.model.Employee;
import com.salarypayoutsystem.employeeservice.springboot.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    @Caching(evict = {
        @CacheEvict(value = "employees", allEntries = true),
        @CacheEvict(value = "employee", allEntries = true)
    })
    public Employee addEmployee(Employee employee) {
        if (employeeRepository.existsByEmail(employee.getEmail())) {
            throw new RuntimeException("An employee with this email already exists!");
        }
        if (employeeRepository.existsByPhone(employee.getPhone())) {
            throw new RuntimeException("An employee with this phone number already exists!");
        }
        if (employeeRepository.existsByBankAccountUpi(employee.getBankAccountUpi())) {
            throw new RuntimeException("An employee with this bank account/UPI already exists!");
        }

        // Set default status if not provided
        if (employee.getStatus() == null || employee.getStatus().isEmpty()) {
            employee.setStatus("ACTIVE");
        }
        return employeeRepository.save(employee);
    }

    @Override
    @Cacheable("employees")
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    @Cacheable(value = "employee", key = "#id")
    public Optional<Employee> getEmployeeById(Long id) {
        return employeeRepository.findById(id);
    }

    @Override
    @Caching(evict = {
        @CacheEvict(value = "employees", allEntries = true),
        @CacheEvict(value = "employee", allEntries = true)
    })
    public Employee updateEmployee(Long id, Employee employeeDetails) {
        Optional<Employee> optionalEmployee = employeeRepository.findById(id);
        if (optionalEmployee.isPresent()) {
            Employee existingEmployee = optionalEmployee.get();
            existingEmployee.setName(employeeDetails.getName());
            existingEmployee.setEmail(employeeDetails.getEmail());
            existingEmployee.setPhone(employeeDetails.getPhone());
            existingEmployee.setBankAccountUpi(employeeDetails.getBankAccountUpi());
            existingEmployee.setBasicSalary(employeeDetails.getBasicSalary());
            existingEmployee.setStatus(employeeDetails.getStatus());
            return employeeRepository.save(existingEmployee);
        }
        throw new RuntimeException("Employee not found with id " + id);
    }

    @Override
    @Caching(evict = {
        @CacheEvict(value = "employees", allEntries = true),
        @CacheEvict(value = "employee", allEntries = true)
    })
    public void deleteEmployee(Long id) {
        Optional<Employee> optionalEmployee = employeeRepository.findById(id);
        if (optionalEmployee.isPresent()) {
            Employee existingEmployee = optionalEmployee.get();
            existingEmployee.setStatus("INACTIVE");
            employeeRepository.save(existingEmployee);
        } else {
            throw new RuntimeException("Employee not found with id " + id);
        }
    }
}

