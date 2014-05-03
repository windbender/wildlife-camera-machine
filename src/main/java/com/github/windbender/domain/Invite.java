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

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="invites")
public class Invite {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id", nullable=false)
	private Integer id;
	
	@Column(name="email", nullable=true)
	String email;
	
	@Column(name="inviteCode", nullable=true)
	String inviteCode;
	
	@Column(name="inviteCodeSentDate", nullable=true)
	DateTime inviteCodeSentDate;
	
	@Column(name="userCreated")
	private Boolean userCreated = false;
	
	@JsonIgnore
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="inviter_user_id")
	private User inviter;
	
	@JsonIgnore
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="project_id")
	Project project;
	
	@Column(name="canReport", nullable=false)
	private Boolean canReport = true;
	
	@Column(name="canCategorize", nullable=false)
	private Boolean canCategorize = true;
	
	@Column(name="canUpload", nullable=false)
	private Boolean canUpload = true;
	
	@Column(name="canAdmin", nullable=false)
	private Boolean canAdmin = false;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getInviteCode() {
		return inviteCode;
	}

	public void setInviteCode(String inviteCode) {
		this.inviteCode = inviteCode;
	}

	public DateTime getInviteCodeSentDate() {
		return inviteCodeSentDate;
	}

	public void setInviteCodeSentDate(DateTime inviteCodeSentDate) {
		this.inviteCodeSentDate = inviteCodeSentDate;
	}

	public Boolean getUserCreated() {
		return userCreated;
	}

	public void setUserCreated(Boolean userCreated) {
		this.userCreated = userCreated;
	}

	public User getInviter() {
		return inviter;
	}

	public void setInviter(User inviter) {
		this.inviter = inviter;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public Boolean getCanReport() {
		return canReport;
	}

	public void setCanReport(Boolean canReport) {
		this.canReport = canReport;
	}

	public Boolean getCanCategorize() {
		return canCategorize;
	}

	public void setCanCategorize(Boolean canCategorize) {
		this.canCategorize = canCategorize;
	}

	public Boolean getCanUpload() {
		return canUpload;
	}

	public void setCanUpload(Boolean canUpload) {
		this.canUpload = canUpload;
	}

	public Boolean getCanAdmin() {
		return canAdmin;
	}

	public void setCanAdmin(Boolean canAdmin) {
		this.canAdmin = canAdmin;
	}
	
}
