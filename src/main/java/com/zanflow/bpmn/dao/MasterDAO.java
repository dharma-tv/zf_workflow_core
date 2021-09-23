package com.zanflow.bpmn.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

import org.json.JSONArray;
import org.json.JSONObject;

import com.zanflow.bpmn.dto.master.MasterDataDTO;
import com.zanflow.bpmn.exception.ApplicationException;
import com.zanflow.bpmn.exception.JPAPersistenceException;
import com.zanflow.common.db.JPATransaction;
import com.zanflow.common.db.JPersistenceProvider;


public class MasterDAO extends JPATransaction{

	public MasterDAO(String pJunitName)throws ApplicationException
	{
		System.out.println("#ProcessDAO#pJunitName#"+pJunitName);
		try {
            if(pJunitName != null && pJunitName.trim().length() > 0){
           	 strJPUnitName = pJunitName;
            }
            else {
           	 throw new ApplicationException("Arguments are NULL, Unable to create JPersistenceProvider.");
            }
            System.out.println("#pJunitName#"+strJPUnitName);
       	 	objJProvider = new JPersistenceProvider(strJPUnitName);
		} catch (ApplicationException e) {
			e.printStackTrace();
			throw new ApplicationException(new Exception(e));
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
			throw new ApplicationException(new Exception(e));
		} catch (EntityExistsException e) {
			e.printStackTrace();
			throw new ApplicationException(new Exception(e));
		} 
	}	


	public MasterDAO(JPersistenceProvider objJProvider) throws ApplicationException
	{
		try{
			if (objJProvider != null)
			{
				this.objJProvider = objJProvider;
				strJPUnitName = objJProvider.getpUnitName();
				System.out.println("# ProcessDAO objJProvider constructor invoked");
			}else{
				throw new ApplicationException("ProcessDAO : JPersistenceProvider is null");
			}
		} catch (EntityNotFoundException e) {
			//log.printErrorMessage("# EntityNotFoundException #"+e.getMessage());
			e.printStackTrace();
			throw new ApplicationException(new Exception(e));
		} catch (EntityExistsException e) {
			//log.printErrorMessage("# EntityExistsException #"+e.getMessage());
			e.printStackTrace();
			throw new ApplicationException(new Exception(e));
		} catch (JPAPersistenceException e) {
			//log.printErrorMessage("# JPAPersistenceException #"+e.getMessage());
			e.printStackTrace();
			throw new ApplicationException(new Exception(e));
		}	
	}
	
	
	public String createMaster(String companyCode, String masterName, String metadata) throws ApplicationException {

		long t1 = System.currentTimeMillis();
		int count=0;
	    String message="Master created successfully";
	    
	    
		try {
			 count =  objJProvider.createNativeQuery("insert into {h-schema}zf_mstr_metadata (companycode, mastername, metadata) values (:1,:2,:3)")
					 .setParameter("1", companyCode)
					 .setParameter("2", masterName)
					 .setParameter("3", metadata)
					 .executeUpdate();
			if(count == 0) {
				message="Unable to create Master";
			}
			}	
		catch (Exception e) {
			e.printStackTrace();
			//log.printErrorMessage(e);
			message="Master already available";
			throw new ApplicationException(new Exception(e));
			
		}
		finally
		{
			System.out.println("#MasterDAO#createMaster#End#TT#"+ (System.currentTimeMillis() - t1));
		}
		return message;
	}
	
	
	public String updateMaster(String companyCode, String masterName, String metadata) throws ApplicationException {

		long t1 = System.currentTimeMillis();
		int count=0;
	    String message="Master updated successfully";
	    
	    
		try {
			 count =  objJProvider.createNativeQuery("update {h-schema}zf_mstr_metadata set metadata=:3  where companycode= :1 and mastername = :2")
					 .setParameter("1", companyCode)
					 .setParameter("2", masterName)
					 .setParameter("3", metadata)
					 .executeUpdate();
			if(count == 0) {
				message="Unable to update Master";
			}
			}	
		catch (Exception e) {
			e.printStackTrace();
			//log.printErrorMessage(e);
			message="Unable to update Master";
			throw new ApplicationException(new Exception(e));
			
		}
		finally
		{
			System.out.println("#MasterDAO#updateMaster#End#TT#"+ (System.currentTimeMillis() - t1));
		}
		return message;
	
	}
	
