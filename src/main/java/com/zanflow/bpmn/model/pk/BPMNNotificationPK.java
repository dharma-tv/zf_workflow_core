package com.zanflow.bpmn.model.pk;

import java.io.Serializable;
import javax.persistence.Id;



public class BPMNNotificationPK implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	protected String bpmnId;
	
	@Id
	protected String stepName;
	
	@Id
	protected String triggerEvent;
	
	@Id
	protected String companyCode;
	
//	@Id
//	protected String processId;

	
	
	
	public String getCompanyCode() {
		return companyCode;
	}

	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}

//	public String getProcessId() {
//		return processId;
//	}
//
//	public void setProcessId(String processId) {
//		this.processId = processId;
//	}

	public String getBpmnId() {
		return bpmnId;
	}

	public void setBpmnId(String bpmnId) {
		this.bpmnId = bpmnId;
	}

	public String getStepName() {
		return stepName;
	}

	public void setStepName(String stepName) {
		this.stepName = stepName;
	}

	public String getTriggerEvent() {
		return triggerEvent;
	}

	public void setTriggerEvent(String triggerEvent) {
		this.triggerEvent = triggerEvent;
	}

	public int hashCode()
	{
		return (int) this.getCompanyCode().hashCode()+this.bpmnId.hashCode()+this.stepName.hashCode()+this.triggerEvent.hashCode();
	}
	
	public boolean equals(Object ob)
	{
		if(ob==null)
		{
			return false;
		}
		if(!(ob instanceof BPMNNotificationPK))
		{
			return false;
		}
		if(ob== this)
		{
			return true;
		}
		BPMNNotificationPK objPk=(BPMNNotificationPK) ob;
		return (this.getCompanyCode().equals(objPk.getCompanyCode())&&this.getBpmnId().equals(objPk.getBpmnId())&& this.getStepName().equals(objPk.getStepName()) && this.getTriggerEvent().equals(objPk.getTriggerEvent()));
	}

	@Override
	public String toString() {
		return "BPMNNotificationPK [bpmnId=" + bpmnId + ", stepName=" + stepName + ", triggerEvent=" + triggerEvent
				+ ", companyCode=" + companyCode + "]";
	}
	
}
