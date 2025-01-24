# Spring MVC Implementation Documentation
<<<<<<< HEAD

## Part 1: Creating Models, Views, and Controllers

Throughout this project, I developed several web pages to demonstrate different ways Spring MVC can handle user requests and display information. Let me walk you through each component and what it accomplishes.

### Basic Response Page
My first test page at `/hello/test1` demonstrates the simplest way to send information to a browser. When users visit this page, they see a straightforward greeting: "Hello World! - Owen". This helps show how Spring MVC can communicate directly with web browsers.
=======

## Part 1: Creating Models, Views, and Controllers
### Controller Implementation Screenshots
We have successfully implemented the HelloWorldController with various endpoints demonstrating different Spring MVC concepts. Here are the implementation results:
>>>>>>> d395dc0fc9feac720ed91a2b04305756f68c6679

![Test 1 Endpoint](test1.png)
<<<<<<< HEAD

### Template-Based Pages
Moving on to more advanced features, I created a second page at `/hello/test2` that uses templates. This page displays "Hello Spring MVC Framework! - Owen" and includes navigation links, showing how templates make our pages both more functional and user-friendly.
=======
This endpoint demonstrates a direct text response using @ResponseBody, displaying "Hello World! - Owen" when accessed at localhost:8080/hello/test1.
>>>>>>> d395dc0fc9feac720ed91a2b04305756f68c6679

![Test 2 Endpoint](test2.png)
<<<<<<< HEAD

The third page at `/hello/test3` takes this a step further by showing multiple messages at once. It displays both "Hello World from ModelAndView!" and "Another Hello World from ModelAndView!" on the same page, demonstrating how we can present several pieces of information together.
=======
The template-based view implementation shows "Hello Spring MVC Framework! - Owen" with navigation links to other test endpoints. This demonstrates successful integration of Thymeleaf templating.
>>>>>>> d395dc0fc9feac720ed91a2b04305756f68c6679

![Test 3 Endpoint](test3.png)
<<<<<<< HEAD

### Interactive Features
My fourth test page at `/hello/test4` introduces user interaction. When someone adds text to the web address (like localhost:8080/hello/test4?message=Owen!), the page responds by showing that text. This shows how our pages can respond to user input and create dynamic content.
=======
This endpoint showcases the ModelAndView approach, successfully displaying two messages:
- "Hello World from ModelAndView!"
- "Another Hello World from ModelAndView!"
>>>>>>> d395dc0fc9feac720ed91a2b04305756f68c6679

![Test 4 Endpoint](test4.png)
<<<<<<< HEAD

### Behind the Scenes
To make all these pages work together smoothly, I created several key components:

| Component | What It Does | Why It Matters |
|-----------|--------------|----------------|
| Main Controller | Directs user requests to the right place | Ensures users get the content they're looking for |
| Homepage Controller | Creates a welcoming first page | Gives users a starting point for navigation |
| Page Templates | Provides a consistent look across pages | Makes the site feel professional and cohesive |

## Part 2: Forms and Data Validation

This section focuses on collecting and validating user information through web forms. I created a system that not only accepts user input but also helps ensure the information is correct and complete.

### The Login Experience
When users first arrive at the login page, they see a clean, straightforward form:

![Login Form](loginForm.png)

After they submit their information, the system processes their input and confirms it received their data:

![Console Output](LoginUpdated.png)

### Making Sure Data Is Valid
The form includes features to help users enter correct information on their first try. When something needs fixing, users see clear messages explaining what went wrong:

![Validation Errors](LoginFailed.png)

These error messages appear right where users need them – next to the fields that need attention. The system uses everyday language to explain problems and suggest solutions, making it easy for users to correct their entries.

### Managing Orders
The orders section organizes information in an easy-to-read format:

| What We Track | How We Show It | Why It's Important |
|---------------|----------------|-------------------|
| Order Numbers | Clear IDs | Helps track each purchase |
| Product Info | Descriptive Text | Tells us what was ordered |
| Pricing | Dollar Amount | Tracks costs accurately |
| Amount Ordered | Numbers | Keeps inventory current |

## Part 3: Page Layouts

### Creating a Consistent Look
Every page in the system shares common design elements that make navigation intuitive and familiar. The layout starts with a header containing our logo and main menu. Below that, each page has its own content area that adapts based on what we're showing – whether it's a form, table, or other information.

Here's how it looks in practice:

Login Page Example:
![Login with Layout](OrdersForm.png)

Orders Page Example:
![Orders with Layout](OrdersUpdated.png)

The layout system brings together several technologies to create a seamless experience:

| Part of the System | Technology Used | What It Accomplishes |
|-------------------|-----------------|---------------------|
| Page Header & Footer | Thymeleaf Pieces | Keeps branding consistent |
| Screen Adaptation | Bootstrap Framework | Works well on all devices |
| Site Navigation | Spring Routing | Makes moving between pages easy |
| Page Structure | Thymeleaf Templates | Maintains consistent design |
=======
The parameter handling implementation correctly displays the message "Owen!" when accessed through localhost:8080/hello/test4?message=Owen!, demonstrating successful URL parameter processing.

### Code Implementation
The implementation includes properly structured controllers and templates:
1. HelloWorldController with mappings for all test endpoints
2. HomeController configured for root URL handling
3. Thymeleaf templates with proper navigation between endpoints

