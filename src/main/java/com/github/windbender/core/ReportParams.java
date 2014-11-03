package com.github.windbender.core;

import java.util.Arrays;
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
	long timeStart;
	long timeEnd;
	public long getTimeStart() {
		return timeStart;
	}
	public void setTimeStart(long timeStart) {
		this.timeStart = timeStart;
	}
	public long getTimeEnd() {
		return timeEnd;
	}
	public void setTimeEnd(long timeEnd) {
		this.timeEnd = timeEnd;
	}
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
	public DateTime getDateTimeStart() {
		return new DateTime(timeStart*1000);
	}
	public DateTime getDateTimeEnd() {
		return new DateTime(timeEnd * 1000);
	}

	public String[] getSpecies() {
		return species;
	}
	public void setSpecies(String[] species) {
		this.species = species;
	}
	@Override
	public String toString() {
		return "ReportParams [projectId=" + projectId + ", timeStart="
				+ timeStart + ", timeEnd=" + timeEnd + ", tod=" + tod
				+ ", species=" + Arrays.toString(species) + "]";
	}
	
	
}
