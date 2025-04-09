

# CST-339 Activity 7: Microservices Implementation Report

| Category      | Details             |
| :------------ | :------------------ |
| **Name:**       | Owen Lindsey        |
| **Instructor:** | Professor Estey     |
| **Date:**       | 04/09/2024         |
| **Course:**     | CST-339             |

---

## Introduction

This report details the development process for the CST-339 Activity 7 assignment, which involved constructing a system composed of multiple cooperating services. The project began by building a web application that interacted with two distinct REST APIs (one for Users, one for Orders). Subsequently, the system was enhanced by incorporating Netflix Eureka, enabling the services to locate each other dynamically, thereby increasing flexibility and resilience.

---

## Part 1: Building a Web Application with Separate Service Endpoints

**Objective:** To create a web application that effectively utilizes data and functionality provided by two independent backend services through their respective REST APIs.

**Implementation Overview**

The initial phase focused on building three separate Spring Boot applications designed to work together:

The **Users Service** was developed to manage user information. It connects to a dedicated MongoDB collection and offers its functionality through a specific network address (`/service/users` on port 8081).

The **Orders Service** was similarly constructed to handle order data, also using its own MongoDB collection. It exposes its capabilities via a distinct network address (`/service/orders` on port 8082).

The **Web Application** provides the user interface, utilizing Thymeleaf for presentation. It fetches data by directly calling the specific network addresses of the Users and Orders services using Spring's RestTemplate. This application operates on port 8083, presenting a unified view of the data retrieved from the backend services.

**Key Implementation Aspects**
- Utilization of MongoDB Atlas for persistent data storage for each service.
- Clear separation of concerns, with each service handling a distinct business domain.
- Direct communication between the web application and backend services using predefined addresses.

**Deliverables**

| Description                  | Screenshot                               |
| :--------------------------- | :--------------------------------------- |
| Users Data Displayed via Web | ![Users Table](https://github.com/omniV1/CST-339/blob/main/workspaceCST-339/src/Topic7-Microservices/activity-62/docs/Users8081.png)       |
| Orders Data Displayed via Web | ![Orders Table](https://github.com/omniV1/CST-339/blob/main/workspaceCST-339/src/Topic7-Microservices/activity-62/docs/Orders8081.png)     |

---
https://github.com/omniV1/CST-339/blob/main/workspaceCST-339/src/Topic7-Microservices/activity-62/docs/Users8081.png
## Part 2: Integrating Dynamic Service Discovery with Eureka

**Objective:** To improve the system's design by introducing a service registry (Netflix Eureka) that allows services to find each other automatically without relying on fixed addresses.

**Implementation Overview**

This phase introduced a **Eureka Server** component, acting as a central directory running on port 8761. This server maintains a list of all available services and their current network locations.

The existing Users Service, Orders Service, and Web Application were then updated. Instead of using fixed addresses, they now register themselves with the Eureka Server upon startup. When the Web Application needs to interact with a backend service, it queries the Eureka Server to find the current location of the required service (e.g., Users or Orders) before making the request.

**Architectural Enhancements**
- Establishment of a central service directory (Eureka Server).
- Elimination of hardcoded network addresses for service communication.
- Increased system adaptability and robustness against changes in service locations.

**Deliverables**

| Description                               | Screenshot                                 |
| :---------------------------------------- | :----------------------------------------- |
| Eureka Server Dashboard (Showing Services) | ![Eureka Dashboard](https://github.com/omniV1/CST-339/blob/main/workspaceCST-339/src/Topic7-Microservices/activity-62/docs/Eureka.png)       |
| Users Data via Eureka Discovery           | ![Users with Eureka](https://github.com/omniV1/CST-339/blob/main/workspaceCST-339/src/Topic7-Microservices/activity-62/docs/Orders8083.png)   |
| Orders Data via Eureka Discovery          | ![Orders with Eureka](https://github.com/omniV1/CST-339/blob/main/workspaceCST-339/src/Topic7-Microservices/activity-62/docs/Users8083.png) |

---

## Research Questions

### Exploring the Microservice Approach

The microservice approach is a way of designing software applications as a suite of small, independently deployable services. Each service focuses on a specific business capability and communicates with others over a network, typically using lightweight protocols. Think of it like building a complex system from specialized, interchangeable parts rather than creating one large, intricate machine.

This style differs significantly from building applications as a single, large unit. With microservices, updates can be made to individual parts without redeploying the entire system. Different parts can be scaled up or down based on specific needs, and teams can often work more independently, even using different technologies best suited for their specific service's task.

### Challenges in Transitioning to Microservices

Moving from a single-unit application design to a system composed of many small services introduces unique challenges. Managing data consistency across these separate services requires careful planning. Ensuring reliable communication between services, including finding their current locations and handling network delays, becomes crucial.

Testing also becomes more complex, as interactions between multiple services need to be verified. Deploying and monitoring many independent services adds operational overhead compared to managing a single application. Finally, this shift often requires changes in team structure and development practices to effectively manage the distributed nature of the system.

---

## Christian Worldview Component

Developing systems, especially those composed of many interacting parts like microservices, brings unique responsibilities regarding user privacy and data security. From a Christian perspective, handling user data responsibly is more than a compliance issue; it's a matter of stewardship and integrity. We are called to be trustworthy guardians of the information entrusted to us.

This involves ensuring data is collected transparently, stored securely, and used ethically, respecting user consent. When security concerns arise, particularly if addressing them might introduce project delays, it's important to communicate clearly and respectfully with leadership. This involves presenting the potential risks factually, proposing viable solutions, and framing the importance of security not just in technical terms, but also in terms of ethical responsibility and long-term trust – principles that align with a Christian worldview of diligence and care. This allows for a constructive dialogue that upholds both project goals and ethical standards.

---
