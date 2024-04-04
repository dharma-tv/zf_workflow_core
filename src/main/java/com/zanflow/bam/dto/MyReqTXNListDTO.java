package com.zanflow.bam.dto;

import java.io.Serializable;
import java.util.List;

public class MyReqTXNListDTO extends ResponseDTO implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected List<MyReqDTO> myReqList=null;

	public List<MyReqDTO> getMyReqList() {
		return myReqList;
	}

	public void setMyReqList(List<MyReqDTO> myReqList) {
		this.myReqList = myReqList;
	}
	
	

}
