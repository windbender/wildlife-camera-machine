package com.github.windbender;


import org.eclipse.jetty.server.session.SessionHandler;

import com.bazaarvoice.dropwizard.assets.ConfiguredAssetsBundle;
import com.github.windbender.auth.SessionUserProvider;
import com.github.windbender.core.FileImageStore;
import com.github.windbender.core.HibernateDataStore;
import com.github.windbender.core.ImageStore;
import com.github.windbender.core.S3ImageStore;
import com.github.windbender.dao.EventDAO;
import com.github.windbender.dao.HibernateUserDAO;
import com.github.windbender.dao.IdentificationDAO;
import com.github.windbender.dao.ImageRecordDAO;
import com.github.windbender.dao.SpeciesDAO;
import com.github.windbender.dao.TokenDAO;
import com.github.windbender.domain.Identification;
import com.github.windbender.domain.ImageEvent;
import com.github.windbender.domain.ImageRecord;
import com.github.windbender.domain.Species;
import com.github.windbender.domain.User;
import com.github.windbender.resources.ImageResource;
import com.github.windbender.resources.UserResource;
import com.github.windbender.service.AmazonMessageSender;
import com.github.windbender.service.AsyncEmailSender;
import com.github.windbender.service.EmailService;
import com.github.windbender.service.MessageSender;
import com.github.windbender.service.SMTPMessageSender;
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
			Identification.class,ImageRecord.class,ImageEvent.class,User.class,Species.class) {
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
        final EventDAO ieDAO = new EventDAO(hibernate.getSessionFactory());
        final TokenDAO tokenDAO = new TokenDAO(hibernate.getSessionFactory());
        
        HibernateDataStore ds = new HibernateDataStore(idDAO,irDAO,spDAO,uDAO, ieDAO, hibernate.getSessionFactory());
    	environment.manage(ds);
    	String bucketName = "wlcdm-test";
    	ImageStore store = null;
    	if(configuration.isAmazon()) {
    		store = new S3ImageStore(configuration.getAmazon().getAccesskey(), configuration.getAmazon().getSecretkey(), bucketName);
    	} else {
    		store = new FileImageStore("/Users/chris/Sites/s3fake/upload");
    	}
    	
    	MessageSender ms = null;
		if(configuration.isAmazon()) {
			ms = new AmazonMessageSender(configuration);
		} else {
			ms = new SMTPMessageSender(configuration);
		}
		EmailService emailService;
		if(configuration.isAsync() ) {
			AsyncEmailSender ams = new AsyncEmailSender(configuration, ms);
			environment.manage(ams);
			emailService = new EmailService(configuration, ams);
		} else {
			emailService = new EmailService(configuration, ms);
		}
		environment.addResource(new UserResource(uDAO, tokenDAO, emailService));
		environment.addResource(new ImageResource(ds, store, irDAO));
		
		environment.setSessionHandler(new SessionHandler());
		environment.addProvider(SessionUserProvider.class);
		
	}
}
