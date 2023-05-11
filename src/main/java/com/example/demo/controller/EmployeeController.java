package com.example.demo.controller;

import com.example.demo.exception.DuplicateEmployeeException;
import com.example.demo.schema.Employee;
import com.example.demo.schema.EmployeeWithId;
import com.example.demo.service.EmployeeService;
import org.springframework.http.HttpStatus;
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
    public EmployeeWithId[] getEmployees() {
        return this.service.getEmployees().toArray(new EmployeeWithId[0]);
    }

    /**
     * @RequestBody maps the HttpRequest body to a transfer or domain object and deserializes the body into
     * a POJO (Plain Old Java Object).
     */
    @PostMapping(
            value="/post",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    public ResponseEntity<EmployeeWithId> postEmployee(@RequestBody Employee employee) {
        EmployeeWithId result;

        try {
            result = service.addEmployee(employee);
        } catch (DuplicateEmployeeException e) {
            return ResponseEntity.badRequest().body(
                    new EmployeeWithId(-1, "", "")
            );
        }

        /**
         * Using ResponseEntity here allows us to control the header and status of the http response
         */
        return ResponseEntity.status(HttpStatus.OK)
                .body(result);
    }
}


