package com.zanflow.kanban.dto;



import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.zanflow.bpmn.dto.ResponseDTO;
import com.zanflow.bpmn.dto.TaskDTO;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LanesDTO extends ResponseDTO{
	
    private static final long serialVersionUID = 1L;
	
	private String id;
	private String title;
	private String label;
	private String style;
	private ArrayList<TaskDTO> cards;

	public LanesDTO(){}

	public LanesDTO(String id, String title, String label, String style) {
		super();
		this.id = id;
		this.title = title;
		this.label = label;
		this.style = style;
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

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public ArrayList<TaskDTO> getCards() {
		return cards;
	}

	public void setCards(ArrayList<TaskDTO> cards) {
		this.cards = cards;
	}
	


}
