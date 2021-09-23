package com.zanflow.bpmn.dto.master;



import com.fasterxml.jackson.annotation.JsonInclude;
import com.zanflow.bpmn.dto.ResponseDTO;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MasterSchemaDTO extends ResponseDTO{
	
    private static final long serialVersionUID = 1L;
	

	protected String companycode;
	protected String mastername;
	protected String metadata;
	

	public MasterSchemaDTO(String companycode, String mastername, String metadata) {
		super();
		this.companycode = companycode;
		this.mastername = mastername;
		this.metadata = metadata;
	}


	public MasterSchemaDTO(){}


	public String getCompanycode() {
		return companycode;
	}


	public void setCompanycode(String companycode) {
		this.companycode = companycode;
	}


	public String getMastername() {
		return mastername;
	}


	public void setMastername(String mastername) {
		this.mastername = mastername;
	}


	public String getMetadata() {
		return metadata;
	}


	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}
	


}
