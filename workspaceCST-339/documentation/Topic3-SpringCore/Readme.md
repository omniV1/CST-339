# CST-339 Activity 3 Report
### CST-339: Java Enterprise Application Development

**Date:** January 29, 2025  
**Author:** Owen Lindsey  
**Course:** CST-339 - Java Programming III  
**Professor:** Professor Robert Estey

**Source code:** 
https://github.com/omniV1/CST-339/tree/main/workspaceCST-339/src/Topic3-SpringCore/topic03

---
## Part 1: Creating Spring Bean Services Using Spring Core

### OrdersBusinessService Implementation

The implementation successfully demonstrates the effective use of Spring Beans and dependency injection through the creation of business services. Our testing revealed proper initialization and execution of the OrdersBusinessService, with system logs confirming the expected behavior:

```
OrdersBusinessService.init()
Current Instance ID: 1
Total Instances Created: 1
Thread ID: 297

OrdersBusinessService.test()
SecurityBusinessService.authenticate(cv64, password)
Form with Username of wjat and Password of iamadmi
```

The system successfully initialized the service bean with Instance ID 1, and the test() method executed as expected. The SecurityBusinessService authentication integrated smoothly into the flow, demonstrating proper credential handling.

### Service Implementation Results

| Service Type | Instance Behavior | Thread Management | Initialization Status |
|-------------|-------------------|-------------------|---------------------|
| OrdersBusinessService | Single instance per context | Consistent thread ID | Clean initialization |
| AnotherOrdersBusinessService | New instance on switch | Maintained thread context | Successful transition |
| SecurityBusinessService | Singleton scope | Shared thread pool | Integrated authentication |

#### This screenshot displays the console output

