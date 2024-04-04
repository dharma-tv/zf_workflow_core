package com.zanflow.bam.dto;

import java.io.Serializable;
import java.util.Map;

public class MyReqDTO extends ResponseDTO implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected String bpmTxRefno;
	
	protected String bpmnId;
	
	protected String processName;
	
	protected String currentStepName;
	
	protected int statusCode;

	protected Map<String,Object> keyValues;
	
	public String getBpmTxRefno() {
		return bpmTxRefno;
	}

	public void setBpmTxRefno(String bpmTxRefno) {
		this.bpmTxRefno = bpmTxRefno;
	}

	public String getBpmnId() {
		return bpmnId;
	}

	public void setBpmnId(String bpmnId) {
		this.bpmnId = bpmnId;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getCurrentStepName() {
		return currentStepName;
	}

	public void setCurrentStepName(String currentStepName) {
		this.currentStepName = currentStepName;
	}

	public Map<String, Object> getKeyValues() {
		return keyValues;
	}

	public void setKeyValues(Map<String, Object> keyValues) {
		this.keyValues = keyValues;
	}
	

}
