package com.github.windbender.service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.github.windbender.WLCDMServerConfiguration;
import com.github.windbender.domain.User;

public class VerificationMessageCreator implements MessageCreator {

	User u;
	WLCDMServerConfiguration configuration;
	public VerificationMessageCreator(User u,WLCDMServerConfiguration configuration) {
		this.u = u;
		this.configuration = configuration;
	}

	@Override
	public MimeMessage createMessage(Session session) throws AddressException, MessagingException {
		
		
		MimeMessage message = new MimeMessage(session);

		// Set From: header field of the header.
		message.setFrom(new InternetAddress(configuration.getEmailFrom()));

		// Set To: header field of the header.
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(u.getEmail()));

		// Set Subject: header field
		message.setSubject("wildlifecam: Please verify your account");

		// Send the actual HTML message, as big as you like
		message.setContent(
				"<h4>Please click on the following link to verify your email <a href=\""+configuration.getRootURL()+"/#/verify/?verifyCode="
						+ u.getVerifyCode() + "\" >CLICK ME</a></h4>",
				"text/html");
		return message;

	}

}
