package com.github.windbender.dao;

import org.hibernate.SessionFactory;

import com.github.windbender.domain.Identification;
import com.yammer.dropwizard.hibernate.AbstractDAO;

public class IdentificationDAO extends AbstractDAO<Identification> {

	public IdentificationDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}
	
	public Identification findById(Long id) {
        return get(id);
    }

    public long create(Identification ir) {
        return persist(ir).getId();
    }
	

}
