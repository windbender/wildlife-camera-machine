package com.github.windbender;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AmazonS3Configuration {
	@NotNull
    @JsonProperty
    String accesskey;
	
	@NotNull
    @JsonProperty
    String secretkey;

	public String getAccesskey() {
		return accesskey;
	}

	public void setAccesskey(String accesskey) {
		this.accesskey = accesskey;
	}

	public String getSecretkey() {
		return secretkey;
	}

	public void setSecretkey(String secretkey) {
		this.secretkey = secretkey;
	}
	
}
