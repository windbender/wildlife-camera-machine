package com.github.windbender.resources;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.codec.binary.Base64;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.windbender.auth.SessionCurProj;
import com.github.windbender.auth.SessionUser;
import com.github.windbender.core.AcceptRequest;
import com.github.windbender.core.CurUser;
import com.github.windbender.core.LoginObject;
import com.github.windbender.core.MenuItem;
import com.github.windbender.core.ProjectMenuTO;
import com.github.windbender.core.ResetPWRequest;
import com.github.windbender.core.SetPWRequest;
import com.github.windbender.core.SignUpResponse;
import com.github.windbender.core.SignupRequest;
import com.github.windbender.core.UserTO;
import com.github.windbender.core.UserUpdate;
import com.github.windbender.core.VerifyRequest;
import com.github.windbender.core.VerifyResponse;
import com.github.windbender.dao.HibernateUserDAO;
import com.github.windbender.dao.InviteDAO;
import com.github.windbender.dao.ProjectDAO;
import com.github.windbender.dao.TokenDAO;
import com.github.windbender.dao.UserDAO;
import com.github.windbender.dao.UserProjectDAO;
import com.github.windbender.domain.Invite;
import com.github.windbender.domain.Project;
import com.github.windbender.domain.User;
import com.github.windbender.domain.UserProject;
import com.github.windbender.service.EmailService;
import com.sun.jersey.api.ConflictException;
import com.sun.jersey.api.NotFoundException;
import com.yammer.dropwizard.hibernate.UnitOfWork;
import com.yammer.metrics.annotation.Timed;

