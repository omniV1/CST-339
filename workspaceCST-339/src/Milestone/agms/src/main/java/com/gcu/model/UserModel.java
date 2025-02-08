package com.gcu.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class UserModel {

	private Long id;

	@NotEmpty(message = "Username is required")
	@Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
	private String username;

	@NotEmpty(message = "Password is required")
	@Size(min = 8, message = "Password must be at least 8 characters")
	private String password;

	@NotEmpty(message = "Email is required")
	@Email(message = "Invalid email format")
	private String email;

	private String role;
	private String contactInfo;

	public UserModel() {
	}

	public UserModel(String username, String password, String email, String role, String contactInfo) {
		this.username = username;
		this.password = password;
		this.email = email;
		this.role = role;
		this.contactInfo = contactInfo;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getContactInfo() {
		return contactInfo;
	}

	public void setContactInfo(String contactInfo) {
		this.contactInfo = contactInfo;
	}
}
