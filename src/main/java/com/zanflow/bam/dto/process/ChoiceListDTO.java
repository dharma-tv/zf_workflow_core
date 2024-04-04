package com.zanflow.bam.dto.process;

import java.io.Serializable;
import java.util.List;

public class ChoiceListDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected String choiceName;
	protected List<ChoiceDTO> choiceList;
	
	public String getChoiceName() {
		return choiceName;
	}
	public void setChoiceName(String choiceName) {
		this.choiceName = choiceName;
	}
	public List<ChoiceDTO> getChoiceList() {
		return choiceList;
	}
	public void setChoiceList(List<ChoiceDTO> choiceList) {
		this.choiceList = choiceList;
	}
	
	

}
