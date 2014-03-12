package com.github.windbender.service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.github.windbender.WLCDMServerConfiguration;
import com.github.windbender.domain.User;

public class OurNotificationMessage implements MessageCreator {

	private User u;
	private WLCDMServerConfiguration configuration;

	public OurNotificationMessage(User u,
			WLCDMServerConfiguration configuration) {
		this.u = u;
		this.configuration = configuration;
	}

	@Override
	public MimeMessage createMessage(Session session) throws AddressException,
			MessagingException {
		MimeMessage message = new MimeMessage(session);

		// Set From: header field of the header.
		message.setFrom(new InternetAddress(configuration.getEmailFrom()));

		// Set To: header field of the header.
		message.addRecipient(Message.RecipientType.TO, new InternetAddress("chrischris@1reality.org"));

		// Set Subject: header field
		message.setSubject("we got a new signup "+u.getUsername());

		// Send the actual HTML message, as big as you like
		message.setContent(
				"it looks like "+u.getUsername()+" signed up.  This person's email is "+u.getEmail(),
				"text/html");
		return message;
	}

}
