package com.zanflow.sec.model;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name="CompanyProfile")
@Table(name="zf_id_company")
public class CompanyProfile implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name="mailid" )
	private String companyMailId;
	@Column(name="companyname", nullable=false)
	private String companyName;
	@Column(name="companycode", nullable=false)
	private String companyCode;
	@Column(name="countrycode")
	private String countryCode;
	@Column(name="contact")
	private String contactNo;
	@Column(name="addressline")
	private String address;
	@Column(name="addressline2")
	private String address2;
	@Column(name="city")
	private String city;
	@Column(name="pincode")
	private String pincode;
	@Column(name="alternatmailid")
	private String alternateEmailId;
	@Column(name="subscriptionpack")
	private String subscriptionPack;
	private String status;
	private Date createddate;
	private Date activateddate;
	private String activationkey;
	
	public String getCompanyMailId() {
		return companyMailId;
	}
	public void setCompanyMailId(String companyMailId) {
		this.companyMailId = companyMailId;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public String getContactNo() {
		return contactNo;
	}
	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getAlternateEmailId() {
		return alternateEmailId;
	}
	public void setAlternateEmailId(String alternateEmailId) {
		this.alternateEmailId = alternateEmailId;
	}
	public String getSubscriptionPack() {
		return subscriptionPack;
	}
	public void setSubscriptionPack(String subscriptionPack) {
		this.subscriptionPack = subscriptionPack;
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
	
	public Date getCreateddate() {
		return createddate;
	}
	public void setCreateddate(Date createddate) {
		this.createddate = createddate;
	}
	public Date getActivateddate() {
		return activateddate;
	}
	public void setActivateddate(Date activateddate) {
		this.activateddate = activateddate;
	}
	public String getActivationkey() {
		return activationkey;
	}
	public void setActivationkey(String activationkey) {
		this.activationkey = activationkey;
	}

	public String getAddress2() {
		return address2;
	}
	public void setAddress2(String address2) {
		this.address2 = address2;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getPincode() {
		return pincode;
	}
	public void setPincode(String pincode) {
		this.pincode = pincode;
	}
}
