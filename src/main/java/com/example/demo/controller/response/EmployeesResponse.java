package com.example.demo.controller.response;

import java.util.ArrayList;

import com.example.demo.model.Employee;

public class EmployeesResponse {
    private final ArrayList<Employee> employees;
    private final String msg;

    public EmployeesResponse(ArrayList<Employee> employees, String msg) {
        this.employees = employees;
        this.msg = msg;
    }

    public ArrayList<Employee> getEmployees() {
        return employees;
    }

    public String getMsg() {
        return msg;
    }
}
