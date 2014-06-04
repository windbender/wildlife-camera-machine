package com.github.windbender.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import com.github.windbender.core.LatLonPair;

public class GeoNameTimeZoneGetter implements TimeZoneGetter {
	
	Logger log = LoggerFactory.getLogger(GeoNameTimeZoneGetter.class);

	public static void main(String[] args) {
		GeoNameTimeZoneGetter gntzg = new GeoNameTimeZoneGetter("demo");
		DateTimeZone dtz0 = gntzg.getTimeZone(new LatLonPair(38.5, -122.0));
		
		TimeZoneGetter timeZoneGetter = new CompositeTimeZoneGetter(new CachingTimeZoneGetter(new GeoNameTimeZoneGetter("demo")), new StupidTimeZoneGetter());
		DateTimeZone dtz = timeZoneGetter.getTimeZone(new LatLonPair(38.5, -122.0));
		System.out.println("got timezone "+dtz);
	}

	String username;

	public GeoNameTimeZoneGetter(String username) {
		super();
		this.username = username;
	}

	static private Double reduced(Double in) {
		long minutes = new Double(in * 60).longValue();
		return minutes / 60.0;
	}

	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	public static JSONObject readJsonFromUrl(String url) throws IOException,
			JSONException {
		InputStream is = new URL(url).openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is,
					Charset.forName("UTF-8")));
			String jsonText = readAll(rd);
			JSONObject json = new JSONObject(jsonText);
			return json;
		} finally {
			is.close();
		}
	}

	public DateTimeZone getTimeZone(LatLonPair location) {
		// should reduce accuracy to 1 mile. 1 mile is ROUGHLY 1/60 of a degree.
		// Unless you're in alaska etc
		double nlat = reduced(location.getLat());
		double nlon = reduced(location.getLon());
		// now make a URL:
		// http://api.geonames.org/timezoneJSON?lat=47.01&lng=10.2&username=demo
		String url = "http://api.geonames.org/timezoneJSON?lat=" + nlat
				+ "&lng=" + nlon + "&username=" + username;
		JSONObject obj = null;
		try {
			obj = readJsonFromUrl(url);
			String s = obj.get("timezoneId").toString();
			DateTimeZone dtz = DateTimeZone.forID(s);
			return dtz;
		} catch (IOException e) {
			log.error("unable to contact the timezone machine",e);
		} catch (JSONException e) {
			log.error("unable to parse response from the timezone machine which was: "+obj.toString(),e);
		}
		
		return null;
	}
}
