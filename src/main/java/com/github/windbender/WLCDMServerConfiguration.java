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
	private AmazonS3Configuration amazon = new AmazonS3Configuration();


	public AmazonS3Configuration getAmazon() {
		return amazon;
	}
	
}
