package com.zanflow.sec.common.db;

import com.zanflow.sec.common.exception.ApplicationException;
import com.zanflow.sec.common.exception.JPAIllegalStateException;
import com.zanflow.sec.common.exception.JPAPersistenceException;


public abstract class JPATransaction  implements java.lang.AutoCloseable{

	protected JPersistenceProvider objJProvider = null;
	protected String strJPUnitName = null;
	public void begin(){
		objJProvider.begin();
	}

	public void commit() throws JPAPersistenceException,JPAIllegalStateException{		
		objJProvider.commit();
	}

	public void rollback(){
		objJProvider.rollback();
	}

	public boolean isActive(){
		return objJProvider.isActive();
	}

	
	public boolean isOpen(){
		return objJProvider.isOpen();
	}
	public void close() throws ApplicationException {
		try {
			if(objJProvider != null && objJProvider.isOpen() && objJProvider.getpUnitName() != null) {	
			    objJProvider.close(objJProvider.getpUnitName());
			}
		} 
		catch (Exception e) 
		{
			if(objJProvider != null && objJProvider.isActive() && objJProvider.getpUnitName() != null)
			{
				try
				{
					 objJProvider.rollback();
					 objJProvider.close(objJProvider.getpUnitName());
				}
				catch (Exception ex) 
				{
					ex.printStackTrace();
				}
			}
			else
			{
				e.printStackTrace();
			}
		}
	}
	public void close(String strjunit) throws JPAIllegalStateException {
		try {
			close();
		} catch (ApplicationException e) {
			e.printStackTrace();
		}
	}
	
	public JPersistenceProvider getObjJProvider() {
		return objJProvider;
	}

	public String getStrJPUnitName() {
		return strJPUnitName;
	}

	public void setStrJPUnitName(String strJPUnitName) {
		this.strJPUnitName = strJPUnitName;
	}
}
