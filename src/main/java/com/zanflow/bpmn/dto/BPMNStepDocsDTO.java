package com.zanflow.bpmn.dto;

import java.io.File;
import java.io.Serializable;
import java.sql.Timestamp;


public class BPMNStepDocsDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long bpmnTaskId;
	
	private String stepName;

	private String bpmnId;
	
	private String bpmnTxRefNo;
	
	private String createdBy;
	
	private String documentType;
	
	private Timestamp createdTime;
	
	private String documentName;
	
	private File document;
	
		
	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public Timestamp getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Timestamp createdTime) {
		this.createdTime = createdTime;
	}

	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	public File getDocument() {
		return document;
	}

	public void setDocument(File document) {
		this.document = document;
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

	public long getBpmnTaskId() {
		return bpmnTaskId;
	}

	public void setBpmnTaskId(long bpmnTaskId) {
		this.bpmnTaskId = bpmnTaskId;
	}

	public String getStepName() {
		return stepName;
	}

	public void setStepName(String stepName) {
		this.stepName = stepName;
	}
}
