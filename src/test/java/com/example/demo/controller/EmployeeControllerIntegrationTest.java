package com.example.demo.controller;

import com.example.demo.controller.response.CountEligibleResponse;
import com.example.demo.controller.response.EligibilityAfterResponse;
import com.example.demo.controller.response.EmployeeNamesResponse;
import com.example.demo.controller.response.EmployeeResponse;
import com.example.demo.controller.response.EmployeeResponseMessage;
import com.example.demo.controller.response.EmployeesResponse;
import com.example.demo.exception.InvalidEmployeeAgeException;
import com.example.demo.model.Employee;
import com.example.demo.repository.EmployeeDAO;
import com.example.demo.service.EmployeeService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static java.lang.Math.ceil;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.*;

/**
 * EmployeeControllerTest shows how to perform tests by starting the server.
 * Another way to avoid starting the server and instead have Spring pass http requests to controllers
 * is to use @AutoConfigureMockMvc. However, take note that this method still loads the entire Spring context,
 * just without the server.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EmployeeControllerIntegrationTest {

    private final static String urlTemplate = "http://localhost:%d/employee/%s";
//    private final static double REPLICATION_DELAY = 10.0;
    private final static double REPLICATION_DELAY = 0.0;

    @Value(value = "${local.server.port}")
    private int port;

    @Autowired
    private EmployeeController employeeController;

    @Autowired
    private EmployeeDAO repository;

    @Autowired
    private TestRestTemplate restTemplate;

    /**
     * @SpyBean annotation mocks only specific methods using "when-then" and leaves
     * all others working as implemented.
     */
    @SpyBean
    private EmployeeService employeeService;

    private ResponseEntity<EmployeeResponse> registerEmployee(Employee employee) {
        HttpEntity<Employee> request = new HttpEntity<>(employee);

        return this.restTemplate.postForEntity(
                String.format(urlTemplate, port, "register"), request, EmployeeResponse.class);
    }

    private ResponseEntity<EmployeesResponse> batchRegisterEmployee(Employee[] employees) {
        HttpEntity<Employee[]> request = new HttpEntity<>(employees);

        return this.restTemplate.postForEntity(
                String.format(urlTemplate, port, "batch/register"), request, EmployeesResponse.class);
    }

    private void waitForReplication(double seconds) {
        try {
            Thread.sleep((long) (seconds * 1000));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    Employee saveAndFlush(Employee employee) {
        return this.repository.saveAndFlush(employee);
    }

    List<Employee> batchSaveAndFlush(Employee[] employees) {
        return this.repository.saveAllAndFlush(Arrays.asList(employees));
    }

    /**
     * From JUnit5 onwards, @Before/@After is now @BeforeEach/@AfterEach.
     * @AfterEach runs after the test even if exception is thrown.
     */
    @BeforeEach
    public void resetEmployeeesTable() {
        repository.deleteAll();
        repository.flush();

        waitForReplication(REPLICATION_DELAY);
    }

    /**
     * contextLoads verifies if Spring is able to load the application context successfully.
     */
    @Test
    void contextLoads() {
        assertNotNull(this.employeeController);
    }

    @Test
    void getEmployees_emptyDatabaseEmptyEmployees_Ok() {
        ResponseEntity<EmployeesResponse> response = restTemplate.getForEntity(
                String.format(urlTemplate, port, "get"), EmployeesResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        EmployeesResponse responseBody = response.getBody();

        assertNotNull(responseBody);
        assertEquals(0, responseBody.getEmployees().size());
        assertEquals(EmployeeResponseMessage.GET_ALL_SUCCESS, responseBody.getMsg());
    }

    @Test
    void getEmployees_throwDataAccessException_InternalServerError() {
        when(employeeService.getEmployees()).thenThrow(new DataAccessException("...") {});

        ResponseEntity<EmployeesResponse> response = restTemplate.getForEntity(
                String.format(urlTemplate, port, "get"), EmployeesResponse.class);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        EmployeesResponse responseBody = response.getBody();

        assertNotNull(responseBody);
        assertNull(responseBody.getEmployees());
        assertEquals(EmployeeResponseMessage.GET_ALL_ERROR, responseBody.getMsg());
    }

    @Test
    void getEmployees_nonEmptyDatabaseNonEmptyEmployees_Ok() {
        Random rand = new Random();

        for (int i = 0; i < 5; i++) {
            Employee employee = new Employee("John", String.valueOf(i),
                    rand.nextInt(0, 100), false);
            ResponseEntity<EmployeeResponse> result = registerEmployee(employee);

            assertEquals(result.getStatusCode(), HttpStatus.OK);
        }

        // flush to commit changes immediately to master so it can be replicated to slave
        repository.flush();

        waitForReplication(REPLICATION_DELAY);

        EmployeesResponse responseBody = restTemplate.getForObject(
                String.format(urlTemplate, port, "get"),
                EmployeesResponse.class
        );

        assertNotNull(responseBody);

        ArrayList<Employee> employees = responseBody.getEmployees();
        assertEquals(5, employees.size());
        for (int i = 0; i < 5; i++) {
            Employee curr = employees.get(i);

            assertEquals("John", curr.getFirstName());
            assertEquals(String.valueOf(i), curr.getLastName());
        }
    }

    @Test
    void registerEmployee_addSingleEmployeeSingleEmployeeAdded_Ok() throws InvalidEmployeeAgeException {
        Employee employee = new Employee("John", "Wick", 55, false);
        ResponseEntity<EmployeeResponse> response = registerEmployee(employee);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        EmployeeResponse responseBody = response.getBody();

        assertNotNull(responseBody);
        assertEquals(EmployeeResponseMessage.SAVE_SUCCESS, responseBody.getMsg());

        Employee responseEmployee = responseBody.getEmployee();

        assertEquals("John", responseEmployee.getFirstName());
        assertEquals("Wick", responseEmployee.getLastName());
        assertEquals(55, responseEmployee.getAge());
        assertFalse(responseEmployee.isEligibility());
    }

    @Test
    void registerEmployee_registerEmployeeWithNullLastNameS_Ok() throws InvalidEmployeeAgeException {
        Employee employee = new Employee("John", null, 55, false);
        ResponseEntity<EmployeeResponse> response = registerEmployee(employee);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        EmployeeResponse responseBody = response.getBody();

        assertNotNull(responseBody);
        assertEquals(EmployeeResponseMessage.SAVE_SUCCESS, responseBody.getMsg());

        Employee responseEmployee = responseBody.getEmployee();

        assertEquals("John", responseEmployee.getFirstName());
        assertNull(responseEmployee.getLastName());
        assertEquals(55, responseEmployee.getAge());
        assertFalse(responseEmployee.isEligibility());
    }

    @Test
    void registerEmployee_registerEmployeeWithNullFirstName_BadRequestSaveError() throws InvalidEmployeeAgeException {
        Employee employee = new Employee(null, "Wick", 55, false);

        ResponseEntity<EmployeeResponse> response = registerEmployee(employee);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        EmployeeResponse responseBody = response.getBody();

        assertNotNull(responseBody);
        assertEquals(EmployeeResponseMessage.SAVE_ERROR, responseBody.getMsg());

        Employee responseEmployee = responseBody.getEmployee();
        assertNull(responseEmployee.getFirstName());
        assertEquals("Wick", responseEmployee.getLastName());
        assertEquals(55, responseEmployee.getAge());
        assertFalse(responseEmployee.isEligibility());
    }

    @Test
    void registerEmployee_registerEmployeeWithNullAge_BadRequestInvalidAge() throws InvalidEmployeeAgeException {
        Employee employee = new Employee("John", "Wick", null, false);

        ResponseEntity<EmployeeResponse> response = registerEmployee(employee);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        EmployeeResponse responseBody = response.getBody();

        assertNotNull(responseBody);
        assertEquals(EmployeeResponseMessage.INVALID_AGE, responseBody.getMsg());

        Employee responseEmployee = responseBody.getEmployee();
        assertEquals("John", responseEmployee.getFirstName());
        assertEquals("Wick", responseEmployee.getLastName());
        assertNull(responseEmployee.getAge());
        assertFalse(responseEmployee.isEligibility());
    }

    @Test
    void registerEmployee_registerEmployeeWithNegativeAge_BadRequestInvalidAge() throws InvalidEmployeeAgeException {
        Employee employee = new Employee("John", "Wick", -1, false);

        ResponseEntity<EmployeeResponse> response = registerEmployee(employee);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        EmployeeResponse responseBody = response.getBody();

        assertNotNull(responseBody);
        assertEquals(EmployeeResponseMessage.INVALID_AGE, responseBody.getMsg());

        Employee responseEmployee = responseBody.getEmployee();
        assertEquals("John", responseEmployee.getFirstName());
        assertEquals("Wick", responseEmployee.getLastName());
        assertEquals(-1, responseEmployee.getAge());
        assertFalse(responseEmployee.isEligibility());
    }

    @Test
    void batchRegisterEmployee_registerEmployees_Ok() {
        Employee employee1 = new Employee("John", "Wick1", 55, false);
        Employee employee2 = new Employee("John", "Wick2", 55, false);
        Employee employee3 = new Employee("John", "Wick3", 55, false);
        Employee employee4 = new Employee("John", "Wick4", 55, false);
        Employee[] employees = new Employee[]{employee1, employee2, employee3, employee4};

        ResponseEntity<EmployeesResponse> response = batchRegisterEmployee(employees);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        EmployeesResponse responseBody = response.getBody();

        assertNotNull(responseBody);
        assertEquals(EmployeeResponseMessage.BATCH_SAVE_SUCCESS, responseBody.getMsg());

        ArrayList<Employee> responseEmployees = responseBody.getEmployees();
        responseEmployees.sort(Comparator.comparing(Employee::getLastName));
        for (int i = 0; i < responseEmployees.size(); i++) {
            Employee responseEmployee = responseEmployees.get(i);
            assertEquals("John", responseEmployee.getFirstName());
            assertEquals("Wick" + (i + 1), responseEmployee.getLastName());
            assertEquals(55, responseEmployee.getAge());
            assertFalse(responseEmployee.isEligibility());
        }
    }

    @Test
    void batchRegisterEmployee_throwDataAccessException_InternalServerError() {
        /**
         * For some reason, any() here causes null to passes as the argument.
         * This happens even if eq() is used so idk.
         * Just handle null input in the service class.
         */
        when(employeeService.batchSaveEmployee(any(Employee[].class))).thenThrow(
                new DataAccessException("...") {});

        Employee[] employees = new Employee[]{};

        ResponseEntity<EmployeesResponse> response = batchRegisterEmployee(employees);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        EmployeesResponse responseBody = response.getBody();

        assertNotNull(responseBody);
        assertEquals(EmployeeResponseMessage.BATCH_SAVE_ERROR, responseBody.getMsg());
        assertNotNull(responseBody.getEmployees());
        assertEquals(0, responseBody.getEmployees().size());
    }

    @Test
    void getDistinctEmployeeFirstNames_multipleEmployeesWithSameFirstName_DistinctFirstNames() throws InvalidEmployeeAgeException {
        Employee employee1 = new Employee("John", "Wick", 55, false);
        Employee employee2 = new Employee("John", "Wick", 55, false);
        Employee employee3 = new Employee("John", "Wick", 55, false);
        Employee employee4 = new Employee("Born", "Wick", 55, false);
        Employee[] employees = new Employee[]{employee1, employee2, employee3, employee4};

        batchSaveAndFlush(employees);

        waitForReplication(REPLICATION_DELAY);

        ResponseEntity<EmployeeNamesResponse> response = restTemplate.getForEntity(
                String.format(urlTemplate, port, "names"), EmployeeNamesResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        EmployeeNamesResponse responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(EmployeeResponseMessage.DISTINCT_FIRST_NAMES_SUCCESS, responseBody.getMsg());

        ArrayList<String> firstNames = responseBody.getNames();

        assertNotNull(firstNames);
        Collections.sort(firstNames);
        assertEquals(2, firstNames.size());
        assertEquals("Born", firstNames.get(0));
        assertEquals("John", firstNames.get(1));
    }

    @Test
    void getDistinctEmployeeFirstNames_throwDataAccessException_InternalServerError() throws InvalidEmployeeAgeException {
        when(employeeService.findEmployeesWithDistinctFirstName()).thenThrow(new DataAccessException("..."){});

        ResponseEntity<EmployeeNamesResponse> response = restTemplate.getForEntity(
                String.format(urlTemplate, port, "names"), EmployeeNamesResponse.class);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        EmployeeNamesResponse responseBody = response.getBody();
        assertNotNull(responseBody);
        assertNull(responseBody.getNames());
        assertEquals(EmployeeResponseMessage.DISTINCT_FIRST_NAMES_ERROR, responseBody.getMsg());
    }

    @Test
    void countEligible_mockReturn3Count3_Ok() {
        when(employeeService.countEligible()).thenReturn(3L);

        ResponseEntity<CountEligibleResponse> response = restTemplate.getForEntity(
                String.format(urlTemplate, port, "count/eligible"), CountEligibleResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        CountEligibleResponse responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(3, responseBody.getCount());
        assertEquals(EmployeeResponseMessage.GET_ELIGIBLE_SUCCESS, responseBody.getMsg());
    }

    @Test
    void countEligible_throwDataAccessException_InternalServerError() {
        when(employeeService.countEligible()).thenThrow(
                new DataAccessException("...") {});

        ResponseEntity<CountEligibleResponse> response = restTemplate.getForEntity(
                String.format(urlTemplate, port, "count/eligible"), CountEligibleResponse.class);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        CountEligibleResponse responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(-1, responseBody.getCount());
        assertEquals(EmployeeResponseMessage.GET_ELIGIBLE_ERROR, responseBody.getMsg());
    }

    /**
     * There is no library method for PUT requests in TestRestTemplate, so need to use exchange method
     * which require us to specify http method and http request entity.
     */
    @Test
    void addEligibilityAfterAge_SetEligibilityToTrueAfterHalf_Ok() {
        int size = 5;
        int mid = (int) (ceil(size / 2)) - 1;

        Employee[] employees = new Employee[size];
        for (int i = 0; i < size; i++) {
            String name = i < size/2 ? "John" : "Born";
            employees[i] = new Employee(name, "Wick" + i, i, false);
        }

        batchSaveAndFlush(employees);

        waitForReplication(REPLICATION_DELAY);

        ResponseEntity<EligibilityAfterResponse> response = restTemplate.exchange(
                String.format(urlTemplate, port, "/addEligibilityAfter/" + mid),
                HttpMethod.PUT,
                null,
                EligibilityAfterResponse.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());

        EligibilityAfterResponse responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(EmployeeResponseMessage.SET_ELIGIBILITY_AFTER_SUCCESS, responseBody.getMsg());
        assertEquals(mid, responseBody.getAfterAge());
        assertTrue(responseBody.getEligibilitySet());

        // need to flush to commit results to db for testing purposes
        repository.flush();

        waitForReplication(REPLICATION_DELAY);

        CountEligibleResponse countEligibleResponse = restTemplate.getForObject(
                String.format(urlTemplate, port, "count/eligible"), CountEligibleResponse.class);
        assertNotNull(countEligibleResponse);
        assertEquals(size - (mid + 1), countEligibleResponse.getCount());
    }

    /**
     * When mocking exception thrown by a method that returns void, a different syntax is required
     * as shown below.
     */
    @Test
    void addEligibilityAfterAge_throwDataAccessException_InternalServerError() {
        doThrow(new DataAccessException("...") {}).when(employeeService).addEligibilityAfterAge(anyInt());

        Employee employee = new Employee("John", "Wick", 55, false);
        ResponseEntity<EmployeeResponse> saveResponse = registerEmployee(employee);

        assertEquals(HttpStatus.OK, saveResponse.getStatusCode());

        ResponseEntity<EligibilityAfterResponse> response = restTemplate.exchange(
                String.format(urlTemplate, port, "/addEligibilityAfter/" + 0),
                HttpMethod.PUT,
                null,
                EligibilityAfterResponse.class
        );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        EligibilityAfterResponse responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(EmployeeResponseMessage.SET_ELIGIBILITY_AFTER_ERROR, responseBody.getMsg());
        assertEquals(0, responseBody.getAfterAge());
        assertTrue(responseBody.getEligibilitySet());

        CountEligibleResponse countEligibleResponse = restTemplate.getForObject(
                String.format(urlTemplate, port, "count/eligible"), CountEligibleResponse.class);
        assertNotNull(countEligibleResponse);
        assertEquals(0, countEligibleResponse.getCount());
    }

    @Test
    void batchAddEligibilityAfterAge_SetEligibilityToTrueAfterHalf_Ok() {
        int size = 50;
        int mid = (int) (ceil(size / 2)) - 1;

        Employee[] employees = new Employee[size];
        for (int i = 0; i < size; i++) {
            String name = i < size/2 ? "John" : "Born";
            employees[i] = new Employee(name, "Wick" + i, i, false);
        }

        batchSaveAndFlush(employees);

        waitForReplication(REPLICATION_DELAY);

        ResponseEntity<EligibilityAfterResponse> response = restTemplate.exchange(
                String.format(urlTemplate, port, "/batch/addEligibilityAfter/" + mid),
                HttpMethod.PUT,
                null,
                EligibilityAfterResponse.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());

        EligibilityAfterResponse responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(EmployeeResponseMessage.SET_ELIGIBILITY_AFTER_SUCCESS, responseBody.getMsg());
        assertEquals(mid, responseBody.getAfterAge());
        assertTrue(responseBody.getEligibilitySet());

        repository.flush();

        waitForReplication(REPLICATION_DELAY);

        CountEligibleResponse countEligibleResponse = restTemplate.getForObject(
                String.format(urlTemplate, port, "count/eligible"), CountEligibleResponse.class);
        assertNotNull(countEligibleResponse);
        assertEquals(size - (mid + 1), countEligibleResponse.getCount());
    }

    @Test
    void masterSlaveDB_employeesInsertedInMasterReplicatedInSlave_Ok() {
        Employee employee1 = new Employee("John", "Wick1", 55, false);
        Employee employee2 = new Employee("John", "Wick2", 55, false);
        Employee employee3 = new Employee("John", "Wick3", 55, false);
        Employee employee4 = new Employee("John", "Wick4", 55, false);
        Employee[] employees = new Employee[]{employee1, employee2, employee3, employee4};

        batchSaveAndFlush(employees);

        waitForReplication(REPLICATION_DELAY);

        EmployeesResponse slaveResponseBody = restTemplate.getForObject(
                String.format(urlTemplate, port, "get"), EmployeesResponse.class);

        assertNotNull(slaveResponseBody);

        ArrayList<Employee> slaveResponseEmployees = slaveResponseBody.getEmployees();
        assertEquals(employees.length, slaveResponseEmployees.size());
        for (int i = 0; i < slaveResponseEmployees.size(); i++) {
            Employee slaveResponseEmployee = slaveResponseEmployees.get(i);
            assertEquals("John", slaveResponseEmployee.getFirstName());
            assertEquals("Wick" + (i + 1), slaveResponseEmployee.getLastName());
            assertEquals(55, slaveResponseEmployee.getAge());
            assertFalse(slaveResponseEmployee.isEligibility());
        }
    }

    @Test
    void masterSlaveDB_employeeUpdatedInMasterReplicatedInSlave_Ok() {
        Employee employee = new Employee("John", "Wick1", 55, false);
        saveAndFlush(employee);

        employee.setEligibility(true);

        // update and flush employee
        Employee savedEmployee = saveAndFlush(employee);

        waitForReplication(REPLICATION_DELAY);

        Optional<Employee> slaveEmployee = this.repository.findById(savedEmployee.getId());
        assertTrue(slaveEmployee.isPresent());
        assertTrue(slaveEmployee.get().isEligibility());
    }

    @Test
    void masterSlaveDB_employeeDeletedInMasterReplicatedInSlave_Ok() {
        Employee employee = new Employee("John", "Wick1", 55, false);
        Employee savedEmployee = saveAndFlush(employee);

        Long id = savedEmployee.getId();

        Optional<Employee> check = this.repository.findByIdInMaster(id);
        assertTrue(check.isPresent());
        assertEquals(id, check.get().getId());

        // delete and flush employee
        this.repository.deleteById(id);
        this.repository.flush();

        waitForReplication(REPLICATION_DELAY);

        Optional<Employee> slaveEmployee = this.repository.findById(id);
        assertFalse(slaveEmployee.isPresent());
    }

    @Test
    void testTransaction_employeeChangesRolledBack() {
        Employee e = new Employee("John", "Wick", 55, false);

        Employee e1 = repository.saveAndFlush(e);

        Long e1Id = e1.getId();

        try {
            employeeService.testTransaction(e1);
        } catch (IndexOutOfBoundsException ex) {
            System.out.println("transaction failed halfway");
        }

        Optional<Employee> masterE = repository.findByIdInMaster(e1Id);
        assertTrue(masterE.isPresent());
        assertEquals("John", masterE.get().getFirstName());
        assertEquals("Wick", masterE.get().getLastName());

        waitForReplication(REPLICATION_DELAY);

        Optional<Employee> slaveE = repository.findById(e1Id);
        assertTrue(slaveE.isPresent());
        assertEquals("John", slaveE.get().getFirstName());
        assertEquals("Wick", slaveE.get().getLastName());
    }
}