package com.zanflow.bpmn;

import java.util.Map;

import com.zanflow.bpmn.model.BPMNTask;

public class BPMNData {
	
	private String bpmnId;
	private String processId;
	private String countryCode;
	private String pUnitName;
	private long bpmnTaskId;
	private String stepName;
	
	private Map<String,Object> dataMap;
	private BPMNTask bpmnTask;
	
	private String processVariabledef;
	private String uischema;
	private String data;
	private String companyCode;
	private String completedBy;
	private String initatedBy;
	

	public String getCompletedBy() {
		return completedBy;
	}

	public void setCompletedBy(String completedBy) {
		this.completedBy = completedBy;
	}

	public String getCompanyCode() {
		return companyCode;
	}

	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}

	public String getProcessVariabledef() {
		return processVariabledef;
	}

	public void setProcessVariabledef(String processVariabledef) {
		this.processVariabledef = processVariabledef;
	}

	public String getUischema() {
		return uischema;
	}

	public void setUischema(String uischema) {
		this.uischema = uischema;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public BPMNTask getBpmnTask() {
		return bpmnTask;
	}

	public void setBpmnTask(BPMNTask bpmnTask) {
		this.bpmnTask = bpmnTask;
	}

	
	public String getStepName() {
		return stepName;
	}

	public void setStepName(String stepName) {
		this.stepName = stepName;
	}

	public long getBpmnTaskId() {
		return bpmnTaskId;
	}

	public void setBpmnTaskId(long bpmnTaskId) {
		this.bpmnTaskId = bpmnTaskId;
	}

	

	public String getBpmnId() {
		return bpmnId;
	}

	public void setBpmnId(String bpmnId) {
		this.bpmnId = bpmnId;
	}

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getpUnitName() {
		return pUnitName;
	}

	public void setpUnitName(String pUnitName) {
		this.pUnitName = pUnitName;
	}

	public Map<String,Object> getDataMap() {
		return dataMap;
	}

	public void setDataMap(Map<String,Object> dataMap) {
		this.dataMap = dataMap;
	}
	
	public String getInitatedBy() {
		return initatedBy;
	}


	public void setInitatedBy(String initatedBy) {
		this.initatedBy = initatedBy;
	}
}
