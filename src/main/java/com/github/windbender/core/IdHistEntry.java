package com.github.windbender.core;

public class IdHistEntry {
	Long id;
	Integer count;
	public IdHistEntry(Long id, Integer count) {
		super();
		this.id = id;
		this.count = count;
	}
	public Long getId() {
		return id;
	}
	public Integer getCount() {
		return count;
	}
	

}
