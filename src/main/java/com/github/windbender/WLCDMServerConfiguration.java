package com.github.windbender;

import javax.validation.constraints.NotNull;

import com.bazaarvoice.dropwizard.assets.AssetsBundleConfiguration;
import com.bazaarvoice.dropwizard.assets.AssetsConfiguration;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.config.Configuration;
import com.yammer.dropwizard.db.DatabaseConfiguration;

public class WLCDMServerConfiguration extends Configuration implements
AssetsBundleConfiguration {

    @NotNull
    @JsonProperty
    private final AssetsConfiguration assets = new AssetsConfiguration();


	@Override
	public AssetsConfiguration getAssetsConfiguration() {
		return assets;
	}

	@NotNull
	@JsonProperty
    private DatabaseConfiguration database = new DatabaseConfiguration();
	
	public DatabaseConfiguration getDatabaseConfiguration() {
		return database;
	}
	
	@NotNull
	@JsonProperty
	private AmazonS3Configuration amazonS3 = new AmazonS3Configuration();
	
	@JsonProperty
	private String SMTPMachine;
	@JsonProperty
	private String SMTPPort;
	@JsonProperty
	private String SMTPUser;
	@JsonProperty
	private String SMTPPass;
	
	@JsonProperty
	@NotNull
	private boolean async;
	
	@JsonProperty
	@NotNull
	private boolean amazon;
	
	@JsonProperty
	private AmazonEmailConfiguration amazonEmailConfiguration;
	
	@JsonProperty
	@NotNull
	private String emailFrom;
	
	@JsonProperty
	@NotNull
	private String rootURL;;


	public AmazonS3Configuration getAmazon() {
		return amazonS3;
	}


	public String getEmailFrom() {
		return emailFrom;
	}


	public String getRootURL() {
		return rootURL;
	}


	public AmazonEmailConfiguration getAmazonEmailConfiguration() {
		return amazonEmailConfiguration;
	}


	public boolean isAmazon() {
		return amazon;
	}


	public boolean isAsync() {
		return async;
	}


	public String getSMTPPass() {
		return SMTPPass;
	}


	public String getSMTPUser() {
		return SMTPUser;
	}


	public String getSMTPPort() {
		return SMTPPort;
	}


	public String getSMTPMachine() {
		return SMTPMachine;
	}
	
}
