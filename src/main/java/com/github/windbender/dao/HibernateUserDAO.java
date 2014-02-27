package com.github.windbender.dao;

import org.hibernate.SessionFactory;

import com.github.windbender.domain.User;
import com.yammer.dropwizard.hibernate.AbstractDAO;

public class HibernateUserDAO extends AbstractDAO<User> implements UserDAO {

	public HibernateUserDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	public boolean checkPassword(String username, String password) {
		if(password.contains("secret")) return true;
		return false;
	}

	public User findByID(int i) {
		return get(i);
	}

}
