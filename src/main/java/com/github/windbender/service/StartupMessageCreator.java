package com.github.windbender.service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.github.windbender.WLCDMServerConfiguration;
import com.github.windbender.domain.User;

public class StartupMessageCreator implements MessageCreator {

	

	
	@Override
	public MimeMessage createMessage(Session session) throws AddressException,
			MessagingException {
		MimeMessage message = new MimeMessage(session);

		// Set From: header field of the header.
		message.setFrom(new InternetAddress("chrischris+startup@1reality.org"));

		// Set To: header field of the header.
		message.addRecipient(Message.RecipientType.TO, new InternetAddress("chris8schaefer@gmail.com"));

		// Set Subject: header field
		message.setSubject("wildlifecam: STARTUP");

		// Send the actual HTML message, as big as you like
		message.setContent(
				"<h4>the camera program just restarted</h4>",
				"text/html");
		return message;
	}
}
