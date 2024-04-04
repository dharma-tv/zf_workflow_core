package com.zanflow.sec.dto;

public class AppIntegrationDTO extends ResponseDTO{
	
	private static final long serialVersionUID = 1L;
	private String integrationapp;
	private String processid;
	private String action;
	private String companycode;
	private String apiauthkey;
	private String createddate;
	private String createdby;
	private String expirydate;
	private String modififeddate;
	private String modififedby;
	private int validityseconds;
	
	public String getIntegrationapp() {
		return integrationapp;
	}
	public void setIntegrationapp(String integrationapp) {
		this.integrationapp = integrationapp;
	}
	public String getProcessid() {
		return processid;
	}
	public void setProcessid(String processid) {
		this.processid = processid;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getCompanycode() {
		return companycode;
	}
	public void setCompanycode(String companycode) {
		this.companycode = companycode;
	}
	public String getApiauthkey() {
		return apiauthkey;
	}
	public void setApiauthkey(String apiauthkey) {
		this.apiauthkey = apiauthkey;
	}
	public String getCreateddate() {
		return createddate;
	}
	public void setCreateddate(String createddate) {
		this.createddate = createddate;
	}
	public String getCreatedby() {
		return createdby;
	}
	public void setCreatedby(String createdby) {
		this.createdby = createdby;
	}
	public String getExpirydate() {
		return expirydate;
	}
	public void setExpirydate(String expirydate) {
		this.expirydate = expirydate;
	}
	public String getModififeddate() {
		return modififeddate;
	}
	public void setModififeddate(String modififeddate) {
		this.modififeddate = modififeddate;
	}
	public String getModififedby() {
		return modififedby;
	}
	public void setModififedby(String modififedby) {
		this.modififedby = modififedby;
	}
	public int getValidityseconds() {
		return validityseconds;
	}
	public void setValidityseconds(int validityseconds) {
		validityseconds = validityseconds;
	}
	
	
}
