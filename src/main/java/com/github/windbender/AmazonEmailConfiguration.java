package com.github.windbender;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AmazonEmailConfiguration {
	@NotNull
    @JsonProperty
    String SMTPUsername;
		
	@NotNull
    @JsonProperty
    String SMTPPassword;
		
	@NotNull
    @JsonProperty
    String port;
		
	@NotNull
    @JsonProperty
    String SMTPHost;

	public String getSMTPUsername() {
		return SMTPUsername;
	}

	public void setSMTPUsername(String sMTPUsername) {
		SMTPUsername = sMTPUsername;
	}

	public String getSMTPPassword() {
		return SMTPPassword;
	}

	public void setSMTPPassword(String sMTPPassword) {
		SMTPPassword = sMTPPassword;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getSMTPHost() {
		return SMTPHost;
	}

	public void setSMTPHost(String sMTPHost) {
		SMTPHost = sMTPHost;
	}

	
}
