package com.zanflow.bam.dto.process;

import java.io.Serializable;

public class StepFieldDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected String FIELDNAME;
	protected String REQUIRED;
	protected String LABEL;
	protected String EDITABLE;
	protected String VISIBLE;
	
	
	public String getFIELDNAME() {
		return FIELDNAME;
	}
	public void setFIELDNAME(String fIELDNAME) {
		FIELDNAME = fIELDNAME;
	}
	public String getREQUIRED() {
		return REQUIRED;
	}
	public void setREQUIRED(String rEQUIRED) {
		REQUIRED = rEQUIRED;
	}
	public String getLABEL() {
		return LABEL;
	}
	public void setLABEL(String lABEL) {
		LABEL = lABEL;
	}
	public String getEDITABLE() {
		return EDITABLE;
	}
	public void setEDITABLE(String eDITABLE) {
		EDITABLE = eDITABLE;
	}
	public String getVISIBLE() {
		return VISIBLE;
	}
	public void setVISIBLE(String vISIBLE) {
		VISIBLE = vISIBLE;
	}
	
	
}
