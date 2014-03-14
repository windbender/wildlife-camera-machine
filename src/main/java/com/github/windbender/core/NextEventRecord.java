package com.github.windbender.core;

import com.github.windbender.domain.ImageEvent;

public class NextEventRecord {
	public NextEventRecord(ImageEvent upie) {
		imageEvent = upie;
	}
	ImageEvent imageEvent;
	int remainingToIdentify;
	int numberIdentified;
	public ImageEvent getImageEvent() {
		return imageEvent;
	}
	public void setImageEvent(ImageEvent imageEvent) {
		this.imageEvent = imageEvent;
	}
	public int getRemainingToIdentify() {
		return remainingToIdentify;
	}
	public void setRemainingToIdentify(int remainingToIdentify) {
		this.remainingToIdentify = remainingToIdentify;
	}
	public int getNumberIdentified() {
		return numberIdentified;
	}
	public void setNumberIdentified(int numberIdentified) {
		this.numberIdentified = numberIdentified;
	}

	
}
