package com.github.windbender.core;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

public class Series {
	String key;
	List<List<Number>> values = new ArrayList<List<Number>>();
	
	public void setSeriesName(String name) {
		this.key = name;
	}
	
	public void addPoint(DateTime dt, Number val) {
		List<Number> p = new ArrayList<Number>();
		Long millis = dt.getMillis();
		p.add(millis);
		p.add(val);
		values.add(p);
	}
	public void addPoint(Number category, Number val) {
		List<Number> p = new ArrayList<Number>();
		p.add(category);
		p.add(val);
		values.add(p);
	}
	public String getKey() {
		return key;
	}

	public List<List<Number>> getValues() {
		return values;
	}
	
}
