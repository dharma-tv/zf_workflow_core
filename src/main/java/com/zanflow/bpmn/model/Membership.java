package com.zanflow.bpmn.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.zanflow.bpmn.model.pk.MembershipPK;



@Entity(name="Membership")
@Table(name="ZF_ID_MEMBERSHIP")
@IdClass(MembershipPK.class)
public class Membership implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	protected String userId;
	@Id
	protected String roleId;
	protected String status;
	protected String accessGivenBy;
	protected Timestamp provisionDate;
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getAccessGivenBy() {
		return accessGivenBy;
	}
	public void setAccessGivenBy(String accessGivenBy) {
		this.accessGivenBy = accessGivenBy;
	}
	public Timestamp getProvisionDate() {
		return provisionDate;
	}
	public void setProvisionDate(Timestamp provisionDate) {
		this.provisionDate = provisionDate;
	}
	
	

}
