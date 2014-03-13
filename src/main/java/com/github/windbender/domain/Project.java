package com.github.windbender.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="projects")
public class Project {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id", nullable=false)
	Long id;
	
	@Column(name="name", nullable=false)
	String name;
	
	@Column(name="description", nullable=true)
	String description;
	
	@JsonIgnore
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="primary_admin_id")
	User primaryAdmin;

	public Project(String string, String string2) {
		this.id = (long)string.hashCode();
		this.name=string;
		this.description = string2;
	}

	public Project() {
		
	}
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public User getPrimaryAdmin() {
		return primaryAdmin;
	}

	public void setPrimaryAdmin(User primaryAdmin) {
		this.primaryAdmin = primaryAdmin;
	}
	
	
}
