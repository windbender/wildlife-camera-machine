package com.github.windbender.service;

import org.joda.time.DateTimeZone;

import com.github.windbender.core.LatLonPair;

public class CompositeTimeZoneGetter implements TimeZoneGetter {

	private TimeZoneGetter primary;
	private TimeZoneGetter backup;

	public CompositeTimeZoneGetter(TimeZoneGetter primary, TimeZoneGetter backup) {
		this.primary = primary;
		this.backup = backup;
	}
	
	@Override
	public DateTimeZone getTimeZone(LatLonPair loc) {
		DateTimeZone zone = primary.getTimeZone( loc);
		if(zone == null) zone = backup.getTimeZone( loc);
		return zone;
	}

}
