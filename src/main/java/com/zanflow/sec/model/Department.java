package com.zanflow.sec.model;


import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name="Department")
@Table(name="ZF_ID_DEPARTMENT")
public class Department implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name ="departmentid")
	protected String departmentId;
	@Column(name ="companycode")
	protected String companyCode;
	@Column(name ="departmentname")
	protected String departmentName;
	@Column(name ="departmenthead")
	protected String departmentHead;
	protected String status;
	@Column(name ="createdby")
	protected String createdBy;
	@Column(name= "lastactiondate" , columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	protected Timestamp lastActionDate;

	public String getDepartmentId() {
		return departmentId;
	}
	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}
	public String getDepartmentName() {
		return departmentName;
	}
	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}
	public String getDepartmentHead() {
		return departmentHead;
	}
	public void setDepartmentHead(String departmentHead) {
		this.departmentHead = departmentHead;
	}
	public String getCompanyCode() {
		return companyCode;
	}
	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
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
