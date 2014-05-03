package com.github.windbender.dao;

import java.util.List;

import javax.ws.rs.WebApplicationException;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import com.github.windbender.domain.Camera;
import com.github.windbender.domain.Invite;
import com.github.windbender.domain.Project;
import com.yammer.dropwizard.hibernate.AbstractDAO;

public class InviteDAO extends AbstractDAO<Invite>{

	public InviteDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	public Invite save(Invite i) {
		if (i.getId() != null) {
			// this is an update
			Invite iold = this.get(i.getId());
			this.currentSession().evict(iold);
		}
		Invite newP = this.persist(i);
		return newP;
	}

	public Invite findByCode(String code) {
		Session currentSession = this.currentSession();
		Criteria crit = currentSession.createCriteria(Invite.class);
		crit.add(Restrictions.eq("inviteCode", code));

		Invite i = (Invite) crit.uniqueResult();
		
		return i;
	}
	public List<Invite> findAllByProject(Project p) {
		Session currentSession = this.currentSession();
		Criteria crit = currentSession.createCriteria(Invite.class);
		crit.add(Restrictions.eq("project", p));
		List<Invite> list = (List<Invite>)crit.list();
		return list;
	}

	public void delete(Invite inv) {
		if (inv != null) {
			Invite v = this.get(inv.getId());

			this.currentSession().delete(v);
		} else {
			throw new WebApplicationException();
		}
		
	}

	public Invite findByById(int invite_id) {
		Session currentSession = this.currentSession();
		Criteria crit = currentSession.createCriteria(Invite.class);
		crit.add(Restrictions.eq("id", invite_id));

		Invite i = (Invite) crit.uniqueResult();
		
		return i;
	}
}
