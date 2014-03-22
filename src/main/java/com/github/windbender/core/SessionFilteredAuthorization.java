package com.github.windbender.core;

import com.github.windbender.domain.Project;


public class SessionFilteredAuthorization {

	long projectId;
	Project project;
	public long getProjectId() {
		return projectId;
	}
	public void setProjectId(long projectId) {
		this.projectId = projectId;
	}
	public Project getProject() {
		return project;
	}
	public void setProject(Project project) {
		this.project = project;
	}
	

}
