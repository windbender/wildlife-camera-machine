package com.github.windbender.core;

public class UserStats {
	int imageToDo;
	int imagesCompleted;
	public int getImageToDo() {
		return imageToDo;
	}
	public void setImageToDo(int imageToDo) {
		this.imageToDo = imageToDo;
	}
	public int getImagesCompleted() {
		return imagesCompleted;
	}
	public void setImagesCompleted(int imagesCompleted) {
		this.imagesCompleted = imagesCompleted;
	}
	public UserStats(int imageToDo, int imagesCompleted) {
		super();
		this.imageToDo = imageToDo;
		this.imagesCompleted = imagesCompleted;
	}
	
}