@Path("/users/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {
	Logger log = LoggerFactory.getLogger(UserResource.class);

	private UserDAO ud;
	private TokenDAO tokendao;
	private EmailService emailService;
	private ProjectDAO projectDAO;
	private UserProjectDAO upDAO;

	private InviteDAO inviteDAO;
	
	

	public UserResource(UserDAO ud, TokenDAO tokendao,ProjectDAO projectDAO,UserProjectDAO upDAO,InviteDAO inviteDAO,
			EmailService emailService) {
		super();
		this.ud = ud;
		this.tokendao = tokendao;
		this.emailService = emailService;
		this.projectDAO = projectDAO;
		this.upDAO = upDAO;
		this.inviteDAO = inviteDAO;
	}


	@POST
	@Timed
	@Path("logout")
	@Consumes(MediaType.APPLICATION_XML)
	@UnitOfWork
	public Response logout(@SessionUser User user, @Context HttpServletRequest request) {
		log.info("attempting logout for user "+user.toString());
		clearSession(request);

		return Response.status(Response.Status.OK).build();
	}
	

	@GET
	@Timed
	@Path("menus")
	@UnitOfWork
	public List<MenuItem> getMenus(@SessionUser(required=false) User user, @SessionCurProj Project currentProject ) {
		List<MenuItem> list = new ArrayList<MenuItem>();
		if(user == null) {
			return list;
		}
		if(currentProject == null) {
			return list;
		}
		User u = this.ud.findById(user.getId());
		Project p = this.projectDAO.findById(currentProject.getId());
		boolean isAdmin = false;
		if(p.getPrimaryAdmin().equals(u)) isAdmin = true;
		List<UserProject> upl = this.upDAO.findByUserIdProjectId(u,p);
		if(upl.size() > 0) {
			UserProject up = upl.get(0);
			
			if(up.getCanAdmin()) {
				list.add(new MenuItem("admin","#/setup"));
			}
			if(up.getCanUpload() ) {
				list.add(new MenuItem("upload","#/upload"));
			}
			if(up.getCanCategorize() || p.getPublicCategorize() ) {
				list.add(new MenuItem("categorize","#/categorize"));
			}
			if(up.getCanReport()  || p.getPublicReport() ) {
				list.add(new MenuItem("report","#/report"));
			}
			if(up.getCanReport()  || p.getPublicReport() ) {
				list.add(new MenuItem("best of","#/bestof"));
			}
		} else if (isAdmin ) {
			list.add(new MenuItem("admin","#/setup"));
			list.add(new MenuItem("upload","#/upload"));
			list.add(new MenuItem("categorize","#/categorize"));
			list.add(new MenuItem("report","#/report"));			
			list.add(new MenuItem("best of","#/bestof"));
		}
		

		return list;
	}

	@GET
	@Timed
	@Path("lookup")
	@UnitOfWork
	public List<UserTO> lookupToo(@SessionUser User user, @QueryParam("text") String snippet,@QueryParam("p") String projectIdStr) {
		if(snippet == null) throw new NotFoundException();
		if(snippet.length() < 3) throw new NotFoundException();
		Project project = null;
		try {
			Long projectId = null;
			projectId = Long.parseLong(projectIdStr);
			project = this.projectDAO.findById(projectId);
		} catch(NumberFormatException nfe) {}
		User u = this.ud.findByPortionOfEmailUsername(snippet);
		if(u == null) return null;
		// now remove it, if it's already in existence.
		List<UserProject> current= this.upDAO.findByUserIdProjectId(u, project);
		if(current.size() > 0) throw new NotFoundException();
		ArrayList<UserTO> l = new ArrayList<UserTO>();
		l.add(new UserTO(u));
		return l;
	}
	
	@GET
	@Timed
	@Path("check")
	@UnitOfWork
	public Response checkExists(@QueryParam("username") String username) {
		if(username == null) throw new NotFoundException("needs query param \"username\"");
		if(username.length() < 4) throw new NotFoundException("username needs decent length > 3");
		User u = ud.findByUsername(username);
		if(u == null) {
			throw new NotFoundException("user does not exist");
		}
		return Response.ok( MediaType.APPLICATION_JSON).build();
	}
	
	@GET
	@Timed
	@Path("myprojects")
	@UnitOfWork
	public List<Project> myProjects(@SessionUser User user) {
		User u = this.ud.findById(user.getId());
		if(u==null) throw new WebApplicationException();
		List<Project> lp = this.projectDAO.findByPrimaryAdmin(u);
		Set<Project> sp = new TreeSet<Project>();
		if(lp!=null) sp.addAll(lp);
		
		List<Project> outList = new ArrayList<Project>(sp);
		return outList;
	}
	@GET
	@Timed
	@Path("projects")
	@UnitOfWork
	public List<ProjectMenuTO> projects(@SessionUser(required=false) User user) {
		List<ProjectMenuTO> outList = new ArrayList<ProjectMenuTO>();
		if(user!=null) {
			User u = this.ud.findById(user.getId());
			List<Project> lp = this.projectDAO.findByPrimaryAdmin(u);
			Set<Project> sp = new TreeSet<Project>();
			if(lp!=null) sp.addAll(lp);
			List<UserProject> lup = this.upDAO.findAllByUser(u);
			if(lup != null) {
				for(UserProject up: lup) {
					Project p = up.getProject();
					sp.add(p);
				}
			}
			
			for(Project p: sp) {
				outList.add(new ProjectMenuTO(p,user));
			}
		}
		return outList;
	}
	@POST
	@Timed
	@Path("currentProject")
	@UnitOfWork
	public Response currentProject(@SessionUser User user, @Context HttpServletRequest request, int project_id) {
		log.info("attempting to set current project to "+project_id);
		Project p = this.projectDAO.findById(project_id);
		if(!userHasPrivsOn(user,p)) throw new WebApplicationException();
		
		if(p != null)
			request.getSession().setAttribute("current_project", p);

		return Response.status(Response.Status.OK).build();
	}
	
	@GET
	@Timed
	@Path("currentProject")
	@UnitOfWork
	public Project currentProjectGet(@SessionUser(required=false) User user, @SessionCurProj Project currentProject) {
		if(currentProject == null) return null;
		Project cp = this.projectDAO.findById(currentProject.getId());
		
		return cp;
	}
	
	

	
	private boolean userHasPrivsOn(User user, Project p) {
// TODO Auto-generated method stub
		return true;
	}


	@GET
	@Timed
	@Path("getLoggedIn")
	@UnitOfWork
	public CurUser getLoggedInUser(@Context HttpServletRequest request) {
		User user = (User) request.getSession().getAttribute("user");
		if(user == null) return new CurUser(null);
		String username = user.getUsername();
		CurUser cu = new CurUser(username);
		return cu;
	}

	@POST
	@Timed
	@Path("userupdate")
	@UnitOfWork
	public Response userupdate(@SessionUser User user, @Context HttpServletRequest request, UserUpdate updatedCurUser) {
		int userId = user.getId();
		User editUser = this.ud.findById(userId);
		
//		email  -- this should require a REVERIFICATION
		
		
		this.ud.save(editUser);

		clearSession(request);
		loadAssociatedAndSetSessionWith(request, editUser);
		loadStartingProject(request,editUser);

		
		// there is an implicit SAVE on editUser
		return Response.status(Response.Status.OK).build();
	}
	
	public static <T> T initializeAndUnproxy(T entity) {
	    if (entity == null) {
	        throw new 
	           NullPointerException("Entity passed for initialization is null");
	    }

	    Hibernate.initialize(entity);
	    if (entity instanceof HibernateProxy) {
	        entity = (T) ((HibernateProxy) entity).getHibernateLazyInitializer().getImplementation();
	    }
	    return entity;
	}
	
	@POST
	@Timed
	@Path("login")
	@UnitOfWork
	public Response login(  @Valid LoginObject login, @Context HttpServletRequest request) {
		log.info("attempting login for user "+login.getUsername());
		
		if(ud.checkPassword(login.getUsername(), login.getPassword())) {
			log.info("success");
			User p = ud.findByUsername(login.getUsername());
			if(p == null) {
				log.info("fail");
				clearSession(request);
				
				return Response.status(Response.Status.FORBIDDEN).build();
			}
			boolean ver = false;
			if(p.getVerified() != null) {
				ver = p.getVerified().booleanValue();
			}
			if(ver) {
				loadAssociatedAndSetSessionWith(request, p);
				loadStartingProject(request,p);

				return Response.status(Response.Status.OK).build();
			} else {
				log.info("fail not verified");
				clearSession(request);
				//throw new ForbiddenException("sorry that user is not yet verified");
				return Response.status(Response.Status.FORBIDDEN).build();
			}
		    
		} else {
			log.info("fail");
			clearSession(request);
			
			return Response.status(Response.Status.FORBIDDEN).build();

		}

	}


	private void clearSession(HttpServletRequest request) {
		request.getSession().setAttribute("user", null);
		request.getSession().setAttribute("current_project", null);
		request.getSession().setAttribute("user_projects", null);
		request.getSession().setAttribute("primary_admins", null);
		
	}

	private void loadStartingProject(HttpServletRequest request, User p) {
		List<Project> primaryAdminProjects = (List<Project>) request.getSession().getAttribute("primary_admins");
		Project currentProject = null;
		if((primaryAdminProjects != null) && (primaryAdminProjects.size() > 0) ){
			currentProject = primaryAdminProjects.get(0);
		} 
		if(currentProject == null) {
			List<UserProject> upl = (List<UserProject>) request.getSession().getAttribute("user_projects");
			if((upl != null) && (upl.size() > 0) ) {
				UserProject up = upl.get(0);
				currentProject = this.projectDAO.findById(up.getProject().getId().intValue());
			}
		}
		request.getSession().setAttribute("current_project", currentProject);
		
	}

	private void loadAssociatedAndSetSessionWith(HttpServletRequest request,
			User p) {
		request.getSession().setAttribute("user", p);
		
		User u = this.ud.findById(p.getId());
		List<Project> primaryAdminProjects = this.projectDAO.findByPrimaryAdmin(u);
		request.getSession().setAttribute("primary_admins", primaryAdminProjects);
		
		List<UserProject> upl = this.upDAO.findAllByUser(u);
		request.getSession().setAttribute("user_projects", upl);
		
	}
	
	@POST
	@Timed
	@Path("logout")
	@UnitOfWork
	public Response logout(@SessionUser User User, @Context HttpServletRequest request, String req) {
		log.info("attempting logout for user "+User.toString());
		clearSession(request);
		
		request.getSession().invalidate();
		return Response.status(Response.Status.OK).build();

	}
	
	@POST
	@Timed
	@Path("accept")
	@UnitOfWork
	public Response accept(AcceptRequest req, @Context HttpServletRequest request) {
		Invite inv = this.inviteDAO.findByCode(req.getInviteCode());
		if(inv == null) throw new NotFoundException();

		User oldUser = ud.findByUsername(req.getUsername());
		if(oldUser != null) {
			throw new ConflictException("That username is already taken");
		}
		User emailUser = ud.findByEmail(inv.getEmail());
		if(emailUser != null) {
			throw new ConflictException("That email already has an account. Consider using password recovery");
		}
		if(!validatePassword(req.getPassword())) {
			throw new ConflictException("Password does not follow the complexity rules");
		}
		User u = new User();
		u.setEmail(inv.getEmail());
		u.setUsername(req.getUsername());
		u.setPassword(req.getPassword());
		u.setVerified(true);
		u.setVerifyCode(inv.getInviteCode());
		u.setVerifyCodeSentDate(inv.getInviteCodeSentDate());
		Long userid = null;
		try {
			userid = ud.create(u);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e1) {
			log.error("can't create the user because ",e1);
			throw new WebApplicationException(e1);
		}
		// OK now make a User Project record also!!
		UserProject up = new UserProject();
		User u2 = this.ud.findById(userid.intValue());
		Project p2 = this.projectDAO.findById(inv.getProject().getId());
		
		up.setProject(p2);
		up.setUser(u2);
		up.setCanAdmin(inv.getCanAdmin());
		up.setCanCategorize(inv.getCanCategorize());
		up.setCanReport(inv.getCanReport());
		up.setCanUpload(inv.getCanReport());
		this.upDAO.save(up);
		
		
		loadAssociatedAndSetSessionWith(request, u2);
		loadStartingProject(request,u2);
		//now delete the invite
		this.inviteDAO.delete(inv);
		String sur ="done";
		return Response.ok(sur, MediaType.APPLICATION_JSON).build();

	}
	
	@POST
	@Timed
	@Path("signup")
	@UnitOfWork
	public Response signup(SignupRequest req) {
		log.info("sign up started");

		User oldUser = ud.findByUsername(req.getUsername());
		if(oldUser != null) {
			throw new ConflictException("That username is already taken");
		}
				
		User emailUser = ud.findByEmail(req.getEmail());
		if(emailUser != null) {
			throw new ConflictException("That email already has an account. Consider using password recovery");
		}
		if(!validatePassword(req.getPassword())) {
			throw new ConflictException("Password does not follow the complexity rules");
		}
		
		
		log.info("no conflicts. ");
		User u = new User();
		u.setEmail(req.getEmail());
		u.setUsername(req.getUsername());
		u.setPassword(req.getPassword());
		
		String verifyCode;
		try {
			verifyCode = makeVerifyCode();
		} catch (NoSuchAlgorithmException e1) {
			throw new ConflictException("unable to create a verification code");
		}
		log.info(" verify code created");

		u.setVerifyCode(verifyCode);
		try {
			long userid = ud.create(u);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e1) {
			log.error("can't create the user because ",e1);
			throw new WebApplicationException(e1);
		}
		log.info(" user created");
		
		DateTime verifySent = null;
		try {
			emailService.sendVerificationEmail(u);
			verifySent = new DateTime();
		} catch (MessagingException e) {
			log.error("unable to send email because of ",e);
			throw new ConflictException("unable to send an email to that address");
		}
		u.setVerifyCodeSentDate(verifySent);
		// note that the update is automatic thanks to hibernate session magic!!
		
		SignUpResponse sur = new SignUpResponse(true,"worked!");
		return Response.ok(sur, MediaType.APPLICATION_JSON).build();
	}

	
	public static int indexOf(Pattern pattern, String s) {
	    Matcher matcher = pattern.matcher(s);
	    return matcher.find() ? matcher.start() : -1;
	}

	public static boolean validatePassword(String password) {
		// must be 8 or longer characters
		if(password.length() < 8) {
			return false;
		}
		// must have a letter
		int index = indexOf(Pattern.compile("[A-z]"), password);
		if(index < 0) return false;
		// must have a number
		int index2 = indexOf(Pattern.compile("[0-9]"), password);
		if(index2 < 0) return false;
		
		return true;
	}

	@GET
	@Timed
	@Path("validtoken/{token}")
	@UnitOfWork
	public Response isValidToken(@PathParam("token") String token) {
		if(tokendao.isTokenValid(token)) {
			return Response.status(Response.Status.OK).build();
		} else {
	        ResponseBuilder b = Response.status(Status.NOT_FOUND);
		    b.tag("invalid password reset token");
		    return b.build();
		}
	}
	
	@POST
	@Timed
	@Path("lostpw")
	@UnitOfWork
	public Response lostpw(String resetemail) {
		log.info("starting reset password on "+resetemail);
		// now create and send via email a token which will return to a page on which a new PW can be chosen.
		// that page should simple reset the password IF  the token is valid and matches the email.
		// token should live for a fixed amount of time. 10 minutes ?
		User p = this.ud.findByEmail(resetemail);
		if(p != null) {
			String token = tokendao.createToken(p);
			try {
				emailService.sendPasswordResetEmail(p, token);
			} catch (MessagingException e) {

			}
		}
		// return OK regardless so that we don't "leak" data about membership.
		return Response.status(Response.Status.OK).build();
	}
	
	@POST
	@Timed
	@Path("resetpw")
	@UnitOfWork
	public Response resetpw(ResetPWRequest request) {
		log.info("starting reset password on "+request);
		User u = tokendao.getUserForToken(request.getToken());
		try {
			if(u != null) {
				if(!validatePassword(request.getPass())) {
					throw new ConflictException("Password does not follow the complexity rules");
				}
				String pass = request.getPass();
				String hashedPW = HibernateUserDAO.getSaltedHash(pass);
				u.setHashedPassword(hashedPW);
				this.emailService.sendUpdatedPassword(u);
				return Response.status(Response.Status.OK).build();
			}
		} catch (NoSuchAlgorithmException e) {
			log.error("unable to update password for "+u,e);
		} catch (InvalidKeySpecException e) {
			log.error("unable to update password for "+u,e);
		} catch (MessagingException e) {
			log.error("unable to update password for "+u,e);
		}
		ResponseBuilder b = Response.status(Status.NOT_FOUND);
	    b.tag("invalid password reset token");
	    return b.build();	    
	}
	
	@POST
	@Timed
	@Path("setpassword")
	@UnitOfWork
	public Response setpassword(@SessionUser User user, SetPWRequest request) {
		log.info("starting reset password on "+request);
		try {
			if(user != null) {
				String pass = request.getPass();
				String hashedPW = HibernateUserDAO.getSaltedHash(pass);
				user.setHashedPassword(hashedPW);
				this.emailService.sendUpdatedPassword(user);
				return Response.status(Response.Status.OK).build();
			}
		} catch (NoSuchAlgorithmException e) {
			log.error("unable to set password for "+user,e);
		} catch (InvalidKeySpecException e) {
			log.error("unable to set password for "+user,e);
		} catch (MessagingException e) {
			log.error("unable to set password for "+user,e);
		}
		ResponseBuilder b = Response.status(Status.NOT_FOUND);
	    b.tag("unable to reset password");
	    return b.build();	    
	}
	
	final boolean autoLoginOnVerify = true;
	@POST
	@Timed
	@Path("verify")
	@UnitOfWork
	public Response verify(@Context HttpServletRequest request,VerifyRequest req) {
		String code = req.getVerifyCode();
		User p = ud.findByVerifyCode(code);
		if(p == null) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		p.setVerified(true);
		try {
			this.emailService.sendUsANotification(p);
		} catch (MessagingException e) {
			// do nothing here
		}
		if(autoLoginOnVerify) {
			loadAssociatedAndSetSessionWith(request,p);
			loadStartingProject(request,p);
		}
		VerifyResponse vr = new VerifyResponse(p);
		return Response.ok(vr, MediaType.APPLICATION_JSON).build();
	}
	

	final static int CODE_LENGTH = 25;
	public static String makeVerifyCode() throws NoSuchAlgorithmException {
		int saltLen = 35;
		String finalCode = null;
		int len =0;
		do {
			byte[] randomBytes = SecureRandom.getInstance("SHA1PRNG").generateSeed(saltLen);
			String code = Base64.encodeBase64String(randomBytes);
			String newCode = code.replaceAll("[^a-zA-Z0-9]" , "");
			finalCode = newCode.substring(0, CODE_LENGTH);
			len = finalCode.length();
		} while(len  != CODE_LENGTH);
		return finalCode;
	}
}
