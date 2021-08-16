# TEnmo Capstone

## Introduction
Java server-side API and command-line client for funds transfer management.

---
## Architecture
---
### Server

Java server-side API provides funds transfer management using:  
- Spring MVC,
- Spring JdbcTemplate,
- and a PostgreSQL database.  

#### Details

- The API is documented using Swagger annotations. 

- JSON objects received from client requests are validated using the @Valid annotation on the controller method and Bean Validation on the corresponding Java model classes. 

- Custom exceptions are used to control the information that is sent to the client about failed requests.

- Authentication is required for all funds transfer management requests, and the information that is returned or updated is controlled based on the user associated with the request.

- Multiple DML statements used to process transfer are executed as an ACID transaction using a stored procedure.

##### Tests

The tests for this project use JUnit 4.

- JUnits for the JDBC implementations of the DAO interfaces:  

    - use SingleConnectionDataSource to test data modification methods without committing the changes, 
    - use the @Value annotation to read the database properties from the application.properties file of the main application, and 
    - because @Value is not supported on static fields and the @BeforeClass method needs to be static, the Data Source is configured on the first run of the @Before method with a static boolean variable keeping track of whether the Data Source has already been configured.  
- REST Controller JUnits:  
    - Spring's MockMvc is used to send requests to the REST Controller endpoints and check for the expected responses,  
    - the @WithMockUser annotation is used to simulate sending a request as an authenticated user,
    - separate DAO implementations using in-memory JPA Repositories are associated with a "test" profile using the @Profile annotation, while the Jdbc DAO implementations are associated with a "prod" profile,
    - the @TestPropertySource annotation is used on the test class to read the in-memory database properties from the application-test.properties file, where the "test" profile is set as the active profile, while the application.properties file for the main application has the "prod" profile as the active profile and the JDBC connection properties, and
    - the REST Controller Autowires the appropriate DAO implementation, depending on the active profile.  

    ---
### Client

Java command-line client uses Spring RestTemplate with JSON Web Tokens to make authenticated requests to the server-side API in order to:  
- display a user's account balance,  
- display a history of transfers to or from the user's account, 
- display the details of a previous transfer, and 
- enable a user to create a transfer and send funds to another user.













