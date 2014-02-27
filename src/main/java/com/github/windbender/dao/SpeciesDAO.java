package com.github.windbender.dao;

import org.hibernate.SessionFactory;

import com.github.windbender.domain.Species;
import com.yammer.dropwizard.hibernate.AbstractDAO;

public class SpeciesDAO extends AbstractDAO<Species>{

	public SpeciesDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
		// TODO Auto-generated constructor stub
	}

	public Species findById(long speciesId) {
        return get(speciesId);
	}

}
