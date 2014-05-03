package com.github.windbender.service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.github.windbender.WLCDMServerConfiguration;
import com.github.windbender.domain.Project;
import com.github.windbender.domain.User;

public class InviteEmailMessageCreator implements MessageCreator {

	
	private WLCDMServerConfiguration configuration;
	private Project currentProject;
	private String inviteEmail;
	private String inviteCode;
	private User user;

	public InviteEmailMessageCreator(User user, String inviteEmail, String inviteCode,
			Project currentProject, WLCDMServerConfiguration configuration) {
		this.user = user;
		this.inviteEmail = inviteEmail;
		this.inviteCode = inviteCode;
		this.currentProject = currentProject;
		this.configuration = configuration;
	}

	@Override
	public MimeMessage createMessage(Session session) throws AddressException,
			MessagingException {
		MimeMessage message = new MimeMessage(session);

		// Set From: header field of the header.
		message.setFrom(new InternetAddress(configuration.getEmailFrom()));

		// Set To: header field of the header.
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(inviteEmail));

		// Set Subject: header field
		message.setSubject("You've been invited to help");

		// Send the actual HTML message, as big as you like
		message.setContent(
				"<h4>You have been invited to help work on the "+currentProject.getName()+" project by "+user.getUsername()+" at "+user.getEmail()
				+" If you would like to help please click on the following link <a href=\""+configuration.getRootURL()+"/#/accept/?inviteCode="
						+ inviteCode + "\" >CLICK HERE</a>"
				+"<p> The "+currentProject.getName()+" is described as "+currentProject.getDescription(),
				"text/html");
		return message;
	}

}
