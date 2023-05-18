package com.example.demo.service;

import com.example.demo.exception.InvalidEmployeeAgeException;
import com.example.demo.model.Employee;
import com.example.demo.repository.EmployeeDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Aside from controllers, there is no need to tell spring to instantiate the web layer let alone the whole
 * spring context. So we won't be using either of @WebMvc or @SpringBootTest here.
 *
 * Instead, we will specify our mocks using @Mock, and specify the class to inject these mocks into using
 * @InjectMocks.
 *
 * Note: We need a @BeforeEach method to inject mocks before every test using openMocks(testclass), otherwise
 * the fields of the mocked classes will be null.
 */
class EmployeeServiceTest {

    @Mock
    private EmployeeDAO repository;

    @InjectMocks
    @Autowired
    private EmployeeService service;

    private static final Employee DUMMY_EMPLOYEE =
            new Employee("John", "Wick", 55, false);

    private static final Employee[] DUMMY_EMPLOYEES = new Employee[]{
            DUMMY_EMPLOYEE, DUMMY_EMPLOYEE, DUMMY_EMPLOYEE};

    @BeforeEach
    void initMocks() {
        /** By right, openMocks returns a AutoCloseable instance, which can be used to
         * release resources used to mock.
         * However, we need to use the service instance across all tests here, so this is not
         * necessary.
         */
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveEmployee_givenEmployeeCallsSaveForEmployee_Ok() throws InvalidEmployeeAgeException {
        when(repository.save(ArgumentMatchers.any(Employee.class))).thenReturn(DUMMY_EMPLOYEE);

        Employee result = service.saveEmployee(DUMMY_EMPLOYEE);

        assertEquals(result, DUMMY_EMPLOYEE);
    }

    @Test
    void getEmployees_callsGetForAllEmployees_Ok() throws InvalidEmployeeAgeException {
        ArrayList<Employee> dummyList = new ArrayList<>(Arrays.asList(DUMMY_EMPLOYEES));

        when(repository.findAll()).thenReturn(dummyList);

        ArrayList<Employee> result = service.getEmployees();

        for (Employee e: result) {
            assertEquals(DUMMY_EMPLOYEE, e);
        }
    }
}