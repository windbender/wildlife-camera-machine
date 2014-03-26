package com.github.windbender.resources;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.windbender.auth.Priv;
import com.github.windbender.auth.SessionAuth;
import com.github.windbender.auth.SessionCurProj;
import com.github.windbender.auth.SessionUser;
import com.github.windbender.core.SessionFilteredAuthorization;
import com.github.windbender.dao.ProjectDAO;
import com.github.windbender.dao.UserDAO;
import com.github.windbender.dao.UserProjectDAO;
import com.github.windbender.domain.Project;
import com.github.windbender.domain.User;
import com.github.windbender.domain.UserProject;
import com.sun.jersey.api.ConflictException;
import com.sun.jersey.api.NotFoundException;
import com.yammer.dropwizard.hibernate.UnitOfWork;
import com.yammer.metrics.annotation.Timed;

@Path("/userproject/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserProjectResource {
	Logger log = LoggerFactory.getLogger(UserProjectResource.class);

	ProjectDAO pd;
	UserProjectDAO upd;

	private UserDAO ud;
	
	public UserProjectResource(UserProjectDAO upd, ProjectDAO pd,UserDAO ud) {
		this.upd = upd;
		this.pd = pd;
		this.ud = ud;
	}

	@GET
	@Timed
	@UnitOfWork
	public List<UserProject> list(
			@SessionAuth(required = { Priv.ADMIN }) SessionFilteredAuthorization auths,
			@SessionUser User user, @SessionCurProj Project currentProject) {
		Project p = pd.findById(currentProject.getId());
		List<UserProject> l = upd.findAllInProject(p);
		return l;
	}

	@GET
	@Timed
	@UnitOfWork
	@Path("{id}")
	public UserProject fetch(
			@SessionAuth(required = { Priv.ADMIN }) SessionFilteredAuthorization auths,
			@SessionUser User user, @SessionCurProj Project currentProject,
			@PathParam("id") Long userProjectId) {
		UserProject c = upd.findById(userProjectId);
		if(c == null) throw new NotFoundException();
		if (!c.getProject().getId().equals(currentProject.getId()))
			throw new WebApplicationException(Response.Status.FORBIDDEN);
		return c;
	}

	@POST
	@Timed
	@UnitOfWork
	public Response add(
			@SessionAuth(required = { Priv.ADMIN }) SessionFilteredAuthorization auths,
			@SessionUser User user, @SessionCurProj Project currentProject,
			@Valid UserProject userProject) {
		log.info("Ok we have the following session user " + user);
		Project p = pd.findById(currentProject.getId());
		if (p == null)
			throw new WebApplicationException(Response.Status.FORBIDDEN);
		if(p.getId().intValue() != userProject.getIdForProject()) throw new WebApplicationException(Response.Status.FORBIDDEN);
		
		userProject.setProject(p);
		User u = this.ud.findById(userProject.getIdForUser());
		List<UserProject> current = upd.findByUserIdProjectId(u,p);
		if(current.size() != 0) throw new ConflictException("that user already exists");

		userProject.setUser(u);
		UserProject newUserProject = upd.save(userProject);

		URI uri = UriBuilder.fromResource(UserProjectResource.class).build(
				newUserProject.getId());
		log.info("the response uri will be " + uri);
		return Response.created(uri).build();
	}

	@PUT
	@Timed	@Path("{id}")
	@UnitOfWork
	public Response update(
			@SessionAuth(required = { Priv.ADMIN }) SessionFilteredAuthorization auths,
			@SessionUser User user, @SessionCurProj Project currentProject,
			@PathParam("id") Long id, @Valid UserProject userProject) {
		Project p = pd.findById(currentProject.getId());
		if (p == null)
			throw new WebApplicationException(Response.Status.FORBIDDEN);
		User u = this.ud.findById(userProject.getIdForUser());
		userProject.setUser(u);
		userProject.setProject(p);
		
		UserProject newUserProject = upd.save(userProject);
		//
		URI uri = UriBuilder.fromResource(UserProjectResource.class).build(
				newUserProject.getId());
		log.info("the response uri will be " + uri);
		return Response.created(uri).build();
	}

	@DELETE
	@Timed
	@Path("{id}")
	@Consumes(MediaType.TEXT_PLAIN)
	@UnitOfWork
	public Response delete(
			@SessionAuth(required = { Priv.ADMIN }) SessionFilteredAuthorization auths,
			@SessionUser User user, @SessionCurProj Project currentProject,
			@PathParam("id") Long id) {
		Project p = pd.findById(currentProject.getId());

		UserProject deleteableUserProject = upd.findById(id);
		if(p.getId() != deleteableUserProject.getProject().getId()) throw new WebApplicationException(Response.Status.FORBIDDEN);
		upd.delete(id);
		return Response.ok().build();
	}
}
