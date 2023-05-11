package com.example.demo.database;

import com.example.demo.exception.DuplicateEmployeeException;
import com.example.demo.schema.Employee;
import com.example.demo.schema.EmployeeWithId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicLong;

public class DB {
    private HashMap<Long, Employee> employees;
    private HashSet<Employee> reverseEmployees;
    private final AtomicLong counter = new AtomicLong();

    public DB() {
        if (this.employees == null) {
            this.employees = new HashMap<>();
            this.reverseEmployees = new HashSet<>();
        }
    }

    public long addEmployee(Employee employee) throws DuplicateEmployeeException {
        if (reverseEmployees.contains(employee)) {
            throw new DuplicateEmployeeException(employee);
        }

        long id = counter.incrementAndGet();
        this.employees.put(id, employee);
        this.reverseEmployees.add(employee);

        return id;
    }

    public ArrayList<EmployeeWithId> getEmployees() {
        ArrayList<EmployeeWithId> employeeWithIds = new ArrayList<>();
        this.employees.forEach((k, v) -> {
            employeeWithIds.add(new EmployeeWithId(k, v.getFirstName(), v.getLastName()));
        });

        return employeeWithIds;
    }
}
