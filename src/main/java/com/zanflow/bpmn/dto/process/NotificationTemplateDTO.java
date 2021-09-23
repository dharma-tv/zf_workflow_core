package com.zanflow.bpmn.dto.process;

import java.io.Serializable;

public class NotificationTemplateDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected String subject;
	protected String templateId;
	protected String message;
	protected String templateName;
	
	
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getTemplateId() {
		return templateId;
	}
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getTemplateName() {
		return templateName;
	}
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
	
	@Override
	public String toString() {
		return "NotificationTemplateDTO [subject=" + subject + ", templateId=" + templateId + ", message=" + message
				+ ", templateName=" + templateName + "]";
	}
	

}
