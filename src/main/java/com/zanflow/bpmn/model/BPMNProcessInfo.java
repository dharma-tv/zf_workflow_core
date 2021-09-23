package com.zanflow.bpmn.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity(name="BPMNProcessInfo")
@Table(name="zf_txn_bpmnprocessinfo")
public class BPMNProcessInfo implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	protected String bpmnTxRefNo;
	protected String bpmnId;		
	protected Timestamp createdTime;
	protected String lastStepName;
	
	@Column(name ="VERSION_NUM")
	@Version
	protected int version;
	protected int statusCode;

	protected String processdata;
	
	@Column(name="companycode")
	protected String compamnyCode;
	
	@Column(name="initatedby")
	protected String initatedBy;
	
	@Column(name="currentstepname")
	protected String currentStepName;
	
	@Column(name="processid")
	protected String processid;
	
	

	public String getProcessid() {
		return processid;
	}

	public void setProcessid(String processid) {
		this.processid = processid;
	}

	public String getCompamnyCode() {
		return compamnyCode;
	}

	public void setCompamnyCode(String compamnyCode) {
		this.compamnyCode = compamnyCode;
	}

	public String getBpmnTxRefNo() {
		return bpmnTxRefNo;
	}

	public void setBpmnTxRefNo(String bpmnTxRefNo) {
		this.bpmnTxRefNo = bpmnTxRefNo;
	}

	public String getBpmnId() {
		return bpmnId;
	}

	public void setBpmnId(String bpmnId) {
		this.bpmnId = bpmnId;
	}

	public Timestamp getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Timestamp createdTime) {
		this.createdTime = createdTime;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getLastStepName() {
		return lastStepName;
	}

	public void setLastStepName(String lastStepName) {
		this.lastStepName = lastStepName;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
	
	public String getProcessdata() {
		return processdata;
	}

	public void setProcessdata(String processdata) {
		this.processdata = processdata;
	}

	public String getInitatedBy() {
		return initatedBy;
	}

	public void setInitatedBy(String initatedBy) {
		this.initatedBy = initatedBy;
	}

	public String getCurrentStepName() {
		return currentStepName;
	}

	public void setCurrentStepName(String currentStepName) {
		this.currentStepName = currentStepName;
	}
	
	
}
