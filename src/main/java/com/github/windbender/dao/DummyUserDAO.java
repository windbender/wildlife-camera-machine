package com.github.windbender.dao;

public class DummyUserDAO implements UserDAO {

	@Override
	public boolean checkPassword(String username, String password) {
		if("secret".equals(password)) return true;
		return false;
	}

}
