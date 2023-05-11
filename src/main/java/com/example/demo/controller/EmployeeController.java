package com.example.demo.controller;

import java.util.ArrayList;

import com.example.demo.model.Employee;
import com.example.demo.service.EmployeeService;
import org.springframework.http.MediaType;
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
    public Employee[] getEmployees() {
        return this.service.getEmployees().toArray(new Employee[0]);
    }

//    @GetMapping("/names")
//    public ArrayList<String> getDistinctEmployeeFirstNames() {
//        ArrayList<String> result = new ArrayList<>();
//        this.service.findEmployeesWithDistinctFirstName().forEach(v -> result.add(v.getFirstName()));
//
//        return result;
//    }

    /**
     * @RequestBody maps the HttpRequest body to a transfer or domain object and deserializes the body into
     * a POJO (Plain Old Java Object).
     */
    @PostMapping(
            value="/register",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    public Employee registerEmployee(@RequestBody Employee employee) {
        return service.saveEmployee(employee);
    }

    @PutMapping(
            value="/addEligibilityAfter/{age}"
    )
    public void addEligibilityAfterAge(@PathVariable Integer age) {
        service.addEligibilityAfterAge(age);
    }

    @PutMapping(
            value="/setEligibilityBetween/{begin}/{end}/{eligible}"
    )
    public void setEligibilityBetweenAge(@PathVariable Integer begin, Integer end, boolean eligible) {
        service.setEligibilityBetweenAge(eligible, begin, end);
    }

}


