package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @SpringBootApplication is shorthand for @Configuration, @EnableAutoConfiguration, @ComponentScan.
 * @Configuration tags the DemoApplication class as the source of bean definitions for the application context.
 * @EnableAutoConfiguration allows adding of beans based on class path settings, other beans, and various property
 * settings.
 * @ComponentScan tells SpringBoot to automatically scan within the package for classes marked with @Component, or
 * any other annotations that is a sub-type of it. i.e. @Service, @Controller, @Repository.
 */
@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
