package com.github.windbender.core;

import com.github.windbender.domain.User;

public class UserTO {
	public UserTO(User in) {
		this.username = in.getUsername();
		this.id = in.getId();
		this.email = in.getEmail();
	}
	
	String username;
	int id;
	String email;
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
}
