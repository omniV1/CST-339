package com.gcu.topic04.model;

public class RegistrationModel {

	private Long id;
	private String username;
	private String password;
	private String address;
	private int phoneNumber;

	public RegistrationModel(Long id, String username, String password, String address, int phoneNumber) {
		super();
		this.id = id;
		this.username = username;
		this.password = password;
		this.address = address;
		this.phoneNumber = phoneNumber;
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(int phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	@Override
	public String toString() {
		return "RegistrationModel [id=" + id + ", username=" + username + ", password=" + password + ", address="
				+ address + ", phoneNumber=" + phoneNumber + "]";
	}

}
