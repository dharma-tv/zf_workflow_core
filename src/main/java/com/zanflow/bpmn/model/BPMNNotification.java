package com.zanflow.bpmn.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.zanflow.bpmn.model.pk.BPMNNotificationPK;


@Entity(name="BPMNNotification")
@Table(name="zf_cfg_bpmnnotification")
@IdClass(BPMNNotificationPK.class)
public class BPMNNotification implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name ="bpmnid")
	protected String bpmnId;
	
	@Id
	@Column(name ="stepname")
	protected String stepName;
	
	@Id
	@Column(name ="triggerevent")
	protected String triggerEvent;
	
	@Column(name ="subject")
	protected String subject;
	
	@Column(name ="toemail")
	protected String toEmail;
	
	@Column(name ="ccemail")
	protected String ccEmail;
	
	@Column(name ="mailContent")
	protected String mailContent;
	
	@Id
	@Column(name ="companycode")
	protected String companyCode;
	
	@Column(name ="processid")
	protected String processId;
	
	

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

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getToEmail() {
		return toEmail;
	}

	public void setToEmail(String toEmail) {
		this.toEmail = toEmail;
	}

	public String getCcEmail() {
		return ccEmail;
	}

	public void setCcEmail(String ccEmail) {
		this.ccEmail = ccEmail;
	}

	public String getMailContent() {
		return mailContent;
	}

	public void setMailContent(String mailContent) {
		this.mailContent = mailContent;
	}

	@Override
	public String toString() {
		return "BPMNNotification [bpmnId=" + bpmnId + ", stepName=" + stepName + ", triggerEvent=" + triggerEvent
				+ ", subject=" + subject + ", toEmail=" + toEmail + ", ccEmail=" + ccEmail + ", mailContent="
				+ mailContent + ", companyCode=" + companyCode + ", processId=" + processId + "]";
	}
	
	
	

}
