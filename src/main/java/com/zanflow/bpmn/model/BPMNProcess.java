package com.zanflow.bpmn.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.json.JSONObject;

@Entity(name="BPMNProcess")
@Table(name="zf_cfg_bpmnprocess")
public class BPMNProcess implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="bpmnid")
	protected String bpmnId;
	
	protected String processname;
	@Column(name="bpmnfilecontent")
	protected String bpmnFileContent;
	
	protected String isactive;
	
	
	@Column(name="createdtime")
	protected Timestamp createdTime;
	
	@Column(name="processconfig")
	protected String processConfig;

	protected String initiaterole;
	
	protected String companycode;
	
	protected String enquiryrole;
	
	protected String monitorrole;
	
	protected String processid;
	
	protected String rendertype;

	

	public String getBpmnId() {
		return bpmnId;
	}

	public String getProcessConfig() {
		return processConfig;
	}

	public void setProcessConfig(String processConfig) {
		this.processConfig = processConfig;
	}

	public void setBpmnId(String bpmnId) {
		this.bpmnId = bpmnId;
	}
	
	public String getProcessname() {
		return processname;
	}

	public void setProcessname(String processname) {
		this.processname = processname;
	}

	public String getBpmnFileContent() {
		return bpmnFileContent;
	}

	public void setBpmnFileContent(String bpmnFileContent) {
		this.bpmnFileContent = bpmnFileContent;
	}

	public String getIsactive() {
		return isactive;
	}

	public void setIsactive(String isactive) {
		this.isactive = isactive;
	}

	public Timestamp getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Timestamp createdTime) {
		this.createdTime = createdTime;
	}

	
	public String getInitiaterole() {
		return initiaterole;
	}

	public void setInitiaterole(String initiaterole) {
		this.initiaterole = initiaterole;
	}
	
	public String toString(){
		return bpmnId + " ------ "+ processname;
	}

	public String getCompanycode() {
		return companycode;
	}

	public void setCompanycode(String companycode) {
		this.companycode = companycode;
	}

	public String getEnquiryrole() {
		return enquiryrole;
	}

	public void setEnquiryrole(String enquiryrole) {
		this.enquiryrole = enquiryrole;
	}

	public String getMonitorrole() {
		return monitorrole;
	}

	public void setMonitorrole(String monitorrole) {
		this.monitorrole = monitorrole;
	}
	
	public String getProcessid() {
		return processid;
	}

	public void setProcessid(String processid) {
		this.processid = processid;
	}
	
	public String getRendertype() {
		return rendertype;
	}

	public void setRendertype(String rendertype) {
		this.rendertype = rendertype;
	}
}
