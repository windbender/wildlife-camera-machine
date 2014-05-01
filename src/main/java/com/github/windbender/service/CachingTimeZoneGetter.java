package com.github.windbender.service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTimeZone;

import com.github.windbender.core.LatLonPair;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class CachingTimeZoneGetter implements TimeZoneGetter {

	LoadingCache<LatLonPair, DateTimeZone> graphs;

	public CachingTimeZoneGetter(final TimeZoneGetter rootGetter) {

		graphs = CacheBuilder.newBuilder().maximumSize(1000).expireAfterAccess(1, TimeUnit.HOURS)
				.build(new CacheLoader<LatLonPair, DateTimeZone>() {
					public DateTimeZone load(LatLonPair key) {
						return rootGetter.getTimeZone(key);
					}
				});

	}

	@Override
	public DateTimeZone getTimeZone(LatLonPair pair) {
		try {
			return graphs.get(pair);
		} catch (ExecutionException e) {
			return null;
		}

	}

}
