package com.example.demo.exception;

import com.example.demo.model.Employee;

public class DuplicateEmployeeException extends Exception {
    private static final String template = "%s %s is a duplicate name";

    public DuplicateEmployeeException(Employee employee) {
        super(String.format(template, employee.getFirstName(), employee.getLastName()));
    }
}
