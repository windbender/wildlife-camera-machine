package com.github.windbender.dao;

import org.hibernate.SessionFactory;

import com.github.windbender.domain.Good;
import com.github.windbender.domain.User;
import com.yammer.dropwizard.hibernate.AbstractDAO;

public class GoodDAO extends AbstractDAO<Good>{

	public GoodDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}
	public Integer getGoodFlagCount(String id, User user) {
		// TODO Auto-generated method stub
		return null;
	}

}
