package com.github.windbender.resources;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.windbender.auth.SessionUser;
import com.github.windbender.core.NV;
import com.github.windbender.core.ReportParams;
import com.github.windbender.core.ReportResponse;
import com.github.windbender.dao.ReportDAO;
import com.github.windbender.domain.User;
import com.yammer.dropwizard.hibernate.UnitOfWork;
import com.yammer.metrics.annotation.Timed;

@Path("/report")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReportResource {
	
	Logger log = LoggerFactory.getLogger(ReportResource.class);

	ReportDAO rd;
	
	public ReportResource(ReportDAO rd) {
		this.rd = rd;
	}

	@POST
	@Timed
	@UnitOfWork
	public ReportResponse logout(@SessionUser User user, ReportParams reportParams) {

		List<NV> bySpecies = rd.makeBySpecies();
		
		ReportResponse rr = new ReportResponse();
		rr.setBySpeciesData(bySpecies);
		return rr;
	}

}
