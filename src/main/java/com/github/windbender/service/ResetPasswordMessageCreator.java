package com.github.windbender.service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.github.windbender.WLCDMServerConfiguration;
import com.github.windbender.domain.User;

public class ResetPasswordMessageCreator implements MessageCreator {

	User u;
	String token;
	WLCDMServerConfiguration configuration;
	public ResetPasswordMessageCreator(User u, String token,
			WLCDMServerConfiguration configuration) {
		this.u = u;
		this.token = token;
		this.configuration = configuration;
	}

	@Override
	public MimeMessage createMessage(Session session) throws AddressException,
			MessagingException {


		
		MimeMessage message = new MimeMessage(session);

		// Set From: header field of the header.
		message.setFrom(new InternetAddress(configuration.getEmailFrom()));

		// Set To: header field of the header.
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(u.getEmail()));

		// Set Subject: header field
		message.setSubject("wildlifecam: Password Reset");

		// Send the actual HTML message, as big as you like
		message.setContent(
				"<h4>Please click on the following link to reset your password.<a href=\""
						+configuration.getRootURL()+"#/resetpw/"+ token 
						+ "\" >CLICK ME</a>.  This link will only be valid for 10 minutes from time of creation</h4>",
				"text/html");
		return message;
	}

}
