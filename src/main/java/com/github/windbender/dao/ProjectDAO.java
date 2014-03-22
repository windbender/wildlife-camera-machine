package com.github.windbender.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.windbender.domain.Project;
import com.github.windbender.domain.User;
import com.yammer.dropwizard.hibernate.AbstractDAO;

public class ProjectDAO extends AbstractDAO<Project>{

	Logger logger = LoggerFactory.getLogger(ProjectDAO.class);

	public ProjectDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	public List<Project> findAll() {
		Session currentSession = this.currentSession();
		Criteria crit = currentSession.createCriteria(Project.class);
		logger.info("the criteria is " + crit.toString());
		List findList = (List<Project>) crit.list();
		return findList;
	}

	public Project findByName(String projectName) {
		Session currentSession = this.currentSession();
		Criteria crit = currentSession.createCriteria(Project.class);
		crit.add(Restrictions.eq("name", projectName));

		logger.info("the criteria is " + crit.toString());
		List findList = (List<Project>) crit.list();
		if (findList.size() > 0) {
			if (findList.size() > 1) {
				logger.error("found more than one project with that id and account");
			}
			Project l = (Project) findList.get(0);
			return l;
		}
		return null;
	}

	public List<Project> findByPrimaryAdmin(User user) {
		Session currentSession = this.currentSession();
		Criteria crit = currentSession.createCriteria(Project.class);
		crit.add(Restrictions.eq("primaryAdmin", user));
		logger.info("the criteria is " + crit.toString());
		List findList = (List<Project>) crit.list();
		return findList;
	}

	public Project save(Project p) {
		if (p.getId() != null) {
			// this is an update
			Project pold = this.get(p.getId());
			this.currentSession().evict(pold);
		}
				
		Project newP = this.persist(p);
		return newP;
		
	}

	public Project findById(int i) {
		Session currentSession = this.currentSession();
		Criteria crit = currentSession.createCriteria(Project.class);
		logger.info("the criteria is " + crit.toString());
		List findList = (List<Project>) crit.list();
		if(findList.size() ==1) {
			return (Project)findList.get(0);
		}
		logger.error("more than one with that ID",i);
		return null;
	}

}
