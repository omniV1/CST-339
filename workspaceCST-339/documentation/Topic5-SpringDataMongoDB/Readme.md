# CST-339 Activity 5 Report
### Spring Data MongoDB Integration

**Date:** March 22, 2025  
**Student:** Owen Lindsey  
**Course:** CST-339 - Java Enterprise Application Development  
**Professor:** Professor Robert Estey

## Table of Contents
- [CST-339 Activity 5 Report](#cst-339-activity-5-report)
    - [Spring Data MongoDB Integration](#spring-data-mongodb-integration)
  - [Table of Contents](#table-of-contents)
  - [Part 1: Creating Data Services Using Spring Data MongoDB](#part-1-creating-data-services-using-spring-data-mongodb)
    - [Implementation Steps](#implementation-steps)
    - [Screenshots](#screenshots)
  - [Part 2: Adding New Queries in the MongoDB Repository](#part-2-adding-new-queries-in-the-mongodb-repository)
    - [Implementation Steps](#implementation-steps-1)
    - [Screenshots](#screenshots-1)
  - [Research Questions](#research-questions)
    - [Question 1: Relational vs. Non-relational Schema Design](#question-1-relational-vs-non-relational-schema-design)
      - [Additional Migration Impacts:](#additional-migration-impacts)
    - [Question 2: Database Feature Comparison](#question-2-database-feature-comparison)
      - [Relational Database Advantages](#relational-database-advantages)
      - [Non-relational Database Advantages](#non-relational-database-advantages)

## Part 1: Creating Data Services Using Spring Data MongoDB

### Implementation Steps

In this part of the activity, I performed the following tasks:

1. Set up a MongoDB Atlas project following the guidelines in Appendix A of the activity guide
2. Created a copy of the topic4-2 project and renamed it to topic5-1
3. Modified the project by:
   - Removing JDBC and MySQL dependencies and adding MongoDB dependency
   - Updated application.properties for MongoDB configuration
   - Modified the Order entity to work with MongoDB (changed annotations and ID type)
   - Changed the Repository from CrudRepository to MongoRepository
   - Removed unnecessary mapper classes
4. Tested the application functionality to ensure proper data retrieval and display
5. Built the application JAR and tested from terminal

### Screenshots

**Orders Page Display:**
![Orders Page](https://github.com/omniV1/CST-339/blob/main/workspaceCST-339/documentation/Topic5-SpringDataMongoDB/Screenshots/Orders.png)

**JSON API Response:**
![JSON Response](https://github.com/omniV1/CST-339/blob/main/workspaceCST-339/documentation/Topic5-SpringDataMongoDB/Screenshots/Json.png)

**XML API Response:**
![XML Response](https://github.com/omniV1/CST-339/blob/main/workspaceCST-339/documentation/Topic5-SpringDataMongoDB/Screenshots/Xml.png)

## Part 2: Adding New Queries in the MongoDB Repository

### Implementation Steps

In this part of the activity, I implemented the following:

1. Modified the findById method to work with String IDs (MongoDB's format)
2. Added a getOrderById method to the OrdersRepository interface
3. Implemented the findById method in the OrdersDataService class
4. Updated OrdersBusinessServiceInterface with a getOrderById method declaration
5. Implemented the getOrderById method in both business service classes
6. Created a new REST API endpoint (/getorder/{id}) that:
   - Accepts GET requests with an ID parameter
   - Returns appropriate HTTP status codes (OK, NOT_FOUND, INTERNAL_SERVER_ERROR)
   - Returns the order data as JSON when found

### Screenshots

**REST API Response with Valid ID:**
![Valid ID Response](https://github.com/omniV1/CST-339/blob/main/workspaceCST-339/documentation/Topic5-SpringDataMongoDB/Screenshots/ByIdOrderValid.png)

**REST API Response with Invalid ID:**
![Invalid ID Response](https://github.com/omniV1/CST-339/blob/main/workspaceCST-339/documentation/Topic5-SpringDataMongoDB/Screenshots/InvalidOrderID.png)

## Research Questions

### Question 1: Relational vs. Non-relational Schema Design

The table below compares schema design in relational and non-relational databases and highlights the impacts of migration:

| Characteristic | Relational Database | Non-relational Database (MongoDB) | Migration Impact |
|----------------|---------------------|-----------------------------------|-----------------|
| **Schema Structure** | Fixed schema with predefined tables, columns, and data types | Flexible, schema-less document structure | Applications must be redesigned to handle dynamic structures instead of fixed schemas |
| **Data Relationships** | Explicit relationships through foreign keys and joins | Embedded documents or references between collections | Join operations must be replaced with document embedding or application-level joins |
| **Normalization** | Highly normalized to reduce redundancy (3NF, BCNF) | Often denormalized for performance and accessibility | Data modeling approach shifts from normalized tables to embedded documents |
| **Schema Changes** | Requires formal migrations, often with downtime | Dynamic schema evolution with no formal migrations | Easier deployment of changes, but requires careful validation |
| **Data Integrity** | Enforced through constraints (foreign keys, check constraints) | Limited constraints, mostly application-enforced | More validation logic must be moved to application layer |
| **ID Generation** | Often numeric sequences (Long/Integer types) | String-based ObjectIDs | ID type changes require updates to entity classes and business logic |
| **Query Language** | SQL (structured, standardized) | MongoDB Query Language (JSON-based) | Query syntax and mechanisms must be rewritten |
| **Data Representation** | Row and column format | BSON documents (binary JSON) | Data serialization/deserialization approaches change |

#### Additional Migration Impacts:

1. **Code Changes:**
   - Repository interfaces change from JPA interfaces to MongoDB interfaces
   - Entity annotations change from JPA annotations to MongoDB annotations
   - Mapper classes often become unnecessary
   - Database connection configuration changes significantly

2. **Performance Considerations:**
   - Query patterns may need to be redesigned for MongoDB's strengths
   - Index creation strategies differ significantly
   - Aggregation operations replace complex joins

3. **Architectural Changes:**
   - Data access patterns often shift to better align with document model
   - Transaction boundaries may need reconsideration due to different ACID guarantees
   - Batch processing approaches may need redesign

### Question 2: Database Feature Comparison

The tables below present three key advantages of relational and non-relational databases with rationales for selecting each type:

#### Relational Database Advantages

| Advantage | Description | Rationale for Selection |
|-----------|-------------|-------------------------|
| **ACID Compliance** | Provides strong guarantees for Atomicity, Consistency, Isolation, and Durability | Essential for financial systems, ERP applications, and any system where data consistency is critical and transactions must be reliable |
| **Standardized Query Language** | SQL is a mature, standardized language with broad industry support | Reduces development and maintenance costs through consistent syntax, transferable skills, and rich tooling ecosystem |
| **Complex Query Support** | Excellent support for complex joins, subqueries, and aggregations | Ideal for reporting, analytics, and business intelligence applications where data relationships are complex and data needs to be viewed from multiple dimensions |

#### Non-relational Database Advantages

| Advantage | Description | Rationale for Selection |
|-----------|-------------|-------------------------|
| **Horizontal Scalability** | Designed for distributed architectures that scale out by adding more servers | Cost-effective for high-volume applications that need to scale with growing data and traffic, particularly in cloud environments |
| **Schema Flexibility** | No fixed schema requirements, allowing fields to vary between documents | Perfect for agile development with evolving data models, for handling varied data structures, and for rapid prototyping without migration overhead |
| **Performance for Specific Workloads** | Optimized for high-throughput read/write operations and for certain data access patterns | Superior performance for real-time applications, content management systems, social media platforms, IoT applications, and scenarios with high write loads |

The choice between relational and non-relational databases should be driven by specific application requirements, expected scale, development velocity needs, and the nature of the data being stored and accessed. Many modern systems adopt a polyglot persistence approach, using different database types for different components based on their specific needs.