package com.github.windbender.core;

public class NameHistEntry {
	String name;
	Integer count;
	public NameHistEntry(String name, Integer count) {
		super();
		this.name = name;
		this.count = count;
	}
	public String getName() {
		return name;
	}
	public Integer getCount() {
		return count;
	}
	
}
