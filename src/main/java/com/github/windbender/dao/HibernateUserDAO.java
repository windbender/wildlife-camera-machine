package com.github.windbender.dao;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.windbender.domain.User;
import com.yammer.dropwizard.hibernate.AbstractDAO;

public class HibernateUserDAO extends AbstractDAO<User> implements UserDAO {

	Logger logger = LoggerFactory.getLogger(HibernateUserDAO.class);

	public HibernateUserDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	public boolean checkPassword(String username, String password) {
		try {
			User p = findByUsername(username);
			if (p == null)
				return false;
			String stored = p.getHashedPassword();
			return check(password, stored);
		} catch (Exception e) {
			logger.error("can't check password because ", e);
			return false;
		}

	}

	// The higher the number of iterations the more
	// expensive computing the hash is for us
	// and also for a brute force attack.
	private static final int iterations = 10 * 1024;
	private static final int saltLen = 32;
	private static final int desiredKeyLen = 256;

	/**
	 * Computes a salted PBKDF2 hash of given plaintext password suitable for
	 * storing in a database. Empty passwords are not supported.
	 * 
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	public static String getSaltedHash(String password)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] salt = SecureRandom.getInstance("SHA1PRNG")
				.generateSeed(saltLen);
		// store the salt with the password
		return Base64.encodeBase64String(salt) + "$" + hash(password, salt);
	}

	/**
	 * Checks whether given plaintext password corresponds to a stored salted
	 * hash of the password.
	 */
	public static boolean check(String password, String stored)
			throws Exception {
		String[] saltAndPass = stored.split("\\$");
		if (saltAndPass.length != 2)
			return false;
		String hashOfInput = hash(password, Base64.decodeBase64(saltAndPass[0]));
		return hashOfInput.equals(saltAndPass[1]);
	}

	// using PBKDF2 from Sun, an alternative is https://github.com/wg/scrypt
	// cf. http://www.unlimitednovelty.com/2012/03/dont-use-bcrypt.html
	private static String hash(String password, byte[] salt)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		if (password == null || password.length() == 0)
			throw new IllegalArgumentException(
					"Empty passwords are not supported.");
		SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		SecretKey key = f.generateSecret(new PBEKeySpec(password.toCharArray(),
				salt, iterations, desiredKeyLen));
		return Base64.encodeBase64String(key.getEncoded());
	}



	@Override
	public User findByUsername(String username) {
		Session currentSession = this.currentSession();
		Criteria crit = currentSession.createCriteria(User.class);
		crit.add(Restrictions.eq("username", username));
		logger.info("the criteria is " + crit.toString());
		List findList = crit.list();
		logger.info("the list is " + findList);
		if (findList.size() > 0) {
			if (findList.size() > 1) {
				logger.error("found more than one user object with username "+ username);
			}
			User u = (User) findList.get(0);
			logger.info("found a user " + u);
			
			return u;
		}
		logger.info("returning null");
		return null;
	}

	@Override
	public User findById(int userId) {
		return get(userId);
	}

	@Override
	public void save(User editUser) {
		this.persist(editUser);
	}

	@Override
	public User findByEmail(String resetemail) {
		Criteria crit = this.currentSession().createCriteria(User.class);
		crit.add(Restrictions.eq("email", resetemail));
		logger.info("the criteria is " + crit.toString());
		List findList = crit.list();
		logger.info("the list is " + findList);
		if (findList.size() > 0) {
			if (findList.size() > 1) {
				logger.error("found more than one user object with email " + resetemail);
			}
			User u = (User) findList.get(0);
			logger.info("found a user " + u);
			return u;
		}
		logger.info("returning null");
		return null;
	}

	@Override
	public User findByVerifyCode(String code) {
		Criteria crit = this.currentSession().createCriteria(User.class);
		crit.add(Restrictions.eq("verifyCode", code));
		logger.info("the criteria is " + crit.toString());
		List findList = crit.list();
		logger.info("the list is " + findList);
		if (findList.size() > 0) {
			if (findList.size() > 1) {
				logger.error("found more than one user object with code " + code);
			}
			User u = (User) findList.get(0);
			logger.info("found a user " + u);
			
			return u;
		}
		logger.info("returning null");
		return null;
	}

	@Override
	public long create(User u) throws NoSuchAlgorithmException, InvalidKeySpecException {
		
		String password = u.getPassword();
		long start = System.currentTimeMillis();
		String hashedPW = getSaltedHash(password);
		long end = System.currentTimeMillis();
		u.setHashedPassword(hashedPW);
		
		long delta = end - start;
		//give them an account... this is sort of hardwired for now
				
		int id = persist(u).getId();
		logger.info("hashing took "+delta+" milli seconds");
		return id;
	}
	

}
