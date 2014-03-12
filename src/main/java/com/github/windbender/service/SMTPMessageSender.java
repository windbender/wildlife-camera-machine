package com.github.windbender.service;

import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.windbender.WLCDMServerConfiguration;

public class SMTPMessageSender implements MessageSender {
	Logger log = LoggerFactory.getLogger(SMTPMessageSender.class);

	
	WLCDMServerConfiguration conf;
	public SMTPMessageSender(WLCDMServerConfiguration configuration) {
		this.conf = configuration;
	}

	public void sendMessage(MessageCreator mc) throws MessagingException {

		// Sender's email ID needs to be mentioned

		long start = System.currentTimeMillis();

		// Get system properties
		String smtpMachine = conf.getSMTPMachine();
		String smtpPort = conf.getSMTPPort();
		final String smtpUser = conf.getSMTPUser();
		final String smtpPass = conf.getSMTPPass();
		if( (smtpMachine == null) || (smtpPort == null) || (smtpUser == null ) || (smtpPass == null)) {
			throw new IllegalArgumentException("SMTP setup cannot have nulls in the config file");
		}
		
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", smtpMachine);
		props.put("mail.smtp.port", smtpPort);
		
		// Setup mail server

		// Get the default Session object.
		Session session = Session.getInstance(props,
				  new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(smtpUser, smtpPass);
					}
				  });
		
    	MimeMessage msg = mc.createMessage(session);

		// Send message
		Transport.send(msg);
		long end = System.currentTimeMillis();
		long delta = end - start;
		log.info("Message succesfully sent.  elapsed time "+delta+" milliseconds");
	}
}
