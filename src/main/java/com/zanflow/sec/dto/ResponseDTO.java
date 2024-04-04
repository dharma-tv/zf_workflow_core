package com.zanflow.sec.dto;

import java.io.Serializable;
import java.util.List;

public class ResponseDTO implements Serializable{

	String responsMsg;
	String responseCode;
	List<ResponseDTO> listRes;
	
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
	public List<ResponseDTO> getListRes() {
		return listRes;
	}
	public void setListRes(List<ResponseDTO> listRes) {
		this.listRes = listRes;
	}
	
	
}
