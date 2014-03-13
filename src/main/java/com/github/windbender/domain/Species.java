package com.github.windbender.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="species")
public class Species {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id", nullable=false)
	private long id;

	@Column(name="common_name", nullable=false)
	String name;

	@Column(name="latin_name", nullable=true)
	String latinName;
	
	@Column(name="keychar", nullable=false)
	Character c;
	public String getName() {
		return name;
	}
	public Species setName(String name) {
		this.name = name;
		return this;
	}
	public int getKeycode() {
		int x = 0;
		if(c != null) {
			Character upper = c.toUpperCase(c);
			x = (int)upper;
		}
		return x;
	}
	public Character getC() {
		return c;
	}
	public Species setC(Character c) {
		this.c = c;
		return this;
	}
	public long getId() {
		return id;
	}
	public Species setId(int id) {
		this.id = id;
		return this;
	}
	public String getLatinName() {
		return latinName;
	}
	public void setLatinName(String latinName) {
		this.latinName = latinName;
	}
	
//	$scope.keys.push({
//	keycode: 80,
//	character: 'p',
//	species: 'puma'
//});
	
	
}
