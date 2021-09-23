package com.zanflow.kanban.dto;



import com.fasterxml.jackson.annotation.JsonInclude;
import com.zanflow.bpmn.dto.ResponseDTO;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CardDTO extends ResponseDTO{
	
    private static final long serialVersionUID = 1L;
	
    private String id;
	private String title;
	private String label;
	private String description;
	private String jsonschema;
	private String uischema;
	private String formdata;
	
	public CardDTO(){}
	
	public CardDTO(String id, String title, String label, String description) {
		super();
		this.id = id;
		this.title = title;
		this.label = label;
		this.description = description;
	}



	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	

	

}
