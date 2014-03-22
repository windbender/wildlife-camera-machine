package com.github.windbender.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.windbender.auth.Role;
import com.github.windbender.auth.SessionAuth;
import com.github.windbender.auth.SessionUser;
import com.github.windbender.core.CreateProjectRequest;
import com.github.windbender.core.JoinProjectRequest;
import com.github.windbender.core.SessionFilteredAuthorization;
import com.github.windbender.dao.ProjectDAO;
import com.github.windbender.dao.UserDAO;
import com.github.windbender.dao.UserProjectDAO;
import com.github.windbender.domain.Camera;
import com.github.windbender.domain.Project;
import com.github.windbender.domain.User;
import com.github.windbender.domain.UserProject;
import com.sun.jersey.api.ConflictException;
import com.yammer.dropwizard.hibernate.UnitOfWork;
import com.yammer.metrics.annotation.Timed;

@Path("/projects/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProjectResource {
	private static final int MAX_ADMIN = 20;

	Logger log = LoggerFactory.getLogger(ProjectResource.class);

	private ProjectDAO projectDAO;

	private UserDAO userDAO;
	private UserProjectDAO upDAO;
	
	public ProjectResource(ProjectDAO projectDAO,UserDAO userDAO,UserProjectDAO upDAO) {
		super();
		this.projectDAO =  projectDAO;
		this.userDAO = userDAO;
		this.upDAO = upDAO;
	}
	@GET
	@Timed
	@UnitOfWork
	public List<String> list(@SessionAuth(required={Role.UPLOAD,Role.ADMIN}) SessionFilteredAuthorization auths,@SessionUser User user) {
		List<Project> list = projectDAO.findAll();
		List<String> listNames = new ArrayList<String>();
		for(Project p :list) {
			String n = p.getName();
			listNames.add(n);
		}
		return listNames;
	}
	
	@GET
	@Timed
	@UnitOfWork
	@Path("cameras")
	public List<Camera> listCameras(@SessionAuth(required={Role.UPLOAD}) SessionFilteredAuthorization auths,@SessionUser User user) {
		List<Camera> listNames = new ArrayList<Camera>();
		Project p = projectDAO.findById(1);
		Set<Camera> s = p.getCameras();
		List<Camera> l = new ArrayList<Camera>(s);
		return l;
	}
	
	@POST
	@Timed
	@UnitOfWork
	@Path("cameras")
	public Response addCamera(@SessionAuth(required={Role.UPLOAD,Role.ADMIN}) SessionFilteredAuthorization auths,@SessionUser User user, Camera newC)  {

		return Response.ok().build();

	}
	
	@POST
	@Timed
	@UnitOfWork
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createProject(@SessionUser User user,CreateProjectRequest request) {
		log.info("GOT an new project "+request);
		if(request.getProjectName() == null) throw new IllegalArgumentException("sorry, the name cannot be null");
		// verify the name is not already taken!!
		Project pOld = this.projectDAO.findByName(request.getProjectName());
		if(pOld != null) {
			throw new ConflictException("Sorry that project name is already taken");
		}
		// verify the name is not offensive since everyone will see it.
//TODO something here about offensive words		
		// verify the user doesn't have the limit of primary admins already.  20 ?
		List<Project> allUserProjects = this.projectDAO.findByPrimaryAdmin(user);
		if(allUserProjects.size() > MAX_ADMIN) {
			throw new ConflictException("Sorry you have reached the limit on the number of projects you can create");
		}
		// create the project.
		Project p = new Project();
		p.setName(request.getProjectName());
		p.setDescription(request.getProjectDescription());
		// set the primary admin to the user.
		p.setPrimaryAdmin(user);
		// and save
		this.projectDAO.save(p);
		
		return Response.ok().build();
	}
	
	@POST
	@Timed
	@UnitOfWork
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("join")
	public Response joinProject(@SessionUser User user,JoinProjectRequest request) {
		log.info("GOT an new project "+request);
//TODO  setup a permissions sort of things.
		String name = request.getSelectedProject();
		Project p = this.projectDAO.findByName(name);
		if(p != null) {
			UserProject up = new UserProject();
			User u = this.userDAO.findById(user.getId());
			
			up.setProject(p);
			up.setUser(u);
			this.upDAO.save(up);
		} else {
			
		}
		return Response.ok().build();
	}

}
