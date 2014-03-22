package com.github.windbender.auth;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import com.github.windbender.core.SessionFilteredAuthorization;
import com.github.windbender.domain.Project;
import com.github.windbender.domain.UserProject;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;

public class SessionAuthProvider implements InjectableProvider<SessionAuth, Type> {
	private static class SessionPrivInjectable extends AbstractHttpContextInjectable<SessionFilteredAuthorization> {

		private Role[] required;
		HttpServletRequest request;

		private SessionPrivInjectable(HttpServletRequest request, Role[] required2) {
			this.required = required2;
			this.request = request;
		}

		@Override
		public SessionFilteredAuthorization getValue(HttpContext c) {
			Project curProject = (Project) request.getSession().getAttribute("current_project");
			
			//find the current projects user_project record
			@SuppressWarnings("unchecked")
			List<UserProject> upls = (List<UserProject>) request.getSession().getAttribute("user_projects");
			UserProject curUP = null;
			if ((curProject != null) && (upls != null)) {
				for (UserProject up : upls) {
					if (up.getProject().getId().equals(curProject.getId())) {
						curUP = up;
					}
				}
			}
			
			Set<Role> requiredSetOR= new HashSet<Role>(Arrays.asList(required));
			// the user must have at least ONE of the required roles listed here.
			boolean foundEnoughPriv = false;
			for(Role reqRole: requiredSetOR) {
				if(curProject != null) {
					if(reqRole.equals(Role.CATEGORIZE) && curProject.getPublicCategorize()) foundEnoughPriv = true;
					if(reqRole.equals(Role.REPORT) && curProject.getPublicReport()) foundEnoughPriv = true;
				}
				if(curUP != null) {
					if(reqRole.equals(Role.CATEGORIZE) && curUP.getCanCategorize()) foundEnoughPriv = true;
					if(reqRole.equals(Role.REPORT) && curUP.getCanReport()) foundEnoughPriv = true;
					if(reqRole.equals(Role.UPLOAD) && curUP.getCanUpload()) foundEnoughPriv = true;
					if(reqRole.equals(Role.ADMIN) && curUP.getCanAdmin()) foundEnoughPriv = true;
				}
			}
			if(!foundEnoughPriv) throw new WebApplicationException(Response.Status.FORBIDDEN);
			

			SessionFilteredAuthorization out = new SessionFilteredAuthorization();
			out.setProject(curProject);
			out.setProjectId(curProject.getId());
			return out;
		}
	}
	
	private final HttpServletRequest request;
	private Role[] required;

	public SessionAuthProvider(@Context HttpServletRequest request) {
		this.request = request;
	}

	@Override
	public Injectable<SessionFilteredAuthorization> getInjectable(ComponentContext cc, SessionAuth a, Type c) {
		required = a.required();
		
		if (c.equals(SessionFilteredAuthorization.class)) {
			return new SessionPrivInjectable(request,required);
			
		}
		
		return null;
	}

	@Override
	public ComponentScope getScope() {
		return ComponentScope.PerRequest;
	}
}
