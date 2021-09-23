package com.zanflow.bpmn.dto;

import java.io.Serializable;

public class BPMNStepDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long bpmnTaskId;
	
	private String stepName;

	private String bpmnId;
	
	private String bpmnTxRefNo;
	
		
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
