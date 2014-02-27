package com.github.windbender.core;

import java.util.ArrayList;
import java.util.List;

import com.github.windbender.domain.ImageRecord;

public class ImageEvent {
	List<ImageRecord> images;

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
