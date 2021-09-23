package com.zanflow.bpmn.dto;

import java.io.Serializable;

public class ResponseDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String responsMsg;
	String responseCode;
	public ResponseDTO() {
	}
	public ResponseDTO(String msg,String code) {
		responsMsg = msg;
		responseCode=code;
	}
	public String getResponsMsg() {
		return responsMsg;
	}
	public void setResponsMsg(String responsMsg) {
		this.responsMsg = responsMsg;
	}
	public String getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}
	
}
