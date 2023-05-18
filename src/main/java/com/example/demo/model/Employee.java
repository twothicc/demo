package com.example.demo.model;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(name = "Employees", indexes = @Index(name = "eligibilityAgeIndex", columnList = "eligibility, age ASC"))
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
    private boolean eligibility;

    public Employee() {}

    public Employee(Long id, String firstName, String lastName, Integer age, boolean eligibility) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.eligibility = eligibility;
    }

    public Employee(String firstName, String lastName, Integer age, boolean eligibility) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.eligibility = eligibility;
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

    public boolean isEligibility() {
        return eligibility;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public void setEligibility(boolean eligibility) {
        this.eligibility = eligibility;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Employee other) {
            return Objects.equals(this.id, other.id) && this.firstName.equals(other.firstName)
                    && this.lastName.equals(other.lastName) && this.eligibility == other.eligibility;
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

