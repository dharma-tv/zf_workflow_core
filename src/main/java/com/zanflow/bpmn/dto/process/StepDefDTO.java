package com.zanflow.bpmn.dto.process;

import java.io.Serializable;

public class StepDefDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected int TAT;

	protected String ESCALATIONFLAG;
	protected String NOTIFICATIONFLAG;
	protected String AssignmentType;
	protected String LABEL;
	protected String BPMNSTEPTYPE;
	protected String id;
	protected String NAME;
	
	public int getTAT() {
		return TAT;
	}
	public void setTAT(int tAT) {
		TAT = tAT;
	}
	public String getESCALATIONFLAG() {
		return ESCALATIONFLAG;
	}
	public void setESCALATIONFLAG(String eSCALATIONFLAG) {
		ESCALATIONFLAG = eSCALATIONFLAG;
	}
	public String getNOTIFICATIONFLAG() {
		return NOTIFICATIONFLAG;
	}
	public void setNOTIFICATIONFLAG(String nOTIFICATIONFLAG) {
		NOTIFICATIONFLAG = nOTIFICATIONFLAG;
	}
	public String getAssignmentType() {
		return AssignmentType;
	}
	public void setAssignmentType(String assignmentType) {
		AssignmentType = assignmentType;
	}
	public String getLABEL() {
		return LABEL;
	}
	public void setLABEL(String lABEL) {
		LABEL = lABEL;
	}
	public String getBPMNSTEPTYPE() {
		return BPMNSTEPTYPE;
	}
	public void setBPMNSTEPTYPE(String bPMNSTEPTYPE) {
		BPMNSTEPTYPE = bPMNSTEPTYPE;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getNAME() {
		return NAME;
	}
	public void setNAME(String nAME) {
		NAME = nAME;
	}
	
	
}
