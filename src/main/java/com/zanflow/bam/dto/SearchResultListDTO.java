package com.zanflow.bam.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class SearchResultListDTO extends ResponseDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected List<Map<String,Object>> resultList;
	protected List<String> columns;
	public List<Map<String, Object>> getResultList() {
		return resultList;
	}
	public void setResultList(List<Map<String, Object>> resultList) {
		this.resultList = resultList;
	}
	public List<String> getColumns() {
		return columns;
	}
	public void setColumns(List<String> columns) {
		this.columns = columns;
	}
	@Override
	public String toString() {
		return "SearchResultListDTO [resultList=" + resultList + ", columns=" + columns + "]";
	}

	
	

}
