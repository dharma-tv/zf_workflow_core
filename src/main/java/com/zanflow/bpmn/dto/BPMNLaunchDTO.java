package com.zanflow.bpmn.dto;

import java.io.Serializable;
import java.util.Map;

public class BPMNLaunchDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String bpmnId;
	private Map<String,String> dataMap;
	
	public String getBpmnId() {
		return bpmnId;
	}
	public void setBpmnId(String bpmnId) {
		this.bpmnId = bpmnId;
	}
	public Map<String, String> getDataMap() {
		return dataMap;
	}
	public void setDataMap(Map<String, String> dataMap) {
		this.dataMap = dataMap;
	}
	
	

}
