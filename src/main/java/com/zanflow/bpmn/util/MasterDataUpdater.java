package com.zanflow.bpmn.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.zanflow.bpmn.dao.MasterDAO;
import com.zanflow.bpmn.dto.master.MasterDataDTO;
import com.zanflow.bpmn.exception.JPAIllegalStateException;
import com.zanflow.common.db.Constants;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

public class MasterDataUpdater 
{
	protected String companyCode;

	public MasterDataUpdater(String companyCode)
	{
		this.companyCode=companyCode;
	}
	
	public String updateMasterData(String masterTableName,Map<String,Object>dataMap)
	{
		String result="Master Data updated succcessfully";
		System.out.println("updateMasterData#"+companyCode+"#"+masterTableName+"#"+dataMap);
		MasterDAO objMasterDAO=null;
		try
		{
			objMasterDAO=new MasterDAO(Constants.DB_PUNIT);
			JSONObject masterTableDetails=objMasterDAO.getMasterDetail(companyCode, masterTableName);
			Set<String> fieldNameSet=new HashSet<String>();
			ObjectMapper mapperObj=new ObjectMapper();
			String primaryKey=null;
			if(masterTableDetails!=null)
			{
				if(!masterTableDetails.isNull("metadata"))
				{
					JSONArray metaDataArr=masterTableDetails.getJSONArray("metadata");
					for(int i=0;i<metaDataArr.length();i++)
					{
						JSONObject metaDataObj=metaDataArr.getJSONObject(i);
						fieldNameSet.add(metaDataObj.getString("columnname"));
						if((primaryKey==null)&&(!metaDataObj.isNull("isprimary")))
						{
							if(metaDataObj.getString("isprimary").equalsIgnoreCase("yes"))
							{
								primaryKey=metaDataObj.getString("columnname");
							}
						}
					}
					String primaryKeyValue=(String) dataMap.get(primaryKey);
					String oldPrimaryKeyValue=(String) dataMap.get("oldkey");
					dataMap.remove("oldkey");
					if(primaryKeyValue!=null && primaryKeyValue.length()>0)
					{
						for(String key:fieldNameSet)
						{
							if(!dataMap.containsKey(key))
							{
								result=key+"# column is missing";
								return result;
							}
						}
						MasterDataDTO objMasterDataDTO=new MasterDataDTO();
						objMasterDataDTO.setCompanycode(companyCode);
						objMasterDataDTO.setMastername(masterTableName);
						objMasterDataDTO.setKey(primaryKeyValue);
						if(oldPrimaryKeyValue!=null && oldPrimaryKeyValue.length()>0)
						{
							objMasterDataDTO.setOldkey(oldPrimaryKeyValue);
						}
						else
						{
							objMasterDataDTO.setOldkey(primaryKeyValue);
						}
						objMasterDataDTO.setValue(mapperObj.writeValueAsString(dataMap));
						System.out.println("objMasterDataDTO.getValue()#"+objMasterDataDTO.getValue());
						objMasterDAO.begin();
						objMasterDAO.updateMasterData("UPDATE", objMasterDataDTO);
						objMasterDAO.commit();
					}
					else
					{
						result=primaryKey+"# value is null/empty";
					}
				}
			}
		}
		catch(Exception ex)
		{
			if(objMasterDAO!=null && objMasterDAO.isActive())
			{
				objMasterDAO.rollback();
			}
			ex.printStackTrace();
			result="ERR#unable to updateMaster Data#"+ex.getMessage();
		}
		finally 
		{
			if(objMasterDAO!=null)
			{
				try {
					objMasterDAO.close(Constants.DB_PUNIT);
				} catch (JPAIllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	public String deleteMasterData(String masterTableName,Map<String,Object>dataMap)
	{
		String result="Master Data deleted succcessfully";
		MasterDAO objMasterDAO=null;
		System.out.println("deleteMasterData#"+companyCode+"#"+masterTableName+"#"+dataMap);
		try
		{
			objMasterDAO=new MasterDAO(Constants.DB_PUNIT);
			JSONObject masterTableDetails=objMasterDAO.getMasterDetail(companyCode, masterTableName);
//			Set<String> fieldNameSet=new HashSet<String>();
			ObjectMapper mapperObj=new ObjectMapper();
			String primaryKey=null;
			if(masterTableDetails!=null)
			{
				if(!masterTableDetails.isNull("metadata"))
				{
					JSONArray metaDataArr=masterTableDetails.getJSONArray("metadata");
					for(int i=0;i<metaDataArr.length();i++)
					{
						JSONObject metaDataObj=metaDataArr.getJSONObject(i);
//						fieldNameSet.add(metaDataObj.getString("columnname"));
						if((primaryKey==null)&&(!metaDataObj.isNull("isprimary")))
						{
							if(metaDataObj.getString("isprimary").equalsIgnoreCase("yes"))
							{
								primaryKey=metaDataObj.getString("columnname");
								break;
							}
						}
					}
					String primaryKeyValue=(String) dataMap.get(primaryKey);
					String oldPrimaryKeyValue=(String) dataMap.get("oldkey");
					dataMap.remove("oldkey");
					if(primaryKeyValue!=null && primaryKeyValue.length()>0)
					{
						MasterDataDTO objMasterDataDTO=new MasterDataDTO();
						objMasterDataDTO.setCompanycode(companyCode);
						objMasterDataDTO.setMastername(masterTableName);
						objMasterDataDTO.setKey(primaryKeyValue);
						if(oldPrimaryKeyValue!=null && oldPrimaryKeyValue.length()>0)
						{
							objMasterDataDTO.setOldkey(oldPrimaryKeyValue);
						}
						else
						{
							objMasterDataDTO.setOldkey(primaryKeyValue);
						}
						objMasterDataDTO.setValue(mapperObj.writeValueAsString(dataMap));
						System.out.println("objMasterDataDTO.getValue()#"+objMasterDataDTO.getValue());
						objMasterDAO.begin();
						objMasterDAO.updateMasterData("DELETE", objMasterDataDTO);
						objMasterDAO.commit();
					}
					else
					{
						result=primaryKey+"# value is null/empty";
					}
				}
			}
		}
		catch(Exception ex)
		{
			if(objMasterDAO!=null && objMasterDAO.isActive())
			{
				objMasterDAO.rollback();
			}
			ex.printStackTrace();
			result="ERR#unable to delete Master Data#"+ex.getMessage();
		}
		finally 
		{
			if(objMasterDAO!=null)
			{
				try {
					objMasterDAO.close(Constants.DB_PUNIT);
				} catch (JPAIllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return result;
	}
	
	public String insertMasterData(String masterTableName,Map<String,Object>dataMap)
	{
		String result="Master Data inserted succcessfully";
		MasterDAO objMasterDAO=null;
		System.out.println("updateMasterData#"+companyCode+"#"+masterTableName+"#"+dataMap);
		try
		{
			objMasterDAO=new MasterDAO(Constants.DB_PUNIT);
			JSONObject masterTableDetails=objMasterDAO.getMasterDetail(companyCode, masterTableName);
			Set<String> fieldNameSet=new HashSet<String>();
			ObjectMapper mapperObj=new ObjectMapper();
			String primaryKey=null;
			if(masterTableDetails!=null)
			{
				if(!masterTableDetails.isNull("metadata"))
				{
					JSONArray metaDataArr=masterTableDetails.getJSONArray("metadata");
					for(int i=0;i<metaDataArr.length();i++)
					{
						JSONObject metaDataObj=metaDataArr.getJSONObject(i);
						fieldNameSet.add(metaDataObj.getString("columnname"));
						if((primaryKey==null)&&(!metaDataObj.isNull("isprimary")))
						{
							if(metaDataObj.getString("isprimary").equalsIgnoreCase("yes"))
							{
								primaryKey=metaDataObj.getString("columnname");
							}
						}
					}
					String primaryKeyValue=(String) dataMap.get(primaryKey);
					String oldPrimaryKeyValue=(String) dataMap.get("oldkey");
					dataMap.remove("oldkey");
					if(primaryKeyValue!=null && primaryKeyValue.length()>0)
					{
						for(String key:fieldNameSet)
						{
							if(!dataMap.containsKey(key))
							{
								result=key+"# column is missing";
								return result;
							}
						}
						JSONObject rowData=objMasterDAO.getRowData(companyCode, masterTableName, primaryKeyValue);
						if(rowData!=null)
						{
							result=primaryKeyValue+"# Duplicate primary key";
							return result;
						}
						MasterDataDTO objMasterDataDTO=new MasterDataDTO();
						objMasterDataDTO.setCompanycode(companyCode);
						objMasterDataDTO.setMastername(masterTableName);
						objMasterDataDTO.setKey(primaryKeyValue);
						if(oldPrimaryKeyValue!=null && oldPrimaryKeyValue.length()>0)
						{
							objMasterDataDTO.setOldkey(oldPrimaryKeyValue);
						}
						else
						{
							objMasterDataDTO.setOldkey(primaryKeyValue);
						}
						objMasterDataDTO.setValue(mapperObj.writeValueAsString(dataMap));
						System.out.println("objMasterDataDTO.getValue()#"+objMasterDataDTO.getValue());
						objMasterDAO.begin();
						objMasterDAO.updateMasterData("INSERT", objMasterDataDTO);
						objMasterDAO.commit();
					}
					else
					{
						result=primaryKey+"# value is null/empty";
					}
				}
			}
		}
		catch(Exception ex)
		{
			if(objMasterDAO!=null && objMasterDAO.isActive())
			{
				objMasterDAO.rollback();
			}
			ex.printStackTrace();
			result="ERR#unable to insert Master Data#"+ex.getMessage();
		}
		finally 
		{
			if(objMasterDAO!=null)
			{
				try {
					objMasterDAO.close(Constants.DB_PUNIT);
				} catch (JPAIllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return result;
	}
	
	
	public Map<String,String> getMasterData(String masterTableName,String primaryKey)
	{
		Map<String,String>dataMap=null;
		MasterDAO objMasterDAO=null;
		try
		{
			objMasterDAO=new MasterDAO(Constants.DB_PUNIT);
			JSONObject rowData=objMasterDAO.getRowData(companyCode, masterTableName, primaryKey);
			if(rowData !=null)
				dataMap = new ObjectMapper().readValue(rowData.toString(), HashMap.class);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();	
		}
		finally 
		{
			if(objMasterDAO!=null)
			{
				try {
					objMasterDAO.close(Constants.DB_PUNIT);
				} catch (JPAIllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return dataMap;
	}
	public int getDaysDiff(String startDate,String endDate)
	{
		int days=0;
		try
		{
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
			Date stDate=sdf.parse(startDate);
			Date edDate=sdf.parse(endDate);
			long diffDays = (edDate.getTime()-stDate.getTime()) / (1000 * 60 * 60 * 24);
			days=new Long(diffDays).intValue();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return days;
	}
	
	public String getCompanyCode() {
		return companyCode;
	}

	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}
	
	public static void main(String[] args) 
	{
		
		Map<String,Object> dataMap=new HashMap<String, Object>();
//		dataMap.put("AssetNo", "901");
//		dataMap.put("AssetName", "acer desktop");
//		dataMap.put("AssetLocation", "chennai");
//		dataMap.put("oldkey", "903");
//		dataMap.put("masterTableName", "AssetDetails");
		
		dataMap.put("EmployeeId", "user1@zanflow.com");
//		dataMap.put("EmployeeId", "SUB001");
		dataMap.put("StartDate", "2020-09-18");
		dataMap.put("EndDate", "2020-09-23");

		Binding objBinding = new Binding();
		
		objBinding.setVariable("zf", dataMap);
		objBinding.setVariable("masterObj", new MasterDataUpdater("zanflow"));
		
		
//		String script="def dataMap=new HashMap();"
//				+ "dataMap.put('AssetNo',zf.AssetNo);"
//				+ "dataMap.put('AssetName',zf.AssetName);"
//				+ "dataMap.put('AssetLocation',zf.AssetLocation);"
//				+"zf.result=masterObj.updateMasterData(zf.masterTableName,dataMap); println(zf.result);";
//		
//		String script1="def dataMap=new HashMap();"
//				+ "dataMap.put('AssetNo',zf.AssetNo);"
//				+ "dataMap.put('AssetName',zf.AssetName);"
//				+ "dataMap.put('AssetLocation',zf.AssetLocation);"
//				+ "dataMap.put('oldkey',zf.oldkey);"
//				+"zf.result=masterObj.updateMasterData(zf.masterTableName,dataMap); println(zf.result);";
//		
//		
//		String insertScript="def dataMap=new HashMap();"
//				+ "dataMap.put('AssetNo',zf.AssetNo);"
//				+ "dataMap.put('AssetName',zf.AssetName);"
//				+ "dataMap.put('AssetLocation',zf.AssetLocation);"
//				+"zf.result=masterObj.insertMasterData(zf.masterTableName,dataMap); println(zf.result);";
//		
//		String deleteScript="def dataMap=new HashMap();"
//				+ "dataMap.put('AssetNo',zf.AssetNo);"
//				+ "dataMap.put('AssetName',zf.AssetName);"
//				+ "dataMap.put('AssetLocation',zf.AssetLocation);"
//				+"zf.result=masterObj.deleteMasterData(zf.masterTableName,dataMap); println(zf.result);";
		
		String leaveRequestScript="def dataMap=new HashMap();"
		+"\n"+ "dataMap.put('employeeid',zf.EmployeeId);"
		+"\n"+ "dataMap.put('masterTableName','empleavemaster');"
		+"\n"+"int diffDays=masterObj.getDaysDiff(zf.StartDate,zf.EndDate);"
		+"\n"+" println(diffDays);"
		+"\n"+"HashMap rowData=masterObj.getMasterData('empleavemaster',zf.EmployeeId);"
		+"\n"+"println(rowData);"
		+"\n"+"rowData.leavetaken=String.valueOf(Integer.parseInt(rowData.leavetaken)+diffDays);"
		+"\n"+"rowData.leavebalance=String.valueOf(Integer.parseInt(rowData.leavebalance)-diffDays);"
		+"\n"+"println(rowData);"
		+"\n"+"zf.result=masterObj.updateMasterData('empleavemaster',rowData);";
		
	
		System.out.println("##########################");
		
		
		GroovyShell shell = new GroovyShell(objBinding);
		
//		shell.parse(script).run();
		
		shell.parse(leaveRequestScript).run();
		
//		shell.parse(deleteScript).run();
		
//		shell.parse(insertScript).run();
		
//		System.out.println("RESULT#####"+objBinding.getVariable("result"));
		
		dataMap=(Map<String, Object>) objBinding.getVariable("zf");
		
		System.out.println("RESULT#####"+dataMap);
		System.out.println(leaveRequestScript);
	}
}
