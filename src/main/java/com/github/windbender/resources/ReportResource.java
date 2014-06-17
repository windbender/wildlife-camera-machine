package com.github.windbender.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.windbender.auth.Priv;
import com.github.windbender.auth.SessionAuth;
import com.github.windbender.auth.SessionCurProj;
import com.github.windbender.auth.SessionUser;
import com.github.windbender.core.CurrentEventInfo;
import com.github.windbender.core.GoodParams;
import com.github.windbender.core.Limiter;
import com.github.windbender.core.ReportParams;
import com.github.windbender.core.ReportResponse;
import com.github.windbender.core.ReviewParams;
import com.github.windbender.core.Series;
import com.github.windbender.core.SessionFilteredAuthorization;
import com.github.windbender.core.SpeciesCount;
import com.github.windbender.dao.EventDAO;
import com.github.windbender.dao.GoodDAO;
import com.github.windbender.dao.ImageRecordDAO;
import com.github.windbender.dao.ReportDAO;
import com.github.windbender.dao.ReviewDAO;
import com.github.windbender.dao.StringSeries;
import com.github.windbender.domain.Good;
import com.github.windbender.domain.ImageEvent;
import com.github.windbender.domain.ImageRecord;
import com.github.windbender.domain.Project;
import com.github.windbender.domain.Review;
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
	private ImageRecordDAO imageRecordDAO;
	private ReviewDAO reviewDao;
	private GoodDAO goodDao;
	
	public ReportResource(ReportDAO rd,EventDAO eventDAO,ImageRecordDAO imageRecordDAO, ReviewDAO reviewDao, GoodDAO goodDao) {
		this.rd = rd;
		this.eventDAO = eventDAO;
		this.imageRecordDAO = imageRecordDAO;
		this.reviewDao = reviewDao;
		this.goodDao = goodDao;
	}

	@POST
	@Timed
	@UnitOfWork
	@Path("good")
	public Response updateGood(@SessionAuth(required={Priv.REPORT}) SessionFilteredAuthorization auths,@SessionUser User user, @SessionCurProj Project currentProject, GoodParams goodParams) {
		if(goodParams.getImageId() != null) {
			ImageRecord ir = imageRecordDAO.findById(goodParams.getImageId());
			Good g = new Good(ir,user,goodParams.getGood());
			goodDao.saveOrUpdate(g);
		}
		return Response.ok().build();
	}

	@POST
	@Timed
	@UnitOfWork
	@Path("review")
	public Response updateReview(@SessionAuth(required={Priv.REPORT}) SessionFilteredAuthorization auths,@SessionUser User user, @SessionCurProj Project currentProject, ReviewParams reviewParams) {
		long eventId = Long.parseLong(reviewParams.getEventId());
		log.info("event "+eventId+" is "+reviewParams.getReview());
		ImageEvent ie = eventDAO.findById(eventId);
		Review r = new Review(ie,user,reviewParams.getReview()>0);
		reviewDao.saveOrUpdate(r);
		return Response.ok().build();
	}

	@GET
	@Timed
	@UnitOfWork
	@Path("event/{eventId}")
	public Response getEventData(@SessionAuth(required={Priv.REPORT}) SessionFilteredAuthorization auths,@SessionUser User user, @SessionCurProj Project currentProject, @PathParam("eventId") Long eventId) {
		if(eventId == null) return Response.status(Status.NOT_FOUND).build();
		ImageEvent e = eventDAO.findById(eventId);
		if(e == null) return Response.status(Status.NOT_FOUND).build();
		// load data about categorization
		List<SpeciesCount> lsc = this.rd.findCategorizationData(e);
		// load flagging data
		Integer reviewCount = this.reviewDao.getReviewFlagCount(e,user);
		
		Map<String, Integer> m = new HashMap<String,Integer>();
		// load images data included "good" images data
		for(ImageRecord ir :e.getImageRecords()) {
			Integer good = this.goodDao.getGoodFlagCount(ir,user);
			m.put(ir.getId(),good);
		}
		CurrentEventInfo cei = new CurrentEventInfo(lsc,reviewCount,m);
		return Response.ok(cei).build();		
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
		
		ReportResponse rr = new ReportResponse();
		rr.setBySpeciesData(bySpecies);
		rr.setByHourData(byHour);
		rr.setByDayData(byDay);
		rr.setImageEvents(lout);

		return rr;
	}

}
