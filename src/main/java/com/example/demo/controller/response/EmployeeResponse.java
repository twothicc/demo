package com.example.demo.controller.response;

import com.example.demo.model.Employee;

public class EmployeeResponse {
    private final Employee employee;
    private final String msg;

    public EmployeeResponse(Employee employee, String msg) {
        this.employee = employee;
        this.msg = msg;
    }

    public Employee getEmployee() {
        return employee;
    }

    public String getMsg() {
        return msg;
    }
}
