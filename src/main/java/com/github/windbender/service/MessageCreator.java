package com.github.windbender.service;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.MimeMessage;

public interface MessageCreator {

	MimeMessage createMessage(Session session) throws AddressException, MessagingException;

}
