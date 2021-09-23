package com.zanflow.bpmn.util.screen;

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
import com.zanflow.bpmn.util.MasterDataUpdater;
import com.zanflow.common.db.Constants;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

public class MasterHelper {
	protected String companyCode;

	HashMap<String,JSONObject> rowDataMap=new HashMap<String, JSONObject>();
	
	public MasterHelper(String companyCode)
	{
		this.companyCode=companyCode;
	}
	
	public String lookupByKey(String masterTableName,String columnname, String key)
	{
		String result="Master value fetched succcessfully";
		System.out.println("lookupByKey#"+companyCode+"#"+masterTableName+"#"+columnname+"#"+key);
		MasterDAO objMasterDAO=null;
		String masterTableDetails = "";
		try
		{
			String cacheKey=companyCode+"#"+masterTableName+"#"+key;
			JSONObject rowData=null;
			if(rowDataMap.containsKey(cacheKey))
			{
				rowData=rowDataMap.get(cacheKey);
			}
			else
			{
				objMasterDAO=new MasterDAO(Constants.DB_PUNIT);
				rowData=objMasterDAO.getRowData(companyCode, masterTableName, key);
				rowDataMap.put(cacheKey,rowData);
//				masterTableDetails=objMasterDAO.lookupByKey(companyCode, masterTableName, columnname, key);
			}
			if(rowData!=null && (!rowData.isNull(columnname)))
			{
				
				Object data = rowData.get(columnname);
				//if(data instanceof String) {
					masterTableDetails= String.valueOf(data);
				//}
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
		return masterTableDetails;
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
		dataMap.put("AssetNo", "901");
		dataMap.put("AssetName", "acer desktop");
		dataMap.put("AssetLocation", "chennai");
		dataMap.put("oldkey", "903");
		dataMap.put("masterTableName", "AssetDetails");

		Binding objBinding = new Binding();
		
		objBinding.setVariable("zf", dataMap);
		objBinding.setVariable("masterObj", new MasterDataUpdater("zanflowdev"));
		
		
		String script="AssetLocation=zf.AssetLocation";
		
		
		GroovyShell shell = new GroovyShell(objBinding);
		
		
		Script s= shell.parse(script);
		
		dataMap=(Map<String, Object>) objBinding.getVariable("zf");
		
		System.out.println("RESULT#####"+dataMap);
	}
	
	public String getDaysDiff(String startDate,String endDate)
	{
		String days=null;
		try
		{
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
			Date stDate=sdf.parse(startDate);
			Date edDate=sdf.parse(endDate);
			long diffDays = (edDate.getTime()-stDate.getTime()) / (1000 * 60 * 60 * 24);
			days=String.valueOf(new Long(diffDays).intValue());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return days;
	}
}
