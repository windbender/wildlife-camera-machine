package com.github.windbender;

import java.util.List;

import org.eclipse.jetty.server.session.HashedSession;

import com.github.windbender.dao.ProjectDAO;
import com.github.windbender.dao.UserDAO;
import com.github.windbender.dao.UserProjectDAO;
import com.github.windbender.domain.Project;
import com.github.windbender.domain.User;
import com.github.windbender.domain.UserProject;

public class SessionReloaderOperator implements SessionOperator {

	private IterableHashSessionManager ihsm;
	private UserDAO ud;
	private ProjectDAO projectDAO;
	private UserProjectDAO upDAO;

	public SessionReloaderOperator(IterableHashSessionManager ihsm, UserDAO ud,ProjectDAO projectDAO,UserProjectDAO upDAO ) {
		this.ihsm = ihsm;
		this.ud = ud;
		this.projectDAO = projectDAO;
		this.upDAO = upDAO;
	}

	public void reloadSessionForUser(User findUser) {
		ihsm.operateOn(this, findUser);
	}
	@Override
	public void operate(HashedSession session, User findUser) {
		User u = (User) session.getAttribute("user");
		if(u == null) return;
		if(u.getId() == findUser.getId()) {
			reloadSessionOnFoundSession(session, findUser);
		}

	}

	private void reloadSessionOnFoundSession(HashedSession sess, User p) {
		
		User u = this.ud.findById(p.getId());
		sess.setAttribute("user", u);
		
		List<Project> primaryAdminProjects = this.projectDAO.findByPrimaryAdmin(u);
		sess.setAttribute("primary_admins", primaryAdminProjects);
		
		List<UserProject> upl = this.upDAO.findAllByUser(u);
		sess.setAttribute("user_projects", upl);
	}
	
}
