package com.zanflow.sec.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name="Role")
@Table(name="ZF_ID_ROLE")
public class Role implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name ="roleid")
	protected String roleId;
	@Column(name ="companycode")
	protected String companyCode;
	@Column(name ="rolename")
	protected String roleName;
	@Column(name ="roledescription")
	protected String roleDescription;
	protected String status;
	@Column(name ="createdby")
	protected String createdBy;
	@Column(name= "lastactiondate" , columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	protected Timestamp lastActionDate;
	public String getRoleId() {
		return roleId;
	}
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
	public String getCompanyCode() {
		return companyCode;
	}
	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public String getRoleDescription() {
		return roleDescription;
	}
	public void setRoleDescription(String roleDescription) {
		this.roleDescription = roleDescription;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public Timestamp getLastActionDate() {
		return lastActionDate;
	}
	public void setLastActionDate(Timestamp lastActionDate) {
		this.lastActionDate = lastActionDate;
	}


	
}
