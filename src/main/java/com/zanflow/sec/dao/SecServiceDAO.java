package com.zanflow.sec.dao;

import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;

import org.json.JSONObject;

import com.zanflow.sec.common.db.JPATransaction;
import com.zanflow.sec.common.db.JPersistenceProvider;
import com.zanflow.sec.common.exception.ApplicationException;
import com.zanflow.sec.common.exception.JPAPersistenceException;
import com.zanflow.sec.model.Role;

public class SecServiceDAO extends JPATransaction implements java.lang.AutoCloseable{
	
	public SecServiceDAO(String pJunitName)throws ApplicationException
	{
		//System.out.println("#UserMgmtDAO#pJunitName#"+pJunitName);
		try {
            if(pJunitName != null && pJunitName.trim().length() > 0){
           	 strJPUnitName = pJunitName;
            }
            else {
           	 throw new ApplicationException("Arguments are NULL, Unable to create JPersistenceProvider.");
            }
            //System.out.println("#pJunitName#"+strJPUnitName);
       	 	objJProvider = new JPersistenceProvider(strJPUnitName);
		} catch (ApplicationException |EntityNotFoundException |EntityExistsException e) {
			e.printStackTrace();
			throw new ApplicationException(new Exception(e));
		} 
	}	


	public SecServiceDAO(JPersistenceProvider objJProvider) throws ApplicationException
	{
		try{
			if (objJProvider != null)
			{
				this.objJProvider = objJProvider;
				strJPUnitName = objJProvider.getpUnitName();
				//System.out.println("# UserMgmtDAO objJProvider constructor invoked");
			}else{
				throw new ApplicationException("UserMgmtDAO : JPersistenceProvider is null");
			}
		} catch (EntityNotFoundException |EntityExistsException |JPAPersistenceException e) {
			e.printStackTrace();
			throw new ApplicationException(new Exception(e));
		}	
	}
	
	public boolean validateApiKey(String companyCode,String apikey)throws ApplicationException
	{
		long t1 = System.currentTimeMillis();
		//System.out.println("#SecServiceDAO#validateApiKey#"+companyCode);
		List<Role> roleList=null;
		try
		{
			Query objQuery=objJProvider.createQuery("select t from AppIntegrationModel t where companycode=:companyCode and apiauthkey=:apikey");
			objQuery.setParameter("companyCode", companyCode);
			objQuery.setParameter("apikey", apikey);
			roleList=objQuery.getResultList();
			if (roleList.size()>0) {
				return true;
			}else {
				throw new ApplicationException("Not valid API key or Account ID ");
			}
		}
		catch(Exception ex)
		{
			//ex.printStackTrace();
			throw new ApplicationException(ex);
		}
		finally
		{
			//System.out.println("#SecServiceDAO#validateApiKey#" + (System.currentTimeMillis() - t1));
		}
	  }
	
	public String genAPIkey(String companyCode, String apikey) throws ApplicationException {

		long t1 = System.currentTimeMillis();
		int count=0;
	    String message="API KEY generated successfully";
	
	    
		try {
			objJProvider.begin();
			objJProvider.createNativeQuery("delete from {h-schema}zf_id_appintegrationdetails where companycode=:companyCode").setParameter("companyCode", companyCode).executeUpdate();
			count = objJProvider.createNativeQuery("insert into {h-schema}zf_id_appintegrationdetails (companycode, apiauthkey, validityseconds, processid, action, integrationapp) values (:companyCode, :apikey, -1 , 'ALL', 'ALL', 'ANY')").setParameter("companyCode", companyCode).setParameter("apikey", apikey).executeUpdate();
			objJProvider.commit();
			if(count == 0) {
				throw new ApplicationException();
			}
			}	
		catch (Exception e) {
			e.printStackTrace();
			//log.printErrorMessage(e);
			throw e;
		}
		return message;
	
	}
	
	public String getAPIkey(String companyCode) throws ApplicationException {
	   
	    List<Object[]> obj=null;
	    
		try {
			obj = objJProvider.createNativeQuery("select apiauthkey, companycode from {h-schema}zf_id_appintegrationdetails where companycode=:companycode").setParameter("companycode", companyCode).getResultList();
			if(obj.size()>0) {
				return String.valueOf(obj.get(0)[0]);
			}else {
				throw new ApplicationException();
			}
			}	
		catch (Exception e) {
			e.printStackTrace();
			//log.printErrorMessage(e);
			throw e;
		}
	
	}

}
