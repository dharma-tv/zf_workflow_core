package com.zanflow.bpmn.dto;

import java.io.Serializable;
import java.sql.Timestamp;


public class TXNDocumentDTO extends ResponseDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected long documentId;
	
	protected String bpmnTxRefNo;
	
	protected String createdBy;
	
	protected String companyCode;
	
	protected String stepName;
	
	protected String documentType;
	
	protected String documentName;
	
	protected String createdTime;
	
	

	public long getDocumentId() {
		return documentId;
	}

	public void setDocumentId(long documentId) {
		this.documentId = documentId;
	}

	public String getBpmnTxRefNo() {
		return bpmnTxRefNo;
	}

	public void setBpmnTxRefNo(String bpmnTxRefNo) {
		this.bpmnTxRefNo = bpmnTxRefNo;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getCompanyCode() {
		return companyCode;
	}

	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}

	public String getStepName() {
		return stepName;
	}

	public void setStepName(String stepName) {
		this.stepName = stepName;
	}

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	public String getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(String createdTime) {
		this.createdTime = createdTime;
	}

	

}
