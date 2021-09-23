package com.zanflow.bpmn.dto.master;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MasterDataDTO {

    private static final long serialVersionUID = 1L;
	protected String companycode;
	protected String mastername;
	protected String oldkey;
	protected String key;
	protected String value;
	
	
	public MasterDataDTO(String companycode, String mastername, String key, String value) {
		super();
		this.companycode = companycode;
		this.mastername = mastername;
		this.key = key;
		this.value = value;
	}
	
	public MasterDataDTO() {
	}
	
	public String getCompanycode() {
		return companycode;
	}
	public void setCompanycode(String companycode) {
		this.companycode = companycode;
	}
	public String getMastername() {
		return mastername;
	}
	public void setMastername(String mastername) {
		this.mastername = mastername;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getOldkey() {
		return oldkey;
	}

	public void setOldkey(String oldkey) {
		this.oldkey = oldkey;
	}
	
}
