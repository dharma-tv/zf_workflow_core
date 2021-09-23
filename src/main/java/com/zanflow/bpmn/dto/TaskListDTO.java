package com.zanflow.bpmn.dto;



import java.util.ArrayList;

import javax.persistence.Convert;

import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskListDTO extends ResponseDTO{
	
    private static final long serialVersionUID = 1L;
	
	private long taskId;
	private String refId;
	private String taskName;
	private String processName;
	private String taskSubject;
	private String createdDate;
	private String dueDate;
	private String lockedUser;
	private String bpmnId;
	private String formData;
	private ArrayList<TaskListDTO> taskHistory;

	public TaskListDTO(){}
	


	
	public long getTaskId() {
		return taskId;
	}
	public void setTaskId(long taskId) {
		this.taskId = taskId;
	}
	public String getRefId() {
		return refId;
	}
	public void setRefId(String refId) {
		this.refId = refId;
	}
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public String getProcessName() {
		return processName;
	}
	public void setProcessName(String processName) {
		this.processName = processName;
	}
	public String getTaskSubject() {
		return taskSubject;
	}
	public void setTaskSubject(String taskSubject) {
		this.taskSubject = taskSubject;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public String getDueDate() {
		return dueDate;
	}
	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}
	public String getLockedUser() {
		return lockedUser;
	}
	public void setLockedUser(String lockedUser) {
		this.lockedUser = lockedUser;
	}
	
	public String getBpmnId() {
		return bpmnId;
	}

	public void setBpmnId(String bpmnId) {
		this.bpmnId = bpmnId;
	}
	
	public String getFormData() {
		return formData;
	}

	public void setFormData(String formData) {
		this.formData = formData;
	}

	public ArrayList<TaskListDTO> getTaskHistory() {
		return taskHistory;
	}

	public void setTaskHistory(ArrayList<TaskListDTO> taskHistory) {
		this.taskHistory = taskHistory;
	}

}
