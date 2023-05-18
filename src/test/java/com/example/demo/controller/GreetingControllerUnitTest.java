package com.example.demo.controller;

import com.example.demo.model.Greeting;
import com.example.demo.service.GreetingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * GreetingControllerTest shows how we can only instantiate the web layer rather than the whole spring context.
 * In this case, we can specify to have only the GreetingController instantiated.
 */
@WebMvcTest(GreetingController.class)
class GreetingControllerUnitTest {

    private static final String url = "/greeting/get";

    @Autowired
    private MockMvc mockMvc;

    /**
     * Adding greetingService as a mock bean allows us to specify to Mockito exactly
     * what we want returned from the service's methods (Can even configure it to return
     * something only when a certain parameter is received by the method)
     */
    @MockBean
    private GreetingService greetingService;

    /**
     * DirtiesContext is a Spring testing annotation. It indicates that the test/class modifies the ApplicationContext.
     * Tells the testing framework (in this case JUnit4) to close and recreate the context for later tests.
     * By specifying AFTER_METHOD, we tell JUnit to recreate the context after the method, and in this case allow
     * the auto-incrementing id to be reset to 1.
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void greeting_ReturnsDefaultMsg() throws Exception {
        /**
         * Configures mockito to return a greeting with content "mock" when any kind of String param is provided
         */
        when(greetingService.greet(any(String.class))).thenReturn(new Greeting(1, "mock"));
        this.mockMvc.perform(
                get(url)).andDo(print())
                .andExpect(jsonPath("$.content").value("mock"))
                .andExpect(jsonPath("$.id").value(1));
    }
}