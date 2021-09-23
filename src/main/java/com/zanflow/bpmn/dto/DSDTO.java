package com.zanflow.bpmn.dto;

import java.io.Serializable;
import java.util.Map;

public class DSDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String processId;
	private String countrycode;
	private String bpmnId;
	
	public String getProcessId() {
		return processId;
	}
	public void setProcessId(String processId) {
		this.processId = processId;
	}
	public String getCountrycode() {
		return countrycode;
	}
	public void setCountrycode(String countrycode) {
		this.countrycode = countrycode;
	}
	public String getBpmnId() {
		return bpmnId;
	}
	public void setBpmnId(String bpmnId) {
		this.bpmnId = bpmnId;
	}
	
	
	
	

}
