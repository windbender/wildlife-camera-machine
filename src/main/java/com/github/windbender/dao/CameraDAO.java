package com.github.windbender.dao;

import java.util.List;

import javax.ws.rs.WebApplicationException;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.windbender.domain.Camera;
import com.github.windbender.domain.Project;
import com.yammer.dropwizard.hibernate.AbstractDAO;

public class CameraDAO extends AbstractDAO<Camera> {

	Logger logger = LoggerFactory.getLogger(CameraDAO.class);

	public CameraDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	public List<Camera> findAllInProject(Project currentProject) {
		Session currentSession = this.currentSession();
		Criteria crit = currentSession.createCriteria(Camera.class);
		crit.add(Restrictions.eq("project", currentProject));

		logger.info("the criteria is " + crit.toString());
		List<Camera> findList = (List<Camera>) crit.list();
		return findList;
	}

	public Camera findById(Long cameraId) {
		Session currentSession = this.currentSession();
		Criteria crit = currentSession.createCriteria(Camera.class);
		crit.add(Restrictions.eq("id", cameraId));
		return (Camera) crit.uniqueResult();
	}

	public Camera save(Camera camera) {
		if (camera.getId() != null) {
			// this is an update
			Camera c = this.get(camera.getId());
			this.currentSession().evict(c);
		}
		Camera newC = this.persist(camera);

		return newC;
	}

	public void delete(Long id) {
		if (id != null) {
			Camera v = this.get(id);

			this.currentSession().delete(v);
		} else {
			throw new WebApplicationException();
		}

	}

}
