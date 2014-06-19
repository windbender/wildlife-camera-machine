package com.github.windbender.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import com.github.windbender.domain.Identification;
import com.github.windbender.domain.ImageEvent;
import com.github.windbender.domain.Invite;
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
	
	public List<Identification> findAllIdentificationForEvent(ImageEvent ie) {
		Session currentSession = this.currentSession();
		Criteria crit = currentSession.createCriteria(Identification.class);
		crit.add(Restrictions.eq("identifiedEvent", ie));
		List<Identification> list = (List<Identification>)crit.list();
		return list;
	}

}
