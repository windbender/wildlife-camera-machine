package com.github.windbender.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.joda.time.DateTime;



@Entity
@Table(name="events")
public class ImageEvent {
	
	public ImageEvent() {
		this.images = new ArrayList<ImageRecord>();
	}
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id", nullable=false)
	private long id;
	
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Column(name="camera_id", nullable=true)
	String cameraID;
	
	public String getCameraID() {
		return cameraID;
	}

	public void setCameraID(String cameraID) {
		this.cameraID = cameraID;
	}

	@OneToMany(fetch = FetchType.EAGER, targetEntity=ImageRecord.class)
	@JoinColumn(name="event_id",referencedColumnName="id")
	List<ImageRecord> images;
	
	@Column(name="event_start_time", nullable=false)
	DateTime eventStartTime;
	
	
	public DateTime getEventStartTime() {
		return eventStartTime;
	}

	public void setEventStartTime(DateTime eventStartTime) {
		this.eventStartTime = eventStartTime;
	}

	public List<ImageRecord> getImages() {
		return images;
	}

	public void setImages(List<ImageRecord> images) {
		this.images = images;
	}

	public void addImage(ImageRecord newImage) {
		images.add(newImage);
		if(this.eventStartTime.isAfter(newImage.getDatetime()) ){
			this.eventStartTime = newImage.getDatetime();
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((eventStartTime == null) ? 0 : eventStartTime.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((images == null) ? 0 : images.hashCode());
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
		if (eventStartTime == null) {
			if (other.eventStartTime != null)
				return false;
		} else if (!eventStartTime.equals(other.eventStartTime))
			return false;
		if (id != other.id)
			return false;
		if (images == null) {
			if (other.images != null)
				return false;
		} else if (!images.equals(other.images))
			return false;
		return true;
	}

}
