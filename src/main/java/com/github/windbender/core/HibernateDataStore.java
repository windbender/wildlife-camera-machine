package com.github.windbender.core;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.context.internal.ManagedSessionContext;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.windbender.dao.EventDAO;
import com.github.windbender.dao.HibernateUserDAO;
import com.github.windbender.dao.IdentificationDAO;
import com.github.windbender.dao.ImageRecordDAO;
import com.github.windbender.dao.SpeciesDAO;
import com.github.windbender.domain.Identification;
import com.github.windbender.domain.ImageEvent;
import com.github.windbender.domain.ImageRecord;
import com.github.windbender.domain.Species;
import com.github.windbender.domain.User;
import com.yammer.dropwizard.lifecycle.Managed;

public class HibernateDataStore implements DataStore, Managed, Runnable {

	Logger log = LoggerFactory.getLogger(HibernateDataStore.class);

	IdentificationDAO idDAO;
	ImageRecordDAO irDAO;
	SpeciesDAO speciesDAO;
	HibernateUserDAO uDAO;
	EventDAO eventDAO;
	SessionFactory sessionFactory;
	private final ConcurrentLinkedQueue<ImageRecord> eventSearchQueue;


	public HibernateDataStore(IdentificationDAO idDAO, ImageRecordDAO irDAO, SpeciesDAO speciesDAO, HibernateUserDAO uDAO, EventDAO eventDAO, SessionFactory sessionFactory) {
		this.idDAO = idDAO;
		this.irDAO = irDAO;
		this.speciesDAO = speciesDAO;
		this.uDAO = uDAO;
		this.eventDAO = eventDAO;
		this.sessionFactory = sessionFactory;
		eventSearchQueue = new ConcurrentLinkedQueue<ImageRecord>();
	}

	@Override
	public void addImage(ImageRecord newImage) {
		log.info("adding image "+newImage);
		// store the image in DS
		String id =irDAO.create(newImage);
		// find or make corresponding event
		queueForEventSearch(newImage);
	}

	private int secondsDelta = 5;

	private void queueForEventSearch(ImageRecord newImage) {
		eventSearchQueue.add(newImage);
	}
	
	@Override
	public void run() {
		while(this.threadShouldRun) {
			Session session = null;
			try {
	            do {
					session = sessionFactory.openSession();
		            ManagedSessionContext.bind(session);
					ImageRecord ir = eventSearchQueue.poll();
					if(ir == null) break;
					// search in DB for one that might work
					DateTime before = ir.getDatetime().minusSeconds(secondsDelta);
					DateTime after = ir.getDatetime().plusSeconds(secondsDelta);
					List<ImageEvent> l =eventDAO.findEventsBetween(ir.getCameraID(),before, after);
					if(l.size() >0) {
						// ensure only one.
						if(l.size() ==1) {
							checkAndAddToFirst(ir, l);
						} else {
							// wow.. this could be good or bad.
							boolean bad = false;
							ImageEvent first = l.get(0);
							for(ImageEvent rest: l) {
								if(first !=rest) {
									bad = true;
								}
							}
							if(bad) {
								System.out.println("OH NO MR BILL");
							} else {
								checkAndAddToFirst(ir, l);
							}
						}
					} else {
						// make a new Event
						ImageEvent ie = new ImageEvent();
						ie.setCameraID(ir.getCameraID());
						ie.setEventStartTime(ir.getDatetime());
						ImageRecord addImage = irDAO.findById(ir.getId());
						if(addImage != null) {
							ie.addImage(addImage);
							irDAO.save(addImage);
						} else {
							// so probably we're too fast for this thing.  let's push the orginal request and try it all again
							eventSearchQueue.add(ir);
						}
						eventDAO.create(ie);
					}
	                session.flush();
	                session.close();
	                ManagedSessionContext.unbind(sessionFactory);
	                session = null;
	            } while(true);
	            try {
					Thread.sleep(250);
				} catch (InterruptedException e) {
				}
	        } finally {
	        	if(session != null) {
	        		session.flush();
	        		session.close();
	        		session = null;
	        	}
	        	
	        }
		}
	}

	private void checkAndAddToFirst(ImageRecord ir, List<ImageEvent> l) {
		ImageEvent ie = l.get(0);
		ImageRecord addImage = irDAO.findById(ir.getId());
		if(addImage != null) {
			ie.addImage(addImage);
			irDAO.save(addImage);
		}else {
			System.out.println("WHAT... no image to bind ?");
			eventSearchQueue.add(ir);
		}
		eventDAO.save(ie);
	}
		

	public ImageRecord pollEventSearchQueue() {
		return eventSearchQueue.poll();
	}
	class ImageEventHolder {
		ImageEvent ie;
		DateTime whenExpired;
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((ie == null) ? 0 : ie.hashCode());
			result = prime * result
					+ ((whenExpired == null) ? 0 : whenExpired.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ImageEventHolder other = (ImageEventHolder) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (ie == null) {
				if (other.ie != null)
					return false;
			} else if (!ie.equals(other.ie))
				return false;
			if (whenExpired == null) {
				if (other.whenExpired != null)
					return false;
			} else if (!whenExpired.equals(other.whenExpired))
				return false;
			return true;
		}
		private HibernateDataStore getOuterType() {
			return HibernateDataStore.this;
		}
		
	}
	

	@Override
	public List<ImageRecord> getTimeOrderedImages() {
		return irDAO.findAll();
	}

	@Override
	public List<ImageEvent> getImageEvents() {
		return eventDAO.findAll();
	}

	@Override
	public void recordIdentification(IdentificationRequest idRequest, User u) {
		ImageRecord identifiedImage = irDAO.findById(idRequest.getImageid());
		Species speciesIdentified = speciesDAO.findById(idRequest.getSpeciesId());
		
//TODO remove
		if(u == null) {
			u = uDAO.findByID(1);
		}
		Identification id = new Identification();
		id.setIdentifiedImage(identifiedImage);
		id.setIdentifier(u);
		id.setTimeOfIdentification(new DateTime());
		id.setSpeciesIdentified(speciesIdentified);
		id.setNumberOfIndividuals(idRequest.getNumberOfAnimals());

			
		
		long idid = idDAO.create(id);
		
	}

	@Override
	public ImageRecord getRecordFromId(String id) {
		ImageRecord identifiedImage = irDAO.findById(id);
		return identifiedImage;
	}

	
	@Override
	public void start() throws Exception {
		threadShouldRun = true;
		t = new Thread(this);
		t.setName("Event Detector Thread");
		t.start();
		
	}
	volatile boolean threadShouldRun = false;

	Thread t;
	
	@Override
	public void stop() throws Exception {
		threadShouldRun = false;
		t.join();
	}

}
