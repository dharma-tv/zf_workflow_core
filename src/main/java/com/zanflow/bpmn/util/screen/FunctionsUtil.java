package com.zanflow.bpmn.util.screen;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zanflow.bpmn.util.MasterDataUpdater;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

public class FunctionsUtil {
	protected String companyCode;

	HashMap<String,JSONObject> rowDataMap=new HashMap<String, JSONObject>();
	
	public FunctionsUtil(String companyCode)
	{
		this.companyCode=companyCode;
	}

	
	public String getCompanyCode() {
		return companyCode;
	}

	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}
	
	public String add(String values[]) {
		int res=0;
		for(int i=0;i<values.length; i++) {
			if(values[i] != null && values[i].trim() != "") {
			res = res + Integer.parseInt(values[i]);
			//System.out.println(values[i]);
			}
		}
		return String.valueOf(res);
	}
	
	public String sub(String values[]) {
		int res=Integer.parseInt(values[0]);
		for(int i=1;i<values.length; i++) {
			if(values[i] != null && values[i].trim() != "") {
			res = res - Integer.parseInt(values[i]);
			//System.out.println(values[i]);
			}
		}
		return String.valueOf(res);
	}
	
	public String multiply(String values[]) {
		int res=Integer.parseInt(values[0]);
		for(int i=1;i<values.length; i++) {
			if(values[i] != null && values[i].trim() != "") {
			res = res * Integer.parseInt(values[i]);
			//System.out.println(values[i]);
			}
		}
		return String.valueOf(res);
	}
	
	public String concatenate(String values[]) {
		String res="";
		for(int i=0;i<values.length; i++) {
			res = res + values[i];
		}
		return res;
	}
	
	public static void main(String[] args) throws JsonMappingException, JsonProcessingException 
	{
		
		ObjectMapper mapper = new ObjectMapper();
		String s = "{\"FromDate\":\"2021-10-11\",\"ToDate\":\"2021-10-24\",\"Employee\":\"admin@ria.com\",\"Manager\":\"\",\"LeaveReason\":\"\",\"BillingDetails\":[{\"Product\":\"pen\",\"Amount\":\"34\",\"Quantity\":\"4543\",\"total\":\"5345\",\"tableData\":{\"id\":0}},{\"Product\":\"hel\",\"Amount\":\"45\",\"Quantity\":\"56\",\"total\":\"53\",\"tableData\":{\"id\":1}}]}";
	
		
		Map<String, Object> dataMap =mapper.readValue(s, Map.class);
		
		//Map<String,Object> dataMap=new HashMap<String, Object>();
		dataMap.put("AssetNo", "901");
		dataMap.put("AssetName", "acer desktop");
		dataMap.put("AssetLocation", "chennai");
		dataMap.put("oldkey", "903");
		dataMap.put("oldkey1", "20");
		dataMap.put("masterTableName", "AssetDetails");

		Binding objBinding = new Binding();
		
		objBinding.setVariable("zf", dataMap);
		objBinding.setVariable("masterObj", new MasterDataUpdater("zanflowdev"));
		objBinding.setVariable("fn", new FunctionsUtil("zanflowdev"));
		
		String script="fn.getWeekDaysDiff(zf.FromDate,zf.ToDate) + '  ' + fn.multiply(zf.AssetNo,zf.oldkey,zf.oldkey1)";
		
		
		GroovyShell shell = new GroovyShell(objBinding);
		
		String computedValue = (String) shell.evaluate(script);
		//Script s= shell.parse(script);
		
		//dataMap=(Map<String, Object>) objBinding.getVariable("zf");
		
		//System.out.println("RESULT#####"+computedValue);
		//System.out.println("RESULT#####"+dataMap.get("BillingDetails"));
	}
	
	public String getDaysDiff(String startDate,String endDate)
	{
		if(startDate==null || endDate==null || "".equals(startDate) || "".equals(endDate)) {
			return "";
		}
		String days="";
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
	
	public String getWeekDaysDiff(String startDateStr, String endDateStr) throws ParseException {
		if(startDateStr==null || endDateStr==null || "".equals(endDateStr) || "".equals(startDateStr)) {
			return "";
		}
		try {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		Date startDate=sdf.parse(startDateStr);
		Date endDate=sdf.parse(endDateStr);
	    Calendar startCal = Calendar.getInstance();
	    startCal.setTime(startDate);        

	    Calendar endCal = Calendar.getInstance();
	    endCal.setTime(endDate);

	    int workDays = 0;

	    //Return 0 if start and end are the same
	    if (startCal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && startCal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY && startCal.getTimeInMillis() == endCal.getTimeInMillis()) {
	        return "1";
	    }

	    if (startCal.getTimeInMillis() > endCal.getTimeInMillis()) {
	        startCal.setTime(endDate);
	        endCal.setTime(startDate);
	    }

	    do {
	               
	        if (startCal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && startCal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
	            ++workDays;
	        }
	        startCal.add(Calendar.DAY_OF_MONTH, 1);
	    } while (startCal.getTimeInMillis() <= endCal.getTimeInMillis()); //excluding end date

	       return String.valueOf(workDays);
	    }catch(Exception e) {
	    	return "";
	    }
	}
}
