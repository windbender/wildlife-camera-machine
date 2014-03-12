package com.github.windbender.service;

import javax.mail.MessagingException;

import com.github.windbender.WLCDMServerConfiguration;
import com.github.windbender.domain.User;

public class EmailService {

	
	public EmailService(WLCDMServerConfiguration configuration,
			MessageSender messageSender) {
		super();
		this.configuration = configuration;
		this.messageSender = messageSender;
	}
	private WLCDMServerConfiguration configuration;
	private MessageSender messageSender;
	
	public void sendPasswordResetEmail(User u, String token) throws MessagingException {
		MessageCreator mc = new ResetPasswordMessageCreator(u,token,configuration);
		messageSender.sendMessage(mc);
	}
	public void sendUpdatedPassword(User u) throws MessagingException {
		MessageCreator mc = new PasswordUpdatedMessage(u,configuration);
		messageSender.sendMessage(mc);
	}
	public void sendUsANotification(User u) throws MessagingException {
		MessageCreator mc = new OurNotificationMessage(u,configuration);
		messageSender.sendMessage(mc);
		
	}
	public void sendVerificationEmail(User u) throws MessagingException {
		MessageCreator mc = new VerificationMessageCreator(u,configuration);
		messageSender.sendMessage(mc);
		
	}
	

}
