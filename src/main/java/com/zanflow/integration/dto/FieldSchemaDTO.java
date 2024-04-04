package com.zanflow.integration.dto;

public class FieldSchemaDTO {
	
   
	private static final long serialVersionUID = 1L;
	
    private String key;
	private String label;
	private String type;
	private boolean required;
	private String[] choices;
	private boolean list;
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	public String[] getChoices() {
		return choices;
	}
	public void setChoices(String[] choices) {
		this.choices = choices;
	}
	public boolean isRequired() {
		return required;
	}
	public void setRequired(boolean required) {
		this.required = required;
	}
	public boolean isList() {
		return list;
	}
	public void setList(boolean list) {
		this.list = list;
	}

	
}
