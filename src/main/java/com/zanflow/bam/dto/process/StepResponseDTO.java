package com.zanflow.bam.dto.process;

import java.io.Serializable;

public class StepResponseDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected String LABEL;
	protected String COMMENTS;
	protected String RESPONSENAME;
	
	public String getLABEL() {
		return LABEL;
	}
	public void setLABEL(String lABEL) {
		LABEL = lABEL;
	}
	public String getCOMMENTS() {
		return COMMENTS;
	}
	public void setCOMMENTS(String cOMMENTS) {
		COMMENTS = cOMMENTS;
	}
	public String getRESPONSENAME() {
		return RESPONSENAME;
	}
	public void setRESPONSENAME(String rESPONSENAME) {
		RESPONSENAME = rESPONSENAME;
	}
	
	

}
