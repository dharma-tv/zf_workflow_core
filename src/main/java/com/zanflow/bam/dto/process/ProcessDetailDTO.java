package com.zanflow.bam.dto.process;

import java.io.Serializable;
import java.util.List;

public class ProcessDetailDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected String PROCESSID;
	protected String NAME;
	protected String DESCRIPTION;
	protected String COUNTRYCODE;
	protected String BPMNID;
	
	protected List<FieldDTO> fields;
	protected List<ResponseListDTO> response;
	protected List<StepDTO> steps;
	protected List<ChoiceListDTO> choices;
	
	
	public String getPROCESSID() {
		return PROCESSID;
	}
	public void setPROCESSID(String pROCESSID) {
		PROCESSID = pROCESSID;
	}
	public String getNAME() {
		return NAME;
	}
	public void setNAME(String nAME) {
		NAME = nAME;
	}
	public String getDESCRIPTION() {
		return DESCRIPTION;
	}
	public void setDESCRIPTION(String dESCRIPTION) {
		DESCRIPTION = dESCRIPTION;
	}
	public String getCOUNTRYCODE() {
		return COUNTRYCODE;
	}
	public void setCOUNTRYCODE(String cOUNTRYCODE) {
		COUNTRYCODE = cOUNTRYCODE;
	}
	public String getBPMNID() {
		return BPMNID;
	}
	public void setBPMNID(String bPMNID) {
		BPMNID = bPMNID;
	}
	public List<FieldDTO> getFields() {
		return fields;
	}
	public void setFields(List<FieldDTO> fields) {
		this.fields = fields;
	}
	public List<ResponseListDTO> getResponse() {
		return response;
	}
	public void setResponse(List<ResponseListDTO> response) {
		this.response = response;
	}
	public List<StepDTO> getSteps() {
		return steps;
	}
	public void setSteps(List<StepDTO> steps) {
		this.steps = steps;
	}
	public List<ChoiceListDTO> getChoices() {
		return choices;
	}
	public void setChoices(List<ChoiceListDTO> choices) {
		this.choices = choices;
	}
	
	

}