![Part 1 Console Output](https://github.com/omniV1/CST-339/blob/main/workspaceCST-339/documentation/Topic3-SpringCore/Screenshots/Part1ConsoleOutput.png)

### Orders Page Functionality

The Orders page implementation successfully renders data through the getOrders() method, displaying a well-structured table with essential order information. The following sample data demonstrates the proper formatting and data flow:

| Order Number | Product Name | Price | Quantity |
|-------------|--------------|-------|----------|
| 000000000 | Product 0 | 0.0 | 0 |
| 000000001 | Product 1 | 1.0 | 1 |
| 000000002 | Product 2 | 2.0 | 2 |

![Orders page](https://github.com/omniV1/CST-339/blob/main/workspaceCST-339/documentation/Topic3-SpringCore/Screenshots/OrdersPage1.png)

## Part 2: Spring Bean Life Cycle and Scopes

### Scope Behavior Analysis

The implementation revealed distinct behaviors across different scope types. Under prototype scope, each bean request generates a new instance, as evidenced by the following console output:

```
OrdersBusinessService.init()
Current Instance ID: 1
Total Instances Created: 1
Thread ID: 213

OrdersBusinessService.test()

OrdersBusinessService.init()
Current Instance ID: 2
Total Instances Created: 2
Thread ID: 213
```
#### This is the output of Prototyoe Scope

![Prototype](https://github.com/omniV1/CST-339/blob/main/workspaceCST-339/documentation/Topic3-SpringCore/Screenshots/PrototypeScope.png)

#### This is the output of Request Scope

![Request](https://github.com/omniV1/CST-339/blob/main/workspaceCST-339/documentation/Topic3-SpringCore/Screenshots/RequestScope.png)

#### This is the output of Session Scope

![Session](https://github.com/omniV1/CST-339/blob/main/workspaceCST-339/documentation/Topic3-SpringCore/Screenshots/SessionScope.png)

#### This is the output of Singleton Scope
![Singleton](https://github.com/omniV1/CST-339/blob/main/workspaceCST-339/documentation/Topic3-SpringCore/Screenshots/SingletonScope.png)

The request scope implementation demonstrated proper lifecycle management through three key phases:
1. Initialization at request start
2. Service execution during request processing
3. Cleanup through destroy() method at request completion

### Session Management Overview

Session scope implementation provided consistent state management across requests while maintaining proper isolation. Key observations from testing include:

```
OrdersBusinessService.init()
Current Instance ID: 1
Total Instances Created: 1
Thread ID: 297
```

| Scope Type | Instance Creation | State Management | Resource Cleanup |
|------------|------------------|------------------|------------------|
| Prototype | Per request | Independent | Garbage collection |
| Request | Per HTTP request | Request-bound | Immediate |
| Session | Per user session | Session-preserved | Session timeout |

## REST Service Implementation

The REST service implementation provides dual-format endpoints supporting both JSON and XML responses. Our testing through Postman verified proper content negotiation and data formatting. Let's examine the complete API specification and implementation details.

### Implementation Overview

The API implementation follows RESTful best practices, using appropriate HTTP methods and content types. Our endpoints demonstrate the following key design principles:

1. Content Negotiation: Each endpoint specifies its response format through proper content type headers, allowing clients to receive data in their preferred format.

2. Clear Resource Naming: The endpoint paths (/service/getjson and /service/getxml) clearly indicate their purpose and response format.

3. Consistent Response Structure: Both JSON and XML responses maintain the same data structure, ensuring consistency across formats.

### Sample Responses

The API provides responses in two formats:

JSON Response Example:
```json
{
  "orders": [
    {
      "id": 0,
      "orderNo": "000000000",
      "productName": "Product 0",
      "price": 0.0,
      "quantity": 0
    }
  ]
}
```

XML Response Example:
```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<orders>
    <order>
        <id>0</id>
        <orderNo>000000000</orderNo>
        <productName>Product 0</productName>
        <price>0.0</price>
        <quantity>0</quantity>
    </order>
</orders>
```

### Testing Results

Our Postman testing confirmed proper functionality of both endpoints. The testing focused on verifying:

| Test Case | Expected Result | Actual Result |
|-----------|----------------|---------------|
| JSON Endpoint Response | 200 OK with JSON data | Passed |
| XML Endpoint Response | 200 OK with XML data | Passed |
| Content Type Headers | Correct MIME types | Passed |
| Data Structure | Matches schema definition | Passed |

[Rest of the document remains the same...]

![get json in browser](https://github.com/omniV1/CST-339/blob/main/workspaceCST-339/documentation/Topic3-SpringCore/Screenshots/getjson-in-browser.png)

![get json in postman](https://github.com/omniV1/CST-339/blob/main/workspaceCST-339/documentation/Topic3-SpringCore/Screenshots/getjson-postman.png)

![get xml in browser](https://github.com/omniV1/CST-339/blob/main/workspaceCST-339/documentation/Topic3-SpringCore/Screenshots/getxml-in-browser.png)

![get xml in postman](https://github.com/omniV1/CST-339/blob/main/workspaceCST-339/documentation/Topic3-SpringCore/Screenshots/getxml-postman.png)


### Postman Documentation
https://documenter.getpostman.com/view/32764813/2sAYXCiyKe

## Research Questions

### Spring Annotation Analysis

The Spring framework's annotation system provides targeted solutions for different architectural needs. The @Component annotation serves as a foundational stereotype, while @Service adds semantic value for service layer components. The @Bean annotation differs by operating at the method level, offering explicit control over bean instantiation and configuration.

Consider the following annotation comparison:

| Annotation | Scope | Usage Context | Auto-Detection |
|------------|-------|---------------|----------------|
| @Component | Class | Generic components | Yes |
| @Service | Class | Business logic layer | Yes |
| @Bean | Method | Configuration classes | No |

### IoC Container Design Principles

The Inversion of Control Container's emphasis on interface contracts supports several key architectural benefits. By enforcing interface-based design, applications achieve loose coupling between components, enabling more flexible testing and maintenance approaches. This design principle manifested clearly in our implementation through the OrdersBusinessServiceInterface, which allowed seamless transitions between different concrete implementations.

The interface-driven approach particularly shines in testing scenarios, where mock implementations can be easily substituted for actual services. Consider the following implementation example from our activity:

```java
@Service
public class OrdersBusinessService implements OrdersBusinessServiceInterface {
    @Override
    public void test() {
        System.out.println("Hello from the OrdersBusinessService");
    }
}
```

This implementation demonstrates how interface contracts enable clean separation between interface definition and concrete implementation, supporting the core principles of dependency injection and inversion of control.
