package com.zanflow.bpmn.model.pk;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Id;

public class BPMNRptTemplatesPK implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	protected String companyCode;
	
	@Id
	protected String processId;
	
	@Id
	protected String bpmnId;
	
	@Id
	@Column(name ="tempateid")
	protected String templateId;

	public BPMNRptTemplatesPK(String companyCode, String processid, String bpmnid, String templateid) {
		this.companyCode = companyCode;
		this.processId=processid;
		this.bpmnId = bpmnid;
		this.templateId = templateid;
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

	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	

}
