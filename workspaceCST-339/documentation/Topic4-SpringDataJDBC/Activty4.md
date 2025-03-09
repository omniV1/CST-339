# CST-339 Activity 4 Report
### CST-339: Java Enterprise Application Development

**Date:** March 08, 2025  
**Author:** [Your Name]  
**Course:** CST-339 - Java Programming III  
**Professor:** Professor Robert Estey

**Source code:** 
https://github.com/omniV1/CST-339/tree/main/workspaceCST-339/src/Topic4-SpringDataJDBC/topic04

---
## Part 1: Creating Data Services Using Spring JDBC

### Implementation Overview

In this section, we successfully implemented data services using the Data Access Object (DAO) design pattern to persist data to a MySQL database using Spring JDBC. The implementation focused on using the standard JDBC template built into Spring JDBC and leveraging SQL to interact with the database.

### OrdersDataService Implementation

The OrdersDataService class demonstrates effective implementation of the DataAccessInterface using Spring's JdbcTemplate. Key aspects include:

- Constructor injection for DataSource dependency
- SQL query execution using jdbcTemplateObject
- Result set mapping to OrderModel objects
- Proper exception handling with try-catch blocks

### Data Access Layer Integration

The implementation successfully integrates with the business layer through dependency injection:

1. The OrdersBusinessService injects the DataAccessInterface<OrderModel>
2. The getOrders() method calls findAll() on the service
3. The retrieved data is displayed in the Orders page

The Orders page successfully displays data retrieved from the MySQL database through the JDBC implementation. The table shows the complete list of orders with their respective details.

#### This screenshot displays the Orders page displaying data from the database

