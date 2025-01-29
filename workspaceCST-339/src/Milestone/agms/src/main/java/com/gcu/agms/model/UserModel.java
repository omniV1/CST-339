package com.gcu.agms.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * User model class that represents a user in the system
 * Contains all necessary fields for user registration and validation
 */
/**
 * UserModel represents a user in the system with various attributes such as
 * first name, last name, email, phone number, username, password, and role.
 * It includes validation constraints to ensure data integrity.
 * 
 * <p>Attributes:</p>
 * <ul>
 *   <li>authCode: Authorization code for the user.</li>
 *   <li>firstName: User's first name, must be between 2 and 32 characters.</li>
 *   <li>lastName: User's last name, must be between 2 and 32 characters.</li>
 *   <li>email: User's email address, must be a valid email format.</li>
 *   <li>phoneNumber: User's phone number, must be a valid phone number format.</li>
 *   <li>username: User's username, must be between 3 and 32 characters.</li>
 *   <li>password: User's password, must be at least 8 characters long and contain at least one digit, one lowercase letter, one uppercase letter, and one special character.</li>
 *   <li>role: User's role in the system, default is PUBLIC.</li>
 * </ul>
 * 
 * <p>Validation Constraints:</p>
 * <ul>
 *   <li>@NotEmpty: Ensures the field is not empty.</li>
 *   <li>@Size: Ensures the field length is within the specified range.</li>
 *   <li>@Email: Ensures the field is a valid email address.</li>
 *   <li>@Pattern: Ensures the field matches the specified regular expression.</li>
 *   <li>@NotNull: Ensures the field is not null.</li>
 * </ul>
 * 
 * <p>Methods:</p>
 * <ul>
 *   <li>getAuthCode: Returns the authorization code.</li>
 *   <li>setAuthCode: Sets the authorization code.</li>
 *   <li>getFirstName: Returns the first name.</li>
 *   <li>setFirstName: Sets the first name.</li>
 *   <li>getLastName: Returns the last name.</li>
 *   <li>setLastName: Sets the last name.</li>
 *   <li>getEmail: Returns the email address.</li>
 *   <li>setEmail: Sets the email address.</li>
 *   <li>getPhoneNumber: Returns the phone number.</li>
 *   <li>setPhoneNumber: Sets the phone number.</li>
 *   <li>getUsername: Returns the username.</li>
 *   <li>setUsername: Sets the username.</li>
 *   <li>getPassword: Returns the password.</li>
 *   <li>setPassword: Sets the password.</li>
 *   <li>getRole: Returns the user role.</li>
 *   <li>setRole: Sets the user role.</li>
 * </ul>
 */
@Data
public class UserModel {

    private String authCode;

    @NotEmpty(message = "First name is required")
    @Size(min = 2, max = 32, message = "First name must be between 2 and 32 characters")
    private String firstName;

    @NotEmpty(message = "Last name is required")
    @Size(min = 2, max = 32, message = "Last name must be between 2 and 32 characters")
    private String lastName;

    @NotEmpty(message = "Email address is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotEmpty(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[1-9][0-9]{7,14}$", message = "Please provide a valid phone number")
    private String phoneNumber;

    @NotEmpty(message = "Username is required")
    @Size(min = 3, max = 32, message = "Username must be between 3 and 32 characters")
    private String username;

    @NotEmpty(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).*$", 
             message = "Password must contain at least one digit, one lowercase, one uppercase, and one special character")
    private String password;

    @NotNull(message = "User role is required")
    private UserRole role = UserRole.PUBLIC;  // Set default value

    
    /** 
     * @param authCode
     */
    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }
    
    
    /** 
     * @return String
     */
    public String getAuthCode() {
        return authCode;
    }
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}
