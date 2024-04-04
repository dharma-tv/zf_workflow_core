package com.zanflow.bam.dto.process;

import java.io.Serializable;

public class FieldDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected String FIELDTYPENAME;
	protected String SEARCHPARAMNAME;
	protected String DEFAULTVALUE;
	protected String BUSINESSFIELDTEMPLATE;
	protected String CHOICENAME;
	protected String NAME;
	protected String VALIDATIONEXPR;
	protected String GRIDFIELDVISIBILITY;
	protected String UIFIELDTEMPLATENAME;
	protected String GRIDNAME;
	protected String LABEL;
	protected String CHOICELIST;
	
	protected int LENGTH;
	protected int FLDCAT;
	public String getFIELDTYPENAME() {
		return FIELDTYPENAME;
	}
	public void setFIELDTYPENAME(String fIELDTYPENAME) {
		FIELDTYPENAME = fIELDTYPENAME;
	}
	public String getSEARCHPARAMNAME() {
		return SEARCHPARAMNAME;
	}
	public void setSEARCHPARAMNAME(String sEARCHPARAMNAME) {
		SEARCHPARAMNAME = sEARCHPARAMNAME;
	}
	public String getDEFAULTVALUE() {
		return DEFAULTVALUE;
	}
	public void setDEFAULTVALUE(String dEFAULTVALUE) {
		DEFAULTVALUE = dEFAULTVALUE;
	}
	public String getBUSINESSFIELDTEMPLATE() {
		return BUSINESSFIELDTEMPLATE;
	}
	public void setBUSINESSFIELDTEMPLATE(String bUSINESSFIELDTEMPLATE) {
		BUSINESSFIELDTEMPLATE = bUSINESSFIELDTEMPLATE;
	}
	public String getCHOICENAME() {
		return CHOICENAME;
	}
	public void setCHOICENAME(String cHOICENAME) {
		CHOICENAME = cHOICENAME;
	}
	public String getNAME() {
		return NAME;
	}
	public void setNAME(String nAME) {
		NAME = nAME;
	}
	public String getVALIDATIONEXPR() {
		return VALIDATIONEXPR;
	}
	public void setVALIDATIONEXPR(String vALIDATIONEXPR) {
		VALIDATIONEXPR = vALIDATIONEXPR;
	}
	public String getGRIDFIELDVISIBILITY() {
		return GRIDFIELDVISIBILITY;
	}
	public void setGRIDFIELDVISIBILITY(String gRIDFIELDVISIBILITY) {
		GRIDFIELDVISIBILITY = gRIDFIELDVISIBILITY;
	}
	public String getUIFIELDTEMPLATENAME() {
		return UIFIELDTEMPLATENAME;
	}
	public void setUIFIELDTEMPLATENAME(String uIFIELDTEMPLATENAME) {
		UIFIELDTEMPLATENAME = uIFIELDTEMPLATENAME;
	}
	public String getGRIDNAME() {
		return GRIDNAME;
	}
	public void setGRIDNAME(String gRIDNAME) {
		GRIDNAME = gRIDNAME;
	}
	public String getLABEL() {
		return LABEL;
	}
	public void setLABEL(String lABEL) {
		LABEL = lABEL;
	}
	public String getCHOICELIST() {
		return CHOICELIST;
	}
	public void setCHOICELIST(String cHOICELIST) {
		CHOICELIST = cHOICELIST;
	}
	public int getLENGTH() {
		return LENGTH;
	}
	public void setLENGTH(int lENGTH) {
		LENGTH = lENGTH;
	}
	public int getFLDCAT() {
		return FLDCAT;
	}
	public void setFLDCAT(int fLDCAT) {
		FLDCAT = fLDCAT;
	}
	
	

}
