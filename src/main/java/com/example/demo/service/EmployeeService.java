package com.example.demo.service;

import com.example.demo.exception.InvalidEmployeeAgeException;
import com.example.demo.repository.EmployeeRepository;
import com.example.demo.model.Employee;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Service marks a class as a service layer component.
 * In general, a service layer component holds business logic and calls methods in the repository layer, if any. (i.e.
 * classes marked with @Repository)
 * Component-scan will pick this up and register this class as a bean to be managed by Spring context.
 */
@Service
public class EmployeeService {


    @Autowired
    private EmployeeRepository repository;

    public Employee saveEmployee(Employee employee) throws DataAccessException, InvalidEmployeeAgeException {
        if (employee.getAge() == null || employee.getAge() < 0) {
            throw new InvalidEmployeeAgeException(employee.getAge());
        }

        return this.repository.save(employee);
    }

    public ArrayList<Employee> batchSaveEmployee(Employee[] employees) throws DataAccessException {
        ArrayList<Employee> result = new ArrayList<>();
        if (employees == null) {
            return result;
        }

        List<Employee> employeesList = new ArrayList<>(Arrays.asList(employees));
        this.repository.saveAll(employeesList).forEach(result::add);
        return result;
    }

    public ArrayList<Employee> getEmployees() throws DataAccessException {
        return new ArrayList<>(this.repository.findAll());
    }

    public ArrayList<Employee> findEmployeesWithDistinctFirstName() throws DataAccessException {
        return new ArrayList<>(this.repository.findDistinctFirstName());
    }

    public long countEligible() throws DataAccessException {
        return this.repository.countByEligibility(true);
    }

    public void addEligibilityAfterAge(Integer age) throws DataAccessException {
        ArrayList<Employee> records = new ArrayList<>(
                this.repository.findByEligibilityAndAgeAfterOrderByAgeAsc(false, age)
        );

        for (Employee curr : records) {
            this.repository.updateEmployeeEligibility(curr.getId(), true);
        }
    }

    public void batchAddEligibilityAfterAge(Integer age) throws DataAccessException {
        ArrayList<Employee> records = new ArrayList<>(
                this.repository.findByEligibilityAndAgeAfterOrderByAgeAsc(false, age)
        );

        for (Employee curr : records) {
            curr.setEligibility(true);
        }

        this.repository.saveAll(records);
    }
}


