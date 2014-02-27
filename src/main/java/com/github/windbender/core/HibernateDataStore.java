package com.github.windbender.core;

import java.util.List;
import java.util.SortedMap;

import org.joda.time.DateTime;

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

public class HibernateDataStore implements DataStore {

	IdentificationDAO idDAO;
	ImageRecordDAO irDAO;
	SpeciesDAO speciesDAO;
	HibernateUserDAO uDAO;
	EventDAO eventDAO;
	public HibernateDataStore(IdentificationDAO idDAO, ImageRecordDAO irDAO, SpeciesDAO speciesDAO, HibernateUserDAO uDAO, EventDAO eventDAO) {
		this.idDAO = idDAO;
		this.irDAO = irDAO;
		this.speciesDAO = speciesDAO;
		this.uDAO = uDAO;
		this.eventDAO = eventDAO;
	}

	@Override
	public void addImage(ImageRecord newImage) {
		String id =irDAO.create(newImage);
		ImageEvent ie = findOrMakeImageEventFor(newImage.getDatetime());
		ie.addImage(newImage);
	}

	private ImageEvent findOrMakeImageEventFor(DateTime imageTime) {
		int beforeAfter = 5;
		DateTime before = imageTime.minusSeconds(beforeAfter);
		DateTime after = imageTime.plusSeconds(beforeAfter);
		List<ImageEvent> list = eventDAO.findEventsBetween(before,after);
		
		if(list.size() > 0) {
			if(list.size() > 1) {
				//GAH!  should not happen.
				// perhaps find the closest ?
				return null;
			} else {
				return list.get(0);
			}
		} else {
			ImageEvent ie = new ImageEvent();
			eventDAO.create(ie);
			return ie;
		}
	}
	@Override
	public List<ImageRecord> getTimeOrderedImages() {
		return irDAO.findAll();
	}

	@Override
	public List<ImageEvent> getImageEvents() {
		// TODO Auto-generated method stub
		return null;
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

}
