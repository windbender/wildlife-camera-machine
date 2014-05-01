package com.github.windbender.core;

public class LatLonPair {
	Double lat;
	Double lon;
	public LatLonPair(Double lat, Double lon) {
		super();
		this.lat = lat;
		this.lon = lon;
	}
	public Double getLat() {
		return lat;
	}
	public Double getLon() {
		return lon;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((lat == null) ? 0 : lat.hashCode());
		result = prime * result + ((lon == null) ? 0 : lon.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LatLonPair other = (LatLonPair) obj;
		if (lat == null) {
			if (other.lat != null)
				return false;
		} else if (!lat.equals(other.lat))
			return false;
		if (lon == null) {
			if (other.lon != null)
				return false;
		} else if (!lon.equals(other.lon))
			return false;
		return true;
	}
	
}
