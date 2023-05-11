package com.example.demo.service;

import com.example.demo.repository.EmployeeRepository;
import com.example.demo.model.Employee;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

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

    public Employee saveEmployee(Employee employee) {
        return this.repository.save(employee);
    }

    public ArrayList<Employee> getEmployees() {
        System.out.println("yolo");
        System.out.println(this.repository.findAll());
        System.out.println("yolo");
        return new ArrayList<>(this.repository.findAll());
    }

//    public ArrayList<Employee> findEmployeesWithDistinctFirstName() {
//        return new ArrayList<>(this.repository.findDistinctFirstName());
//    }

    public void addEligibilityAfterAge(Integer age) {
        ArrayList<Employee> records = new ArrayList<>(
                this.repository.findByAgeAfterOrderByAgeAsc(age)
        );

        for (Employee curr : records) {
            this.repository.updateEmployeeEligibility(curr.getId(), true);
        }
    }

    public void setEligibilityBetweenAge(boolean isEligible, Integer startAge, Integer endAge) {
        ArrayList<Employee> records = new ArrayList<>(
                this.repository.findByAgeBetweenOrderByAgeAsc(startAge, endAge)
        );

        for (Employee curr : records) {
            this.repository.updateEmployeeEligibility(curr.getId(), isEligible);
        }
    }
}


