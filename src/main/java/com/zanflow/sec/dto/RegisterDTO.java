package com.zanflow.sec.dto;

public class RegisterDTO extends ResponseDTO {
	
	private String companyMailId;
	private String companyName;
	private String countryCode;
	private String contactNo;
	private String address;
	private String alternateEmailId;
	private String subscriptionPack;
	private String firstName;
	private String lastName;
	private String password;
	
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

}
