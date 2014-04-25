package com.github.windbender.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="event_review_needed")
public class Review {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id", nullable=false)
	private Integer id;
	
	@ManyToOne
    @JoinColumn(name="image_event_id", nullable=false)
    ImageEvent imageEvent;
	
	@ManyToOne
    @JoinColumn(name="user_id", nullable=false)
	private User user;
	
	@Column(name="flagged", nullable=false)
	boolean flagged;
	
	public Review() {
		
	}
	public Review(ImageEvent imageEvent, User user, boolean flagged) {
		super();
		this.imageEvent = imageEvent;
		this.user = user;
		this.flagged = flagged;
	}

	public ImageEvent getImageEvent() {
		return imageEvent;
	}

	public void setImageEvent(ImageEvent imageEvent) {
		this.imageEvent = imageEvent;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public boolean isFlagged() {
		return flagged;
	}

	public void setFlagged(boolean flagged) {
		this.flagged = flagged;
	}

	public Integer getId() {
		return id;
	}
	

}
