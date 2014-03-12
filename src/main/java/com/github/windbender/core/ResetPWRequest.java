package com.github.windbender.core;

public class ResetPWRequest {
	String token;
	String pass;
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getPass() {
		return pass;
	}
	public void setPass(String pass) {
		this.pass = pass;
	}
	@Override
	public String toString() {
		return "ResetPWRequest [token=" + token + ", pass=" + "XXXX" + "]";
	}

	
}
