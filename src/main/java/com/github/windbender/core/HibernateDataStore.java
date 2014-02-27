package com.github.windbender.core;

import java.util.List;

import org.joda.time.DateTime;

import com.github.windbender.dao.HibernateUserDAO;
import com.github.windbender.dao.IdentificationDAO;
import com.github.windbender.dao.ImageRecordDAO;
import com.github.windbender.dao.SpeciesDAO;
import com.github.windbender.domain.Identification;
import com.github.windbender.domain.ImageRecord;
import com.github.windbender.domain.Species;
import com.github.windbender.domain.User;

public class HibernateDataStore implements DataStore {

	IdentificationDAO idDAO;
	ImageRecordDAO irDAO;
	SpeciesDAO speciesDAO;
	HibernateUserDAO uDAO;
	public HibernateDataStore(IdentificationDAO idDAO, ImageRecordDAO irDAO, SpeciesDAO speciesDAO, HibernateUserDAO uDAO) {
		this.idDAO = idDAO;
		this.irDAO = irDAO;
		this.speciesDAO = speciesDAO;
		this.uDAO = uDAO;
	}

	@Override
	public void addImage(ImageRecord newImage) {
		String id =irDAO.create(newImage);
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
