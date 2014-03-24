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
import com.github.windbender.dao.CameraDAO;
import com.github.windbender.dao.ProjectDAO;
import com.github.windbender.domain.Camera;
import com.github.windbender.domain.Project;
import com.github.windbender.domain.User;
import com.sun.jersey.api.ConflictException;
import com.yammer.dropwizard.hibernate.UnitOfWork;
import com.yammer.metrics.annotation.Timed;

@Path("/cameras/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CameraResource {

	Logger log = LoggerFactory.getLogger(CameraResource.class);

	CameraDAO cd;
	ProjectDAO pd;

	public CameraResource(CameraDAO cd, ProjectDAO pd) {
		this.cd = cd;
		this.pd = pd;
	}

	@GET
	@Timed
	@UnitOfWork
	public List<Camera> list(
			@SessionAuth(required = { Priv.ADMIN }) SessionFilteredAuthorization auths,
			@SessionUser User user, @SessionCurProj Project currentProject) {
		Project p = pd.findById(currentProject.getId());
		List<Camera> l = cd.findAllInProject(p);
		return l;
	}

	@GET
	@Timed
	@UnitOfWork
	@Path("{id}")
	public Camera fetch(
			@SessionAuth(required = { Priv.ADMIN }) SessionFilteredAuthorization auths,
			@SessionUser User user, @SessionCurProj Project currentProject,
			@PathParam("id") Long cameraId) {
		Camera c = cd.findById(cameraId);
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
			@Valid Camera camera) {
		log.info("Ok we have the following session user " + user);
		Project p = pd.findById(currentProject.getId());
		if (p == null)
			throw new WebApplicationException(Response.Status.FORBIDDEN);
		camera.setProject(p);
		Camera newCamera = cd.save(camera);

		URI uri = UriBuilder.fromResource(CameraResource.class).build(
				newCamera.getId());
		log.info("the response uri will be " + uri);
		return Response.created(uri).build();
	}

	@PUT
	@Timed
	@Path("{id}")
	@UnitOfWork
	public Response update(
			@SessionAuth(required = { Priv.ADMIN }) SessionFilteredAuthorization auths,
			@SessionUser User user, @SessionCurProj Project currentProject,
			@PathParam("id") Long id, @Valid Camera camera) {
		Project p = pd.findById(currentProject.getId());
		if (p == null)
			throw new WebApplicationException(Response.Status.FORBIDDEN);
		if (!p.getId().equals(camera.getProject().getId()))
			throw new WebApplicationException(Response.Status.FORBIDDEN);

		Camera newCamera = cd.save(camera);
		//
		URI uri = UriBuilder.fromResource(CameraResource.class).build(
				newCamera.getId());
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

		Camera deleteableCamera = cd.findById(id);
		if(p.getId() != deleteableCamera.getProject().getId()) throw new WebApplicationException(Response.Status.FORBIDDEN);
		cd.delete(id);
		return Response.ok().build();
	}

}
