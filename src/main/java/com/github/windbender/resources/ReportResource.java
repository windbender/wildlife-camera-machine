package com.github.windbender.resources;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.windbender.auth.Priv;
import com.github.windbender.auth.SessionAuth;
import com.github.windbender.auth.SessionCurProj;
import com.github.windbender.auth.SessionUser;
import com.github.windbender.core.GoodParams;
import com.github.windbender.core.Limiter;
import com.github.windbender.core.ReportParams;
import com.github.windbender.core.ReportResponse;
import com.github.windbender.core.ReviewParams;
import com.github.windbender.core.Series;
import com.github.windbender.core.SessionFilteredAuthorization;
import com.github.windbender.dao.EventDAO;
import com.github.windbender.dao.ReportDAO;
import com.github.windbender.dao.StringSeries;
import com.github.windbender.domain.ImageEvent;
import com.github.windbender.domain.ImageRecord;
import com.github.windbender.domain.Project;
import com.github.windbender.domain.User;
import com.yammer.dropwizard.hibernate.UnitOfWork;
import com.yammer.metrics.annotation.Timed;

@Path("/report")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReportResource {
	
	Logger log = LoggerFactory.getLogger(ReportResource.class);

	ReportDAO rd;

	private EventDAO eventDAO;
	
	public ReportResource(ReportDAO rd,EventDAO eventDAO) {
		this.rd = rd;
		this.eventDAO = eventDAO;
	}

	@POST
	@Timed
	@UnitOfWork
	@Path("good")
	public Response updateGood(@SessionAuth(required={Priv.REPORT}) SessionFilteredAuthorization auths,@SessionUser User user, @SessionCurProj Project currentProject, GoodParams goodParams) {
		log.info("image "+goodParams.getImageId()+" is "+goodParams.getGood());
		return Response.ok().build();
	}

	@POST
	@Timed
	@UnitOfWork
	@Path("review")
	public Response updateReview(@SessionAuth(required={Priv.REPORT}) SessionFilteredAuthorization auths,@SessionUser User user, @SessionCurProj Project currentProject, ReviewParams reviewParams) {
		log.info("event "+reviewParams.getEventId()+" is "+reviewParams.getReview());
		return Response.ok().build();
	}

	@POST
	@Timed
	@UnitOfWork
	public ReportResponse makeReport(@SessionAuth(required={Priv.REPORT}) SessionFilteredAuthorization auths,@SessionUser User user, @SessionCurProj Project currentProject, ReportParams reportParams) {
		log.info("making a report for "+user.getUsername()+" on project "+currentProject.getName()+" with params "+reportParams+" and  auth TBD");
		Limiter limits = new Limiter(reportParams,currentProject);
		List<StringSeries> bySpecies = rd.makeBySpecies(limits);
		List<Series> byHour = rd.makeByHour(limits);
		List<Series> byDay = rd.makeByDay(limits);
		List<Long> l = rd.makeImageEvents(limits);
		List<ImageEvent> lout = new ArrayList<ImageEvent>();
		for(Long lng: l) {
			ImageEvent ie = eventDAO.findById(lng);
			for(ImageRecord ir : ie.getImageRecords()) {
				ir.getId();
			}
			lout.add(ie);
		}
		
		List<ImageEvent> ies;
		ReportResponse rr = new ReportResponse();
		rr.setBySpeciesData(bySpecies);
		rr.setByHourData(byHour);
		rr.setByDayData(byDay);
		rr.setImageEvents(lout);

		return rr;
	}

}
