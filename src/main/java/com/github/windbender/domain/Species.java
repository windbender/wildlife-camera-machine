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

	@Column(name="name", nullable=false)
	String name;
	
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
		Character upper = c.toUpperCase(c);
		int x = (int)upper;
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
	
//	$scope.keys.push({
//	keycode: 80,
//	character: 'p',
//	species: 'puma'
//});
	
	
}
