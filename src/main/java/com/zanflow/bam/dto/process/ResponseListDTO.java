package com.zanflow.bam.dto.process;

import java.io.Serializable;

public class ResponseListDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected String LABEL;
	protected String EXECUTERULES;
	protected String NAME;
	
	public String getLABEL() {
		return LABEL;
	}
	public void setLABEL(String lABEL) {
		LABEL = lABEL;
	}
	public String getEXECUTERULES() {
		return EXECUTERULES;
	}
	public void setEXECUTERULES(String eXECUTERULES) {
		EXECUTERULES = eXECUTERULES;
	}
	public String getNAME() {
		return NAME;
	}
	public void setNAME(String nAME) {
		NAME = nAME;
	}
	

}
