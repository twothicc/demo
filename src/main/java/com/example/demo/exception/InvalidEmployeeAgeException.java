package com.example.demo.exception;

public class InvalidEmployeeAgeException extends Exception {
    private static final String template = "%d is not a valid age";

    public InvalidEmployeeAgeException(Integer age) {
        super(String.format(template, age));
    }
}
