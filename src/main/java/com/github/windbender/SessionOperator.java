package com.github.windbender;

import org.eclipse.jetty.server.session.HashedSession;

import com.github.windbender.domain.User;

public interface SessionOperator {

	void operate(HashedSession session, User findUser);

}
