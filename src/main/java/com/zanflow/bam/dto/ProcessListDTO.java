package com.zanflow.bam.dto;

import java.io.Serializable;
import java.util.List;

public class ProcessListDTO extends ResponseDTO implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected List<ProcessDTO> processListDTO;

	public List<ProcessDTO> getProcessListDTO() {
		return processListDTO;
	}

	public void setProcessListDTO(List<ProcessDTO> processListDTO) {
		this.processListDTO = processListDTO;
	}
	
	

}
