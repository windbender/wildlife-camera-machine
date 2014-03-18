package com.github.windbender.core;

import java.util.HashSet;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.Interval;

public class Limiter {

	ReportParams reportParams;
	public Limiter(ReportParams reportParams) {
		this.reportParams = reportParams;
	}

	public String makeSQL() {
		boolean excludeMode = false;
		if(reportParams.getSpecies().length ==0) {
			return " and species_id= -77 ";
		}
		for(String species: reportParams.getSpecies()) {
			if("all".equals(species)) excludeMode = true;
			if(species.startsWith("-")) excludeMode = true;
			break;
		}
		Set<Long> excludeSet = new HashSet<Long>();
		Set<Long> includeSet = new HashSet<Long>();
		StringBuilder s = new StringBuilder();
		if(excludeMode) {
			for(String species: reportParams.getSpecies()) {
				if("all".equals(species)) {
					
				} else {
					Long id = Long.parseLong(species);
					excludeSet.add(id);
				}
			}
			for(Long id: excludeSet) {
				s.append(" and species_id != "+id+" ");
			}
		} else {
			for(String species: reportParams.getSpecies()) {
				Long id = Long.parseLong(species);
				includeSet.add(id);
			}
			if(includeSet.size() > 0) s.append(" and ( ");
			boolean first = true;
			for(Long id: includeSet) {
  				if(!first) s.append(" or ");
				s.append(" species_id = "+id+" ");
				first = false;
			}
			if(includeSet.size() > 0) s.append(" ) ");
			
		}
		return s.toString();
//		return "";
//		return " and species_id=701 ";
//		return " and species_id=390 ";
//		return " and species_id != 396 and species_id != 702 ";
	}

	public Interval getTimeInterval() {
		DateTime st = new DateTime(2013,9,1,0,0);
		DateTime e = new DateTime(2014,4,1,0,0);
		Interval i = new Interval(st,e);
		return i;
	}

}
