package com.zanflow.bam.dto;

import java.io.Serializable;

public class SearchResultDTO extends ResponseDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected String bpmnTxRefno;
	
	protected String statusCode;

	protected String createdTime;
	
	protected String lastStepName;

	public String getBpmnTxRefno() {
		return bpmnTxRefno;
	}

	public void setBpmnTxRefno(String bpmnTxRefno) {
		this.bpmnTxRefno = bpmnTxRefno;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(String createdTime) {
		this.createdTime = createdTime;
	}

	public String getLastStepName() {
		return lastStepName;
	}

	public void setLastStepName(String lastStepName) {
		this.lastStepName = lastStepName;
	}
	
	
}
