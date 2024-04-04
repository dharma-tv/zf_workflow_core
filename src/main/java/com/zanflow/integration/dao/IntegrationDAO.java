package com.zanflow.integration.dao;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zanflow.bpmn.dao.BPMNTaskDAO;
import com.zanflow.bpmn.dto.TXNDocumentDTO;
import com.zanflow.bpmn.exception.ApplicationException;
import com.zanflow.bpmn.exception.JPAPersistenceException;
import com.zanflow.bpmn.model.BPMNProcessInfo;
import com.zanflow.bpmn.model.TXNDocments;
import com.zanflow.cms.serv.GoogleCloudStorageServiceImpl;
import com.zanflow.cms.serv.StorageService;
import com.zanflow.common.db.Constants;
import com.zanflow.common.db.JPATransaction;
import com.zanflow.common.db.JPersistenceProvider;
import com.zanflow.integration.dto.DropDownDTO;
import com.zanflow.integration.dto.FieldSchemaDTO;
import com.zanflow.sec.model.Role;

public class IntegrationDAO extends JPATransaction{
	
	public IntegrationDAO(String pJunitName)throws ApplicationException
	{
		//System.out.println("#IntegrationDAO#pJunitName#"+pJunitName);
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


	public IntegrationDAO(JPersistenceProvider objJProvider) throws ApplicationException
	{
		try{
			if (objJProvider != null)
			{
				this.objJProvider = objJProvider;
				strJPUnitName = objJProvider.getpUnitName();
				//System.out.println("# IntegrationDAO objJProvider constructor invoked");
			}else{
				throw new ApplicationException("IntegrationDAO : JPersistenceProvider is null");
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
	
	public boolean validateApiKey(String companyCode,String apikey)throws ApplicationException
	{
		long t1 = System.currentTimeMillis();
		//System.out.println("#IntegrationDAO#validateApiKey#"+companyCode);
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
			//System.out.println("#IntegrationDAO#validateApiKey#" + (System.currentTimeMillis() - t1));
		}
	  }
	
	
	public List<DropDownDTO> getProcessList(String companyCode) throws ApplicationException	{
		long t1 = System.currentTimeMillis();
		//System.out.println("#IntegrationDAO#getProcessList#"+companyCode);
		List<Object[]> processList=null;
		List<DropDownDTO> dto = new ArrayList<DropDownDTO>();
		String query = "select t.processname,t.processid,t.rendertype from {h-schema}zf_cfg_bpmnprocess t where rendertype='PROCESS' and isactive='Y' and companyCode=:companyCode";
		try
		{
			Query objQuery=objJProvider.createNativeQuery(query);
			objQuery.setParameter("companyCode", companyCode);
			
			processList=objQuery.getResultList();
			//System.out.println(processList.size() + " -------- " + processList);
			for (int i = 0; i < processList.size(); i++) {
				 DropDownDTO processDTO = new DropDownDTO();
			     processDTO.setLabel((String) processList.get(i)[0]);
			     processDTO.setValue((String) processList.get(i)[1]);
			     processDTO.setId((String) processList.get(i)[1]);
			     processDTO.setSample("");
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
			//System.out.println("#IntegrationDAO#getProcessList#End#TT#" + (System.currentTimeMillis() - t1));
		}
		return dto;
	  }
	
	
		
	public List<DropDownDTO> getstepList(String companyCode, String processid) throws ApplicationException	{
		long t1 = System.currentTimeMillis();
		List<Object[]> obj=null;
		JSONArray steps = null;
		
		List<DropDownDTO> stepList = new ArrayList<DropDownDTO>();
		try {
			//System.out.println("#IntegrationDAO#getstepList#" + processid);
			if (processid !=null) {
			obj=  objJProvider.createNativeQuery("select processconfig ->> 'steps' as Steps, processid from {h-schema}zf_cfg_bpmnprocess where processid = :processid and companyCode=:companycode").setParameter("processid", processid).setParameter("companycode", companyCode).getResultList();
			////System.out.println("obj "+obj.get(0)[0]);
			if(obj.size()>0) {
			steps = new  JSONArray(String.valueOf(obj.get(0)[0]));
			JSONObject step = null;
			for (Iterator<Object> i = steps.iterator(); i.hasNext(); ){
				   step = (JSONObject)i.next();
				   DropDownDTO stepDTO = new DropDownDTO();
				   stepDTO.setLabel(((JSONObject)step.get("stepDefinition")).getString("LABEL"));
				   stepDTO.setValue(((JSONObject)step.get("stepDefinition")).getString("NAME"));
				   stepDTO.setId(((JSONObject)step.get("stepDefinition")).getString("NAME"));
				   stepDTO.setSample("");
				   stepList.add(stepDTO);
			}	
			}
			return stepList;	
			}	
		} catch (Exception e) {
			e.printStackTrace();
			//log.printErrorMessage(e);
			throw new ApplicationException(new Exception(e));
		}
		finally
		{
			//System.out.println("#IntegrationDAO#getstepList#End#TT#"+ (System.currentTimeMillis() - t1));
		}
		return stepList;
	}
	
	public DropDownDTO createSubscription(String companyCode, String processid, String stepName, String url) throws ApplicationException	{
		
		//long t1 = System.currentTimeMillis();
		int count=0;
	    String message="Subscription created successfully";
	
	    DropDownDTO response = new DropDownDTO();
		try {
			objJProvider.begin();
			//objJProvider.createNativeQuery("delete from {h-schema}zf_id_appintegrationdetails where companycode=:companyCode").setParameter("companyCode", companyCode).executeUpdate();
			count = objJProvider.createNativeQuery("insert into {h-schema}zf_cfg_subscription (companycode, processid, stepname, hookurl) values (:companyCode, :processid, :stepName, :url)").setParameter("companyCode", companyCode).setParameter("processid", processid).setParameter("stepName", stepName).setParameter("url", url).executeUpdate();
			objJProvider.commit();
			if(count == 0) {
				throw new ApplicationException();
			}else {
				response.setId(url);
			}
			}	
		catch (Exception e) {
			e.printStackTrace();
			//log.printErrorMessage(e);
			throw e;
		}
		return response;

	}
	
	public DropDownDTO deleteSubscription(String companyCode, String url) throws ApplicationException	{
		
		//long t1 = System.currentTimeMillis();
		int count=0;
	    String message="Subscription deleted successfully";
	
	    DropDownDTO response = new DropDownDTO();
		try {
			objJProvider.begin();
			count = objJProvider.createNativeQuery("delete from {h-schema}zf_cfg_subscription where companycode=:companyCode and hookurl:url").setParameter("companyCode", companyCode).setParameter("url", url).executeUpdate();
			objJProvider.commit();
			if(count == 0) {
				throw new ApplicationException();
			}else {
				response.setId(message);
			}
			}	
		catch (Exception e) {
			e.printStackTrace();
			//log.printErrorMessage(e);
			throw e;
		}
		return response;
	}
	
	
	public List<Map<String, Object>> getTaskData(String processid, String companycode) throws Exception{
		
		long t1 = System.currentTimeMillis();
		List<Object[]> obj=null;
		
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		try {
			//System.out.println("#IntegrationDAO#getstepList#" + processid);
			if (processid !=null) {
			obj=  objJProvider.createNativeQuery("select cast(processdata as varchar), processid from {h-schema}zf_txn_bpmnprocessinfo where processid = :processid and companyCode=:companycode order by createdtime desc limit 1").setParameter("processid", processid).setParameter("companycode", companycode).getResultList();
			////System.out.println("obj "+obj.get(0)[0]);
			if(obj.size()>0) {
			//steps = new  JSONArray(String.valueOf(obj.get(0)[0]));
			JSONObject processdata = new JSONObject();
			ObjectMapper mapper = new ObjectMapper();
			Map<String, Object> datamap =mapper.readValue(String.valueOf(obj.get(0)[0]), Map.class);
			dataList.add(datamap);
			//System.out.println("jsonschema --> " + String.valueOf(obj.get(0)[0]));
			//System.out.println("jsonschema.keySet() --> " + dataList);
			/*for(Iterator iterator = jsonschema.keySet().iterator(); iterator.hasNext();) {
			    String key = (String) iterator.next();
			    //System.out.println(jsonschema.get(key));
			    //if(((JSONObject)jsonschema.get(key)).has("GRIDNAME") == false || ((JSONObject)jsonschema.get(key)).get("GRIDNAME") == null || ((JSONObject)jsonschema.get(key)).getString("GRIDNAME") == "") {
			       DropDownDTO dataDTO = new DropDownDTO();
			       dataDTO.setLabel(((JSONObject)jsonschema.get(key)).getString("LABEL"));
			       dataDTO.setValue("");
			       dataDTO.setId(((JSONObject)jsonschema.get(key)).getString("NAME"));
			       dataDTO.setSample("");
				   dataList.add(dataDTO);
			    //}
			}*/
			}
			return dataList;	
			}	
		} catch (Exception e) {
			e.printStackTrace();
			//log.printErrorMessage(e);
			throw new ApplicationException(new Exception(e));
		}
		finally
		{
			//System.out.println("#IntegrationDAO#getstepList#End#TT#"+ (System.currentTimeMillis() - t1));
		}
		return dataList;
	}
	
	
	public List<FieldSchemaDTO> getMetaData(String processid, String companycode) throws Exception{
		
		long t1 = System.currentTimeMillis();
		List<Object[]> obj=null;
		
		List<FieldSchemaDTO> fieldSchemaList = new ArrayList<FieldSchemaDTO>();
		try {
			//System.out.println("#IntegrationDAO#getstepList#" + processid);
			if (processid !=null) {
			obj=  objJProvider.createNativeQuery("select processconfig ->> 'jsonschema' as jsonschema, processconfig ->> 'steps' as Steps from {h-schema}zf_cfg_bpmnprocess  where processid = :processid and companyCode=:companycode").setParameter("processid", processid).setParameter("companycode", companycode).getResultList();
			if(obj.size()>0) {
			JSONObject jsonschema  = new  JSONObject(String.valueOf(obj.get(0)[0]));
			JSONArray steps = new  JSONArray(String.valueOf(obj.get(0)[1]));
			//steps = obj.get(0).toJSONArray(steps);
			JSONObject step = null;
			for (Iterator<Object> i = steps.iterator(); i.hasNext(); ){
				   step = (JSONObject)i.next();
				   if("FLEXI".equalsIgnoreCase(((JSONObject)step.get("stepDefinition")).getString("BPMNSTEPTYPE"))) {
					   break;
				   }
			}
			String stepName = step.getJSONObject("stepDefinition").getString("NAME");
			
			for(Iterator iterator = jsonschema.keySet().iterator(); iterator.hasNext();) {
			    String key = (String) iterator.next();
			    //System.out.println(jsonschema.get(key));
			    //if(((JSONObject)jsonschema.get(key)).has("GRIDNAME") == false || ((JSONObject)jsonschema.get(key)).get("GRIDNAME") == null || ((JSONObject)jsonschema.get(key)).getString("GRIDNAME") == "") {
			      // if(jsonschema.getJSONObject(key).getJSONObject("VISIBILITY").has(stepName) && (jsonschema.getJSONObject(key).getJSONObject("VISIBILITY").getString(stepName) == "Mandatory" || jsonschema.getJSONObject(key).getJSONObject("VISIBILITY").getString(stepName) == "Editable")) {
			       FieldSchemaDTO dataDTO = new FieldSchemaDTO();
			       dataDTO.setLabel(((JSONObject)jsonschema.get(key)).getString("LABEL"));
			       dataDTO.setKey(((JSONObject)jsonschema.get(key)).getString("NAME"));
			       String fieldType = ((JSONObject)jsonschema.get(key)).getString("FIELDTYPENAME");
			       if(!("STRING".equalsIgnoreCase(fieldType) || "INTEGER".equalsIgnoreCase(fieldType) || "FILE".equalsIgnoreCase(fieldType))) {
			    	   fieldType = "string";
			       }else {
			    	   fieldType = fieldType.toLowerCase();
			       }
			       dataDTO.setType(fieldType);
			       //if(jsonschema.getJSONObject(key).getJSONObject("VISIBILITY").getString(stepName) == "Mandatory") {
			    	//   dataDTO.setRequired(true);
			       //}else {
			    	   dataDTO.setRequired(false);
			       //}
			       dataDTO.setList(false);
			       fieldSchemaList.add(dataDTO);
				 //}
			    //}
			}
			}
			return fieldSchemaList;	
			}	
		} catch (Exception e) {
			e.printStackTrace();
			throw new ApplicationException(new Exception(e));
		}
		finally
		{
			//System.out.println("#IntegrationDAO#getstepList#End#TT#"+ (System.currentTimeMillis() - t1));
		}
		return fieldSchemaList;
	}

	public void saveDocuments(JSONObject processdata, String companyCode,String bpmnTxRefNo) throws Exception{
		
		//for(Iterator iterator = processdata.keySet().iterator(); iterator.hasNext();) {
		boolean fileUpload = false;
		for (String key: processdata.keySet()) {
			//String key = (String) iterator.next();
			String value = "";
			try{
				value = processdata.getString(key);
			}catch (Exception e) {
				
			}
			if(value.startsWith("https://zapier.com/")) {
				fileUpload= true;
				URL url = new URL(value);
				HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
				int responseCode = httpConn.getResponseCode();
				
				
				if (responseCode == HttpURLConnection.HTTP_OK) {
				String fileName = "";
				String disposition = httpConn.getHeaderField("Content-Disposition");
				String contentType = httpConn.getContentType();
				int contentLength = httpConn.getContentLength();
				

				if (disposition != null) {
				// extracts file name from header field
				int index = disposition.indexOf("filename=");
				if (index > 0) {
					fileName = disposition.substring(index + 10,disposition.length() - 1);}
				} else {
					fileName = value.substring(value.lastIndexOf("/") + 1,value.length());
				}

				InputStream inputStream = httpConn.getInputStream();
				byte[] buffer = new byte[8192];
			    int bytesRead;
			    ByteArrayOutputStream output = new ByteArrayOutputStream();
			    while ((bytesRead = inputStream.read(buffer)) != -1)
			    {
			        output.write(buffer, 0, bytesRead);
			    }
			    TXNDocumentDTO doc = uploadDocument(companyCode, bpmnTxRefNo, fileName, contentType, output.toByteArray());
			    JSONObject docJson = new JSONObject();
			    docJson.put("documentId", doc.getDocumentId());
			    docJson.put("documentName", doc.getDocumentName());
			    processdata.put(key, docJson);
				} else {
					//System.out.println("No file to download. Server replied HTTP code: " + responseCode);
				}
				httpConn.disconnect();
				//{"documentId":109,"documentName":"1.png"}
			}
		}
		if(fileUpload) {
			saveProcessData(bpmnTxRefNo, processdata);
		}
	}
	
	public TXNDocumentDTO uploadDocument(String companyCode,String bpmnTxRefNo, String filename, String contentType, byte[] data) throws Exception
	{
		BPMNTaskDAO objBPMNTaskDAO=null;
		TXNDocumentDTO objTXNDocumentDTO=null;
		try
		{
			objBPMNTaskDAO=new BPMNTaskDAO(Constants.DB_PUNIT);
			objBPMNTaskDAO.begin();
			TXNDocments objTXNDocments=objBPMNTaskDAO.createDocument(bpmnTxRefNo, "START", filename, null,"ZAPIER", companyCode,contentType, null);
			objBPMNTaskDAO.commit();
			StorageService service = new GoogleCloudStorageServiceImpl();
			service.uploadFile(data, filename, companyCode,objTXNDocments.getDocumentId());
			if(objTXNDocments!=null)
			{
				objTXNDocumentDTO=new TXNDocumentDTO();
				objTXNDocumentDTO.setBpmnTxRefNo(bpmnTxRefNo);
				objTXNDocumentDTO.setCompanyCode(objTXNDocments.getCompanyCode());
				objTXNDocumentDTO.setCreatedBy(objTXNDocments.getUserId());
				objTXNDocumentDTO.setStepName(objTXNDocments.getStepName());
				objTXNDocumentDTO.setDocumentType(objTXNDocments.getDocumentType());
				objTXNDocumentDTO.setDocumentName(objTXNDocments.getDocumentName());
				objTXNDocumentDTO.setCreatedTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss a").format(objTXNDocments.getCreatedTime()));
				objTXNDocumentDTO.setDocumentId(objTXNDocments.getDocumentId());
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			if(objBPMNTaskDAO!=null && objBPMNTaskDAO.isActive())
			{
				objBPMNTaskDAO.rollback();
			}
			throw new Exception(ex);
		}
		finally
		{
			if(objBPMNTaskDAO!=null)
			{
				objBPMNTaskDAO.close(Constants.DB_PUNIT);
			}
		}
		return objTXNDocumentDTO;
	}
	
	public TXNDocumentDTO saveProcessData(String bpmnTxRefNo, JSONObject processdata) throws Exception
	{
		BPMNTaskDAO objBPMNTaskDAO=null;
		TXNDocumentDTO objTXNDocumentDTO=null;
		try
		{
			objBPMNTaskDAO=new BPMNTaskDAO(Constants.DB_PUNIT);
			BPMNProcessInfo objBPMNProcessInfo=objBPMNTaskDAO.findBPMNProcessInfo(bpmnTxRefNo);
			objBPMNProcessInfo.setProcessdata(processdata.toString());
			
			objBPMNTaskDAO.begin();
			objBPMNTaskDAO.updateBPMNProcessInfo(objBPMNProcessInfo);
			objBPMNTaskDAO.commit();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			if(objBPMNTaskDAO!=null && objBPMNTaskDAO.isActive())
			{
				objBPMNTaskDAO.rollback();
			}
			throw new Exception(ex);
		}
		finally
		{
			if(objBPMNTaskDAO!=null)
			{
				objBPMNTaskDAO.close(Constants.DB_PUNIT);
			}
		}
		return objTXNDocumentDTO;
	}
	
	public static void main(String[] args) {
		//System.out.println("https://zapier.com/trte rtr".startsWith("https://zapier.com/"));
	}
	
	}
