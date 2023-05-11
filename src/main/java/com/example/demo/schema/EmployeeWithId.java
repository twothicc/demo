package com.example.demo.schema;

public class EmployeeWithId extends Employee {

    private long id;

    public EmployeeWithId(long id, String firstName, String lastName) {
        super(firstName, lastName);
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
