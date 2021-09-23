package com.zanflow.bpmn.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

/**
 * @author 1267078
 *
 */
public class BPMNCompleterResultDTO extends ResponseDTO implements Serializable
{
	private static final long serialVersionUID = 1L;
	private ArrayList<BPMNStepDTO> bpmnNextSteps;
	private ArrayList<BPMNStepDTO> bpmnScriptSteps=new ArrayList<BPMNStepDTO>();
	private Map<String,Object> dataMap;
	
	private String bpmnTxRefNo;
	
	

	public String getBpmnTxRefNo() {
		return bpmnTxRefNo;
	}

	public void setBpmnTxRefNo(String bpmnTxRefNo) {
		this.bpmnTxRefNo = bpmnTxRefNo;
	}

	public Map<String, Object> getDataMap() {
		return dataMap;
	}
	
	public void setDataMap(Map<String,Object> dataMap) {
		this.dataMap = dataMap;
	}
	
	public ArrayList<BPMNStepDTO> getBpmnNextSteps() {
		return bpmnNextSteps;
	}
	
	public void setBpmnNextSteps(ArrayList<BPMNStepDTO> bpmnNextSteps) {
		this.bpmnNextSteps = bpmnNextSteps;
	}

	public ArrayList<BPMNStepDTO> getBpmnScriptSteps() {
		return bpmnScriptSteps;
	}

	public void setBpmnScriptSteps(ArrayList<BPMNStepDTO> bpmnScriptSteps) {
		this.bpmnScriptSteps = bpmnScriptSteps;
	}
	
	

}
