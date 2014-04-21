package com.github.windbender.core;

public class GoodParams {
	//imageId: $scope.imageEvents[$scope.reportEventIndex].imageRecords[$scope.reportImgIndex].id,
	//good: $scope.isGood
	
	String imageId;
	int good;
	public String getImageId() {
		return imageId;
	}
	public void setImageId(String imageId) {
		this.imageId = imageId;
	}
	public int getGood() {
		return good;
	}
	public void setGood(int good) {
		this.good = good;
	}
	public GoodParams(String imageId, int good) {
		super();
		this.imageId = imageId;
		this.good = good;
	}
	public GoodParams(){
		
	}
}
