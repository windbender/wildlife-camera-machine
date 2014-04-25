package com.github.windbender.core;

import java.util.List;
import java.util.Map;

public class CurrentEventInfo {

	List<SpeciesCount> speciesCounts;
	int flaggedCount;
	Map<String,Integer> goodMap;

	public List<SpeciesCount> getSpeciesCounts() {
		return speciesCounts;
	}

	public CurrentEventInfo(List<SpeciesCount> lsc, Integer flaggedCount, Map<String,Integer> goodMap) {
		this.speciesCounts = lsc;
		this.flaggedCount = flaggedCount;
		this.goodMap = goodMap;
	}

	public int getFlaggedCount() {
		return flaggedCount;
	}

	public Map<String, Integer> getGoodMap() {
		return goodMap;
	}
	

}
