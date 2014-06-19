package com.github.windbender.core;

import com.github.windbender.domain.ImageEvent;

public class ImageRec {
	public ImageRec(ImageEvent ie, IdHist idHist, NameHist nameHist) {
		imageEvent = ie;
		this.idHist = idHist;
		this.nameHist = nameHist;
	}

	ImageEvent imageEvent;
	public IdHist getIdHist() {
		return idHist;
	}

	public void setIdHist(IdHist idHist) {
		this.idHist = idHist;
	}

	IdHist idHist;
	NameHist nameHist;

	public NameHist getNameHist() {
		return nameHist;
	}

	public void setNameHist(NameHist nameHist) {
		this.nameHist = nameHist;
	}

	public ImageEvent getImageEvent() {
		return imageEvent;
	}

	public void setImageEvent(ImageEvent imageEvent) {
		this.imageEvent = imageEvent;
	}
	
}
