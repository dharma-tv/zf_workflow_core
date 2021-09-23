package com.zanflow.bpmn.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.zanflow.bpmn.model.pk.BPMNRptTemplatesPK;


@Entity(name="BPMNRptTemplates")
@Table(name="zf_cfg_bpmntmplates")
@IdClass(BPMNRptTemplatesPK.class)
public class BPMNRptTemplates implements Serializable
{

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name ="companycode")
	protected String companyCode;
	
	@Column(name ="processid")
	protected String processId;
	
	@Id
	@Column(name ="bpmnid")
	protected String bpmnId;
	
	@Id
	@Column(name ="tempateid")
	protected String templateId;
	
	@Column(name ="templatecontent")
	protected String templateContent;

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

	public String getTemplateContent() {
		return templateContent;
	}

	public void setTemplateContent(String templateContent) {
		this.templateContent = templateContent;
	}
	
	
	
	

}
