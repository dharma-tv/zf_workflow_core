package com.zanflow.sec.common.exception;



public class JPAIllegalStateException extends ApplicationException {
	private static final long serialVersionUID = 1L;
	public JPAIllegalStateException(){
		super();
	}
	public JPAIllegalStateException(String message){
		super(message);
	}
	public JPAIllegalStateException(String message,int errorCode){
		super(message,errorCode);
	}
	public JPAIllegalStateException(String message,int errorCode,String errorType){
		super(message,errorCode,errorType);
	}


}
