package com.example.demo.service;

import com.example.demo.schema.Greeting;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Service
public class GreetingService {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    public Greeting greet(String name) {
        return new Greeting(counter.incrementAndGet(), String.format(template, name));
    }
}
