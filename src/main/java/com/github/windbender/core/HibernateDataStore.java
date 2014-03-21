package com.github.windbender.core;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.context.internal.ManagedSessionContext;
import org.joda.time.DateTime;
import org.joda.time.Interval;
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
import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;
import com.yammer.dropwizard.lifecycle.Managed;

public class HibernateDataStore implements Managed, Runnable {

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
					ImageRecord addImage = irDAO.findById(ir.getId());
					if(addImage == null) {
						eventSearchQueue.add(ir);
						break;
					}
					// search in DB for one that might work
					DateTime before = ir.getDatetime().minusSeconds(secondsDelta);
					DateTime after = ir.getDatetime().plusSeconds(secondsDelta);
					List<ImageEvent> l =eventDAO.findEventsBetween(ir.getCameraID(),before, after);
					if(l.size() >0) {
						// ensure only one.
						if(l.size() ==1) {
							checkAndAddToFirst(addImage,ir, l);
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
								checkAndAddToFirst(addImage,ir, l);
							}
						}
					} else {
						// make a new Event
						ImageEvent ie = new ImageEvent();
						ie.setCameraID(ir.getCameraID());
						ie.setEventStartTime(ir.getDatetime());
						
						if(addImage == null ) log.error("could not look up image, addImage is null is was "+ir.getId());
						TypeOfDay tod = dayNightTwilight(ie, addImage);
						ie.setTypeOfDay(tod);
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
	        } catch(Exception e) {
	        	log.error("Caught an exception processing events ",e);
	        } finally {
	        
	        	if(session != null) {
	        		session.flush();
	        		session.close();
	        		session = null;
	        	}
	        	
	        }
		}
	}

	private TypeOfDay dayNightTwilight(ImageEvent ie, ImageRecord addImage) {
		DateTime whenDT = ie.getEventStartTime();
		Calendar when = whenDT.toCalendar(Locale.US);
		Location location = new Location(addImage.getLat(), addImage.getLon());
		SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(location, "America/New_York");
		Calendar rise = calculator.getNauticalSunriseCalendarForDate(when);
		Calendar set = calculator.getNauticalSunsetCalendarForDate(when);
		DateTime risedt = new DateTime(rise.getTime());
		DateTime setdt = new DateTime(set.getTime());
		Interval morning = new Interval(risedt.minusHours(1),risedt.plusMinutes(30));
		Interval evening = new Interval(setdt.minusMinutes(30),setdt.plusHours(1));
		log.info("TOD we have "+whenDT+" somewhere in morning: "+morning+"  and evening: "+evening);
		if(morning.contains(whenDT)) return TypeOfDay.MORNING;
		if(evening.contains(whenDT)) return TypeOfDay.EVENING;
		if(whenDT.isAfter(morning.getEnd()) && whenDT.isBefore(evening.getStart()) ) return TypeOfDay.DAYTIME;
		return TypeOfDay.NIGHTTIME;
	}

	private void checkAndAddToFirst(ImageRecord ir, ImageRecord ir2, List<ImageEvent> l) {
		ImageEvent ie = l.get(0);
		ImageRecord addImage = irDAO.findById(ir.getId());
		ie.addImage(addImage);
		irDAO.save(addImage);
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
	

	public List<ImageRecord> getTimeOrderedImages() {
		return irDAO.findAll();
	}

	public List<ImageEvent> getImageEvents() {
		return eventDAO.findAll();
	}

	public NextEventRecord makeNextEventRecord(User u, Long lastEventId) {
		// We want to identify events which are
		// a) haven't been previously identifed by this user
		List<Integer> done = this.eventDAO.findEventIdsDoneByUser(u);
		SortedSet<Integer> doneSet = new TreeSet<Integer>(done);
		// b) have a number of zero or 1 previous identifications
		int number =2;
		List<Integer> lowNumber = this.eventDAO.findEventsIdsWithFewerThanIdentifications(number);
		SortedSet<Integer> lowNumberSet =  new TreeSet<Integer>(lowNumber);
		
		lowNumberSet.removeAll(doneSet);
		
		// Ok we could cache that set... or we could just pick one and go with it for now.
		if(lowNumberSet.size() == 0) {
			NextEventRecord ner = new NextEventRecord(null);
			ner.setNumberIdentified(doneSet.size());
			ner.setRemainingToIdentify(0);
			return ner;
		}
		Long eventId = lowNumberSet.first().longValue();
		if(eventId.equals(lastEventId)) {
			Iterator<Integer> nit = lowNumberSet.iterator();
			// we know there is at least one so this first next() should always be fine
			eventId = nit.next().longValue();
			if(nit.hasNext()) {
				eventId = nit.next().longValue();				
			} 
			if(eventId.equals(lastEventId)) {
				// we just finished the last event, so return the "done" type record.
				NextEventRecord ner = new NextEventRecord(null);
				ner.setNumberIdentified(doneSet.size());
				ner.setRemainingToIdentify(0);
				return ner; 
			}
		}
		ImageEvent ie = eventDAO.findById(eventId);
//		ImageEvent ie = null;
		NextEventRecord ner = new NextEventRecord(ie);
		ner.setNumberIdentified(doneSet.size());
		ner.setRemainingToIdentify(lowNumberSet.size());
		return ner;
	}
	private SortedSet<Long> makeSetFromId(List<ImageEvent> done) {
		SortedSet<Long> s = new TreeSet<Long>();
		for(ImageEvent ie: done) {
			s.add(ie.getId());
		}
		return s;
	}

	public long recordIdentification(IdentificationRequest idRequest, User u) {
		
		ImageRecord identifiedImage = null;
		Species speciesIdentified = null;
		ImageEvent identifiedEvent = null;
		
		if(idRequest.getImageid() != null)
			identifiedImage = irDAO.findById(idRequest.getImageid());
		if(idRequest.getEventid() != null)
			identifiedEvent = eventDAO.findById(idRequest.getEventid());
		if(idRequest.getSpeciesId() == -1) {
			// this means no species was seen....  what do we do here ?  we should have a "none" species
			speciesIdentified = speciesDAO.findByNameContains("none");
			
		} else {
			speciesIdentified = speciesDAO.findById(idRequest.getSpeciesId());
		}
		Identification id = new Identification();
		if(identifiedEvent != null) {
			id.setIdentifiedEvent(identifiedEvent);
		} else if(identifiedImage != null) {
			id.setIdentifiedImage(identifiedImage);
		} else {
			throw new IllegalArgumentException("could not find either the event or the image supplied");
		}
		id.setIdentifier(u);
		id.setTimeOfIdentification(new DateTime());
		id.setSpeciesIdentified(speciesIdentified);
		id.setNumberOfIndividuals(idRequest.getNumberOfAnimals());

			
		
		long idid = idDAO.create(id);
		return idid;
	}

	public void removeId(long idToClear) {
		idDAO.delete(idToClear);
		
	}

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
