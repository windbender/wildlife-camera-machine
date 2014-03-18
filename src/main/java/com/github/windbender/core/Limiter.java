package com.github.windbender.core;

import org.joda.time.DateTime;
import org.joda.time.Interval;

public class Limiter {

	public Limiter(ReportParams reportParams) {
		// TODO Auto-generated constructor stub
	}

	public String makeSQL() {
		return "";
//		return " and species_id=701 ";
	}

	public Interval getTimeInterval() {
		DateTime st = new DateTime(2013,9,1,0,0);
		DateTime e = new DateTime(2014,4,1,0,0);
		Interval i = new Interval(st,e);
		return i;
	}

}
