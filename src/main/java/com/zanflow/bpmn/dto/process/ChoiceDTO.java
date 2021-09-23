package com.zanflow.bpmn.dto.process;

import java.io.Serializable;

public class ChoiceDTO implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected String label;
	protected String value;
	
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	
	
}
