package com.github.windbender.service;

import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.windbender.AmazonEmailConfiguration;
import com.github.windbender.WLCDMServerConfiguration;

public class AmazonMessageSender implements MessageSender {
	Logger log = LoggerFactory.getLogger(AmazonMessageSender.class);

	// from   http://docs.aws.amazon.com/ses/latest/DeveloperGuide/send-using-smtp-java.html
	
	AmazonEmailConfiguration aec;
	public AmazonMessageSender(WLCDMServerConfiguration configuration) {
		aec = configuration.getAmazonEmailConfiguration();
	}


   
	@Override
	public void sendMessage(MessageCreator mc) throws MessagingException {

		Properties props = System.getProperties();
    	props.put("mail.transport.protocol", "smtp");
    	props.put("mail.smtp.port", Integer.parseInt(aec.getPort())); 
    	
    	// needed for java 7 on amazon
    	props.put("mail.smtp.ssl.ciphersuites","SSL_RSA_WITH_RC4_128_MD5 SSL_RSA_WITH_RC4_128_SHA TLS_RSA_WITH_AES_128_CBC_SHA TLS_DHE_RSA_WITH_AES_128_CBC_SHA TLS_DHE_DSS_WITH_AES_128_CBC_SHA SSL_RSA_WITH_3DES_EDE_CBC_SHA SSL_DHE_RSA_WITH_3DES_EDE_CBC_SHA SSL_DHE_DSS_WITH_3DES_EDE_CBC_SHA SSL_RSA_WITH_DES_CBC_SHA SSL_DHE_RSA_WITH_DES_CBC_SHA SSL_DHE_DSS_WITH_DES_CBC_SHA SSL_RSA_EXPORT_WITH_RC4_40_MD5 SSL_RSA_EXPORT_WITH_DES40_CBC_SHA SSL_DHE_RSA_EXPORT_WITH_DES40_CBC_SHA SSL_DHE_DSS_EXPORT_WITH_DES40_CBC_SHA TLS_EMPTY_RENEGOTIATION_INFO_SCSV");
    	// Set properties indicating that we want to use STARTTLS to encrypt the connection.
    	// The SMTP session will begin on an unencrypted connection, and then the client
        // will issue a STARTTLS command to upgrade to an encrypted connection.
    	props.put("mail.smtp.auth", "true");
//    	props.put("mail.smtp.starttls.enable", "true");
//    	props.put("mail.smtp.starttls.required", "true");
    	props.put("mail.smtp.ssl.enable", "true");
//    	props.put("mail.smtp.starttls.required", "true");

        // Create a Session object to represent a mail session with the specified properties. 
    	Session session = Session.getDefaultInstance(props);

        // Create a message with the specified information. 
    	MimeMessage msg = mc.createMessage(session);
    	

            
        // Create a transport.        
        Transport transport = session.getTransport();
                    
        // Send the message.
        try
        {
        	long start = System.currentTimeMillis();
            log.info("Attempting to send an email through the Amazon SES SMTP interface...");
            
            // Connect to Amazon SES using the SMTP username and password you specified above.
            transport.connect(aec.getSMTPHost(), aec.getSMTPUsername(), aec.getSMTPPassword());
        	
            // Send the email.
            transport.sendMessage(msg, msg.getAllRecipients());
        	long end = System.currentTimeMillis();
            log.info("Email sent in "+(end-start)+" milliseconds");
        }
        catch (Exception ex) {
        	log.error("The email was not sent to "+msg.getAllRecipients()+"  because ",ex);
        }
        finally
        {
            // Close and terminate the connection.
            transport.close();        	
        }

	}


	

}
