package com.zanflow.bpmn.util;

//import java.util.HashSet;
import java.util.Map;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.json.JSONObject;

import com.fasterxml.jackson.databind.util.TypeKey;

//import groovy.lang.Binding;
//import groovy.lang.GroovyShell;

public class FlowConditionValidator 
{
	
	public static boolean validateCondition(Map dataMap,String condition)throws Exception
	{
		boolean flag=false;
		try
		{
//			Binding objBinding = new Binding();
//			HashSet<String> keys=new HashSet<String>();
//			if(condition!=null)
//			{
//				Pattern p=Pattern.compile("#"+"(.*?)"+"#");
//				Matcher m = p.matcher(condition);
//				 while (m.find()) {
//					 System.out.println(m.group(1));
//					 keys.add(m.group(1));
//				 }
//				 condition=condition.replaceAll("#", "");
//			}
//			for(Object key:keys)
//			{
//				Object value=dataMap.get(key);
//				if(value!=null && value instanceof String)
//				{
//					objBinding.setVariable(key.toString(),value.toString());
//				}
//			}
//			GroovyShell shell = new GroovyShell(objBinding);
//			Boolean value = (Boolean) shell.evaluate(condition);
//			if(value!=null)
//			{
//				flag= value.booleanValue();
//			}
			
			ScriptEngineManager manager = new ScriptEngineManager();
			ScriptEngine engine = manager.getEngineByName("js");
//			HashSet<String> keys=new HashSet<String>();
			if(condition!=null)
			{
//				String []keywords=condition.split(" ");
//				if(keywords!=null && keywords.length>0)
//				{
//					for(String key:keywords)
//					{
//						if(key.startsWith("zf."))
//						{
//							key.replaceAll("zf.", "");
//							keys.add(key);
//						}
//					}
//				}
//				
//				for(Object key:keys)
//				{
//					Object value=dataMap.get(key);
//					System.out.println(key + " ------------- " + value);
//					if(value!=null && value instanceof String)
//					{
//						condition = condition.replaceAll(("zf."+key),("'"+value.toString()+"'"));
//					}
//				}
//				System.out.println("condition---------------"+condition);
				Bindings scope=engine.createBindings();
				scope.put("zf", dataMap);
				
				Boolean value = (Boolean) engine.eval(condition,scope);
				if(value!=null)
				{
					flag= value.booleanValue();
				}	
				JSONObject obj = new JSONObject(dataMap);
				System.out.println("flowdata -----------> " +obj.toString());
				System.out.println("flow result -----------> " +flag);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw new Exception(ex);
		}
		return flag;
	}

}
