package com.github.windbender.dao;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import com.github.windbender.domain.Good;
import com.github.windbender.domain.ImageRecord;
import com.github.windbender.domain.User;
import com.yammer.dropwizard.hibernate.AbstractDAO;

public class GoodDAO extends AbstractDAO<Good> {

	public GoodDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	public Integer getGoodFlagCount(ImageRecord ir, User user) {
		Good g= find(ir,user);
		if(g == null) return 0;
		if(g.isFlagged()) return 1;
		return 0;
	}

	private Good find(ImageRecord ir, User user) {
		Session currentSession = this.currentSession();
		Criteria crit = currentSession.createCriteria(Good.class);
		crit.add(Restrictions.eq("user", user));
		crit.add(Restrictions.eq("image", ir));

		Good g = (Good) crit.uniqueResult();
		return g;
	}

	public Good saveOrUpdate(Good g) {
		Good curGood = find(g.getImage(), g.getUser());
		Good newGood = null;
		if (curGood == null) {
			newGood = this.persist(g);
		} else {
			curGood.setFlagged(g.isFlagged());
			newGood = curGood;
		}
		return newGood;

	}

}