	public ArrayList<String> getMasterList(String companyCode) throws ApplicationException {

		long t1 = System.currentTimeMillis();
		int count=0;
	    ArrayList<String> masters = new ArrayList<String>();
		List obj=null;
		try {
			 obj=  objJProvider.createNativeQuery("select mastername from {h-schema}zf_mstr_metadata where companycode=:companyCode").setParameter("companyCode", companyCode).getResultList();
			System.out.println("obj "+obj);
			for(int i=0 ; i < obj.size() ; i++) {
				masters.add(obj.get(i).toString());
			}
		}	
		catch (Exception e) {
			e.printStackTrace();
			//log.printErrorMessage(e);
			throw new ApplicationException(new Exception(e));
		}
		finally
		{
			System.out.println("#MasterDAO#getMasterList#End#TT#"+ (System.currentTimeMillis() - t1));
		}
		return masters;
	
	}
	
	
	public Map<String,String> getMasterMetaData(String companyCode) throws ApplicationException {

		long t1 = System.currentTimeMillis();
	    HashMap<String, String> masters = new HashMap<String, String>();
	    List<Object[]> obj=null;
		try {
			 obj=  objJProvider.createNativeQuery("select mastername, CAST(metadata as TEXT) from {h-schema}zf_mstr_metadata where companycode=:companyCode").setParameter("companyCode", companyCode).getResultList();
			System.out.println("obj "+obj);
			for(int i=0 ; i < obj.size() ; i++) {
				masters.put(String.valueOf(obj.get(i)[0]),String.valueOf(obj.get(i)[1]));
			}
		}	
		catch (Exception e) {
			e.printStackTrace();
			//log.printErrorMessage(e);
			throw new ApplicationException(new Exception(e));
		}
		finally
		{
			System.out.println("#MasterDAO#getMasterMetaData#End#TT#"+ (System.currentTimeMillis() - t1));
		}
		return masters;
	
	}
	
	
	public JSONObject getMasterDetail(String companyCode, String mastername) throws ApplicationException {

		long t1 = System.currentTimeMillis();
	    JSONObject masters = new JSONObject();
		List obj=null;
		List dataobj = null;
		try {
			 obj=  objJProvider.createNativeQuery("select CAST(metadata as TEXT) from {h-schema}zf_mstr_metadata where companycode=:companyCode and mastername=:mastername").setParameter("companyCode", companyCode).setParameter("mastername", mastername).getResultList();
			System.out.println("obj "+obj);
			if(obj.size()>0) {
				JSONArray metadata = new JSONArray(obj.get(0).toString());
				masters.put("metadata", metadata);
			}
			
			dataobj=  objJProvider.createNativeQuery("select CAST(value as TEXT) from {h-schema}zf_mstr_data where companycode=:companyCode and mastername=:mastername").setParameter("companyCode", companyCode).setParameter("mastername", mastername).getResultList();
			JSONArray data = new JSONArray();
			if(dataobj!=null && dataobj.size()>0)
			for(int i=0; i < dataobj.size(); i++) {
				data.put(new JSONObject(dataobj.get(i).toString()));				
			}
			masters.put("data", data);
		}	
		catch (Exception e) {
			e.printStackTrace();
			//log.printErrorMessage(e);
			throw new ApplicationException(new Exception(e));
		}
		finally
		{
			System.out.println("#MasterDAO#getMasterDetail#End#TT#"+ (System.currentTimeMillis() - t1));
		}
		return masters;
	
	}
	
	public Boolean deleteMaster(String companyCode, String mastername) throws ApplicationException {

		long t1 = System.currentTimeMillis();
		try {
			objJProvider.createNativeQuery("delete from {h-schema}zf_mstr_metadata where companycode=:companyCode and mastername=:mastername").setParameter("companyCode", companyCode).setParameter("mastername", mastername).executeUpdate();
			objJProvider.createNativeQuery("delete from {h-schema}zf_mstr_data where companycode=:companyCode and mastername=:mastername").setParameter("companyCode", companyCode).setParameter("mastername", mastername).executeUpdate();
		}	
		catch (Exception e) {
			e.printStackTrace();
			//log.printErrorMessage(e);
			throw new ApplicationException(new Exception(e));
		}
		finally
		{
			System.out.println("#MasterDAO#getMasterDetail#End#TT#"+ (System.currentTimeMillis() - t1));
		}
		return true;
	
	}
	
