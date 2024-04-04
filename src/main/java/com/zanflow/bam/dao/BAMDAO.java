package com.zanflow.bam.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;

import org.json.JSONArray;
import org.json.JSONObject;

import com.zanflow.bam.dto.MyReqDTO;
import com.zanflow.bam.dto.ProcessDTO;
import com.zanflow.bpmn.exception.ApplicationException;
import com.zanflow.bpmn.exception.JPAPersistenceException;
import com.zanflow.bpmn.model.BPMNProcessInfo;
import com.zanflow.common.db.JPATransaction;
import com.zanflow.common.db.JPersistenceProvider;

public class BAMDAO extends JPATransaction
{

	public BAMDAO(String pJunitName)throws ApplicationException
	{
		//System.out.println("#BpmnTaskDAO#pJunitName#"+pJunitName);
		try {
            if(pJunitName != null && pJunitName.trim().length() > 0){
           	 strJPUnitName = pJunitName;
            }
            else {
           	 throw new ApplicationException("Arguments are NULL, Unable to create JPersistenceProvider.");
            }
            //System.out.println("#pJunitName#"+strJPUnitName);
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


	public BAMDAO(JPersistenceProvider objJProvider) throws ApplicationException
	{
		try{
			if (objJProvider != null)
			{
				this.objJProvider = objJProvider;
				strJPUnitName = objJProvider.getpUnitName();
				//System.out.println("# BpmnTaskDAO objJProvider constructor invoked");
			}else{
				throw new ApplicationException("BpmnTaskDAO : JPersistenceProvider is null");
			}
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
			throw new ApplicationException(new Exception(e));
		} catch (EntityExistsException e) {
			e.printStackTrace();
			throw new ApplicationException(new Exception(e));
		} catch (JPAPersistenceException e) {
			e.printStackTrace();
			throw new ApplicationException(new Exception(e));
		}	
	}
	
	public List<Object[]> getStatusWiseTxnCount(String companyCode, String processId, String startDate, String endDate) 
	{
		 List<Object[]> resultList=null;
		 
		 String query="SELECT count(1),statuscode FROM {h-schema}zf_txn_bpmnprocessinfo where companycode=:companyCode and processId=:processId and cast(createdtime as date) between :startDate and :endDate group by statuscode";
		 
		 Query objQuery=objJProvider.createNativeQuery(query);
		 
		 objQuery.setParameter("companyCode", companyCode);
		 objQuery.setParameter("processId", processId);
		 objQuery.setParameter("startDate", startDate);
		 objQuery.setParameter("endDate", endDate);
		 
		 resultList=objQuery.getResultList();
		 
		 return resultList;
	}
	
	public List<Object[]> getStatusWiseTxnCount_Chart(String companyCode, String userId, String processId, String startDate, String endDate) 
	{
		 List<Object[]> resultList=null;
		 
		 String query="SELECT to_char(createdtime, 'Mon DD, YY') as createdtime,statuscode,count(1) FROM {h-schema}zf_txn_bpmnprocessinfo where companycode=:companyCode and bpmnid in(select t.bpmnid from {h-schema}zf_cfg_bpmnprocess t where monitorrole = 'ALL' OR monitorrole in (select roleid from  {h-schema}zf_id_membership where companyCode=:companyCode and userId=:userId and status='A')) and cast(createdtime as date) between :startDate and :endDate group by to_char(createdtime, 'Mon DD, YY'),statuscode order by createdtime";
		
		 if(!"ALL".equalsIgnoreCase(processId)) {
			 query="SELECT to_char(createdtime, 'Mon DD, YY') as createdtime,statuscode,count(1) FROM {h-schema}zf_txn_bpmnprocessinfo where companycode=:companyCode and processId=:processId and cast(createdtime as date) between :startDate and :endDate group by to_char(createdtime, 'Mon DD, YY'),statuscode order by createdtime";
		 }
		 Query objQuery=objJProvider.createNativeQuery(query);
		 objQuery.setParameter("companyCode", companyCode);
		 if(!"ALL".equalsIgnoreCase(processId)) {
			 objQuery.setParameter("processId", processId);
		 }else {
			 objQuery.setParameter("userId", userId);
		 }
		 objQuery.setParameter("startDate", startDate);
		 objQuery.setParameter("endDate", endDate);		 
		 resultList=objQuery.getResultList();
		 
		 return resultList;
	}
	
	public List<Object[]> getStatusWiseTxnCount_All(String companyCode, String userId, String startDate, String endDate) 
	{
		 List<Object[]> resultList=null;
		 
		 //String query="SELECT count(1),statuscode FROM public.zf_txn_bpmnprocessinfo where companycode=:companyCode and bpmnid in(select t.bpmnid from zf_cfg_bpmnprocess t where monitorrole in (select roleid from  zf_id_membership where companyCode=:companyCode and userId=:userId and status='A')) and createdtime > ( CURRENT_DATE - INTERVAL '3 months') group by statuscode";
		 String query="SELECT count(1),statuscode FROM {h-schema}zf_txn_bpmnprocessinfo where companycode=:companyCode and processId in(select t.processId from {h-schema}zf_cfg_bpmnprocess t where monitorrole = 'ALL' OR monitorrole in (select roleid from {h-schema}zf_id_membership where companyCode=:companyCode and userId=:userId and status='A')) and cast(createdtime as date) between :startDate and :endDate group by statuscode";
		 
		 Query objQuery=objJProvider.createNativeQuery(query);
		 
		 objQuery.setParameter("companyCode", companyCode);
		 objQuery.setParameter("userId", userId);
		 objQuery.setParameter("startDate", startDate);
		 objQuery.setParameter("endDate", endDate);
		 resultList=objQuery.getResultList();
		 
		 return resultList;
	}
	
	public List<ProcessDTO> getProcessList(String companyCode,String userId,String moduleName)throws ApplicationException
	{
		long t1 = System.currentTimeMillis();
		//System.out.println("#ProcessDAO#getProcessList#"+companyCode+"#"+userId);
		List<Object[]> processList=null;
		List<ProcessDTO> dto = new ArrayList<ProcessDTO>();
		try
		{
			String query=null;
			if(moduleName!=null && moduleName.equalsIgnoreCase("BAM"))
			{
				query="select t.processid, t.processname from {h-schema}zf_cfg_bpmnprocess t where companyCode=:companyCode1 and (monitorrole in (select roleid from  {h-schema}zf_id_membership where companyCode=:companyCode and userId=:userId and status='A') OR monitorrole ='ALL') and isactive='Y'";
			}
			else
			{
				query="select t.processid, t.processname from {h-schema}zf_cfg_bpmnprocess t where companyCode=:companyCode1 and (enquiryrole in (select roleid from  {h-schema}zf_id_membership where companyCode=:companyCode and userId=:userId and status='A') OR enquiryrole ='ALL') and isactive='Y'";
			}
			Query objQuery=objJProvider.createNativeQuery(query);
			objQuery.setParameter("companyCode1", companyCode);
			objQuery.setParameter("companyCode", companyCode);
			objQuery.setParameter("userId", userId);
			processList=objQuery.getResultList();
			//System.out.println(processList.size() + " -------- " + processList);
			for (int i = 0; i < processList.size(); i++) {
				 ProcessDTO processDTO = new ProcessDTO();
			     processDTO.setProcessId((String) processList.get(i)[0]);
			     processDTO.setProcessname((String) processList.get(i)[1]);
			     dto.add(processDTO);
			}
			//System.out.println(dto.size() + " -------- " + dto);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			//log.printErrorMessage(ex);
			throw new ApplicationException(ex);
		}
		finally
		{
			//System.out.println("#ProcessDAO#getProcessList#End#TT#" + (System.currentTimeMillis() - t1));
		}
		return dto;
	  }
	
//	 bpmntxrefno,createdtime, laststepname, statuscode,
	
	public List<Map<String,Object>> getSearchDtl(String companyCode,String processId, String startDate, String endDate, List<String> indexFields,List<String> columns) 
	{
		 List<Object[]> resultList=null;
		 List<Map<String,Object>> obj = new ArrayList<Map<String,Object>>();
		 Map<String,Object> mapobj=null;
		 try {
			 String query="SELECT COLUMNNAMES FROM {h-schema}zf_txn_bpmnprocessinfo where companycode=:companyCode and processId=:processId and cast(createdtime as date) between :startDate and :endDate";
			 StringBuffer buffer = new StringBuffer(columns.stream().collect(Collectors.joining(",")));
			 for(String str:indexFields) {
				 buffer.append(",processdata ->>'");
				 buffer.append(str);
				 buffer.append("' as ");
				 buffer.append(str);
				 columns.add(str);
			 }

			 query = query.replaceAll("COLUMNNAMES", buffer.toString());
			 //System.out.println("query" +query);
			 Query objQuery=objJProvider.createNativeQuery(query);

			 objQuery.setParameter("companyCode", companyCode);
			 objQuery.setParameter("processId", processId);
			 objQuery.setParameter("startDate", startDate);
			 objQuery.setParameter("endDate", endDate);

			 resultList=objQuery.getResultList();

			 for(Object[] ob:resultList) {
				 mapobj = new HashMap<String, Object>();
				 for(int i = 0 ; i<ob.length ; i++ )
				 {
					 if(columns.get(i).equalsIgnoreCase("statuscode"))
					 {	 
						 String status= ob[i].toString();
						 switch(status) {
						  case "1":
							  status ="Initiated";
							  break;
						  case "2":
							  status ="Completed";
							  break;
						  case "3":
							  status ="InProgress";
							  break;
						} 
						 mapobj.put(columns.get(i), status);
					 }
					 else if(columns.get(i).equalsIgnoreCase("createdtime"))
					 {
						 String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss a").format(ob[i]);
						 mapobj.put(columns.get(i),date );
					 }
					 else {
						 mapobj.put(columns.get(i), ob[i]);
					 }
						 
				 }
				 obj.add(mapobj);
			 }
		 }
		 catch(Exception e) {
			 e.printStackTrace();
		 }
		 return obj;
	}


	public List<String> getReporableFields(String companyCode, String processId) {
		 List<Object[]> resultList=null;
		 JSONArray fields = null;
		 List<String> resultObj =null;
		 try {
			 String query="SELECT processconfig->> 'jsonschema' as jsonschema FROM {h-schema}zf_cfg_bpmnprocess where companycode=:companycode and processId=:processId and isactive='Y'";
			 
			 Query objQuery=objJProvider.createNativeQuery(query);

			 objQuery.setParameter("companycode", companyCode);
			 objQuery.setParameter("processId", processId);
			 

			 resultList=objQuery.getResultList();

			 if(resultList.get(0) != null) {
				 resultObj= new ArrayList<String>();
				 //System.out.println("obj "+resultList.get(0));
				/* fields = new  JSONArray(String.valueOf(resultList.get(0)));

				 JSONObject field = null;
				 for (Iterator<Object> i = fields.iterator(); i.hasNext(); ){
					 field = (JSONObject)i.next();
					 //System.out.println("field json "+field);
					 if(field.has("REPORT")) {
						 boolean value = (field.get("REPORT") != null)? (boolean)field.get("REPORT"):false ;
						 String fieldName = field.getString("NAME");
						 //System.out.println("FieldName "+fieldName+" value "+value);
						 if(value) {
							 resultObj.add(fieldName);
						 }
					 }
				 }*/
				 
				 JSONObject jsonschemaobj = new JSONObject(String.valueOf(resultList.get(0)));
				 Iterator<String> keys = jsonschemaobj.keys();

					while(keys.hasNext()) {
					    String key = keys.next();
					    JSONObject fieldJSON= jsonschemaobj.getJSONObject(key);
					    //System.out.println("fieldJSON "+fieldJSON);
					    if(fieldJSON.has("REPORT") && fieldJSON.get("REPORT")!=null && fieldJSON.getBoolean("REPORT")) {
					    	resultObj.add(key);
					    }
					}
			 }
			 
		 //System.out.println("result index columns "+resultObj);
		 }
		 catch(Exception e) {
			 e.printStackTrace();
		 }
		 return resultObj;
	}
	
	public List<MyReqDTO> getMyRequests(String companyCode,String userId, String processId) throws Exception
	{
		List<MyReqDTO> myReqList=new ArrayList<MyReqDTO>();
		try
		{
			String processQueryString="select fcb.bpmnid,fcb.processname,ftb.bpmntxrefno,ftb.statuscode , ftb.currentstepname, CAST(ftb.processdata AS VARCHAR) from {h-schema}zf_cfg_bpmnprocess fcb , {h-schema}zf_txn_bpmnprocessinfo ftb where fcb.bpmnid = ftb.bpmnid and fcb.companycode=ftb.companycode and fcb.rendertype !='BOARD'" + 
					"and ftb.companycode=:companyCode and ftb.initatedby=:userId and ftb.createdtime >= now() - interval '45 days'";
			//String processQueryString1="select bpmnid,processname from {h-schema}zf_cfg_bpmnprocess where companycode=:companyCode";
			 if(processId != null) {
				 processQueryString = processQueryString + " and ftb.processid=:processId";
			 }
			 Query objProcQuery=objJProvider.createNativeQuery(processQueryString);
			 objProcQuery.setParameter("companyCode", companyCode);
			 objProcQuery.setParameter("userId", userId);
			 if(processId != null) {
				 objProcQuery.setParameter("processId", processId);
			 }
			 List<Object[]> procResultList=objProcQuery.getResultList();
			 //HashMap<String,String> companyProcessMap=new HashMap<String, String>();
			 if(procResultList!=null && procResultList.size()>0)
			 {
				 for(Object []ob:procResultList)
				 {
					 //companyProcessMap.put(ob[0].toString(),ob[1].toString());
					 MyReqDTO obj=new MyReqDTO();
					 Map<String,Object> valueMap = new HashMap<String, Object>();
					// //System.out.println(objBPMNProcessInfo.getBpmnTxRefNo()+"#"+objBPMNProcessInfo.getStatusCode()+"#"+companyProcessMap.get(objBPMNProcessInfo.getBpmnId())+"#"+obj.getCurrentStepName());
					 obj.setBpmTxRefno(String.valueOf(ob[2]));
					 obj.setStatusCode(Integer.parseInt(ob[3].toString()));
					 obj.setProcessName(String.valueOf(ob[1]));
					 obj.setCurrentStepName(String.valueOf(ob[4]));
					 if(ob[5] != null) {
					 JSONObject json =new JSONObject(String.valueOf(ob[5]));
					 json.keySet().forEach(keyStr ->
					    {
					        Object keyvalue = json.get(keyStr);
					        //System.out.println("key: "+ keyStr + " value: " + keyvalue);
					        if(keyStr!=null && !keyStr.equalsIgnoreCase("All") && !keyStr.equalsIgnoreCase("selectedresponse"))
					        	valueMap.put(keyStr, keyvalue);
					    });
					 obj.setKeyValues(valueMap);
					 }
					 myReqList.add(obj);
				 }
			 }
			 
			 ////System.out.println(companyProcessMap);
			
			 
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw ex;
		}
		return myReqList;
	}
	
	public List<MyReqDTO> getMyRequests1(String companyCode,String userId) throws Exception
	{
		List<MyReqDTO> myReqList=new ArrayList<MyReqDTO>();
		try
		{
			String processQueryString="select bpmnid,processname from {h-schema}zf_cfg_bpmnprocess where companycode=:companyCode";
			 Query objProcQuery=objJProvider.createNativeQuery(processQueryString);
			 objProcQuery.setParameter("companyCode", companyCode);
			 List<Object[]> procResultList=objProcQuery.getResultList();
			 HashMap<String,String> companyProcessMap=new HashMap<String, String>();
			 if(procResultList!=null && procResultList.size()>0)
			 {
				 for(Object []ob:procResultList)
				 {
					 companyProcessMap.put(ob[0].toString(),ob[1].toString());
				 }
			 }
			 
			 //System.out.println(companyProcessMap);
			 
			String query="SELECT ob FROM BPMNProcessInfo ob where ob.compamnyCode=:companyCode and ob.initatedBy=:userId";
			 
			 Query objQuery=objJProvider.createQuery(query);
			 objQuery.setParameter("companyCode", companyCode);
			 objQuery.setParameter("userId", userId);
			 
			 List<BPMNProcessInfo> resultList=objQuery.getResultList();
			 if(resultList!=null && resultList.size()>0)
			 {
				 for(BPMNProcessInfo objBPMNProcessInfo:resultList)
				 {
					 MyReqDTO obj=new MyReqDTO();
					 Map<String,Object> valueMap = new HashMap<String, Object>();
					 //System.out.println(objBPMNProcessInfo.getBpmnTxRefNo()+"#"+objBPMNProcessInfo.getStatusCode()+"#"+companyProcessMap.get(objBPMNProcessInfo.getBpmnId())+"#"+obj.getCurrentStepName());
					 obj.setBpmTxRefno(objBPMNProcessInfo.getBpmnTxRefNo());
					 obj.setStatusCode(objBPMNProcessInfo.getStatusCode());
					 obj.setProcessName(companyProcessMap.get(objBPMNProcessInfo.getBpmnId()));
					 obj.setCurrentStepName(objBPMNProcessInfo.getCurrentStepName());
					 if(objBPMNProcessInfo.getProcessdata() != null) {
					 JSONObject json =new JSONObject(objBPMNProcessInfo.getProcessdata());
					 json.keySet().forEach(keyStr ->
					    {
					        Object keyvalue = json.get(keyStr);
					        //System.out.println("key: "+ keyStr + " value: " + keyvalue);
					        if(keyStr!=null && !keyStr.equalsIgnoreCase("All") && !keyStr.equalsIgnoreCase("selectedresponse"))
					        	valueMap.put(keyStr, keyvalue);
					    });
					 obj.setKeyValues(valueMap);
					 }
					 myReqList.add(obj);
				 }
			 }
			 
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw ex;
		}
		return myReqList;
	}
	
	
}
	
	
