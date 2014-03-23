package com.github.windbender.domain;

import java.io.Serializable;
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
public class Project implements Serializable,Comparable<Project> {
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cameras == null) ? 0 : cameras.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((primaryAdmin == null) ? 0 : primaryAdmin.hashCode());
		result = prime
				* result
				+ ((publicCategorize == null) ? 0 : publicCategorize.hashCode());
		result = prime * result
				+ ((publicReport == null) ? 0 : publicReport.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Project other = (Project) obj;
		if (cameras == null) {
			if (other.cameras != null)
				return false;
		} else if (!cameras.equals(other.cameras))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (primaryAdmin == null) {
			if (other.primaryAdmin != null)
				return false;
		} else if (!primaryAdmin.equals(other.primaryAdmin))
			return false;
		if (publicCategorize == null) {
			if (other.publicCategorize != null)
				return false;
		} else if (!publicCategorize.equals(other.publicCategorize))
			return false;
		if (publicReport == null) {
			if (other.publicReport != null)
				return false;
		} else if (!publicReport.equals(other.publicReport))
			return false;
		return true;
	}

	@Override
	public int compareTo(Project o) {
		int x = this.getName().compareTo(o.getName());
		if(x ==0) {
			x = this.getId().compareTo(o.getId());
		}
		return x;
	}
	
	
}
