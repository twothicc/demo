# Spring Boot
Spring Boot is a java backend framework that makes developing backend services faster and easier

The Application Context is often used as the Spring container and is used to manage Spring beans

SpringBeans are objects that the Spring container instantiates, assembles, and manages

# Spring Annotations
Spring Boot uses annotations heavily as a form of metadata to provide info about a program
to the Application Context

## @SpringBootApplication
@SpringBootApplication is the equivalent of the following annotations:
- @Configuration: Tags the class as a source of bean definitions for the Application Context
- @EnableAutoConfiguration: Allows adding of beans based on class path settings, other beans and various
property settings
- @ComponentScan: Tells SpringBoot to automatically look for classes marked with @Component or any subtype of it
in the current package (recursively)

Typically used to annotate the main class

## @RestController
@RestController is the equivalent of the following annotations:
- @Controller: Indicates that the class is a classic controller (MVC)
- @ResponseBody: Enables auto-serialization of the return object into HttpResponse

Spring uses the Jackson JSON library to automatically marshal instances of a particular type into JSON

Typically used to annotate controller classes

## @RequestMapping / @GetMapping / @PostMapping etc
@RequestMapping is used to route requests to controllers

Can add headers via the headers attribute of @RequestMapping

## @RequestBody and @PathVariable
@RequestBody enables automatic deserialization of the inbound HttpRequest body into a POJO

For instance, using the @RequestBody on the function argument will automatically deserialize the request
body into the object type of the argument

@PathVariable extracts the templated part of the URL

For instance, in the url "/employees/{id}", using the @PathVariable on the function argument will map
the string value in {id} to the argument

## @Service
@Service is used to annotate classes that provide business logic

It is typically used to separate business logic from data manipulation logic in the controllers

## @Value
@Value is used to map environment variables specified in the application.properties to the field it is applied
on

## @Component & @Autowired
@Component is used to indicate that a class is a component

Once marked as a component, the class can be autowired into any other classes
that needs to use it using @Autowired. Autowiring refers to automatic dependency injection, allowing
the class to use the class without having to manually inject dependencies via the constructor

Autowiring works by placing an instance of 1 bean into the desired field in an instance of another bean.
Since beans are by default Singleton scope, the instance being injected is by default also the same instance
managed by the Application Context

Spring Boot will look for classes marked with @Component only in packages specified in @ComponentScan

@Service, @Repository, etc can be special cases of @Component, but can similarly be autowired

## @Bean
@Bean refers to a method to specify that it returns a bean to be managed by the Application Context

It is typically used in configuration classes

Take note that **Singleton** is the default scope of all beans defined in Spring, meaning that only
1 instance of any bean exists by default

## @Repository
@Repository is used to indicate the class that provides the mechanism for database operations on objects

## @Entity
@Entity is used to indicate a class that represents data that can be persisted to the database

By using the @Entity annotation, we must ensure that the entity has a no-arg constructor and a primary key

@Table annotation is used to specify the table name in cases of mismatch between POJO name and table name

@Id annotation defined the primary key

@GeneratedValue annotation provides the attribute strategy to specify the identifier generation method used
by the entity

@Column annotation provides attributes, name length, nullable, unique, etc to specify the details of a table
column

@Transient annotation is used to indicate fields that will not be persisted to the database. For instance,
the POJO may want to keep fields that needed while processing data only and should not be saved to the database

## @EnableJpaRepositories
@EnableJpaRepositories enables repositories to be managed by JPA (Java Persistence API)

The attribute basePackages is used to specify packages to scan for @Repository annotated classes

entityManagerFactoryRef and transactionManagerRef are also attributes that can be specified. For instance,
if there are multiple data sources, you may want to specify the entityManager and transactionManager used
for different data sources

excludeFilters can be used to exclude classes that should be skipped by the component scan

## @Primary
In cases where multiple beans of the same type need to be registered, annotating a bean with
@Primary will tell Spring to inject that bean preferentially over other beans of the same type

# Testing

## @Test
@Test tells JUnit that the public void method to which it is attached can be run as a test case

## @DirtiesContext
@DirtiesContext indicates that the ApplicationContext associated with a test is dirty and should be closed
and removed from the context cache

It may be used as a class-level or method-level annotation

## @BeforeEach
@BeforeEach tells JUnit that the annotated method should be executed before each @Test method

## @SpringBootTest
@SpringBootTest loads the entire Spring context and starts a webserver whose properties can be specified
via the attributes

## @WebMvcTest
@WebMvcTest loads the entire Spring context but does not start the webserver. Spring instead directly passes
the http request to the controller

## @MockBean
@MockBean adds mocks to the ApplicationContext. When registered by type, any existing single bean of
matching type in the ApplicationContext will be replaced by the mock

This means that when @MockBean is used, the behavior of the mock must be specified before each test

## @SpyBean
@SpyBean adds spies to the ApplicationContext. When registered by type, all beans in the context of matching
type will be wrapped with the spy. If no existing bean is defined, a new one is added

This allows us to specify the behavior of the spy when we need to, and retain its normal behavior
otherwise

## @Mock and @InjectMocks
We should not be loading the Spring context for unit testing

@InjectMocks creates an instance of the annotated field and injects the mocks that are marked with @Mock
into it

It is necessary to inject the mocks before the tests like so 
```java
MockitoAnnotations.openMocks(this);
```

# Configuring mocks
## Differences between `when(...).thenReturn(...)` and `doReturn(...).when(class).methodCall()`
`when(...).thenReturn(...)` actually makes a real method call just before the specified value will be
returned, so if an exceptions is thrown by the called method, then you need to deal with it.

On the other hand, `doReturn(...).when(class).methodCall()` does not call the method at all, and is
typically used to mock void methods

