package com.github.windbender;


import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.mail.MessagingException;

import org.eclipse.jetty.server.session.SessionHandler;

import com.amazonaws.util.StringInputStream;
import com.bazaarvoice.dropwizard.assets.ConfiguredAssetsBundle;
import com.github.windbender.auth.SessionAuthProvider;
import com.github.windbender.auth.SessionCurProjProvider;
import com.github.windbender.auth.SessionUserProvider;
import com.github.windbender.core.FileImageStore;
import com.github.windbender.core.HibernateDataStore;
import com.github.windbender.core.ImageStore;
import com.github.windbender.core.S3ImageStore;
import com.github.windbender.dao.CameraDAO;
import com.github.windbender.dao.EventDAO;
import com.github.windbender.dao.GoodDAO;
import com.github.windbender.dao.HibernateUserDAO;
import com.github.windbender.dao.IdentificationDAO;
import com.github.windbender.dao.ImageRecordDAO;
import com.github.windbender.dao.InviteDAO;
import com.github.windbender.dao.ProjectDAO;
import com.github.windbender.dao.ReportDAO;
import com.github.windbender.dao.ReviewDAO;
import com.github.windbender.dao.SpeciesDAO;
import com.github.windbender.dao.TokenDAO;
import com.github.windbender.dao.UserProjectDAO;
import com.github.windbender.domain.Camera;
import com.github.windbender.domain.Good;
import com.github.windbender.domain.Identification;
import com.github.windbender.domain.ImageEvent;
import com.github.windbender.domain.ImageRecord;
import com.github.windbender.domain.Invite;
import com.github.windbender.domain.Project;
import com.github.windbender.domain.ResetPasswordToken;
import com.github.windbender.domain.Review;
import com.github.windbender.domain.Species;
import com.github.windbender.domain.User;
import com.github.windbender.domain.UserProject;
import com.github.windbender.resources.CameraResource;
import com.github.windbender.resources.ImageResource;
import com.github.windbender.resources.ProjectResource;
import com.github.windbender.resources.ReportResource;
import com.github.windbender.resources.UserProjectResource;
import com.github.windbender.resources.UserResource;
import com.github.windbender.service.AsyncEmailSender;
import com.github.windbender.service.CachingTimeZoneGetter;
import com.github.windbender.service.CompositeTimeZoneGetter;
import com.github.windbender.service.EmailService;
import com.github.windbender.service.GeoNameTimeZoneGetter;
import com.github.windbender.service.MakeDatesService;
import com.github.windbender.service.MessageSender;
import com.github.windbender.service.SMTPMessageSender;
import com.github.windbender.service.StartupMessageCreator;
import com.github.windbender.service.StupidTimeZoneGetter;
import com.github.windbender.service.TimeZoneGetter;
import com.sun.jersey.multipart.impl.MultiPartConfigProvider;
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
			Identification.class,ImageRecord.class,Invite.class,ImageEvent.class,User.class,Species.class,Project.class, UserProject.class, Camera.class, ResetPasswordToken.class, Review.class, Good.class) {
	    @Override
	    public DatabaseConfiguration getDatabaseConfiguration(WLCDMServerConfiguration configuration) {
	        return configuration.getDatabaseConfiguration();
	    }
	};

	@Override
	public void run(WLCDMServerConfiguration configuration,
			Environment environment) {
        try {
        	InputStream is = new StringInputStream("hello");
        	BufferedInputStream bis = new BufferedInputStream(is);
        	BufferedImage bi = ImageIO.read(bis);
        } catch(Exception e) {
        	System.out.println("that didn't work because:"+e);
        }
		String geoNameUsername = configuration.getGeoNameUsername();
		TimeZoneGetter timeZoneGetter = new CompositeTimeZoneGetter(new CachingTimeZoneGetter(new GeoNameTimeZoneGetter(geoNameUsername)), new StupidTimeZoneGetter());

        final IdentificationDAO idDAO = new IdentificationDAO(hibernate.getSessionFactory());
        final ImageRecordDAO irDAO = new ImageRecordDAO(hibernate.getSessionFactory());
        final SpeciesDAO spDAO = new SpeciesDAO(hibernate.getSessionFactory());
        final HibernateUserDAO uDAO = new HibernateUserDAO(hibernate.getSessionFactory());
        final EventDAO ieDAO = new EventDAO(hibernate.getSessionFactory());
        final TokenDAO tokenDAO = new TokenDAO(hibernate.getSessionFactory());
        final ProjectDAO projDAO = new ProjectDAO(hibernate.getSessionFactory());
        final UserProjectDAO upDAO = new UserProjectDAO(hibernate.getSessionFactory());
        final ReportDAO reportDAO = new ReportDAO(hibernate.getSessionFactory(), ieDAO, idDAO);
        final CameraDAO cameraDAO = new CameraDAO(hibernate.getSessionFactory());
        final ReviewDAO reviewDAO = new ReviewDAO(hibernate.getSessionFactory());
        final GoodDAO goodDAO = new GoodDAO(hibernate.getSessionFactory());
        final InviteDAO inviteDAO = new InviteDAO(hibernate.getSessionFactory());
        
        HibernateDataStore ds = new HibernateDataStore(idDAO,irDAO,spDAO,uDAO, ieDAO, hibernate.getSessionFactory(), timeZoneGetter);
    	environment.manage(ds);
    	String bucketName = "wlcdm-test";
    	ImageStore store = null;
    	if(configuration.isAmazon()) {
    		store = new S3ImageStore(configuration.getAmazon().getAccesskey(), configuration.getAmazon().getSecretkey(), bucketName);
    	} else {
    		store = new FileImageStore("/Users/chris/Sites/s3fake/upload");
    	}
    	
    	MessageSender ms = null;
//		if(configuration.isAmazon()) {
//			ms = new AmazonMessageSender(configuration);
//			
//		} else {
			ms = new SMTPMessageSender(configuration);
//		}
		EmailService emailService;
		if(configuration.isAsync() ) {
			AsyncEmailSender ams = new AsyncEmailSender(configuration, ms);
			environment.manage(ams);
			emailService = new EmailService(configuration, ams);
		} else {
			emailService = new EmailService(configuration, ms);
		}
		
		
//		HashSessionManager hsm = new HashSessionManager();
		IterableHashSessionManager hsm = new IterableHashSessionManager();
		try {
			File dir = new File(configuration.getSessionPersistDirectory());
			File f = dir.getCanonicalFile();
			if(!f.exists()) {
				if(!f.mkdir()) {
					throw new IllegalArgumentException("can't create session directory");
				}
			}
			
			if(!dir.isDirectory()) {
				throw new IOException("persistence directory is not a directory");
			}
			
			hsm.setStoreDirectory(dir);
			hsm.setIdleSavePeriod(10*60); // 10 minutes
			hsm.setSavePeriod(60);
		} catch (IOException e) {
			
		}
		SessionReloaderOperator sro = new SessionReloaderOperator(hsm, uDAO, projDAO, upDAO);

		
		environment.addResource(new UserResource(uDAO, tokenDAO, projDAO, upDAO, inviteDAO, emailService));
		environment.addResource(new ImageResource(ds, store, irDAO, spDAO, reportDAO, timeZoneGetter));
		environment.addResource(new ProjectResource(projDAO, uDAO, upDAO,inviteDAO, emailService));
		environment.addResource(new ReportResource(reportDAO, ieDAO, irDAO, reviewDAO, goodDAO));
		environment.addResource(new CameraResource(cameraDAO, projDAO));
		environment.addResource(new UserProjectResource(upDAO, projDAO, uDAO, sro));

		environment.setSessionHandler(new SessionHandler(hsm));
		environment.addProvider(SessionUserProvider.class);
		environment.addProvider(SessionAuthProvider.class);
		environment.addProvider(SessionCurProjProvider.class);
		
		environment.addProvider(MultiPartConfigProvider.class);
		environment.addProvider(com.sun.jersey.multipart.impl.MultiPartReaderServerSide.class);

		MakeDatesService mds = new MakeDatesService(hibernate.getSessionFactory());
		mds.makeDates();
		
		try {
			ms.sendMessage(new StartupMessageCreator());
			System.out.println("startup message sent");
		} catch (MessagingException e) {
			System.out.println("failed to send startup message "+e);
//			throw new RuntimeException(e);
		}
	}
}
