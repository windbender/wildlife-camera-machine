package com.github.windbender.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.windbender.WLCDMServerConfiguration;
import com.yammer.dropwizard.lifecycle.Managed;

public class AsyncEmailSender implements MessageSender, Managed {
	Logger log = LoggerFactory.getLogger(AsyncEmailSender.class);

	
	ExecutorService executorService = null;
	WLCDMServerConfiguration configuration;
	private MessageSender messageSender;
	
	public AsyncEmailSender(WLCDMServerConfiguration configuration, MessageSender messageSender) {
		this.configuration = configuration;
		this.messageSender = messageSender;
	}

	@Override
	public void sendMessage(final MessageCreator mc) throws MessagingException {
		if(executorService == null) throw new IllegalArgumentException("WHat, you didn't start the thread pool executor");
		log.info("queueing message to send from "+mc);
		executorService.execute(new Runnable() {
		    public void run() {
		    	try {
					messageSender.sendMessage(mc);
				} catch (MessagingException e) {
					log.error("Unable to send message",e);
				}
		    }
		});
		log.info("message is queued");
		
	}

	@Override
	public void start() throws Exception {
		executorService = Executors.newFixedThreadPool(10);
	}

	@Override
	public void stop() throws Exception {
		executorService.shutdown();
	}
	
}
