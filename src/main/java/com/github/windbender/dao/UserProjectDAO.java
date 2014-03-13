package com.github.windbender.dao;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.windbender.domain.Project;
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

}
