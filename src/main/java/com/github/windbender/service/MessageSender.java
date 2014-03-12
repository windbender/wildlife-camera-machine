package com.github.windbender.service;

import javax.mail.MessagingException;


public interface MessageSender {

	public void sendMessage(MessageCreator mc) throws MessagingException;

}
