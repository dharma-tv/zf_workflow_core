package com.zanflow.sec.common.exception;


public class JPAPersistenceException extends ApplicationException {
	private static final long serialVersionUID = 1L;
	public JPAPersistenceException(){
		super();
	}
	public JPAPersistenceException(String message){
		super(message);
	}
	public JPAPersistenceException(String message,int errorCode){
		super(message,errorCode);
	}
	public JPAPersistenceException(String message,int errorCode,String errorType){
		super(message,errorCode,errorType);
	}


}
