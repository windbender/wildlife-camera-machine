package com.github.windbender.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.windbender.domain.Project;
import com.github.windbender.domain.Species;
import com.yammer.dropwizard.hibernate.AbstractDAO;

public class SpeciesDAO extends AbstractDAO<Species>{

	Logger logger = LoggerFactory.getLogger(SpeciesDAO.class);

	public SpeciesDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	public Species findById(long speciesId) {
        return get(speciesId);
	}

	public List<Species> findAll() {
		Session currentSession = this.currentSession();
		Criteria crit = currentSession.createCriteria(Species.class);
		logger.info("the criteria is " + crit.toString());
		List<Species> findList = (List<Species>) crit.list();
		return findList;
	}

	public Species findByNameContains(String string) {
		Session currentSession = this.currentSession();
		Criteria crit = currentSession.createCriteria(Species.class);
		crit.add(Restrictions.or(
				Restrictions.like("name", "%"+string+"%"),
				Restrictions.like("latinName", "%"+string+"%")
				));
		logger.info("the criteria is " + crit.toString());
		List<Species> findList = (List<Species>) crit.list();
		if(findList.size() ==1) return findList.get(0);
		if(findList.size() ==0) {
			logger.error("done for for speces query for "+string);
			return null;
		}
		logger.error("more than one species for "+string+" found");
		return null;
	}

}
