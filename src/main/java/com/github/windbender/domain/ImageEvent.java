package com.github.windbender.domain;

import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.windbender.core.LatLonPair;
import com.github.windbender.core.RegionUtil;
import com.github.windbender.core.TypeOfDay;



@Entity
@Table(name="events")
public class ImageEvent {
	

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id", nullable=false)
	private long id;
	
	
	@JsonProperty
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Column(name="camera_id", nullable=true)
	Long cameraID;
	
	@JsonProperty
	public Long getCameraID() {
		return cameraID;
	}

	public void setCameraID(long l) {
		this.cameraID = l;
	}

	@JsonProperty
	@OneToMany(mappedBy="event",fetch=FetchType.LAZY)
	@Sort(type=SortType.NATURAL)
	@ElementCollection(targetClass=ImageRecord.class)
	SortedSet<ImageRecord> imageRecords = new TreeSet<ImageRecord>();
	//Set<ImageRecord> imageRecords = new HashSet<ImageRecord>();

	//    public SortedSet<ImageRecord> getImageRecords() {
	 public SortedSet<ImageRecord> getImageRecords() {
		return imageRecords;
	}
	

	public void setImageRecords(SortedSet<ImageRecord> images) {
		this.imageRecords = images;
	}

	@Column(name="event_start_time", nullable=false)
	DateTime eventStartTime;
	
	
	@JsonProperty
	public DateTime getEventStartTime() {
		return eventStartTime;
	}

	public void setEventStartTime(DateTime eventStartTime) {
		this.eventStartTime = eventStartTime;
	}



	public void addImage(ImageRecord newImage) {
		imageRecords.add(newImage);
		newImage.setEvent(this);
		if(this.eventStartTime.isAfter(newImage.getDatetime()) ){
			this.eventStartTime = newImage.getDatetime();
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((cameraID == null) ? 0 : cameraID.hashCode());
		result = prime * result
				+ ((eventStartTime == null) ? 0 : eventStartTime.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));

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
		ImageEvent other = (ImageEvent) obj;
		if (cameraID == null) {
			if (other.cameraID != null)
				return false;
		} else if (!cameraID.equals(other.cameraID))
			return false;
		if (eventStartTime == null) {
			if (other.eventStartTime != null)
				return false;
		} else if (!eventStartTime.equals(other.eventStartTime))
			return false;
		if (id != other.id)
			return false;
		if (imageRecords == null) {
			if (other.imageRecords != null)
				return false;
		} else if (!imageRecords.equals(other.imageRecords))
			return false;
		return true;
	}


	@Column(name="time_of_day", nullable=true)
	String todStr;

	@JsonProperty
	public String getTodStr() {
		return todStr;
	}

	public void setTypeOfDay(TypeOfDay tod) {
		todStr = tod.toString();
		
	}

	@JsonProperty
	public LatLonPair getObfuscatedLocation() {
		//TODO  hardwired distance to obfuscate. need to hook up to project somehow		
		double distanceMi =  0.1;
		if(imageRecords.size() > 0) {
			double lat = imageRecords.first().getLat();
			double lon = imageRecords.first().getLon();
			LatLonPair start = new LatLonPair(lat,lon);

			return obfuscate(distanceMi, start);
		}
		return null;
	}

	public static LatLonPair obfuscate(double distanceMi, LatLonPair in) {
		long seed = Double.doubleToLongBits(in.getLat() + in.getLon() *1000);
		Random r = new Random(seed);
		LatLonPair out = RegionUtil.movePoint(in, r.nextDouble() * distanceMi, 360 * r.nextDouble());
		return out;
	}

}
