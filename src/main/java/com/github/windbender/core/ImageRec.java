package com.github.windbender.core;

import com.github.windbender.domain.ImageEvent;

public class ImageRec {
	public ImageRec(ImageEvent ie) {
		imageEvent = ie;
	}

	ImageEvent imageEvent;

	public ImageEvent getImageEvent() {
		return imageEvent;
	}

	public void setImageEvent(ImageEvent imageEvent) {
		this.imageEvent = imageEvent;
	}
	
}
