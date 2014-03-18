package com.github.windbender.core;

import java.util.List;

public class ReportResponse {
	
	
	List<NV> bySpeciesData;
	List<Series> byHourData;
	List<Series> byDayData;

	public List<NV> getBySpeciesData() {
		return bySpeciesData;
	}

	public void setBySpeciesData(List<NV> bySpecies) {
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
