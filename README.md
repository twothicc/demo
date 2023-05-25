# About this demo
This demonstration aims to show the capabilities and highlight some conveniences of using Spring Boot
as a backend framework in 3 parts:
- Employee API
- Serialize Repository
- Testing

# Spring Boot
Spring Boot is a java backend framework that makes developing backend services faster and easier

The Application Context is often used as the Spring container and is used to manage Spring beans

SpringBeans are objects that the Spring container instantiates, assembles, and manages

# 1. Employee API
The first part of this demonstration is a simple MVC (Model View Controller minus the V) implementation along
with some database logic.

You can follow the flow of this API from `EmployeeController` -> `EmployeeService` -> `EmployeeDAO` -> `EmployeeMasterRepository` /
`EmployeeSlaveRepository`. While going through the code, you can look up the various annotations used with the
ones listed below to get a better understanding of how they are used and why are they applied on certain classes/methods/fields

### Multiple data sources

A particularly interesting capability explored in this part is to configure multiple data sources in a single
backend service. 

Under the config package, you can see that there are 2 data source configs, along with 2 accompanying DB scripts
`SETUP_MASTER_DB.sql` and `SETUP_SLAVE_DB.sql` in the scripts directory to help you setup up the DBs if you wish to.

Both `MasterDataSourceConfig` and `SlaveDataSourceConfig` are annotated with `@Configuration` and both produce
a bean of the same type `DataSource`. Without any special considerations made, this will actually be a source
of ambiguity for the Spring Application Context. Imagine a scenario where the user requests a repository like for
instance a `EmployeeRepository`. How will Spring Application Context know which `DataSource` to use to assemble
the user's `EmployeeRepository`? Should it use the one created by `MasterDataSourceConfig` or perhaps `SlaveDataSourceConfig`?

When multiple beans of the same type are used, we can tell Spring Application Context to **preferentially** inject
a certain bean over the rest by using the `@Primary` annotation on the method returning the desired bean. In this
case, we use it on `MasterDataSourceConfig::masterDataSource`, so whenever Spring Application Context returns
a repository, it will use the `DataSource` bean from `MasterDataSourceConfig`.

But then here is another problem. We sometimes want to use the `SlaveDataSourceConfig`'s `DataSource` to interact
with the other DB. So how can we tell Spring Application Context to perform this conditional usage of beans?

Here we have created the `@ReadOnlyRepository` annotation under the config package. Under the `@EnableJpaRepository`
annotation used on `MasterDataSourceConfig`, we have set the `excludeFilter` property to exclude scanning for
classes marked with the `@ReadOnlyRepository`. On the other hand, we have set the `includeFilter` property on the
`@EnableJpaRepository` annotation used on `SlaveDataSourceConfig` to include scanning for classes marked with
`@ReadOnlyRepository`.

This achieves the effect whereby Spring Application Context omits scanning for classes marked with `@ReadOnlyRepository`
when looking to inject the `DataSource` bean from `MasterDataSourceConfig`. It does the opposite instead for the
`DataSource` bean from `SlaveDataSourceConfig`.

This is exactly what we are looking for. We annotate the `EmployeeSlaveRepository` with the `@ReadOnlyRepository` to
tell Spring Application Context that all calls made to it are to interact with the slave DB instead of the master DB.
We then wrap the two repositories under the `EmployeeDAO` to implement logic on when to use which repository.

**Note**: You may notice that in the `application.properties`, I set the exact same database url for both the master
and slave db url property. This is because this guide does not show how to set up database replication on MSSQL, but
do let me know if you want to learn how to.

# 2. SerializeRepository
The second part of this demonstration aims to show how we can utilize the `VARBINARY` or better known as
BLOB in other DBs to store objects of various types in the same table via serialization and deserialization.

So to show this, I've set up the `SerializeObj` entity. You can also use the `SETUP_SERIALIZATION_DB.sql` under
the scripts directory to setup the exact same DB on your MSSQL server.

In particular, I've set the `content` field of this entity to have the datatype `VARBINARY(max)` on the DB,
essentially allowing it to store byte content of any size.

The `SerializeObj` class comes with private methods to serialize and deserialize any objects to and from the content field
, which are `SerializeObj::serializeContent` and `serializeContent::deserializeContent` respectively.

We want to showcase that we can store two objects `SerializedObj` and `SerializedObj2` in the same table.
This can be seen by running the test method `DemoApplicationTests::test`.

Since the method to reset is annotated with `@BeforeEach`, you can even view the results of the test
in the DB yourself, which should be 2 separate records with hexadecimals in their content field.

### Possible Applications
The main reason for this demonstration is to explore how we can possibly set up a config table that
can support many sorts of config field requirements from various teams.

Each team can even maintain a library containing number codes that map to their corresponding config
classes so that other teams can import those libraries and deserialize the team's configs properly.

# 3. Testing
Having tests is good, but if your tests takes minutes to run, it would kinda suck. So this part just
aims to show how we can design more performative tests.

`@SpringBootTest` annotation both starts the web server and the full Spring Application Context, and should
only be used for integration testing. An additional benefit is that all APIs will be available using this
method. However, if the server eventually grows, it may take a very long time to
start up the web server, so use it appropriately.

`@WebMvcTest` annotation does not start the web server, and Spring Boot directly forwards http requests to
a controller. Slightly more performative than `@SpringBootTest` but still should only be used for
integration testing.

For other unit tests, none of the above annotations should be used. If their results or thrown exceptions
are necessary for testing, `@Mock` and `@InjectMocks` annotation should be used instead to utilize
Mockito for mocking.

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

