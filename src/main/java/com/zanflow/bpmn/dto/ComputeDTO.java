package com.zanflow.bpmn.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ComputeDTO extends ResponseDTO implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String processdata;
	private ArrayList<Map<String,String>> computeFormulas;
	
	public ComputeDTO(){}
	
	public ComputeDTO(String processdata, ArrayList<Map<String, String>> computeFormulas) {
		super();
		this.processdata = processdata;
		this.computeFormulas = computeFormulas;
	}
	


	public String getProcessdata() {
		return processdata;
	}

	public void setProcessdata(String processdata) {
		this.processdata = processdata;
	}

	public ArrayList<Map<String, String>> getComputeFormulas() {
		return computeFormulas;
	}
	public void setComputeFormulas(ArrayList<Map<String, String>> computeFormulas) {
		this.computeFormulas = computeFormulas;
	}
	
}
