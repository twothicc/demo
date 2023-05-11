package com.example.demo.model;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "first_name", nullable = false)
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "age", nullable = false)
    private Integer age;
    @Column(name = "eligibility", nullable = false, columnDefinition = "bit")
    private boolean isEligible;

    public Employee() {}

    public Employee(String firstName, String lastName, Integer age, boolean isEligible) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.isEligible = isEligible;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Integer getAge() {
        return age;
    }

    public boolean isEligible() {
        return isEligible;
    }

    public void setEligible(boolean eligible) {
        isEligible = eligible;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Employee other) {
            return Objects.equals(this.id, other.id);
        }

        return false;
    }

    @Override
    public String toString() {
        return String.format("%s %s", this.firstName, this.lastName);
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }
}

