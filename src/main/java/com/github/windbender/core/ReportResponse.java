package com.github.windbender.core;

import java.util.List;

import com.github.windbender.dao.StringSeries;

public class ReportResponse {
	
	
	List<StringSeries> bySpeciesData;
	List<Series> byHourData;
	List<Series> byDayData;
	List<ImageRec> imageEvents;
	List<Series> byMonthData;
	

	public List<Series> getByMonthData() {
		return byMonthData;
	}

	public List<ImageRec> getImageEvents() {
		return imageEvents;
	}

	public void setImageEvents(List<ImageRec> imageEvents) {
		this.imageEvents = imageEvents;
	}

	public List<StringSeries> getBySpeciesData() {
		return bySpeciesData;
	}

	public void setBySpeciesData(List<StringSeries> bySpecies) {
		this.bySpeciesData = bySpecies;
	}

	public void setByHourData(List<Series> byHour) {
		this.byHourData = byHour;
		
	}

	public List<Series> getByHourData() {
		return byHourData;
	}

	public List<Series> getByDayData() {
		return byDayData;
	}

	public void setByDayData(List<Series> byDay) {
		this.byDayData = byDay;
	}

	public void setByMonthData(List<Series> byMonth) {
		this.byMonthData = byMonth;
		
	}
	
}
