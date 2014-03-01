package com.github.windbender.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;

import com.github.windbender.domain.ImageEvent;
import com.yammer.dropwizard.hibernate.AbstractDAO;

public class EventDAO extends AbstractDAO<ImageEvent> {

	public EventDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	public ImageEvent findById(String id) {

		return get(id);
	}

	public long create(ImageEvent ir) {
		return persist(ir).getId();
	}

	public List<ImageEvent> findEventsBetween(DateTime before, DateTime after) {
		Session currentSession = this.currentSession();
		Criteria crit = currentSession.createCriteria(ImageEvent.class);
		crit.add(Restrictions.ge("eventStartTime", before));
		crit.add(Restrictions.le("eventStartTime", after));
		crit.addOrder( Property.forName("eventStartTime").desc() );
		List<ImageEvent> findList = crit.list();
		return findList;
	}

	public List<ImageEvent> findEventsBetween(String cameraID, DateTime before,
			DateTime after) {
		Session currentSession = this.currentSession();
		Criteria crit = currentSession.createCriteria(ImageEvent.class);
		crit.add(Restrictions.ge("eventStartTime", before));
		crit.add(Restrictions.le("eventStartTime", after));
		crit.add(Restrictions.eq("cameraID",cameraID));
		crit.addOrder( Property.forName("eventStartTime").desc() );
		List<ImageEvent> findList = crit.list();
		return findList;
		
	}

	public void save(ImageEvent ie) {
		this.currentSession().saveOrUpdate(ie);
		
	}

	public List<ImageEvent> findAll() {
		Session currentSession = this.currentSession();
		Criteria crit = currentSession.createCriteria(ImageEvent.class);
		crit.addOrder( Property.forName("eventStartTime").desc() );
		List<ImageEvent> findList = crit.list();
		return findList;
	}

}
