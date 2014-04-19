package com.github.windbender.dao;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.windbender.domain.ResetPasswordToken;
import com.github.windbender.domain.User;
import com.github.windbender.resources.UserResource;
import com.yammer.dropwizard.hibernate.AbstractDAO;

public class TokenDAO extends AbstractDAO<ResetPasswordToken>{


	private static final int VALID_FOR_MINUTES = 10;
	Logger log = LoggerFactory.getLogger(TokenDAO.class);

	public TokenDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	public boolean isTokenValid(String token) {
		User p = getUserForToken(token);
		if(p == null) return false;
		return true;
	}
	
	public User getUserForToken(String token) {
		clearOldTokens();
		Session currentSession = this.currentSession();
		Criteria crit = currentSession.createCriteria(ResetPasswordToken.class);
		crit.add(Restrictions.eq("token", token));
		log.info("the criteria is " + crit.toString());
		List<ResetPasswordToken> findList = crit.list();
		log.info("the list is " + findList);
		if(findList.size() ==1) {
			return findList.get(0).getUser();
		}
		return null;
	}
	
	private void clearOldTokens() {
		DateTime now = new DateTime();
		Session currentSession = this.currentSession();
		Criteria crit = currentSession.createCriteria(ResetPasswordToken.class);
		crit.add(Restrictions.lt("expirationTime", now));
		log.info("the criteria is " + crit.toString());
		List<ResetPasswordToken> findList = crit.list();
		log.info("the list is " + findList);
		if (findList.size() > 0) {
			for(ResetPasswordToken rpwt: findList) {
				this.currentSession().delete(rpwt);
			}
		}
		
	}
	// creates a random token, stores in DB, and returns.
	public String createToken(User p) {
		try {
			String code = UserResource.makeVerifyCode();
			ResetPasswordToken tok = new ResetPasswordToken();
			tok.setToken(code);
			DateTime now = new DateTime();
			tok.setExpirationTime(now.plusMinutes(VALID_FOR_MINUTES));
			tok.setUser(p);
			ResetPasswordToken ptok = this.persist(tok);
			return ptok.getToken();
		} catch (NoSuchAlgorithmException e) {
			log.error("can't make a reset password token because ",e);
		}
		return null;
	}


}
