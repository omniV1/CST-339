package com.gcu.agms.model.auth;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * The LoginModel class represents the data model for user login information.
 * It contains fields for username and password, along with validation constraints
 * and getter and setter methods for these fields.
 * 
 * <p>
 * The username and password fields are annotated with validation constraints to ensure
 * that they are not null and have a length between 1 and 32 characters.
 * </p>
 * 
 * <p>
 * Example usage:
 * <pre>
 * {@code
 * LoginModel login = new LoginModel();
 * login.setUsername("user123");
 * login.setPassword("password123");
 * }
 * </pre>
 * </p>
 * 
 * <p>
 * The class also overrides the toString() method to provide a string representation
 * of the login model, including the username and password.
 * </p>
 * 
 * <p>
 * Annotations used:
 * <ul>
 *   <li>@NotNull - Ensures that the field is not null.</li>
 *   <li>@Size - Ensures that the field length is within the specified range.</li>
 * </ul>
 * </p>
 * 
 */
public class LoginModel {

	@NotNull(message="Username is a required field")
	@Size(min=1, max=32, message="Username must be between 1 and 32 characters")
	private String username;
	
	@NotNull(message="Password is a required field")
	@Size(min=1, max=32, message="Password must be between 1 and 32 characters")
	private String password;

	
	/** 
	 * @return String
	 */
	public String getUsername() {
		return username;
	}

	
	/** 
	 * @param username
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "LoginModel [username=" + username + ", password=" + password + "]";
	}
}