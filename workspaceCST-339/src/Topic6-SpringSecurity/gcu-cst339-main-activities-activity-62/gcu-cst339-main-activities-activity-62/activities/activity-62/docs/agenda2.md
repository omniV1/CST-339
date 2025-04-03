# Topic 6 Agenda - 2

## Grand Canyon University Note

- This code will not execute correctly, since the code utilized is not available
- GCU will update this course in a future date
- Turn in code, what you have for full credit

## Securing a Web Application Using a Database

#### users Collection Creation

- Create new cst339.data.entity.UserEntity.java
- Create new cst339.controller.OrdersController.java
- Create new cst339.data.repository.UsersRepository.java
- Create new cst339.business.UsersDataAccessInterface.java
- Create new cst339.business.UsersDataService.java
- Create new cst339.business.BusinessService.java
     - add method loadUserByUsername
- Delete cst.business.SecurityBusinessService.java
     - only remove the implementation
- Update cst339.SecurityConfig.java
     - comment out previous autowired configuration code
     - add AuthenticationManagerBuilder code