	public JSONObject getRowData(String companyCode, String mastername,String primaryKey) throws ApplicationException 
	{
		long t1 = System.currentTimeMillis();
	    JSONObject rowData = null;
		List dataobj = null;
		try {
		
			dataobj=  objJProvider.createNativeQuery("select CAST(value as TEXT) from {h-schema}zf_mstr_data where companycode=:companyCode and mastername=:mastername and key=:primaryKey").setParameter("companyCode", companyCode).setParameter("mastername", mastername).setParameter("primaryKey", primaryKey).getResultList();
			JSONArray data = new JSONArray();
			if(dataobj!=null && dataobj.size()>0)
			for(int i=0; i < dataobj.size(); i++) {
				rowData=new JSONObject(dataobj.get(i).toString());				
			}
		}	
		catch (Exception e) {
			e.printStackTrace();
			//log.printErrorMessage(e);
			throw new ApplicationException(new Exception(e));
		}
		finally
		{
			System.out.println("#MasterDAO#getMasterDetail#End#TT#"+ (System.currentTimeMillis() - t1));
		}
		return rowData;
	    
	}
	
	
	 public String updateMasterData(String action,  MasterDataDTO masterdto) throws ApplicationException {
			
		 long t1 = System.currentTimeMillis();
			int count=0;
		    String message="Master created successfully";
		    
		    System.out.println("update master data --> " + action);
			try {
//				objJProvider.begin();
				if("UPDATE".equalsIgnoreCase(action) || "INSERT".equalsIgnoreCase(action)) {
					String deletekey = masterdto.getKey();
					if("UPDATE".equalsIgnoreCase(action) && masterdto.getOldkey() != masterdto.getKey()) {
						deletekey = masterdto.getOldkey();
					}
					count =  objJProvider.createNativeQuery("delete from {h-schema}zf_mstr_data where companycode=:1 and mastername=:2 and key=:3")
					   		     .setParameter("1", masterdto.getCompanycode())
								 .setParameter("2", masterdto.getMastername())
								 .setParameter("3", deletekey)
								 .executeUpdate();
				    if(count == 0) {
					   message="Unable to delete Master";
					}
				  action="INSERT";
				}
				
				if("INSERT".equalsIgnoreCase(action)) {
				  System.out.println("update master data inside insert --> " + action);
				  count =  objJProvider.createNativeQuery("insert into {h-schema}zf_mstr_data (companycode, mastername, key, value) values (:1,:2,:3,:4)")
						 .setParameter("1", masterdto.getCompanycode())
						 .setParameter("2", masterdto.getMastername())
						 .setParameter("3", masterdto.getKey())
						 .setParameter("4", masterdto.getValue())
						 .executeUpdate();
				  if(count == 0) {
						message="Unable to create Master";
				  }
				}else if("DELETE".equalsIgnoreCase(action)) {
					count =  objJProvider.createNativeQuery("delete from {h-schema}zf_mstr_data where companycode=:1 and mastername=:2 and key=:3")
					 .setParameter("1", masterdto.getCompanycode())
					 .setParameter("2", masterdto.getMastername())
					 .setParameter("3", masterdto.getKey())
					 .executeUpdate();
					  if(count == 0) {
							message="Unable to delete Master";
					  }
				}
//				objJProvider.commit();
				
				}	
			catch (Exception e) {
				e.printStackTrace();
				//log.printErrorMessage(e);
				message="Master already available";
				throw new ApplicationException(new Exception(e));
				
			}
			finally
			{
				System.out.println("#MasterDAO#createMaster#End#TT#"+ (System.currentTimeMillis() - t1));
			}
			return message;
	}
	 
	 
	 public String lookupByKey(String companycode, String mastername, String columnname, String key) throws ApplicationException {
		    long t1 = System.currentTimeMillis();
		    String rowData = null;
			List dataobj = null;
			try {
				dataobj=  objJProvider.createNativeQuery("select value ->> '"+columnname+"' from {h-schema}zf_mstr_data where companycode=:companyCode and mastername=:mastername and key=:primaryKey").setParameter("companyCode", companycode).setParameter("mastername", mastername).setParameter("primaryKey", key).getResultList();
				if(dataobj!=null && dataobj.size()>0)
				for(int i=0; i < dataobj.size(); i++) {
					rowData=dataobj.get(i).toString();				
				}
			}	
			catch (Exception e) {
				e.printStackTrace();
				//log.printErrorMessage(e);
				throw new ApplicationException(new Exception(e));
			}
			finally
			{
				System.out.println("#MasterDAO#getMasterDetail#End#TT#"+ (System.currentTimeMillis() - t1));
			}
			return rowData;
	 }
	 
	 public ArrayList<String> getMasterDropDown(String companyCode, String mastername, String columnname) throws ApplicationException {
			
		 long t1 = System.currentTimeMillis();
		    ArrayList<String> masters = new ArrayList<String>();
			List obj=null;
			try {
				obj=  objJProvider.createNativeQuery("select distinct value ->> '"+columnname+"' from {h-schema}zf_mstr_data where companycode=:companycode and mastername=:mastername").setParameter("companycode", companyCode).setParameter("mastername", mastername).getResultList();
				System.out.println("obj "+obj);
				for(int i=0 ; i < obj.size() ; i++) {
					masters.add(obj.get(i).toString());
				}
			}	
			catch (Exception e) {
				e.printStackTrace();
				//log.printErrorMessage(e);
				throw new ApplicationException(new Exception(e));
			}
			finally
			{
				System.out.println("#MasterDAO#getMasterDropDown#End#TT#"+ (System.currentTimeMillis() - t1));
			}
			return masters;
	}
	

}
