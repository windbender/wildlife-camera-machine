package com.github.windbender.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.joda.time.DateTime;



@Entity
@Table(name="events")
public class ImageEvent {
	
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

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "events")
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
		if(this.images == null) {
			this.images = new ArrayList<ImageRecord>();
		}
		images.add(newImage);
	}

}
