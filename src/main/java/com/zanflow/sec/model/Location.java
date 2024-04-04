package com.zanflow.sec.model;


import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name="Location")
@Table(name="ZF_ID_LOCATION")
public class Location implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name ="locationid")
	protected String locationId;
	@Column(name ="companycode")
	protected String companyCode;
	@Column(name ="locationname")
	protected String locationName;
	@Column(name ="locationhead")
	protected String locationHead;
	protected String status;
	@Column(name ="createdby")
	protected String createdBy;
	@Column(name= "lastactiondate" , columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	protected Timestamp lastActionDate;

	public String getLocationId() {
		return locationId;
	}
	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}
	public String getLocationName() {
		return locationName;
	}
	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}
	public String getLocationHead() {
		return locationHead;
	}
	public void setLocationHead(String locationHead) {
		this.locationHead = locationHead;
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
