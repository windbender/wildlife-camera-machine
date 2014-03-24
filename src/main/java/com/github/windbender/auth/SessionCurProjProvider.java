package com.github.windbender.auth;

import java.lang.reflect.Type;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import com.github.windbender.domain.Project;
import com.github.windbender.domain.User;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;

@Provider
public class SessionCurProjProvider  implements  InjectableProvider<SessionCurProj, Type>{

	private static class CurrentProjectInjectable extends AbstractHttpContextInjectable<Project> {

		HttpServletRequest request;

		private CurrentProjectInjectable(HttpServletRequest request) {
			this.request = request;
		}

		@Override
		public Project getValue(HttpContext c) {
			final Project project = (Project) request.getSession().getAttribute("current_project");
			return project;

		}

	}

	private final HttpServletRequest request;

	public SessionCurProjProvider(@Context HttpServletRequest request) {
		this.request = request;
	}

	@Override
	public Injectable<Project> getInjectable(ComponentContext cc, SessionCurProj a, Type c) {

		if (c.equals(Project.class)) {
			return new CurrentProjectInjectable(request);
			
		}
		return null;
	}

	@Override
	public ComponentScope getScope() {
		return ComponentScope.PerRequest;
	}
}
