package com.zanflow.bpmn.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.json.JSONObject;

@Entity(name="MasterSchema")
@Table(name="zf_mstr_metadata")
public class MasterSchema implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="companycode")
	protected String companycode;
	
	@Id
	protected String mastername;
	
	@Column(name="metadata")
	protected String metadata;

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

	public String getMetadata() {
		return metadata;
	}

	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}
	

}
