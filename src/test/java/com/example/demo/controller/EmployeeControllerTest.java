package com.example.demo.controller;

import com.example.demo.schema.Employee;
import com.example.demo.schema.EmployeeWithId;
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
    private TestRestTemplate restTemplate;

    ResponseEntity<EmployeeWithId> postEmployee(Employee employee) {
        HttpEntity<Employee> request = new HttpEntity<>(employee);

        return this.restTemplate.postForEntity(
                String.format(urlTemplate, port, "post"), request, EmployeeWithId.class);
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
                        EmployeeWithId[].class).length
        );
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void getEmployees_NonEmptyDatabaseNonEmptyEmployees() {
        for (int i = 0; i < 5; i++) {
            Employee employee = new Employee("John", String.valueOf(i));
            ResponseEntity<EmployeeWithId> result = postEmployee(employee);

            assertEquals(result.getStatusCode(), HttpStatus.OK);
        }

        EmployeeWithId[] result = restTemplate.getForObject(
                String.format(urlTemplate, port, "get"),
                EmployeeWithId[].class
        );

        assertEquals(5, result.length);
        for (int i = 0; i < 5; i++) {
            EmployeeWithId curr = result[i];

            assertEquals(i + 1, curr.getId());
            assertEquals("John", curr.getFirstName());
            assertEquals(String.valueOf(i), curr.getLastName());
        }
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void postEmployee_addSingleEmployeeSingleEmployeeAdded() {
        Employee employee = new Employee("John", "Wick");
        ResponseEntity<EmployeeWithId> result = postEmployee(employee);

        EmployeeWithId resultBody = result.getBody();

        assertNotNull(resultBody);
        assertEquals(1, resultBody.getId());
        assertEquals("John", resultBody.getFirstName());
        assertEquals("Wick", resultBody.getLastName());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void postEmployee_addEmployeeWithDuplicateNameBadRequest() {
        Employee employee = new Employee("John", "Wick");
        ResponseEntity<EmployeeWithId> result1 = postEmployee(employee);

        assertEquals(HttpStatus.OK, result1.getStatusCode());

        ResponseEntity<EmployeeWithId> result2 = postEmployee(employee);
        assertEquals(HttpStatus.BAD_REQUEST, result2.getStatusCode());
        assertEquals(new EmployeeWithId(-1, "", ""), result2.getBody());
    }
}