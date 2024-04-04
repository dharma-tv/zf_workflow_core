package com.zanflow.bpmn.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity(name="BPMNTask")
@Table(name="zf_txn_bpmntask")
public class BPMNTask implements Serializable
{
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="bpmntaskseq")
	@SequenceGenerator(name="bpmntaskseq", sequenceName="bpmntaskseq", allocationSize=1)
	protected long bpmnTaskId;
	protected String bpmnTxRefNo;
//	protected String processId;
	protected String companyCode;
	@Column(name ="steplabel")
	protected String stepLabel;
	protected String selectedResponse;
	protected String elementId;
	protected String elementType;
	protected String bpmnId;
	protected String callActivityToken;
	protected String gateWayToken;
	protected int statusCode;
	protected Timestamp taskCreatedDate;
	protected Timestamp dueDate;
	protected String lockedUser;
	protected String completedBy;
	protected String assignedUser;
	protected String assignedRole;
	protected String taskSubject;
	protected String priority;
	protected Timestamp taskCompleteDate;
	protected Timestamp lastModifiedDate;
	
	public Timestamp getTaskCompleteDate() {
		return taskCompleteDate;
	}
	public void setTaskCompleteDate(Timestamp taskCompleteDate) {
		this.taskCompleteDate = taskCompleteDate;
	}
	public Timestamp getLastModifiedDate() {
		return lastModifiedDate;
	}
	public void setLastModifiedDate(Timestamp lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	public String getCompanyCode() {
		return companyCode;
	}
	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}
	
	public Timestamp getTaskCreatedDate() {
		return taskCreatedDate;
	}
	public void setTaskCreatedDate(Timestamp taskCreatedDate) {
		this.taskCreatedDate = taskCreatedDate;
	}
	public Timestamp getDueDate() {
		return dueDate;
	}
	public void setDueDate(Timestamp dueDate) {
		this.dueDate = dueDate;
	}
	public String getLockedUser() {
		return lockedUser;
	}
	public void setLockedUser(String lockedUser) {
		this.lockedUser = lockedUser;
	}
	public long getBpmnTaskId() {
		return bpmnTaskId;
	}
	public void setBpmnTaskId(long bpmnTaskId) {
		this.bpmnTaskId = bpmnTaskId;
	}
	public String getBpmnTxRefNo() {
		return bpmnTxRefNo;
	}
	public void setBpmnTxRefNo(String bpmnTxRefNo) {
		this.bpmnTxRefNo = bpmnTxRefNo;
	}
//	public String getProcessId() {
//		return processId;
//	}
//	public void setProcessId(String processId) {
//		this.processId = processId;
//	}
//	public String getCountryCode() {
//		return countryCode;
//	}
//	public void setCountryCode(String countryCode) {
//		this.countryCode = countryCode;
//	}
//	public String getStepName() {
//		return stepName;
//	}
//	public void setStepName(String stepName) {
//		this.stepName = stepName;
//	}
	public String getSelectedResponse() {
		return selectedResponse;
	}
	public void setSelectedResponse(String selectedResponse) {
		this.selectedResponse = selectedResponse;
	}
	public int getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	
	
	public void setBpmnId(String bpmnId)
	{
		this.bpmnId=bpmnId;
	}
	
	
	public String getBpmnId()
	{
		return this.bpmnId;
	}
	
	
	public void setCallActivityToken(String callActivityToken)
	{
		this.callActivityToken=callActivityToken;
	}
	
	
	public String getCallActivityToken()
	{
		return this.callActivityToken;
	}

	
	public void setGateWayToken(String gateWayToken) {
		this.gateWayToken=gateWayToken;			
	}

	
	public String getGateWayToken() {
		return this.gateWayToken;
	}
	public String getElementId() {
		return elementId;
	}
	public void setElementId(String elementId) {
		this.elementId = elementId;
	}
	public String getElementType() {
		return elementType;
	}
	public void setElementType(String elementType) {
		this.elementType = elementType;
	}
	
	public String getAssignedUser() {
		return assignedUser;
	}
	public void setAssignedUser(String assignedUser) {
		this.assignedUser = assignedUser;
	}
	public String getAssignedRole() {
		return assignedRole;
	}
	public void setAssignedRole(String assignedRole) {
		this.assignedRole = assignedRole;
	}
	public String getTaskSubject() {
		return taskSubject;
	}
	public void setTaskSubject(String taskSubject) {
		this.taskSubject = taskSubject;
	}
	public String getStepLabel() {
		return stepLabel;
	}
	public void setStepLabel(String stepLabel) {
		this.stepLabel = stepLabel;
	}
	public String getCompletedBy() {
		return completedBy;
	}
	public void setCompletedBy(String completedBy) {
		this.completedBy = completedBy;
	}
	
	public String getPriority() {
		return priority;
	}
	public void setPriority(String priority) {
		this.priority = priority;
	}
}
