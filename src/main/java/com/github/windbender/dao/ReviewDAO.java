package com.github.windbender.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.windbender.domain.ImageEvent;
import com.github.windbender.domain.Review;
import com.github.windbender.domain.User;
import com.yammer.dropwizard.hibernate.AbstractDAO;

public class ReviewDAO extends AbstractDAO<Review> {

	Logger logger = LoggerFactory.getLogger(ReviewDAO.class);

	public ReviewDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}
	public Integer getReviewFlagCount(ImageEvent e, User user) {
		Review r = find(e, user);
		if(r == null) return 0;
		if(r.isFlagged()) {
			return 1;
		} else {
			return 0;
		}
	}
	private Review find(ImageEvent e, User user) {
		Session currentSession = this.currentSession();
		Criteria crit = currentSession.createCriteria(Review.class);
		crit.add(Restrictions.eq("user", user));
		crit.add(Restrictions.eq("imageEvent", e));

		logger.info("the criteria is " + crit.toString());
		Review r = (Review) crit.uniqueResult();
		return r;
	}
	
	
	public Review saveOrUpdate(Review r) {
		Review curR = find(r.getImageEvent(),r.getUser());
		Review newR = null;
		if(curR == null) {
			newR = this.persist(r);
		} else {
			curR.setFlagged(r.isFlagged());
			newR = curR;
		}
		return newR;
		
	}

}