All endpoints demonstrate working functionality with proper navigation links between test routes, fulfilling the assignment requirements for creating and implementing Spring MVC controllers, models, and views.

## Part 2: Forms and Data Validation
### Login Form Implementation
We have successfully implemented form handling and data validation using Spring MVC and JSR-303 validation framework.

#### Initial Login Form
![Login Form](loginForm.png)
The initial implementation shows a clean login form with username and password fields, demonstrating proper form structure and Thymeleaf integration.

#### Console Output Verification
![Console Output](LoginUpdated.png)
The console output confirms successful form submission and data handling, showing the captured username and password values.

#### Validation Implementation
![Validation Errors](LoginFailed.png)
The form validation implementation successfully displays error messages when validation rules are violated, showing:
- Field-level error messages for username and password
- Proper error styling and placement
- Clear user feedback for validation failures

### Orders Display Implementation
The orders view successfully displays the submitted data in a structured table format, showing:
- Order numbers
- Product names
- Prices
- Quantities

## Part 3: Thymeleaf Layouts
### Layout Implementation Screenshots

#### Login Page with Layout
![Login with Layout](OrdersForm.png)
The login page demonstrates successful implementation of the common layout, featuring:
- Consistent header with GCU logo
- Navigation bar
- Centered content
- Standardized footer

#### Orders Page with Layout
![Orders with Layout](OrdersUpdated.png)
The orders page shows the same layout consistency, including:
- Proper template inheritance
- Consistent styling
- Maintained functionality within the layout framework

### Layout Features
The implementation includes:
1. Common header and footer fragments
2. Bootstrap integration for responsive design
3. Proper template inheritance structure
4. Consistent navigation across pages
>>>>>>> d395dc0fc9feac720ed91a2b04305756f68c6679

## Research Questions

### Question 1: How does Spring MVC support the MVC design pattern? Draw a diagram that supports the answer to this question.

<<<<<<< HEAD
Spring MVC creates web applications by dividing responsibilities among three main parts that work together seamlessly. Think of it like a busy airport terminal: The Controller is like the air traffic control tower, coordinating all incoming and outgoing traffic and making sure everything gets to where it needs to go. The Model is like the behind-the-scenes operations - baggage handling systems, security protocols, and flight databases - processing information and maintaining the status of every flight and passenger. The View is like the terminal displays and announcement systems that travelers interact with - departure boards, gate information screens, and check-in kiosks that present information in a way that makes sense to passengers. Just as air traffic control coordinates between ground operations and passenger information systems, the Controller in Spring MVC manages the flow of information between the Model's data processing and the View's user interface.

Here's how information flows through the system:

```mermaid
graph LR
    A[User Request] --> B[DispatcherServlet]
=======
**Answer:**

Spring MVC implements the Model-View-Controller pattern through a comprehensive framework that separates concerns while maintaining tight integration:

**Controller Layer**:
- Handles HTTP requests through annotated classes (@Controller)
- Processes user input and manages application flow
- Coordinates between Model and View layers

**Model Layer**:
- Represents business data and logic
- Maintains application state
- Implements validation rules and business constraints

**View Layer**:
- Renders the user interface using Thymeleaf templates
- Displays data from the Model
- Handles user interaction elements

Here's a diagram illustrating the flow:

```mermaid
graph LR
    A[Client Request] --> B[DispatcherServlet]
>>>>>>> d395dc0fc9feac720ed91a2b04305756f68c6679
    B --> C[Controller]
    C --> D[Model]
    C --> E[View Resolver]
    E --> F[View Template]
<<<<<<< HEAD
    F --> G[Response to User]
=======
    F --> G[Response]
>>>>>>> d395dc0fc9feac720ed91a2b04305756f68c6679
```

### Question 2: Research and identify 2 MVC Frameworks other than Spring MVC. What are the frameworks and how do they differ from Spring MVC?

<<<<<<< HEAD
Two other popular ways to build web applications are Django and ASP.NET MVC. Each takes a unique approach to solving similar problems.

Django, written in Python, comes with more built-in features than Spring MVC. Imagine getting a fully furnished house instead of an empty one – Django gives you more to start with, including an administrative interface ready to use. It has stronger opinions about how you should organize your code, which can make projects more consistent but sometimes less flexible.

ASP.NET MVC is Microsoft's approach to web development. It's deeply connected with Windows and the Visual Studio development environment, much like having a house that's specifically designed for a particular neighborhood. It uses its own view system called Razor instead of Thymeleaf, and it works especially well with other Microsoft tools. While this creates a powerful ecosystem, it might limit where and how you can deploy your application compared to Spring MVC's more adaptable nature.
=======
**Answer:**

**Django (Python)**:
- Different from Spring MVC in:
  - Python-based instead of Java
  - More opinionated about project structure
  - Includes built-in admin interface
  - Tighter coupling between components

**ASP.NET MVC (C#)**:
- Different from Spring MVC in:
  - Windows-centric deployment
  - Closer integration with Visual Studio IDE
  - Different view engine (Razor)
  - More reliance on Microsoft ecosystem
>>>>>>> d395dc0fc9feac720ed91a2b04305756f68c6679
