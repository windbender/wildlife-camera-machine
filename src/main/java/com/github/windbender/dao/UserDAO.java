package com.github.windbender.dao;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import com.github.windbender.domain.User;

public interface UserDAO {

	boolean checkPassword(String username, String password);

	User findByUsername(String username);

	User findById(int userId);

	void save(User editUser);

	User findByEmail(String resetemail);

	User findByVerifyCode(String code);

	long create(User u) throws NoSuchAlgorithmException, InvalidKeySpecException;

}
