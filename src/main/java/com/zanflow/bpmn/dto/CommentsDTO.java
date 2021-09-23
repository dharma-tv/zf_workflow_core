package com.zanflow.bpmn.dto;



import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentsDTO extends ResponseDTO{
	
    private static final long serialVersionUID = 1L;
	
	private long taskId;
	private String refId;
	private String createdBy;
	private String createdDate;
	private String comments;
	private String stepName;
	private int commentSeq;
	private String companyCode;
	
	
	public CommentsDTO(){}
	

	public CommentsDTO(long taskId, String refId, String stepName, String comments, int commentsSeq) {
		super();
		this.taskId = taskId;
		this.refId = refId;
		this.stepName = stepName;
		this.comments = comments;
		this.commentSeq=commentsSeq;
		
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


	public String getComments() {
		return comments;
	}


	public void setComments(String comments) {
		this.comments = comments;
	}


	public String getStepName() {
		return stepName;
	}


	public void setStepName(String stepName) {
		this.stepName = stepName;
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

}
