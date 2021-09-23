package com.zanflow.bpmn.dto.process;

import java.io.Serializable;

public class StepNotificationDTO implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected String templateId;
	protected String triggerType;
	protected String toAddr;
	protected String ccAddr;
	protected String stepName;
	protected String companyCode;
	protected String processId;
	protected String bpmnId;
	protected boolean subsCCtype;
	protected boolean subsTotype;
	protected String subsCCField;
	protected String subsToField;
	
	public boolean isSubsCCtype() {
		return subsCCtype;
	}
	public void setSubsCCtype(boolean subsCCtype) {
		this.subsCCtype = subsCCtype;
	}
	public boolean isSubsTotype() {
		return subsTotype;
	}
	public void setSubsTotype(boolean subsTotype) {
		this.subsTotype = subsTotype;
	}
	public String getSubsCCField() {
		return subsCCField;
	}
	public void setSubsCCField(String subsCCField) {
		this.subsCCField = subsCCField;
	}
	public String getSubsToField() {
		return subsToField;
	}
	public void setSubsToField(String subsToField) {
		this.subsToField = subsToField;
	}
	public String getTemplateId() {
		return templateId;
	}
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}
	public String getTriggerType() {
		return triggerType;
	}
	public void setTriggerType(String triggerType) {
		this.triggerType = triggerType;
	}
	public String getToAddr() {
		return toAddr;
	}
	public void setToAddr(String toAddr) {
		this.toAddr = toAddr;
	}
	public String getCcAddr() {
		return ccAddr;
	}
	public void setCcAddr(String ccAddr) {
		this.ccAddr = ccAddr;
	}
	public String getStepName() {
		return stepName;
	}
	public void setStepName(String stepName) {
		this.stepName = stepName;
	}
	public String getCompanyCode() {
		return companyCode;
	}
	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}
	public String getProcessId() {
		return processId;
	}
	public void setProcessId(String processId) {
		this.processId = processId;
	}
	public String getBpmnId() {
		return bpmnId;
	}
	public void setBpmnId(String bpmnId) {
		this.bpmnId = bpmnId;
	}
	
	
	

}
