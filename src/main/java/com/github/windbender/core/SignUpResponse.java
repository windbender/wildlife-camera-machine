package com.github.windbender.core;

public class SignUpResponse {
	boolean succesful;
	String errorMessage;
	public boolean isSuccesful() {
		return succesful;
	}
	public void setSuccesful(boolean succesful) {
		this.succesful = succesful;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public SignUpResponse(boolean succesful, String errorMessage) {
		super();
		this.succesful = succesful;
		this.errorMessage = errorMessage;
	}
	@Override
	public String toString() {
		return "SignUpResponse [succesful=" + succesful + ", errorMessage="
				+ errorMessage + "]";
	}
	

}
