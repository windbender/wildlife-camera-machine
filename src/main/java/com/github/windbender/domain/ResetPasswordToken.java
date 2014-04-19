package com.github.windbender.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.joda.time.DateTime;

@Entity
@Table(name="reset_pw_token")
public class ResetPasswordToken {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id", nullable=false)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="user_id", nullable=false)
    private User user;
	
	@Column(name="expiration_time", nullable=false)
	private DateTime expirationTime;

	@Column(name="token", nullable=false)
	private String token;

	public ResetPasswordToken() {
	}

	public ResetPasswordToken(User user) {
		this.user = user;
	}

	public ResetPasswordToken(User user, DateTime expirationTime, String token) {
		this.user = user;
		this.expirationTime = expirationTime;
		this.token = token;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public DateTime getExpirationTime() {
		return this.expirationTime;
	}

	public void setExpirationTime(DateTime expirationTime) {
		this.expirationTime = expirationTime;
	}

	public String getToken() {
		return this.token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	/**
	 * toString
	 * 
	 * @return String
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();

		buffer.append(getClass().getName()).append("@")
				.append(Integer.toHexString(hashCode())).append(" [");
		buffer.append("id").append("='").append(getId()).append("' ");
		buffer.append("token").append("='").append(getToken()).append("' ");
		buffer.append("]");

		return buffer.toString();
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof ResetPasswordToken))
			return false;
		ResetPasswordToken castOther = (ResetPasswordToken) other;

		return ((this.getId() == castOther.getId()) || (this.getId() != null
				&& castOther.getId() != null && this.getId().equals(
				castOther.getId())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + (getId() == null ? 0 : this.getId().hashCode());

		return result;
	}

}
