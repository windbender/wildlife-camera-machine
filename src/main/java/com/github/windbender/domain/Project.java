package com.github.windbender.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

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

	@Column(name="publicReport", nullable=false)
	private Boolean publicReport = false;
	
	@Column(name="publicCategorize", nullable=false)
	private Boolean publicCategorize = false;
	
	@JsonProperty
	@OneToMany(mappedBy="project",fetch=FetchType.LAZY)
	@ElementCollection(targetClass=ImageRecord.class)
	Set<Camera> cameras = new HashSet<Camera>();
	
	public void addCamera(Camera c) {
		cameras.add(c);
	}
	
	public Set<Camera> getCameras() {
		return cameras;
	}
	
	public Boolean getPublicReport() {
		return publicReport;
	}

	public void setPublicReport(Boolean publicReport) {
		this.publicReport = publicReport;
	}

	public Boolean getPublicCategorize() {
		return publicCategorize;
	}

	public void setPublicCategorize(Boolean publicCategorize) {
		this.publicCategorize = publicCategorize;
	}

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
