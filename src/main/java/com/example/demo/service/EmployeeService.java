package com.example.demo.service;

import com.example.demo.database.DB;
import com.example.demo.exception.DuplicateEmployeeException;
import com.example.demo.schema.Employee;
import com.example.demo.schema.EmployeeWithId;
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
    private final DB db = new DB();

    public EmployeeWithId addEmployee(Employee employee) throws DuplicateEmployeeException {
        long id = this.db.addEmployee(employee);

        return new EmployeeWithId(id, employee.getFirstName(), employee.getLastName());
    }

    public ArrayList<EmployeeWithId> getEmployees() {
        return this.db.getEmployees();
    }
}


