package com.github.windbender.core;

import com.github.windbender.domain.Species;

public class SpeciesCount {
	Species species;
	Integer count;
	public Species getSpecies() {
		return species;
	}
	public void setSpecies(Species species) {
		this.species = species;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
	public SpeciesCount(Species s, Integer count) {
		this.species = s;
		this.count = count;
	}

}
