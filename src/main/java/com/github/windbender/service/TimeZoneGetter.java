package com.github.windbender.service;

import org.joda.time.DateTimeZone;

import com.github.windbender.core.LatLonPair;

public interface TimeZoneGetter {

	public DateTimeZone getTimeZone(LatLonPair location);

}