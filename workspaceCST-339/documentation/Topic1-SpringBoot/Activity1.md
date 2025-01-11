# Activity 1: Spring Boot Setup and Maven
CST-339: Java III
January 7, 2025
Owen Lindsey

## Part 1: Tools Installation and Validation
### Screenshots
### VS Code About
![VS Code About](https://github.com/omniV1/CST-339/blob/main/workspaceCST-339/documentation/Topic1-SpringBoot/screenshots/SpringBootAbout.png)

- ***VS Code Welcome Screen***: Displays the initial setup interface for Spring Boot in VS Code, showing available dependencies and project creation options.

### Console Output 
![Spring Boot Run](https://github.com/omniV1/CST-339/blob/main/workspaceCST-339/documentation/Topic1-SpringBoot/screenshots/SpringBootHelloWorldRun.png)

- ***Spring Boot Console Output***: Shows the application startup with "Hello World" printed and Spring Boot's ASCII art banner, confirming successful initialization.

### Hello World Page
![Hello World](https://github.com/omniV1/CST-339/blob/main/workspaceCST-339/documentation/Topic1-SpringBoot/screenshots/LocalHost8080-HelloWorld.png)

- ***Hello World webpage***: Shows the basic web application running successfully with a simple "Hello World" heading displayed in the browser at localhost:8080.

### index.html File
![index.html](https://github.com/omniV1/CST-339/blob/main/workspaceCST-339/documentation/Topic1-SpringBoot/screenshots/index.html.png)

- ***HTML source code***: showing basic structure with title and heading.

### Whitelabel Error
![Whitelabel](https://github.com/omniV1/CST-339/blob/main/workspaceCST-339/documentation/Topic1-SpringBoot/screenshots/LocalHost-WhiteLabelError.png)

- ***Whitelabel Error Page***: Shows the default Spring Boot error page when accessing localhost:8080 before the index.html was created, displaying a 404 error and stack trace.

## Part 2: Maven Implementation
### Screenshots 

### JAR Console Output
![JAR Console](https://github.com/omniV1/CST-339/blob/main/workspaceCST-339/documentation/Topic1-SpringBoot/screenshots/jar%20console%20output.png)

- ***JAR Execution Console***: Demonstrates running the compiled JAR file with the java -jar command, showing Spring Boot startup logs and successful deployment.

### JAR Hello World Webpage
![JAR Webpage](https://github.com/omniV1/CST-339/blob/main/workspaceCST-339/documentation/Topic1-SpringBoot/screenshots/jar%20Hello%20World%20webpage.png)

- ***Final Hello World Webpage***: Displays the web application running from the compiled JAR file, showing the same "Hello World" heading at localhost:8080.

## Research Questions and Analysis

### 1. Spring Boot vs Spring Framework Comparison

| Aspect | Spring Boot | Spring Framework |
|--------|-------------|------------------|
| Configuration | Auto-configuration, minimal setup required | Manual configuration of components and beans |
| Server | Embedded servers (Tomcat, Jetty) included | Requires manual server setup and configuration |
| Dependencies | Automated dependency management with starters | Manual dependency resolution and version management |
| Development Speed | Rapid development with defaults | More initial setup time required |
| Flexibility | Opinionated defaults, less flexibility | High flexibility and customization options |
| Use Case | Microservices, standalone applications | Large enterprise applications, custom configurations |
| Learning Curve | Lower, suitable for beginners | Steeper, requires deeper understanding |
| Deployment | Simplified with executable JARs | More complex deployment process |

### 2. Gradle vs Maven Comparison

| Feature | Maven | Gradle |
|---------|-------|--------|
| Build Script | XML (pom.xml) | Groovy/Kotlin DSL |
| Build Speed | Standard build times | Faster with incremental builds and caching |
| Learning Curve | Simple, structured learning | Steeper due to DSL complexity |
| Flexibility | Fixed, linear build lifecycle | Flexible, customizable build process |
| Plugin Ecosystem | Large, mature plugin repository | Growing plugin ecosystem |
| Multi-Project Builds | Basic support | Advanced support with better performance |
| IDE Support | Extensive IDE integration | Good but less mature IDE support |
| Dependency Management | Stable, centralized repository | Flexible with multiple repository support |
| Build Logic | XML limitations for complex logic | Full programming language capabilities |
| Performance | Standard performance | Superior for large projects |

## Project Details
- VS Code 1.96.2 
- Spring Boot 3.4.1
- Java 21
- Package: com.gcu
- Project: topic1-1
