package com.zanflow.sec.common.exception;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;


import javax.xml.stream.XMLStreamException;

public class ApplicationException extends Exception {
	private String errorCode;
	private String errorType;
	private String message;
	private int errCode;
	
	final static long serialVersionUID = 20060103001L;
	public ApplicationException() {
		super();
	}
	public ApplicationException(String msg) {
		message = msg;
	}
	public ApplicationException(String errorType, String errorCode) {
		this.errorType = errorType;
		this.errorCode = errorCode;
	}
	
	public ApplicationException(String message,int errorCode){
		//	ExceptionPropertiesUtil.getException(errorCode);
			this.message    = message;
			this.errCode  = errorCode;
		}
	
	public ApplicationException(String message,int errorCode,String errorType){
		this.errorType  = errorType;
		this.errCode  = errorCode;
		this.message    = message;
	}
	//ends here
	
	
	public ApplicationException(String errorType, String errorCode, String msg) {
		this.message = msg;
		this.errorType = errorType;
		this.errorCode = errorCode;
	}
	public ApplicationException(Exception e) {
		message=e.getMessage();
	}
	
	public ApplicationException(Exception e,String msg) {
		message=msg+"#"+e.getMessage();
	}
	public ApplicationException(SecurityException se) {
		this(se.getMessage());
		Exception ie = se;
		if (ie instanceof ApplicationException) {
			this.errorCode = ((ApplicationException) ie).errorCode;
			this.errorType = ((ApplicationException) ie).errorType;
		} }
	public ApplicationException(XMLStreamException xse) {
		this(xse.getMessage());
		Exception ie = xse;
		if (ie instanceof ApplicationException) {
			this.errorCode = ((ApplicationException) ie).errorCode;
			this.errorType = ((ApplicationException) ie).errorType;
		} }
	public ApplicationException(IOException ioe) {
		this(ioe.getMessage());
		Exception ie = ioe;
		if (ie instanceof ApplicationException) {
			this.errorCode = ((ApplicationException) ie).errorCode;
			this.errorType = ((ApplicationException) ie).errorType;
		} }
	public ApplicationException(ClassNotFoundException cnfe) {
		this(cnfe.getMessage());
		Exception ie = cnfe;
		if (ie instanceof ApplicationException) {
			this.errorCode = ((ApplicationException) ie).errorCode;
			this.errorType = ((ApplicationException) ie).errorType;
		} }
	public ApplicationException(NoSuchMethodException nsme) {
		this(nsme.getMessage());
		Exception ie = nsme;
		if (ie instanceof ApplicationException) {
			this.errorCode = ((ApplicationException) ie).errorCode;
			this.errorType = ((ApplicationException) ie).errorType;
		} }
	public ApplicationException(IllegalAccessException iae) {
		this(iae.getMessage());
		Exception ie = iae;
		if (ie instanceof ApplicationException) {
			this.errorCode = ((ApplicationException) ie).errorCode;
			this.errorType = ((ApplicationException) ie).errorType;
		} }
		public ApplicationException(InstantiationException ine) {
			this(ine.getMessage());
			Exception ie = ine;
			if (ie instanceof ApplicationException) {
				this.errorCode = ((ApplicationException) ie).errorCode;
				this.errorType = ((ApplicationException) ie).errorType;
			} 
		}
			public ApplicationException(IllegalArgumentException iae) {
				this(iae.getMessage());
				Exception ie = iae;
				if (ie instanceof ApplicationException) {
					this.errorCode = ((ApplicationException) ie).errorCode;
					this.errorType = ((ApplicationException) ie).errorType;
				} 
			}

	public ApplicationException(InvocationTargetException ite) {
		this(ite.getMessage());
		Exception ie = ite;
		if (ie instanceof ApplicationException) {
			this.errorCode = ((ApplicationException) ie).errorCode;
			this.errorType = ((ApplicationException) ie).errorType;
		} 
	}
	
	

	public java.lang.String getErrorCode() {
		return errorCode;
	}
	public java.lang.String getErrorType() {
		return errorType;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}
	public String getMessage() {
		if (message == null)
			return super.getMessage();
		return message;
	}
	public String toString() {
		return "Application Exception: " + errorType + ", " + errorCode + ", " + getMessage();
	}

	public int getErrCode()

      {

            return errCode;

      }

      public void setErrCode(int errCode)

      {

            this.errCode = errCode;

      }


}
