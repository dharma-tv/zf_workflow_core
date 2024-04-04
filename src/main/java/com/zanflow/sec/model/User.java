package com.zanflow.sec.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name="User")
@Table(name="ZF_ID_USER")
public class User implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name="userid")
	protected String userId;
	@Column(name="firstname")
	protected String firstName;
	@Column(name="lastname")
	protected String lastName;
	protected String password;
	@Column(name="companycode")
	protected String companyCode;
	@Column(name="emailid")
	protected String emailId;
	@Column(name="lastlogintime")
	protected Timestamp lastLoginTime;
	@Column(name="loginattempts")
	protected int loginAttempts;
	@Column(name="managerid")
	protected String managerId;
	protected String status;
	@Column(name="usertype")
	protected String userType;
	@Column(name="loginstatus")
	protected String logInStatus="N";
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getCompanyCode() {
		return companyCode;
	}
	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public Timestamp getLastLoginTime() {
		return lastLoginTime;
	}
	public void setLastLoginTime(Timestamp lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}
	public int getLoginAttempts() {
		return loginAttempts;
	}
	public void setLoginAttempts(int loginAttempts) {
		this.loginAttempts = loginAttempts;
	}
	public String getManagerId() {
		return managerId;
	}
	public void setManagerId(String managerId) {
		this.managerId = managerId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}
	public String getLogInStatus() {
		return logInStatus;
	}
	public void setLogInStatus(String logInStatus) {
		this.logInStatus = logInStatus;
	}

	
}
