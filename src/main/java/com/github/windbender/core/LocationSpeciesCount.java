package com.github.windbender.core;

public class LocationSpeciesCount {
	public class Center {
		public double longitude;
		public double latitude;
		public double getLongitude() {
			return longitude;
		}
		public void setLongitude(double longitude) {
			this.longitude = longitude;
		}
		public double getLatitude() {
			return latitude;
		}
		public void setLatitude(double latitude) {
			this.latitude = latitude;
		}
	};
	public class Stroke {
		String color;
		double weight;
		double opacity;
		public String getColor() {
			return color;
		}
		public void setColor(String color) {
			this.color = color;
		}
		public double getWeight() {
			return weight;
		}
		public void setWeight(double weight) {
			this.weight = weight;
		}
		public double getOpacity() {
			return opacity;
		}
		public void setOpacity(double opacity) {
			this.opacity = opacity;
		}
	}
	public class Fill {
		String color;
		double opacity;
		public double getOpacity() {
			return opacity;
		}
		public void setOpacity(double opacity) {
			this.opacity = opacity;
		}
		public String getColor() {
			return color;
		}
		public void setColor(String color) {
			this.color = color;
		}
	}

	String id;
	Center center;
	double radius;
	Stroke stroke;
	Fill fill;
	
	Integer species_id;
	Integer count;
	public LocationSpeciesCount(LatLonPair ob, int species_id, int count, double radiusM) {
		this.center = new Center();
		this.center.latitude = ob.getLat();
		this.center.longitude = ob.getLon();
		this.stroke = new Stroke();
		this.stroke.color = "#08B21F";
		this.stroke.opacity = 0.5;
		this.stroke.weight = 2;
		this.fill = new Fill();
		this.fill.color = "#08B21F";
		this.fill.opacity =0.25;
		this.species_id = species_id;
		this.count = count;
		this.id=""+species_id+3007*ob.hashCode();
		this.radius =radiusM;  // probably in meeters
	}
	
	public Integer getSpecies_id() {
		return species_id;
	}
	public void setSpecies_id(Integer species_id) {
		this.species_id = species_id;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Center getCenter() {
		return center;
	}

	public void setCenter(Center center) {
		this.center = center;
	}

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	public Stroke getStroke() {
		return stroke;
	}

	public void setStroke(Stroke stroke) {
		this.stroke = stroke;
	}

	public Fill getFill() {
		return fill;
	}

	public void setFill(Fill fill) {
		this.fill = fill;
	}
}
