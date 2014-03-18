package com.github.windbender.dao;

import java.util.ArrayList;
import java.util.List;

public class StringSeries {
	String key;
	List<List<Object>> values = new ArrayList<List<Object>>();
	
	public void setSeriesName(String name) {
		this.key = name;
	}
	
	
	public void addPoint(String category, Number val) {
		List<Object> p = new ArrayList<Object>();
		p.add(category);
		p.add(val);
		values.add(p);
	}
	public String getKey() {
		return key;
	}

	public List<List<Object>> getValues() {
		return values;
	}

	
}
