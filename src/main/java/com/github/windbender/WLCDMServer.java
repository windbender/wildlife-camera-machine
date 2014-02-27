package com.github.windbender;


import org.eclipse.jetty.server.session.SessionHandler;

import com.bazaarvoice.dropwizard.assets.ConfiguredAssetsBundle;
import com.github.windbender.auth.SessionUserProvider;
import com.github.windbender.core.DataStore;
import com.github.windbender.core.FileImageStore;
import com.github.windbender.core.HibernateDataStore;
import com.github.windbender.core.ImageStore;
import com.github.windbender.core.S3ImageStore;
import com.github.windbender.dao.HibernateUserDAO;
import com.github.windbender.dao.IdentificationDAO;
import com.github.windbender.dao.ImageRecordDAO;
import com.github.windbender.dao.SpeciesDAO;
import com.github.windbender.domain.Identification;
import com.github.windbender.domain.ImageRecord;
import com.github.windbender.domain.Species;
import com.github.windbender.domain.User;
import com.github.windbender.resources.ImageResource;
import com.github.windbender.resources.UserResource;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.db.DatabaseConfiguration;
import com.yammer.dropwizard.hibernate.HibernateBundle;
import com.yammer.dropwizard.migrations.MigrationsBundle;

public class WLCDMServer extends Service<WLCDMServerConfiguration> {
	public static void main(String[] args) throws Exception {
		new WLCDMServer().run(args);
	}

	private WLCDMServer() {
		super();
	}
	
	

	@Override
	public void initialize(Bootstrap<WLCDMServerConfiguration> bootstrap) {
		bootstrap.setName("wlcdm");
	    //bootstrap.addBundle(new AssetsBundle("/assets", "/"));
        bootstrap.addBundle(new ConfiguredAssetsBundle("/assets/", "/"));
        bootstrap.addBundle(hibernate);

        bootstrap.addBundle(new MigrationsBundle<WLCDMServerConfiguration>() {
            @Override
            public DatabaseConfiguration getDatabaseConfiguration(WLCDMServerConfiguration configuration) {
                return configuration.getDatabaseConfiguration();
            }
        });
    }

      

	private final HibernateBundle<WLCDMServerConfiguration> hibernate = new HibernateBundle<WLCDMServerConfiguration>(
			Identification.class,ImageRecord.class,User.class,Species.class) {
	    @Override
	    public DatabaseConfiguration getDatabaseConfiguration(WLCDMServerConfiguration configuration) {
	        return configuration.getDatabaseConfiguration();
	    }
	};

	@Override
	public void run(WLCDMServerConfiguration configuration,
			Environment environment) {
        
        final IdentificationDAO idDAO = new IdentificationDAO(hibernate.getSessionFactory());
        final ImageRecordDAO irDAO = new ImageRecordDAO(hibernate.getSessionFactory());
        final SpeciesDAO spDAO = new SpeciesDAO(hibernate.getSessionFactory());
        final HibernateUserDAO uDAO = new HibernateUserDAO(hibernate.getSessionFactory());
        
        
    	DataStore ds = new HibernateDataStore(idDAO,irDAO,spDAO,uDAO);
    	
    	String bucketName = "wlcdm-test";
    	ImageStore store = new S3ImageStore(configuration.getAmazon().getAccesskey(), configuration.getAmazon().getSecretkey(), bucketName);
    	ImageStore bStore = new FileImageStore("/Users/chris/Sites/s3fake/upload");

		environment.addResource(new UserResource());
		environment.addResource(new ImageResource(ds, store, irDAO));
		
		environment.setSessionHandler(new SessionHandler());
		environment.addProvider(SessionUserProvider.class);
		
	}
}
