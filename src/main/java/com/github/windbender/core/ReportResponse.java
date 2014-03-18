package com.github.windbender.core;

import java.util.List;

import com.github.windbender.dao.StringSeries;

public class ReportResponse {
	
	
	List<StringSeries> bySpeciesData;
	List<Series> byHourData;
	List<Series> byDayData;

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
	
}
