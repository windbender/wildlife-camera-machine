package com.github.windbender.dao;

import org.hibernate.SessionFactory;

import com.github.windbender.domain.User;

public class TokenDAO {

	public TokenDAO(SessionFactory sessionFactory) {
		// TODO Auto-generated constructor stub
	}

	public boolean isTokenValid(String token) {
		// TODO Auto-generated method stub
		return false;
	}

	public String createToken(User p) {
		// TODO Auto-generated method stub
		return null;
	}

	public User getUserForToken(String token) {
		// TODO Auto-generated method stub
		return null;
	}

}
