package com.zanflow.bpmn.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


@Entity(name="TXNDocments")
@Table(name="zf_txn_userdocs")
public class TXNDocments implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	@Column(name="bpmntxrefno")
	protected String bpmnTxRefNo;
	
	
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="bpmndocseq")
	@SequenceGenerator(name="bpmndocseq", sequenceName="bpmndocseq", allocationSize=1)
	@Column(name="documentid")
	protected long documentId;
	
	@Column(name="createdby")
	protected String userId;
	
	@Column(name="companycode")
	protected String companyCode;
	

		
	@Column(name="stepname")
	protected String stepName;
	
	@Column(name="documenttype")
	protected String documentType;
	
	@Column(name="documentname")
	protected String documentName;
	

	@Column(name="document")
	protected byte[] document;
	
	@Column(name="createdtime")
	protected Timestamp createdTime;

	
	
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

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
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

	public byte[] getDocument() {
		return document;
	}

	public void setDocument(byte[] document) {
		this.document = document;
	}

	public Timestamp getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Timestamp createdTime) {
		this.createdTime = createdTime;
	}
}
