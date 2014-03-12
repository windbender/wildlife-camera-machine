package com.github.windbender.core;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

public class SignupRequest {
	
	@NotEmpty
	@Size(min = 1)
	String firstName;

	@NotEmpty
	@Size(min = 2)
	String lastName;

	@NotEmpty
	@Size(min = 7)
	String mobile;

	@NotEmpty
	@Size(min = 4)
	String username;
	
	@NotEmpty
	@Email
	String email;
	
	@NotEmpty
	@Size(min = 6)
	String password;
	
	@NotEmpty
	String initialData;
	
	public String getInitialData() {
		return initialData;
	}
	public void setInitialData(String initialData) {
		this.initialData = initialData;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getEmail() {
		return email;
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
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	@Override
	public String toString() {
		return "SignupRequest [username=" + username + ", email=" + email
				+ ", password=" + password + "]";
	}
	
	
}
