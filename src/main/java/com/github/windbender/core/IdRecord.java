package com.github.windbender.core;

public class IdRecord {
	String id;
	
	String speciesName;
	int numberOfAnimals;
	public IdRecord(String id) {
		this.id = id;
	}
	
	public void add(IdentificationRequest idRequest) {
		speciesName = idRequest.getSpeciesName();
		numberOfAnimals = idRequest.getNumberOfAnimals();
		
		
	}

}
