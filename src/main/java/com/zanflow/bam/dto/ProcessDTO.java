package com.zanflow.bam.dto;

import java.io.Serializable;

public class ProcessDTO extends ResponseDTO implements Serializable{


	private static final long serialVersionUID = 1L;
	protected String bpmnId;
	protected String processname;
	protected String processId;
	
	
	public String getBpmnId() {
		return bpmnId;
	}
	public void setBpmnId(String bpmnId) {
		this.bpmnId = bpmnId;
	}
	public String getProcessname() {
		return processname;
	}
	public void setProcessname(String processname) {
		this.processname = processname;
	}
	public String getProcessId() {
		return processId;
	}
	public void setProcessId(String processId) {
		this.processId = processId;
	}
	
	
}
