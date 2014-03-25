package com.github.windbender.dao;

import java.util.List;

import javax.ws.rs.WebApplicationException;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.windbender.domain.Project;
import com.github.windbender.domain.User;
import com.github.windbender.domain.UserProject;
import com.yammer.dropwizard.hibernate.AbstractDAO;

public class UserProjectDAO extends AbstractDAO<UserProject> {
	
	Logger logger = LoggerFactory.getLogger(UserProjectDAO.class);

	public UserProjectDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}
	public UserProject save(UserProject up)  {
		if (up.getId() != null) {
			// this is an update
			UserProject upold = this.get(up.getId());
			this.currentSession().evict(upold);
		}
				
		UserProject newP = this.persist(up);
		return newP;
		
	}
	public List<UserProject> findAllByUser(User u) {
		Session currentSession = this.currentSession();
		Criteria crit = currentSession.createCriteria(UserProject.class);
		crit.add(Restrictions.eq("user", u));
		logger.info("the criteria is " + crit.toString());
		List<UserProject> findList = (List<UserProject>) crit.list();
		return findList;
	}
	public List<UserProject> findAllInProject(Project p) {
		Session currentSession = this.currentSession();
		Criteria crit = currentSession.createCriteria(UserProject.class);
		crit.add(Restrictions.eq("project", p));
		logger.info("the criteria is " + crit.toString());
		List<UserProject> findList = (List<UserProject>) crit.list();
		return findList;
	}
	public UserProject findById(Long userProjectId) {
		Session currentSession = this.currentSession();
		Criteria crit = currentSession.createCriteria(UserProject.class);
		crit.add(Restrictions.eq("id", userProjectId));
		logger.info("the criteria is " + crit.toString());
		UserProject up = (UserProject) crit.uniqueResult();
		
		return up;
	}
	public void delete(Long id) {
        if (id != null) {
                // this is an update
                UserProject up = this.get(id);
                this.currentSession().delete(up);
        } else {
                // trying to delete with no id...
                throw new WebApplicationException();
        }
		
	}
	public List<UserProject> findByUserIdProjectId(User u, Project p) {
		Session currentSession = this.currentSession();
		Criteria crit = currentSession.createCriteria(UserProject.class);
		crit.add(Restrictions.eq("project", p));
		crit.add(Restrictions.eq("user", u));
		logger.info("the criteria is " + crit.toString());
		List<UserProject> findList = (List<UserProject>) crit.list();
		return findList;
	}

}
