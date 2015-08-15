package com.github.windbender.core;


public class RegionUtil {
	public static float distanceInMilesBetween(float lat1, float lng1, float lat2, float lng2) {
	    double earthRadius = 3958.75; //radius in miles
	    double dLat = Math.toRadians(lat2-lat1);
	    double dLng = Math.toRadians(lng2-lng1);
	    double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
	               Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
	               Math.sin(dLng/2) * Math.sin(dLng/2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	    float dist = (float) (earthRadius * c);
	    return dist;
	}
	
	public static LatLonPair movePoint(LatLonPair in, double distanceMi, double angleDegrees) {

		double brng = Math.PI * angleDegrees / 180;
		double d = distanceMi;
		double R = 3959;
		double oldLat = in.getLat();
		double oldLon = in.getLon();
		double newLatRad = Math.asin( Math.sin(oldLat)*Math.cos(d/R) +
                Math.cos(oldLat)*Math.sin(d/R)*Math.cos(brng) );
		
		double newLonRad =  oldLon + Math.atan2(Math.sin(brng)*Math.sin(d/R)*Math.cos(oldLat),
                     Math.cos(d/R)-Math.sin(oldLat)*Math.sin(newLatRad));
		return new LatLonPair(newLatRad * 180/Math.PI,newLonRad* 180/Math.PI);
	}

	public static Float distanceInMilesBetweenDouble(Float centerLat,
			Float centerLon, double lat, double lon) {
		return distanceInMilesBetween(centerLat, centerLon, (float)lat,(float)lon);
	}
	
}
