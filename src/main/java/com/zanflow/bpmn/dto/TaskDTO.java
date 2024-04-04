package com.zanflow.bpmn.dto;



import java.util.ArrayList;
import java.util.List;

import javax.persistence.Convert;

import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskDTO extends ResponseDTO{
	
    private static final long serialVersionUID = 1L;
	
	private long taskId;
	private long previoustaskId;
	private String refId;
	private String taskName;
	private String processName;
	private String taskSubject;
	private String createdDate;
	private String dueDate;
	private String lockedUser;
	private String completedBy;
	private String bpmnId;
	private String formData;
	private ArrayList<TaskDTO> taskHistory;
	private ArrayList<CommentsDTO> comments;
	protected List<TXNDocumentDTO> txnDocList=null;
	protected int statusCode;
	private String stepLabel;
	private String taskType;
	private String assigneduser;
	private String reporter;
	private String priority;
	private String lastModifiedDate;
	private String completedDate;
	private String selectedResponse;

	

	public TaskDTO(){}
	

	public TaskDTO(long taskId, String refId, String taskName, String processName, String taskSubject,
			String createdDate, String dueDate, String lockedUser, String bpmnId,String companyCode) {
		super();
		this.taskId = taskId;
		this.refId = refId;
		this.taskName = taskName;
		this.processName = processName;
		this.taskSubject = taskSubject;
		this.createdDate = createdDate;
		this.dueDate = dueDate;
		this.lockedUser = lockedUser;
		this.bpmnId = bpmnId;
		this.companyCode=companyCode;
	}
	
	private String companyCode;
	
	public String getCompanyCode() {
		return companyCode;
	}


	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}


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

	public ArrayList<TaskDTO> getTaskHistory() {
		return taskHistory;
	}

	public void setTaskHistory(ArrayList<TaskDTO> taskHistory) {
		this.taskHistory = taskHistory;
	}


	public ArrayList<CommentsDTO> getComments() {
		return comments;
	}


	public void setComments(ArrayList<CommentsDTO> comments) {
		this.comments = comments;
	}
	
	

	public List<TXNDocumentDTO> getTxnDocList() {
		return txnDocList;
	}

	public void setTxnDocList(List<TXNDocumentDTO> txnDocList) {
		this.txnDocList = txnDocList;
	}


	public int getStatusCode() {
		return statusCode;
	}


	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}


	public String getStepLabel() {
		return stepLabel;
	}


	public void setStepLabel(String stepLabel) {
		this.stepLabel = stepLabel;
	}


	public long getPrevioustaskId() {
		return previoustaskId;
	}


	public void setPrevioustaskId(long previoustaskId) {
		this.previoustaskId = previoustaskId;
	}


	public String getCompletedBy() {
		return completedBy;
	}


	public void setCompletedBy(String completedBy) {
		this.completedBy = completedBy;
	}
	
	public String getTaskType() {
		return taskType;
	}

	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}
	
	public String getAssigneduser() {
		return assigneduser;
	}


	public void setAssigneduser(String assigneduser) {
		this.assigneduser = assigneduser;
	}


	public String getReporter() {
		return reporter;
	}


	public void setReporter(String reporter) {
		this.reporter = reporter;
	}


	public String getPriority() {
		return priority;
	}


	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(String lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public String getCompletedDate() {
		return completedDate;
	}

	public void setCompletedDate(String completedDate) {
		this.completedDate = completedDate;
	}
	
	public String getSelectedResponse() {
		return selectedResponse;
	}

	public void setSelectedResponse(String selectedResponse) {
		this.selectedResponse = selectedResponse;
	}



}
