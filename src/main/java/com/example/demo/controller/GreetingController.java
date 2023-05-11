package com.example.demo.controller;

import com.example.demo.model.Greeting;
import com.example.demo.service.GreetingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/greeting")
public class GreetingController {

    private final GreetingService service;

    public GreetingController(GreetingService service) {
        this.service = service;
    }

    /**
     * @RequestParam maps URL query param. For instance, localhost:8080?name=Jack, "Jack" here is the request param
     * @PathVariable maps URL path variables. For instance, localhost:8080/books/1, "1" here is the path variable
     */
    @GetMapping("/get")
    public Greeting greeting(@RequestParam(value = "name", defaultValue="World") String name) {
        return service.greet(name);
    }
}
