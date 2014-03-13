package com.github.windbender.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.joda.time.DateTime;

@Entity
@Table(name="users")
public class User implements Serializable {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id", nullable=false)
	private int id;
	
	@Column(name="username", nullable=false)
	String username;

	public User(String username) {
		this.username = username;
	}
	public User() {
		
	}
	
	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", email=" + email
				+ "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + id;
		result = prime * result
				+ ((username == null) ? 0 : username.hashCode());
		result = prime * result
				+ ((verified == null) ? 0 : verified.hashCode());
		result = prime * result
				+ ((verifyCode == null) ? 0 : verifyCode.hashCode());
		result = prime
				* result
				+ ((verifyCodeSentDate == null) ? 0 : verifyCodeSentDate
						.hashCode());
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
		User other = (User) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (id != other.id)
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		if (verified == null) {
			if (other.verified != null)
				return false;
		} else if (!verified.equals(other.verified))
			return false;
		if (verifyCode == null) {
			if (other.verifyCode != null)
				return false;
		} else if (!verifyCode.equals(other.verifyCode))
			return false;
		if (verifyCodeSentDate == null) {
			if (other.verifyCodeSentDate != null)
				return false;
		} else if (!verifyCodeSentDate.equals(other.verifyCodeSentDate))
			return false;
		return true;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getVerifyCode() {
		return verifyCode;
	}
	public void setVerifyCode(String verifyCode) {
		this.verifyCode = verifyCode;
	}
	public DateTime getVerifyCodeSentDate() {
		return verifyCodeSentDate;
	}
	public void setVerifyCodeSentDate(DateTime verifyCodeSentDate) {
		this.verifyCodeSentDate = verifyCodeSentDate;
	}
	public Boolean getVerified() {
		return verified;
	}
	public void setVerified(Boolean verified) {
		this.verified = verified;
	}

	transient String password;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	@Column(name="hashedpassword", nullable=false)
	String hashedPassword;
	
	public String getHashedPassword() {
		return hashedPassword;
	}
	public void setHashedPassword(String hashedPassword) {
		this.hashedPassword = hashedPassword;
	}

	@Column(name="email", nullable=true)
	String email;
	
	@Column(name="verifyCode", nullable=true)
	String verifyCode;
	
	@Column(name="verifyCodeSentDate", nullable=true)
	DateTime verifyCodeSentDate;
	
	@Column(name="verified", nullable=true)
	Boolean verified;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
