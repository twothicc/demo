package com.example.demo.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;


import com.example.demo.controller.response.CountEligibleResponse;
import com.example.demo.controller.response.EligibilityAfterResponse;
import com.example.demo.controller.response.EmployeeNamesResponse;
import com.example.demo.controller.response.EmployeeResponse;
import com.example.demo.controller.response.EmployeeResponseMessage;
import com.example.demo.controller.response.EmployeesResponse;
import com.example.demo.exception.InvalidEmployeeAgeException;
import com.example.demo.model.Employee;
import com.example.demo.service.EmployeeService;

import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @RestController is a shorthand for @Controller and @ResponseBody.
 * @Controller is marks classes to be scanned by Component-scan for @RequestMapping and
 * other types of request mapping, such as @GetMapping or @PostMapping.
 * @ResponseBody tells the controller that the object returned is automatically serialized into JSON and
 * then passed back into the HttpResponse object.
 */
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    private final EmployeeService service;

    public EmployeeController(EmployeeService service) {
        this.service = service;
    }

    @GetMapping("/get")
    public ResponseEntity<EmployeesResponse> getEmployees() {
        try {
            ArrayList<Employee> employees = this.service.getEmployees();
            EmployeesResponse response = new EmployeesResponse(employees, EmployeeResponseMessage.GET_ALL_SUCCESS);
            return ResponseEntity.ok(response);
        } catch (DataAccessException dae) {
            EmployeesResponse response = new EmployeesResponse(null, EmployeeResponseMessage.GET_ALL_ERROR);
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/names")
    public ResponseEntity<EmployeeNamesResponse> getDistinctEmployeeFirstNames() {
        try {
            ArrayList<String> firstNames = new ArrayList<>();
            Collection<Employee> employees = this.service.findEmployeesWithDistinctFirstName();
            employees.forEach(e -> {
                firstNames.add(e.getFirstName());
            });
            EmployeeNamesResponse response = new EmployeeNamesResponse(firstNames,
                    EmployeeResponseMessage.DISTINCT_FIRST_NAMES_SUCCESS);
            return ResponseEntity.ok(response);
        } catch (DataAccessException dae) {
            EmployeeNamesResponse response = new EmployeeNamesResponse(null,
                    EmployeeResponseMessage.DISTINCT_FIRST_NAMES_ERROR);
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/count/eligible")
    public ResponseEntity<CountEligibleResponse> getEligible() {
        try {
            long count = service.countEligible();
            CountEligibleResponse response = new CountEligibleResponse(count,
                    EmployeeResponseMessage.GET_ELIGIBLE_SUCCESS);
            return ResponseEntity.ok(response);
        } catch (DataAccessException dae) {
            CountEligibleResponse response = new CountEligibleResponse(-1,
                    EmployeeResponseMessage.GET_ELIGIBLE_ERROR);
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * @RequestBody maps the HttpRequest body to a transfer or domain object and deserializes the body into
     * a POJO (Plain Old Java Object).
     */
    @PostMapping(
            value = "/register",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    public ResponseEntity<EmployeeResponse> registerEmployee(@RequestBody Employee employee) {
        try {
            Employee registered = service.saveEmployee(employee);
            EmployeeResponse response = new EmployeeResponse(registered, EmployeeResponseMessage.SAVE_SUCCESS);
            return ResponseEntity.ok(response);
        } catch (InvalidEmployeeAgeException ieae) {
            EmployeeResponse response = new EmployeeResponse(employee, EmployeeResponseMessage.INVALID_AGE);
            return ResponseEntity.badRequest().body(response);
        } catch (DataAccessException dae) {
            EmployeeResponse response = new EmployeeResponse(employee, EmployeeResponseMessage.SAVE_ERROR);
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping(
            value = "/batch/register",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    public ResponseEntity<EmployeesResponse> batchRegisterEmployees(@RequestBody Employee[] employees) {
        try {
            ArrayList<Employee> registeredEmployees = service.batchSaveEmployee(employees);
            EmployeesResponse response = new EmployeesResponse(registeredEmployees,
                    EmployeeResponseMessage.BATCH_SAVE_SUCCESS);
            return ResponseEntity.ok(response);
        } catch (DataAccessException dae) {
            ArrayList<Employee> unregisteredEmployees = new ArrayList<>(Arrays.asList(employees));
            EmployeesResponse response = new EmployeesResponse(unregisteredEmployees,
                    EmployeeResponseMessage.BATCH_SAVE_ERROR);
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PutMapping(
            value="/addEligibilityAfter/{age}",
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    public ResponseEntity<EligibilityAfterResponse> addEligibilityAfterAge(@PathVariable Integer age) {
        try {
            service.addEligibilityAfterAge(age);
            EligibilityAfterResponse response = new EligibilityAfterResponse(age, true,
                    EmployeeResponseMessage.SET_ELIGIBILITY_AFTER_SUCCESS);
            return ResponseEntity.ok(response);
        } catch (DataAccessException dae) {
            dae.printStackTrace();
            EligibilityAfterResponse response = new EligibilityAfterResponse(age, true,
                    EmployeeResponseMessage.SET_ELIGIBILITY_AFTER_ERROR);
            return ResponseEntity.internalServerError().body(response);
        }
    }
}


