package com.zanflow.bam.dto.process;

import java.io.Serializable;
import java.util.List;

public class StepDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected StepDefDTO stepDefinition;
	
	protected List<StepFieldDTO> stepFields;

	protected List<StepResponseDTO> stepResponses;

	public StepDefDTO getStepDefinition() {
		return stepDefinition;
	}

	public void setStepDefinition(StepDefDTO stepDefinition) {
		this.stepDefinition = stepDefinition;
	}

	public List<StepFieldDTO> getStepFields() {
		return stepFields;
	}

	public void setStepFields(List<StepFieldDTO> stepFields) {
		this.stepFields = stepFields;
	}

	public List<StepResponseDTO> getStepResponses() {
		return stepResponses;
	}

	public void setStepResponses(List<StepResponseDTO> stepResponses) {
		this.stepResponses = stepResponses;
	}
	
	

}
