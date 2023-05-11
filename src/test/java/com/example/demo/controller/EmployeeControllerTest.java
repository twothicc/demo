package com.example.demo.controller;

import com.example.demo.model.Employee;
import com.example.demo.repository.EmployeeRepository;

import org.junit.After;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;

/**
 * EmployeeControllerTest shows how to perform tests by starting the server.
 * Another way to avoid starting the server and instead have Spring pass http requests to controllers
 * is to use @AutoConfigureMockMvc. However, take note that this method still loads the entire Spring context,
 * just without the server.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EmployeeControllerTest {

    private final static String urlTemplate = "http://localhost:%d/employee/%s";

    @Value(value = "${local.server.port}")
    private int port;

    @Autowired
    private EmployeeController employeeController;

    @Autowired
    private EmployeeRepository repository;

    @Autowired
    private TestRestTemplate restTemplate;

    ResponseEntity<Employee> postEmployee(Employee employee) {
        HttpEntity<Employee> request = new HttpEntity<>(employee);

        return this.restTemplate.postForEntity(
                String.format(urlTemplate, port, "register"), request, Employee.class);
    }

    @After
    public void resetEmployeeesTable() {
        repository.deleteAll();
    }

    /**
     * contextLoads verifies if Spring is able to load the application context successfully.
     */
    @Test
    void contextLoads() {
        assertNotNull(this.employeeController);
    }

    @Test
    void getEmployees_EmptyDatabaseEmptyEmployees() {
        assertEquals(0,
                restTemplate.getForObject(String.format(urlTemplate, port, "get"),
                        Employee[].class).length
        );
    }

    @Test
    void getEmployees_NonEmptyDatabaseNonEmptyEmployees() {
        Random rand = new Random();

        for (int i = 0; i < 5; i++) {
            Employee employee = new Employee("John", String.valueOf(i),
                    rand.nextInt(0, 100), false);
            ResponseEntity<Employee> result = postEmployee(employee);

            assertEquals(result.getStatusCode(), HttpStatus.OK);
        }

        Employee[] result = restTemplate.getForObject(
                String.format(urlTemplate, port, "get"),
                Employee[].class
        );

        assertEquals(5, result.length);
        for (int i = 0; i < 5; i++) {
            Employee curr = result[i];

            assertEquals("John", curr.getFirstName());
            assertEquals(String.valueOf(i), curr.getLastName());
        }
    }

    @Test
    void postEmployee_addSingleEmployeeSingleEmployeeAdded() {
        Employee employee = new Employee("John", "Wick", 55, false);
        ResponseEntity<Employee> result = postEmployee(employee);

        Employee resultBody = result.getBody();

        assertNotNull(resultBody);
        assertEquals("John", resultBody.getFirstName());
        assertEquals("Wick", resultBody.getLastName());
    }
}