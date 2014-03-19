package com.github.windbender.dao;

import org.hibernate.SessionFactory;

import com.github.windbender.domain.Identification;
import com.yammer.dropwizard.hibernate.AbstractDAO;

public class IdentificationDAO extends AbstractDAO<Identification> {

	public IdentificationDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}
	
	public Identification findById(Integer id) {
        return get(id);
    }

    public long create(Identification ir) {
        return persist(ir).getId();
    }

	public void delete(long idToClear) {
		Integer i = new Integer((int)idToClear);
		Identification id = findById(i);
		this.currentSession().delete(id);
	}
	

}