![orders](https://github.com/omniV1/CST-339/blob/main/workspaceCST-339/documentation/Topic4-SpringDataJDBC/images/OrdersList.png)


## Part 2: Creating Data Services Using Spring Data JDBC

### Implementation Overview

In this section, we implemented data services using the Repository design pattern with Spring Data JDBC. This approach leverages an ORM-like interface to persist objects to the database without writing explicit SQL. The implementation demonstrates proper separation of application object models and entity object models.

### Entity and Repository Implementation

The OrderEntity class demonstrates proper use of Spring Data JDBC annotations with @Table and @Column annotations mapping entity fields to database columns. The OrdersRepository interface extends CrudRepository, providing automatic implementation of common data operations.

### OrdersDataService Implementation

The OrdersDataService class demonstrates effective use of the repository pattern by utilizing the injected OrdersRepository to perform data operations. This implementation shows how Spring Data JDBC simplifies data access by providing pre-implemented methods for common operations.

### Domain Model Conversion

A key architectural feature is the conversion between entity and domain models in the business layer. This approach ensures proper separation of concerns, preventing persistence technology dependencies from propagating to the presentation layer.

### Orders Page Functionality

The Orders page continues to display data from the database, now retrieved through the Spring Data JDBC repository implementation.

#### This screenshot displays the Orders page with data retrieved via Spring Data JDBC

![orders](https://github.com/omniV1/CST-339/blob/main/workspaceCST-339/documentation/Topic4-SpringDataJDBC/images/OrdersList.png)


## Part 3: Creating Data Services Using Spring Data JDBC Native Queries

### Implementation Overview

In this section, we extended our Spring Data JDBC implementation to include native SQL queries. This approach demonstrates how to override default repository methods with custom SQL implementation for cases where performance optimization is needed.

### Custom Repository Implementation

The OrdersRepository interface was enhanced with a custom query annotation to override the default findAll() implementation with a custom SQL query while maintaining the repository pattern benefits.

### Custom JDBC Implementation

Additionally, the OrdersDataService was updated to include a custom implementation of the create() method using direct JDBC. This hybrid approach demonstrates the flexibility of Spring Data JDBC, allowing developers to use high-level abstractions while still having the option to implement custom SQL for performance-critical operations.

### Orders Page Functionality

The Orders page continues to display data from the database, now retrieved through a combination of repository pattern and native SQL queries.

#### This screenshot displays the Orders page with data retrieved via native queries

![orders](https://github.com/omniV1/CST-339/blob/main/workspaceCST-339/documentation/Topic4-SpringDataJDBC/images/OrdersList.png)


## Research Questions

### 1. How does Spring Data JDBC differ from standard Java JDBC programming?

Spring Data JDBC provides a significant advancement over standard Java JDBC programming through multiple architectural and productivity improvements. The relationship between these technologies can be analyzed across several dimensions:

#### Abstraction Level and Code Reduction

Standard JDBC programming requires developers to write substantial boilerplate code to manage connections, prepare statements, execute queries, map results, and handle resources. This includes connection setup, statement preparation, result set processing, and careful resource cleanup in finally blocks.

Spring Data JDBC significantly reduces this boilerplate through templates and repositories. The JdbcTemplate handles connection management, statement creation, and resource cleanup automatically. With CrudRepository, these operations are further abstracted, requiring almost no data access code for standard operations.

#### Object Mapping

Spring Data JDBC uses annotations like `@Table` and `@Column` to automatically map between database tables and Java objects. This declarative approach eliminates the manual mapping code required in standard JDBC, where developers must explicitly extract values from result sets and set them on objects.

#### Exception Handling

Spring Data JDBC translates checked SQLException instances into Spring's unchecked DataAccessException hierarchy, providing a more consistent exception management approach. This improves code readability and removes the need for try-catch blocks throughout application code.

#### Query Construction

Spring Data JDBC allows for native queries through annotations, providing a clean, type-safe way to define custom queries compared to string concatenation in standard JDBC. It also supports derived query methods, where the method name itself defines the query criteria.

#### Design Pattern Support

Spring Data JDBC naturally supports the Repository pattern, encouraging a more modular, testable architecture compared to the often procedural nature of standard JDBC code. This promotes better separation of concerns and more maintainable code.

#### Transaction Management

Spring Data JDBC integrates with Spring's transaction management, providing declarative transaction control through annotations rather than manual transaction handling with standard JDBC. This simplifies transaction management and reduces error-prone code.

The evolution across the three parts of this activity effectively demonstrates this progression from low-level JDBC to higher-level abstractions, highlighting the productivity and maintainability benefits of Spring Data JDBC.

### 2. How does Spring Data JDBC support transaction management and the ACID principle?

Spring Data JDBC provides robust transaction management capabilities that ensure adherence to the ACID (Atomicity, Consistency, Isolation, Durability) principles, which are fundamental to reliable database operations. The framework offers multiple approaches to transaction management that together create a comprehensive solution.

#### Declarative Transaction Management

Spring Data JDBC supports declarative transaction management through annotations, primarily `@Transactional`. This annotation instructs Spring to automatically begin a transaction before method execution and commit it upon successful completion, or roll it back if exceptions occur. This declarative approach separates transaction concerns from business logic.

#### Atomicity Implementation

Spring ensures atomicity (all-or-nothing operations) through its transaction manager, which handles transaction boundaries and rollback in case of failures. When applied to service methods, this ensures that either all database operations complete successfully or none of them do, maintaining data integrity.

#### Consistency Maintenance

Spring Data JDBC helps maintain database consistency through entity validation, consistent object-relational mapping, and enforcement of database constraints. The framework ensures that business rules and database constraints are enforced throughout the transaction lifecycle.

#### Isolation Configuration

Spring provides configurable transaction isolation levels through the `@Transactional` annotation's isolation property. These isolation levels control how transactions interact with each other, preventing data consistency issues in concurrent environments such as dirty reads, non-repeatable reads, and phantom reads.

#### Durability Support

Spring relies on the underlying database system to provide durability, ensuring that committed transactions persist despite system failures. The configuration in application.properties establishes the connection to MySQL, which provides this durability guarantee.

#### Integration with Application Architecture

Spring's transaction management integrates seamlessly with different data access approaches:

1. **JdbcTemplate**: Transactions managed internally by the template
2. **CrudRepository**: Repository operations automatically participate in transactions
3. **Native Queries**: Even custom SQL executes within Spring's transaction context

This consistency across different approaches provides a uniform transaction management strategy regardless of the chosen data access method.

Through these mechanisms, Spring Data JDBC ensures that database operations maintain the ACID principles, providing data integrity, consistency, and reliability without requiring developers to implement complex transaction handling code.