package com.github.windbender.service;

import org.joda.time.DateTimeZone;

import com.github.windbender.core.LatLonPair;

public class StupidTimeZoneGetter implements TimeZoneGetter {

	@Override
	public DateTimeZone getTimeZone(LatLonPair location) {
		// ignore lat
		int hoursOffset = new Double(location.getLon() / 15.0).intValue();
		DateTimeZone dtz = DateTimeZone.forOffsetHours( hoursOffset);
		return dtz;
	}


}
