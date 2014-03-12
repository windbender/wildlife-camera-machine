package com.github.windbender.core;

import com.github.windbender.domain.User;


public class VerifyResponse {

	String username;
	public VerifyResponse(User u) {
		username = u.getUsername();
	}

	public String getUsername() {
		return username;
	}
	
	

}
