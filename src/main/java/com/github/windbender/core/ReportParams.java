package com.github.windbender.core;

import java.util.Map;

import org.joda.time.DateTime;

public class ReportParams {
//    $scope.params.projectId = undefined;
//    $scope.params.polyGeoRegion = [];
//    $scope.params.timeStart = undefined;
//    $scope.params.timeEnd = undefined;
//    $scope.params.tod = {}
//    $scope.params.tod.day = true;
//    $scope.params.tod.sunset = true;
//    $scope.params.tod.night = true;
//    $scope.params.tod.sunrise = true;
//    $scope.params.species = [];
	long projectId;
	DateTime timeStart;
	DateTime timeEnd;
	Map<String,Boolean> tod;
	public Map<String, Boolean> getTod() {
		return tod;
	}
	public void setTod(Map<String, Boolean> tod) {
		this.tod = tod;
	}
	String[] species;
	public long getProjectId() {
		return projectId;
	}
	public void setProjectId(long projectId) {
		this.projectId = projectId;
	}
	public DateTime getTimeStart() {
		return timeStart;
	}
	public void setTimeStart(DateTime timeStart) {
		this.timeStart = timeStart;
	}
	public DateTime getTimeEnd() {
		return timeEnd;
	}
	public void setTimeEnd(DateTime timeEnd) {
		this.timeEnd = timeEnd;
	}
	public String[] getSpecies() {
		return species;
	}
	public void setSpecies(String[] species) {
		this.species = species;
	}
	
	
}
