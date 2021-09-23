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

@Entity(name="BPMNComments")
@Table(name="zf_txn_comments")
public class BPMNComments implements Serializable
{
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name ="bpmntaskid")
	private long taskId;

	@Id
	@Column(name ="bpmntxrefno")
	private String refId;
	
	@Column(name ="createdby")
	private String createdBy;
	
	@Column(name ="createddate")
	private String createdDate;
	
	@Id
	@Column(name ="commentseq")
	private int commentSeq;
	
	@Column(name ="companycode")
	protected String companyCode;
	
	@Column(name ="comments")
	private String comments;
	
	@Column(name ="stepname")
	private String stepName;

	protected String statuscode;

	public String getStepName() {
		return stepName;
	}

	public void setStepName(String stepName) {
		this.stepName = stepName;
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
	
	public String getCompanyCode() {
		return companyCode;
	}

	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}
	
	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public int getCommentSeq() {
		return commentSeq;
	}

	public void setCommentSeq(int commentSeq) {
		this.commentSeq = commentSeq;
	}

	public String getCompanycode() {
		return companyCode;
	}

	public void setCompanycode(String companyCode) {
		this.companyCode = companyCode;
	}

	public String getStatuscode() {
		return statuscode;
	}

	public void setStatuscode(String statuscode) {
		this.statuscode = statuscode;
	}
	
		
}
