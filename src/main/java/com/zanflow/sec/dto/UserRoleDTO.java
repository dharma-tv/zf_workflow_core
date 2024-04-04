package com.zanflow.sec.dto;

import java.io.Serializable;



public class UserRoleDTO extends ResponseDTO implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected String userId;
	protected String roleId;
	protected String accessGivenBy;
	protected String status;
	protected String companyCode;
	
	
	
	public String getCompanyCode() {
		return companyCode;
	}
	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getRoleId() {
		return roleId;
	}
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
	public String getAccessGivenBy() {
		return accessGivenBy;
	}
	public void setAccessGivenBy(String accessGivenBy) {
		this.accessGivenBy = accessGivenBy;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	
}
