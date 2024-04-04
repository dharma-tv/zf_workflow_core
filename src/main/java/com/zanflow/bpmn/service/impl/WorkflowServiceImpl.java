package com.zanflow.bpmn.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zanflow.bpmn.BPMNData;
import com.zanflow.bpmn.dao.BPMNTaskDAO;
import com.zanflow.bpmn.dao.MasterDAO;
import com.zanflow.bpmn.dto.BPMNCompleterResultDTO;
import com.zanflow.bpmn.dto.BPMNStepDTO;
import com.zanflow.bpmn.dto.CommentsDTO;
import com.zanflow.bpmn.dto.ComputeDTO;
import com.zanflow.bpmn.dto.TXNDocumentDTO;
import com.zanflow.bpmn.dto.TXNDocumentListDTO;
import com.zanflow.bpmn.dto.TaskDTO;
import com.zanflow.bpmn.dto.master.MasterDataDTO;
import com.zanflow.bpmn.dto.process.NotificationTemplateDTO;
import com.zanflow.bpmn.dto.process.StepNotificationDTO;
import com.zanflow.bpmn.engine.BPMNEngine;
import com.zanflow.bpmn.exception.ApplicationException;
import com.zanflow.bpmn.exception.JPAIllegalStateException;
import com.zanflow.bpmn.model.BPMNComments;
import com.zanflow.bpmn.model.BPMNNotification;
import com.zanflow.bpmn.model.BPMNProcess;
import com.zanflow.bpmn.model.BPMNProcessInfo;
import com.zanflow.bpmn.model.BPMNTask;
import com.zanflow.bpmn.model.TXNDocments;
import com.zanflow.bpmn.service.notification.NotificationHelper;
import com.zanflow.bpmn.util.screen.FunctionsUtil;
import com.zanflow.bpmn.util.screen.MasterHelper;
import com.zanflow.cms.serv.GoogleCloudStorageServiceImpl;
import com.zanflow.cms.serv.StorageService;
import com.zanflow.common.db.Constants;
import com.zanflow.sec.dao.UserMgmtDAO;
import com.zanflow.sec.model.User;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;



public class WorkflowServiceImpl 
{

	//@Autowired
	//private AWSS3Service service;
	
	public BPMNCompleterResultDTO initiateTransaction(@RequestBody TaskDTO objTaskDTO) throws Exception
	{
		BPMNCompleterResultDTO objBPMNCompleterResultDTO=null;
		BPMNTaskDAO objBPMNTaskDAO=null;
		try
		{
			ObjectMapper mapper = new ObjectMapper();
			BPMNEngine objBPMNEngine = BPMNEngine.getInstance();
			BPMNData objBPMNData=new BPMNData();
			objBPMNData.setCompanyCode(objTaskDTO.getCompanyCode());
			objBPMNData.setBpmnId(objTaskDTO.getBpmnId());
			objBPMNData.setBpmnTaskId(objTaskDTO.getTaskId());
			objBPMNData.setpUnitName(Constants.DB_PUNIT); 
			objBPMNData.setCompanyCode(objTaskDTO.getCompanyCode());
			JSONObject formData = new JSONObject(objTaskDTO.getFormData());
			//System.out.println("processdata -----------> " +formData.get("processdata").toString());
			Map<String, Object> datamap =mapper.readValue(formData.get("processdata").toString(), Map.class);
			objBPMNData.setDataMap(datamap);
			objBPMNData.setStepName(objTaskDTO.getTaskName());
			objBPMNCompleterResultDTO=objBPMNEngine.initiateTransaction(objBPMNData,objTaskDTO.getLockedUser());
			objBPMNTaskDAO=new BPMNTaskDAO(Constants.DB_PUNIT);
			BPMNProcessInfo objBPMNProcessInfo=objBPMNTaskDAO.findBPMNProcessInfo(objBPMNCompleterResultDTO.getBpmnTxRefNo());
			//ObjectMapper mapper = new ObjectMapper();
			//String strData=mapper.writeValueAsString(objBPMNCompleterResultDTO.getDataMap());
			JSONObject obj = new JSONObject(objBPMNCompleterResultDTO.getDataMap());
			//System.out.println("processdata 333 -----------> " +objBPMNCompleterResultDTO.getDataMap().keySet());
			//System.out.println("processdata 2222 -----------> " +obj.toString());
			objBPMNProcessInfo.setProcessdata(obj.toString());
			objBPMNTaskDAO.begin();
			
			
			
			if(objBPMNCompleterResultDTO.getBpmnNextSteps()!=null && objBPMNCompleterResultDTO.getBpmnNextSteps().size()>0)
			{
				String currStepName="";
				for(BPMNStepDTO objStep: objBPMNCompleterResultDTO.getBpmnNextSteps())
				{
					currStepName=currStepName+objStep.getStepName()+",";
				}
				currStepName=currStepName.substring(0,currStepName.length()-1);
				objBPMNProcessInfo.setCurrentStepName(currStepName);
			}
			objBPMNTaskDAO.updateBPMNProcessInfo(objBPMNProcessInfo);
			if(objTaskDTO.getTxnDocList()!=null && objTaskDTO.getTxnDocList().size()>0)
			{
				List<String> docIds=new ArrayList<String>();
				for(TXNDocumentDTO objTXNDocumentDTO:objTaskDTO.getTxnDocList())
				{
					docIds.add(String.valueOf(objTXNDocumentDTO.getDocumentId()));
				}
				int updateCount=objBPMNTaskDAO.updateDocuments(objBPMNCompleterResultDTO.getBpmnTxRefNo(), docIds);
				//System.out.println("no of documents update -----------> " +updateCount);
			}
			
			objTaskDTO.setRefId(objBPMNCompleterResultDTO.getBpmnTxRefNo());
			if(objTaskDTO.getComments()!=null) {
				for(int i=0; i < objTaskDTO.getComments().size(); i++) {
					objTaskDTO.getComments().get(i).setRefId(objBPMNCompleterResultDTO.getBpmnTxRefNo());
				}
			}
			/**SaveComments*/
			if(objTaskDTO.getComments()!=null) {
			   updateComments(objTaskDTO,objBPMNTaskDAO);
			}
			objBPMNTaskDAO.commit();
			
			NotificationHelper objNotificationHelper=new NotificationHelper();
			//System.out.println("Notification helpper current step");
			objNotificationHelper.sendMail(objBPMNCompleterResultDTO.getBpmnTxRefNo(), objTaskDTO.getBpmnId(), objTaskDTO.getTaskName(), "Complete", datamap,null,objTaskDTO.getCompanyCode(), objTaskDTO.getProcessName());
			//System.out.println("Notification helpper next step ");
			objNotificationHelper.sendMail(objBPMNCompleterResultDTO.getBpmnTxRefNo(), objBPMNCompleterResultDTO.getBpmnNextSteps(), "Create", datamap,objTaskDTO.getBpmnId(),objTaskDTO.getCompanyCode(),objTaskDTO.getBpmnId() , objTaskDTO.getProcessName());
	
		}
		catch(Exception ex)
		{
			if(objBPMNTaskDAO!=null && objBPMNTaskDAO.isActive())
			{
				objBPMNTaskDAO.rollback();
			}
			throw new Exception(ex);
		}
		finally {
			if(objBPMNTaskDAO!=null)
			{
				objBPMNTaskDAO.close(Constants.DB_PUNIT);
			}
		}
		return objBPMNCompleterResultDTO;
	}
	
	public BPMNCompleterResultDTO completeTask(TaskDTO objTaskDTO) throws Exception
	{
		BPMNCompleterResultDTO objBPMNCompleterResultDTO=null;
		BPMNTaskDAO objBPMNTaskDAO=null;
		try
		{
			ObjectMapper mapper = new ObjectMapper();
			BPMNEngine objBPMNEngine = BPMNEngine.getInstance();
			BPMNData objBPMNData=new BPMNData();
			objBPMNData.setCompanyCode(objTaskDTO.getCompanyCode());
			objBPMNData.setBpmnId(objTaskDTO.getBpmnId());
			objBPMNData.setBpmnTaskId(objTaskDTO.getTaskId());
			objBPMNData.setpUnitName(Constants.DB_PUNIT); 
			JSONObject formData = new JSONObject(objTaskDTO.getFormData());
			
			Map<String, Object> datamap =mapper.readValue(formData.get("processdata").toString(), Map.class);
			objBPMNData.setDataMap(datamap);
			objBPMNData.setStepName(objTaskDTO.getTaskName());
			objBPMNData.setCompletedBy(objTaskDTO.getCompletedBy());
			
			objBPMNCompleterResultDTO=objBPMNEngine.completeTask(objBPMNData);
			objBPMNTaskDAO=new BPMNTaskDAO(Constants.DB_PUNIT);
			
			BPMNProcessInfo objBPMNProcessInfo=objBPMNTaskDAO.findBPMNProcessInfo(objBPMNCompleterResultDTO.getBpmnTxRefNo());
			//String strData=mapper.writeValueAsString(objBPMNCompleterResultDTO.getDataMap());
			JSONObject obj = new JSONObject(objBPMNCompleterResultDTO.getDataMap());
			//System.out.println("processdata 333 -----------> " +objBPMNCompleterResultDTO.getDataMap().keySet());
			//System.out.println("processdata 2222 -----------> " +obj.toString());
			objBPMNProcessInfo.setProcessdata(obj.toString());
			
			objBPMNTaskDAO.begin();
			
			/**SaveComments*/
			updateComments(objTaskDTO,objBPMNTaskDAO);
			
			/**Save/Update BPMNProcessDAta*/
			if(objBPMNCompleterResultDTO.getBpmnNextSteps()!=null && objBPMNCompleterResultDTO.getBpmnNextSteps().size()>0)
			{
				String currStepName="";
				for(BPMNStepDTO objStep: objBPMNCompleterResultDTO.getBpmnNextSteps())
				{
					currStepName=currStepName+objStep.getStepName()+",";
				}
				currStepName=currStepName.substring(0,currStepName.length()-1);
				objBPMNProcessInfo.setCurrentStepName(currStepName);
			}
			objBPMNTaskDAO.updateBPMNProcessInfo(objBPMNProcessInfo);
			
			objBPMNTaskDAO.commit();
			objBPMNTaskDAO.callSubscriptions(objTaskDTO.getCompanyCode(), objTaskDTO.getBpmnId(),objTaskDTO.getTaskName(), obj.toString());
			NotificationHelper objNotificationHelper=new NotificationHelper();
			//System.out.println("Notification helpper current step");
			objNotificationHelper.sendMail(objBPMNCompleterResultDTO.getBpmnTxRefNo(), objTaskDTO.getBpmnId(), objTaskDTO.getTaskName(), "Complete", datamap,null,objTaskDTO.getCompanyCode(), objTaskDTO.getProcessName());
			//System.out.println("Notification helpper next step ");
			objNotificationHelper.sendMail(objBPMNCompleterResultDTO.getBpmnTxRefNo(), objBPMNCompleterResultDTO.getBpmnNextSteps(), "Create", datamap,null,objTaskDTO.getCompanyCode(),objTaskDTO.getBpmnId(), objTaskDTO.getProcessName());
		}
		catch(Exception ex)
		{
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
		return objBPMNCompleterResultDTO;
	}
	
	public BPMNCompleterResultDTO saveTask(TaskDTO objTaskDTO) throws Exception
	{
		BPMNCompleterResultDTO objBPMNCompleterResultDTO=null;
		BPMNTaskDAO objBPMNTaskDAO=null;
		try
		{
			ObjectMapper mapper = new ObjectMapper();
			JSONObject formData = new JSONObject(objTaskDTO.getFormData());
			Map<String, String> datamap =mapper.readValue(formData.get("processdata").toString(), Map.class);
			objBPMNTaskDAO=new BPMNTaskDAO(Constants.DB_PUNIT);
			BPMNProcessInfo objBPMNProcessInfo=objBPMNTaskDAO.findBPMNProcessInfo(objTaskDTO.getRefId());
			JSONObject obj = new JSONObject(datamap);
			objBPMNProcessInfo.setProcessdata(obj.toString());
			
			objBPMNTaskDAO.begin();
			
			/**SaveComments*/
			updateComments(objTaskDTO,objBPMNTaskDAO);
			
			/**Save/Update BPMNProcessDAta*/
			objBPMNTaskDAO.updateBPMNProcessInfo(objBPMNProcessInfo);
			
			objBPMNTaskDAO.commit();
		}
		catch(Exception ex)
		{
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
		return objBPMNCompleterResultDTO;
	}
	
	public BPMNCompleterResultDTO cancelTask(TaskDTO objTaskDTO) throws Exception
	{
		BPMNCompleterResultDTO objBPMNCompleterResultDTO=null;
		BPMNTaskDAO objBPMNTaskDAO=null;
		try
		{
			
			objBPMNTaskDAO=new BPMNTaskDAO(Constants.DB_PUNIT);
			objBPMNTaskDAO.begin();
			objBPMNTaskDAO.updateBPMNTaskStatus(objTaskDTO.getTaskId(), 1,null);
			objBPMNTaskDAO.commit();	
		
		}
		catch(Exception ex)
		{
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
		return objBPMNCompleterResultDTO;
	}
	
	public void updateComments(TaskDTO objTaskDTO,BPMNTaskDAO objBPMNTaskDAO) throws ApplicationException {
		try{
			/**Clean all Comments of this task and then Save all new comments of this task.*/
			int commnetCleanCnt = objBPMNTaskDAO.removeComments(objTaskDTO.getRefId(), objTaskDTO.getTaskId());
			int commentCount=0;
			//System.out.println("CleanComments#cnt#"+commnetCleanCnt);
			for (CommentsDTO commentsDTO : objTaskDTO.getComments()) {
				if(objTaskDTO.getTaskId()==commentsDTO.getTaskId()) {
					commentCount++;
					BPMNComments comments = new BPMNComments();
					BeanUtils.copyProperties(commentsDTO, comments);
					comments.setCommentSeq(commentCount);
					objBPMNTaskDAO.getObjJProvider().save(comments);
				}
			}
			//System.out.println("CleanComments#cnt#"+commnetCleanCnt+"#"+commentCount);
		}catch (ApplicationException e) {
			throw e;
		}finally {
			
		}
	}
	public TaskDTO getLaunchTaskStep(String bpmnId, String userId, String companycode) throws Exception{
		BPMNTaskDAO objBPMNTaskDAO = null;
		UserMgmtDAO objUserMgmtDAO = null;
		JSONObject json = null;
		TaskDTO dto = new TaskDTO();
		try {
			objBPMNTaskDAO = new BPMNTaskDAO(Constants.DB_PUNIT);
			
			json=objBPMNTaskDAO.findStepDetails(bpmnId, companycode);
			LinkedHashMap<String,String> computeFieldMap=new LinkedHashMap<String,String>();
			Map<String, Object> dataMap = new HashMap<String, Object>();
			String companyCode=json.getString("companyCode");
			//System.out.println("companyCode#"+companyCode);
			json.remove("companyCode");
			dto.setTaskName(json.getJSONObject("step").getJSONObject("stepDefinition").getString("NAME"));
			dto.setStepLabel(json.getJSONObject("step").getJSONObject("stepDefinition").getString("LABEL"));
			//JSONArray fieldArr=json.getJSONArray("fields");
			
			JSONObject jsonschemaobj = json.getJSONObject("jsonschema");
			Iterator<String> keys = jsonschemaobj.keys();

			while(keys.hasNext()) {
			    String key = keys.next();
			    if (jsonschemaobj.get(key) instanceof JSONObject) {
			    	JSONObject fieldJSON= jsonschemaobj.getJSONObject(key);
					String fieldName=fieldJSON.getString("NAME");
					String defaultValue=fieldJSON.getString("DEFAULTVALUE");
					//System.out.println("fieldName#"+fieldName+"#defaultValue#"+defaultValue);
					if(defaultValue!=null && (defaultValue.equalsIgnoreCase("=CURRENTUSER()") || defaultValue.equalsIgnoreCase("=INITIATOR()"))) {
						dataMap.put(fieldName, userId);
					}else if (defaultValue!=null && defaultValue.equalsIgnoreCase("=INITIATOR_MANAGER()")) {
						try {
						objUserMgmtDAO=new UserMgmtDAO(Constants.DB_PUNIT);
						User user = objUserMgmtDAO.findUser(userId);
						System.out.println("INITIATOR_MANAGER#"+fieldName+"#value#"+user.getManagerId());
						if(user !=null) {
							dataMap.put(fieldName, user.getManagerId());
						}
						}finally {
							if(objUserMgmtDAO!=null)
							{
								objUserMgmtDAO.close(Constants.DB_PUNIT);
							}
						}
					}else if(defaultValue!=null)
					{
						dataMap.put(fieldName, defaultValue);
					}
					if(!fieldJSON.isNull("COMPUTEFORMULA"))
					{
						String computeFormula=fieldJSON.getString("COMPUTEFORMULA");
						if(computeFormula !=null && computeFormula!="") {
						   computeFieldMap.put(fieldName, computeFormula);
						}
					}
			    }
			}
			
			
				
				if(computeFieldMap.size()>0)
				{
					try {
					Binding objBinding = new Binding();
					objBinding.setVariable("zf", dataMap);
					objBinding.setVariable("master", new MasterHelper(companyCode));
					objBinding.setVariable("fn", new FunctionsUtil(companyCode));
					GroovyShell shell = new GroovyShell(objBinding);
					
					ScriptEngineManager manager = new ScriptEngineManager();
					ScriptEngine engine = manager.getEngineByName("js");
					Bindings scope = engine.createBindings();
					scope.put("zf", dataMap);
					
					Set<String> fieldNames = computeFieldMap.keySet();
					Set<String> remainingFieldNames = new HashSet<String>();
					int k=0;
					do {
						if(remainingFieldNames.size()>0) {
							fieldNames = remainingFieldNames;
							remainingFieldNames = new HashSet<String>();
						}
						int i = 0;
						k++;
						for(String fieldName:fieldNames)
						{
							
							boolean skip=false;
							String[] tmpFieldNames = fieldNames.toArray(new String[0]);
							String formula=computeFieldMap.get(fieldName);
							for(int j=i+1;j<tmpFieldNames.length;j++)
							{ 
								if(formula.contains("zf."+tmpFieldNames[j])) {
									skip = true;
									remainingFieldNames.add(fieldName);
									//System.out.println("skippedfieldName#"+fieldName+"#formula#"+formula);
									break;
								}
							}
							i++;
							if(!skip){
							//System.out.println("fieldName#"+fieldName+"#formula#"+formula);
							String computedValue = "";
							if(formula.startsWith("eval(")) {
							   computedValue = String.valueOf(engine.eval(formula, scope));
							}else{
						       computedValue = (String) shell.evaluate(formula);
							}
							//System.out.println("fieldName#"+fieldName+"#formula#"+formula+"#computedValue#"+computedValue);
							dataMap.put(fieldName, computedValue);
							}
						}
					}while(remainingFieldNames.size()>0 && k<computeFieldMap.keySet().size());
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
				//System.out.println("dataMap#"+dataMap);
				json.put("processdata", dataMap);
			
			dto.setFormData(json.toString());
			dto.setBpmnId(bpmnId);
			List<TXNDocumentDTO> docListDTO=new ArrayList<TXNDocumentDTO>();
			dto.setTxnDocList(docListDTO);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally 
		{
			if(objBPMNTaskDAO!=null)
			{
				objBPMNTaskDAO.close(Constants.DB_PUNIT);
			}
			if(objUserMgmtDAO!=null)
			{
				objUserMgmtDAO.close(Constants.DB_PUNIT);
			}
		}
		return dto;
	}
	
	public TaskDTO getEnqDetailStep(String refNo) throws Exception{
		BPMNTaskDAO objBPMNTaskDAO = null;
		JSONObject json = null;
		TaskDTO dto = new TaskDTO();
		try {
			objBPMNTaskDAO = new BPMNTaskDAO(Constants.DB_PUNIT);
			dto.setRefId(refNo);
			dto.setTaskName("ENQUIRY");
			dto=objBPMNTaskDAO.findEnqTaskDetails(dto);
			ArrayList<TaskDTO> taskHistory = (ArrayList<TaskDTO>) objBPMNTaskDAO.getTaskHistory(refNo);
			dto.setTaskHistory(taskHistory);
			//dto.setFormData(json.toString());
			//dto.setBpmnId(bpmnId);
			ArrayList<CommentsDTO> commentLst = new ArrayList<CommentsDTO>();
			for (BPMNComments comments : (ArrayList<BPMNComments>) objBPMNTaskDAO.getComments(refNo)) {
				CommentsDTO commentdto = new CommentsDTO();
				BeanUtils.copyProperties(comments, commentdto);
				commentLst.add(commentdto);
			}
			dto.setComments(commentLst);
			List<TXNDocments> objDocList=objBPMNTaskDAO.getDocumentList(dto.getCompanyCode(),refNo);
			List<TXNDocumentDTO> docListDTO=new ArrayList<TXNDocumentDTO>();
			if(objDocList!=null && objDocList.size()>0)
			{
				for(TXNDocments objTXNDocments:objDocList)
				{
					TXNDocumentDTO objTXNDocumentDTO=new TXNDocumentDTO();
					objTXNDocumentDTO.setBpmnTxRefNo(refNo);
					objTXNDocumentDTO.setCompanyCode(objTXNDocments.getCompanyCode());
					objTXNDocumentDTO.setCreatedBy(objTXNDocments.getUserId());
					objTXNDocumentDTO.setStepName(objTXNDocments.getStepName());
					objTXNDocumentDTO.setDocumentType(objTXNDocments.getDocumentType());
					objTXNDocumentDTO.setDocumentName(objTXNDocments.getDocumentName());
					objTXNDocumentDTO.setCreatedTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss a").format(objTXNDocments.getCreatedTime()));
					objTXNDocumentDTO.setDocumentId(objTXNDocments.getDocumentId());
					docListDTO.add(objTXNDocumentDTO);
				}
			}
			dto.setTaskId(-1);
			dto.setTxnDocList(docListDTO);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally 
		{
			if(objBPMNTaskDAO!=null)
			{
				objBPMNTaskDAO.close(Constants.DB_PUNIT);
			}
		}
		return dto;
	}
	
	public TaskDTO getWidgetDetail(String widgetid, String userId, String companycode) throws Exception{
		BPMNTaskDAO objBPMNTaskDAO = null;
		JSONObject json = null;
		TaskDTO dto = new TaskDTO();
		try {
			objBPMNTaskDAO = new BPMNTaskDAO(Constants.DB_PUNIT);
			json=objBPMNTaskDAO.findWidgetDetails(widgetid, companycode);
			LinkedHashMap<String,String> computeFieldMap=new LinkedHashMap<String,String>();
			Map<String, Object> dataMap = new HashMap<String, Object>();
			String companyCode=json.getString("companyCode");
			//System.out.println("companyCode#"+companyCode);
			json.remove("companyCode");
			//dto.setTaskName(json.getJSONObject("step").getJSONObject("stepDefinition").getString("LABEL"));
			dto.setTaskName("WIDGET");
			//JSONArray fieldArr=json.getJSONArray("fields");
			
			JSONObject jsonschemaobj = json.getJSONObject("jsonschema");
			Iterator<String> keys = jsonschemaobj.keys();

			while(keys.hasNext()) {
			    String key = keys.next();
			    if (jsonschemaobj.get(key) instanceof JSONObject) {
			    	JSONObject fieldJSON= jsonschemaobj.getJSONObject(key);
					String fieldName=fieldJSON.getString("NAME");
					String defaultValue=fieldJSON.getString("DEFAULTVALUE");
					//System.out.println("fieldName#"+fieldName+"#defaultValue#"+defaultValue);
					if(defaultValue!=null && defaultValue.equalsIgnoreCase("=CURRENTUSER()")) {
						dataMap.put(fieldName, userId);
					}else if(defaultValue!=null)
					{
						dataMap.put(fieldName, defaultValue);
					}
					if(!fieldJSON.isNull("COMPUTEFORMULA"))
					{
						String computeFormula=fieldJSON.getString("COMPUTEFORMULA");
						computeFieldMap.put(fieldName, computeFormula);
					}
			    }
			}
			
			
				
				if(computeFieldMap.size()>0)
				{
					Binding objBinding = new Binding();
					objBinding.setVariable("zf", dataMap);
					objBinding.setVariable("master", new MasterHelper(companyCode));
					objBinding.setVariable("fn", new FunctionsUtil(companyCode));
					GroovyShell shell = new GroovyShell(objBinding);
					
					ScriptEngineManager manager = new ScriptEngineManager();
					ScriptEngine engine = manager.getEngineByName("js");
					Bindings scope = engine.createBindings();
					scope.put("zf", dataMap);
					
					for(String fieldName:computeFieldMap.keySet())
					{
						String formula=computeFieldMap.get(fieldName);
						//System.out.println("fieldName#"+fieldName+"#formula#"+formula);
						String computedValue = "";
						if(formula.startsWith("eval(")) {
						   computedValue = String.valueOf(engine.eval(formula, scope));
						}else{
					       computedValue = (String) shell.evaluate(formula);
						}
						//System.out.println("fieldName#"+fieldName+"#formula#"+formula+"#computedValue#"+computedValue);
						dataMap.put(fieldName, computedValue);
					}
				}
				//System.out.println("dataMap#"+dataMap);
				json.put("processdata", dataMap);
			
			dto.setFormData(json.toString());
			dto.setBpmnId(widgetid);
			List<TXNDocumentDTO> docListDTO=new ArrayList<TXNDocumentDTO>();
			dto.setTxnDocList(docListDTO);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally 
		{
			if(objBPMNTaskDAO!=null)
			{
				objBPMNTaskDAO.close(Constants.DB_PUNIT);
			}
		}
		return dto;
	}
	
	public TaskDTO getTask(TaskDTO task) throws Exception{
		String bpmnId=task.getBpmnId();
		BPMNTaskDAO objBPMNTaskDAO = null;
		JSONObject json = null;
		BPMNTask bpmntask =null;
		boolean unlock= false;
		try {
			objBPMNTaskDAO = new BPMNTaskDAO(Constants.DB_PUNIT);
			bpmntask =objBPMNTaskDAO.findBPMNTaskById(task.getTaskId());
			
			if(bpmntask.getLockedUser() !=null && !bpmntask.getLockedUser().equalsIgnoreCase(task.getLockedUser())) {
				if(objBPMNTaskDAO.findLastLockedTimeDiff(task.getTaskId()) > 15 ) {
					unlock = true;
				}
				//System.out.println("Time Diff of locked user "+unlock);
			}
			
			if(bpmntask != null && bpmntask.getBpmnTaskId() >0 && (bpmntask.getLockedUser() ==null || bpmntask.getLockedUser().equalsIgnoreCase(task.getLockedUser()) || unlock) ) {
				json=objBPMNTaskDAO.findTaskDetails(task);
				task.setFormData(json.toString());
				
				//TODO check if task history needs to be seperate service
				ArrayList<TaskDTO> taskHistory = (ArrayList<TaskDTO>) objBPMNTaskDAO.getTaskHistory(task.getRefId());
				task.setTaskHistory(taskHistory);
				task.setStepLabel(bpmntask.getStepLabel());
				/**
				 * Prepare comments List for the Txn
				 */
				ArrayList<CommentsDTO> commentLst = new ArrayList<CommentsDTO>();
				for (BPMNComments comments : (ArrayList<BPMNComments>) objBPMNTaskDAO.getComments(task.getRefId())) {
					CommentsDTO commentdto = new CommentsDTO();
					BeanUtils.copyProperties(comments, commentdto);
					commentLst.add(commentdto);
				}
				task.setComments(commentLst);
				List<TXNDocments> objDocList=objBPMNTaskDAO.getDocumentList(task.getCompanyCode(),task.getRefId());
				List<TXNDocumentDTO> docListDTO=new ArrayList<TXNDocumentDTO>();
				if(objDocList!=null && objDocList.size()>0)
				{
					for(TXNDocments objTXNDocments:objDocList)
					{
						TXNDocumentDTO objTXNDocumentDTO=new TXNDocumentDTO();
						objTXNDocumentDTO.setBpmnTxRefNo(task.getRefId());
						objTXNDocumentDTO.setCompanyCode(objTXNDocments.getCompanyCode());
						objTXNDocumentDTO.setCreatedBy(objTXNDocments.getUserId());
						objTXNDocumentDTO.setStepName(objTXNDocments.getStepName());
						objTXNDocumentDTO.setDocumentType(objTXNDocments.getDocumentType());
						objTXNDocumentDTO.setDocumentName(objTXNDocments.getDocumentName());
						objTXNDocumentDTO.setCreatedTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss a").format(objTXNDocments.getCreatedTime()));
						objTXNDocumentDTO.setDocumentId(objTXNDocments.getDocumentId());
						docListDTO.add(objTXNDocumentDTO);
					}
				}
				task.setTxnDocList(docListDTO);
				if(task.getPrevioustaskId() != 0) {
				objBPMNTaskDAO.begin();
				objBPMNTaskDAO.updateBPMNTaskStatus(task.getPrevioustaskId(), 1,task.getLockedUser());
				objBPMNTaskDAO.commit();
				}
				objBPMNTaskDAO.begin();
				objBPMNTaskDAO.updateBPMNTaskStatus(task.getTaskId(), 3,task.getLockedUser());
				objBPMNTaskDAO.commit();
				
				
				}
			
			else {
				if(task.getPrevioustaskId() != 0) {
					objBPMNTaskDAO.begin();
					objBPMNTaskDAO.updateBPMNTaskStatus(task.getPrevioustaskId(), 1,task.getLockedUser());
					objBPMNTaskDAO.commit();
					}
				//System.out.println("Task is Already Locked by user - "+bpmntask.getLockedUser()+"!!!");
				task.setResponseCode("409");
				task.setResponsMsg("Task is Already Locked by user - "+bpmntask.getLockedUser()+"!!!");
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		}
		finally 
		{
			if(objBPMNTaskDAO!=null)
			{
				objBPMNTaskDAO.close(Constants.DB_PUNIT);
			}
		}
		return task;
	}
	
	public TXNDocumentDTO uploadDocument(String companyCode,String bpmnTxRefNo,String stepName,String userId,MultipartFile fileObj) throws Exception
	{
		BPMNTaskDAO objBPMNTaskDAO=null;
		TXNDocumentDTO objTXNDocumentDTO=null;
		try
		{
			objBPMNTaskDAO=new BPMNTaskDAO(Constants.DB_PUNIT);
			byte[] docData=fileObj.getBytes();
			objBPMNTaskDAO.begin();
			TXNDocments objTXNDocments=objBPMNTaskDAO.createDocument(bpmnTxRefNo, stepName, fileObj.getOriginalFilename(), null,userId, companyCode,fileObj.getContentType(), null);
			objBPMNTaskDAO.commit();
			StorageService service = new GoogleCloudStorageServiceImpl();
			service.uploadFile(fileObj, companyCode,objTXNDocments.getDocumentId());
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
	
	public TXNDocments getDocument(long documentId) throws Exception
	{
		BPMNTaskDAO objBPMNTaskDAO=null;
		TXNDocments objTxnDocments=null;
		try
		{
			objBPMNTaskDAO=new BPMNTaskDAO(Constants.DB_PUNIT);
			objTxnDocments=objBPMNTaskDAO.getDocument(documentId);
			if(objTxnDocments==null)
			{
				throw new Exception("Document Not available");
			}
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw new Exception(ex);
		}
		finally
		{
			if(objBPMNTaskDAO!=null)
			{
				objBPMNTaskDAO.close(Constants.DB_PUNIT);
			}
		}
		return objTxnDocments;
	}

	public int  deleteDocument(String companyCode,long documentId) throws Exception
	{
		int delCount=0;
		BPMNTaskDAO objBPMNTaskDAO=null;
		try
		{
			objBPMNTaskDAO=new BPMNTaskDAO(Constants.DB_PUNIT);
			objBPMNTaskDAO.begin();
			delCount=objBPMNTaskDAO.deleteDocument(companyCode,documentId);
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
		return delCount;
	}

	public TXNDocumentListDTO getALLDocuments(String companyCode,String bpmnTxRefNo) throws Exception
	{
		TXNDocumentListDTO objTXNDocumentListDTO=new TXNDocumentListDTO();
		BPMNTaskDAO objBPMNTaskDAO=null;
		try
		{
			objBPMNTaskDAO=new BPMNTaskDAO(Constants.DB_PUNIT);
			List<TXNDocments> objDocList=objBPMNTaskDAO.getDocumentList(companyCode,bpmnTxRefNo);
			if(objDocList!=null && objDocList.size()>0)
			{
				List<TXNDocumentDTO> docListDTO=new ArrayList<TXNDocumentDTO>();
				for(TXNDocments objTXNDocments:objDocList)
				{
					TXNDocumentDTO objTXNDocumentDTO=new TXNDocumentDTO();
					objTXNDocumentDTO.setBpmnTxRefNo(bpmnTxRefNo);
					objTXNDocumentDTO.setCompanyCode(objTXNDocments.getCompanyCode());
					objTXNDocumentDTO.setCreatedBy(objTXNDocments.getUserId());
					objTXNDocumentDTO.setStepName(objTXNDocments.getStepName());
					objTXNDocumentDTO.setDocumentType(objTXNDocments.getDocumentType());
					objTXNDocumentDTO.setDocumentName(objTXNDocments.getDocumentName());
					objTXNDocumentDTO.setCreatedTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss a").format(objTXNDocments.getCreatedTime()));
					objTXNDocumentDTO.setDocumentId(objTXNDocments.getDocumentId());
					docListDTO.add(objTXNDocumentDTO);
				}
				objTXNDocumentListDTO.setTxnDocList(docListDTO);
			}
			else
			{
				//System.out.println(bpmnTxRefNo+" No Documents available");
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw new Exception(ex);
		}
		finally
		{
			if(objBPMNTaskDAO!=null)
			{
				objBPMNTaskDAO.close(Constants.DB_PUNIT);
			}
		}
		return objTXNDocumentListDTO;
	}
	
	
   public JSONObject getDSProcessList(String companyCode, String processtype) throws JPAIllegalStateException {
		BPMNTaskDAO objBPMNTaskDAO = null;
		JSONObject json = null;
		try {
			objBPMNTaskDAO = new BPMNTaskDAO(Constants.DB_PUNIT);
			json=objBPMNTaskDAO.getDSProcessList(companyCode, processtype);
			}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally 
		{
			if(objBPMNTaskDAO!=null)
			{
				objBPMNTaskDAO.close(Constants.DB_PUNIT);
			}
		}
		return json;
	}
   
   
   public String checkProcessId(String companyCode, String processid) throws JPAIllegalStateException {
 		BPMNTaskDAO objBPMNTaskDAO = null;
 		String response = null;
 		try {
 			objBPMNTaskDAO = new BPMNTaskDAO(Constants.DB_PUNIT);
 			response=objBPMNTaskDAO.checkProcessId(companyCode,processid);
 			}
 		catch(Exception e) {
 			e.printStackTrace();
 		}
 		finally 
 		{
 			if(objBPMNTaskDAO!=null)
 			{
 				objBPMNTaskDAO.close(Constants.DB_PUNIT);
 			}
 		}
 		return response;
 	}
   
   
   public JSONObject getDSProcess(String companyCode, String processId) throws JPAIllegalStateException {
		BPMNTaskDAO objBPMNTaskDAO = null;
		JSONObject json = null;
		try {
			objBPMNTaskDAO = new BPMNTaskDAO(Constants.DB_PUNIT);
			json=objBPMNTaskDAO.getDSProcess(companyCode, processId);
			}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally 
		{
			if(objBPMNTaskDAO!=null)
			{
				objBPMNTaskDAO.close(Constants.DB_PUNIT);
			}
		}
		return json;
	}
   
   public String createDSProcess(String companyCode,String processJson, String processtype) throws JPAIllegalStateException {
		BPMNTaskDAO objBPMNTaskDAO = null;
		JSONObject json = new JSONObject(processJson);
		String message = null;
		try {
				objBPMNTaskDAO = new BPMNTaskDAO(Constants.DB_PUNIT);
				message=objBPMNTaskDAO.createDSProcess(companyCode, json, processtype);
				JSONArray masterlist= json.getJSONArray("MASTERLIST");
				
				if(masterlist!= null && masterlist.length()>0) {
					for(int i=0;i<masterlist.length();i++) {
						JSONObject masterjson = this.getMasterDetail("ZF", masterlist.getString(i));
						JSONObject masterjsonComp = this.getMasterDetail(companyCode, masterlist.getString(i));
						if(!masterjsonComp.has("metadata")) {
						     this.createMaster(companyCode, masterlist.getString(i), masterjson.getJSONArray("metadata").toString());
						}
					}
				}
			}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally 
		{
			if(objBPMNTaskDAO!=null)
			{
				objBPMNTaskDAO.close(Constants.DB_PUNIT);
			}
		}
		return message;
	}
   

   public String deleteDSProcess(String companyCode, String processId) throws JPAIllegalStateException {
		BPMNTaskDAO objBPMNTaskDAO = null;
		//JSONObject json = new JSONObject(processJson);
		String message = null;
		try {
				objBPMNTaskDAO = new BPMNTaskDAO(Constants.DB_PUNIT);
				message=objBPMNTaskDAO.deleteDSProcess(companyCode, processId);
			}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally 
		{
			if(objBPMNTaskDAO!=null)
			{
				objBPMNTaskDAO.close(Constants.DB_PUNIT);
			}
		}
		return message;
	}
   
   public void createNotificationTemplate(String companyCode,JSONObject objJSON) 
   {
	   	BPMNTaskDAO objBPMNTaskDAO = null;
	   	try 
	   	{
			objBPMNTaskDAO = new BPMNTaskDAO(Constants.DB_PUNIT);
//			JSONObject objJSON = new JSONObject(processJson);
			JSONObject process = (JSONObject) objJSON.get("process");
			String processId=process.getString("PROCESSID");
			String bpmnId=objJSON.getJSONObject("bpmn").getString("BPMNID");
			//System.out.println("createNotificationTemplate#bpmnId#"+bpmnId);
			JSONArray stepArray=objJSON.getJSONArray("steps");
			//System.out.println("createNotificationTemplate#stepArray#"+stepArray);
			ArrayList<StepNotificationDTO> notificationList=new ArrayList<StepNotificationDTO>();
			HashMap<String,NotificationTemplateDTO>  notifyTemplateMap=new HashMap<String, NotificationTemplateDTO>();
			if(!objJSON.isNull("templates"))
			{
				JSONArray templateArray=objJSON.getJSONArray("templates");
				//System.out.println("createNotificationTemplate#templatesArr#"+templateArray);
				if(templateArray!=null && templateArray.length()>0)
				{
					for(int j=0;j<templateArray.length();j++)
					{
						JSONObject notificationJSON= templateArray.getJSONObject(j);
						NotificationTemplateDTO objNotificationTemplateDTO=new NotificationTemplateDTO();
						objNotificationTemplateDTO.setMessage(notificationJSON.getString("message"));
						objNotificationTemplateDTO.setTemplateId(notificationJSON.getString("templateid"));
						objNotificationTemplateDTO.setTemplateName(notificationJSON.getString("templatename"));
						objNotificationTemplateDTO.setSubject(notificationJSON.getString("subject"));
						notifyTemplateMap.put(objNotificationTemplateDTO.getTemplateId(), objNotificationTemplateDTO);
					}
				}
			}
			if(stepArray!=null && stepArray.length()>0)
			{
				for(int i=0;i<stepArray.length();i++)
				{
					JSONObject stepObj=stepArray.getJSONObject(i);
					JSONObject stepDefObj=stepObj.getJSONObject("stepDefinition");
					if(!stepObj.isNull("eventtemplates"))
					{
						JSONArray notficationArray=stepObj.getJSONArray("eventtemplates");
						//System.out.println("createNotificationTemplate#notficationArray#"+notficationArray);
						if(notficationArray!=null && notficationArray.length()>0)
						{
							for(int j=0;j<notficationArray.length();j++)
							{
								JSONObject notificationJSON= notficationArray.getJSONObject(j);
								StepNotificationDTO objStepNotificationDTO=new StepNotificationDTO();
								objStepNotificationDTO.setTemplateId(notificationJSON.getString("templateid"));
								objStepNotificationDTO.setTriggerType(notificationJSON.getString("triggertype"));
								objStepNotificationDTO.setCcAddr(notificationJSON.getString("cc"));
								objStepNotificationDTO.setToAddr(notificationJSON.getString("to"));
								objStepNotificationDTO.setStepName(stepDefObj.getString("NAME"));
								objStepNotificationDTO.setBpmnId(process.getString("BPMNID"));
								objStepNotificationDTO.setProcessId(process.getString("PROCESSID"));
								objStepNotificationDTO.setSubsTotype(notificationJSON.has("subsTotype") ? notificationJSON.getBoolean("subsTotype"):false);
								objStepNotificationDTO.setSubsCCtype(notificationJSON.has("subsCCtype") ? notificationJSON.getBoolean("subsCCtype"):false);
								if(objStepNotificationDTO.isSubsTotype()) {
								objStepNotificationDTO.setSubsToField(notificationJSON.getString("subsToField"));
								}
								if(objStepNotificationDTO.isSubsCCtype()) {
								objStepNotificationDTO.setSubsCCField(notificationJSON.getString("subsCCField"));
								}
								objStepNotificationDTO.setCompanyCode(companyCode);
								notificationList.add(objStepNotificationDTO);
							}
						}
					}
				}
			}
			if(notificationList.size()>0)
			{
				objBPMNTaskDAO.begin();
				objBPMNTaskDAO.deleteNotification(companyCode, processId, bpmnId);
				for(StepNotificationDTO objStepNotificationDTO:notificationList)
				{
					BPMNNotification objBPMNotification=new BPMNNotification();
					objBPMNotification.setBpmnId(bpmnId);
					objBPMNotification.setCompanyCode(companyCode);
					objBPMNotification.setProcessId(processId);
					objBPMNotification.setStepName(objStepNotificationDTO.getStepName());
					objBPMNotification.setTriggerEvent(objStepNotificationDTO.getTriggerType());
					if(objStepNotificationDTO.isSubsTotype()) {
					objBPMNotification.setToEmail("zf."+objStepNotificationDTO.getSubsToField());
					}else {
					  objBPMNotification.setToEmail(objStepNotificationDTO.getToAddr());
					}
					if(objStepNotificationDTO.isSubsCCtype()) {
						objBPMNotification.setCcEmail("zf."+objStepNotificationDTO.getSubsCCField());
				    }else {
					objBPMNotification.setCcEmail(objStepNotificationDTO.getCcAddr());
				    }
					NotificationTemplateDTO objNotificationTemplateDTO=notifyTemplateMap.get(objStepNotificationDTO.getTemplateId());
					objBPMNotification.setMailContent(objNotificationTemplateDTO.getMessage());
					objBPMNotification.setSubject(objNotificationTemplateDTO.getSubject());
					
					objBPMNTaskDAO.createBPMNNotification(objBPMNotification);
				}
				objBPMNTaskDAO.commit();
			}
	   	}
	   	catch(Exception e)
	   	{
			e.printStackTrace();
		}
		finally 
		{
			if(objBPMNTaskDAO!=null)
			{
				try 
				{
					objBPMNTaskDAO.close(Constants.DB_PUNIT);
				} 
				catch (JPAIllegalStateException e) 
				{
					e.printStackTrace();
				}
			}
		}
   }
   
	/**
	 * Save DS Process details
	 * @param companyCode
	 * @param processJson
	 * @return
	 * @throws ApplicationException
	 */
	public String saveDSProcess(String companyCode, String processJson) throws ApplicationException {
		
		try (BPMNTaskDAO objBPMNTaskDAO = new BPMNTaskDAO(Constants.DB_PUNIT)) {
			
			
			return objBPMNTaskDAO.saveDSProcess(companyCode, processJson);

		} catch (Exception e) {
			e.printStackTrace();
			throw new ApplicationException(e.getMessage());
		}
	}
	
    public String createDSProcessVersion(String companyCode, String processJson) throws ApplicationException {
		
		try (BPMNTaskDAO objBPMNTaskDAO = new BPMNTaskDAO(Constants.DB_PUNIT)) {
			
			
			return objBPMNTaskDAO.createDSProcessVersion(companyCode, processJson);

		} catch (Exception e) {
			e.printStackTrace();
			throw new ApplicationException(e.getMessage());
		}
	}
	
	public ArrayList<TaskDTO> getActiveTaskList(String companyCode,String userId,String filterType,String filterValue, String bpmnid) throws Exception
	{
		BPMNTaskDAO objBPMNTaskDAO = null;
		ArrayList<TaskDTO> taskList = null;
		try
		{
			 objBPMNTaskDAO = new BPMNTaskDAO(Constants.DB_PUNIT);
			 taskList = (ArrayList<TaskDTO>) objBPMNTaskDAO.getActiveTaskList(companyCode, userId, filterType, filterValue, bpmnid); 
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw new Exception(ex);
		}
		finally
		{
			if(objBPMNTaskDAO!=null)
			{		
				objBPMNTaskDAO.close(Constants.DB_PUNIT);
			}
		}
		return taskList;
	}

	/**
	 * 
	 * @param companyCode
	 * @param processJson
	 * @throws ApplicationException 
	 */
	public void deployDSProcess(String companyCode, String processJson) throws ApplicationException {
		long t1 = System.currentTimeMillis();
		
		try (BPMNTaskDAO objBPMNTaskDAO = new BPMNTaskDAO(Constants.DB_PUNIT)) {
			
			JSONObject json = new JSONObject(processJson);
			/**Save Designed Process into DS Table*/
			objBPMNTaskDAO.saveDSProcess(companyCode, processJson);
			
			/**Merge Process details and BPMN content into workflow process table*/ 
			BPMNProcess bpmnProcess = new BPMNProcess();
			bpmnProcess.setBpmnId(json.getJSONObject("bpmn").getString("BPMNID"));
			bpmnProcess.setBpmnFileContent(json.getJSONObject("bpmn").getString("BPMNFILECONTENT"));
			bpmnProcess.setInitiaterole(getStringFromJson(json.getJSONObject("process"),"INITIATEROLE"));
			bpmnProcess.setProcessname(json.getJSONObject("process").getString("NAME"));
			bpmnProcess.setProcessConfig(json.toString());
			bpmnProcess.setMonitorrole(getStringFromJson(json.getJSONObject("process"),"MONITORROLE"));
			bpmnProcess.setEnquiryrole(getStringFromJson(json.getJSONObject("process"),"ENQUIRYROLE"));
			bpmnProcess.setCompanycode(companyCode);
			bpmnProcess.setIsactive("Y");
			bpmnProcess.setProcessid(getStringFromJson(json.getJSONObject("process"),"PROCESSID"));
			bpmnProcess.setRendertype(getStringFromJson(json.getJSONObject("process"),"RENDERTYPE"));
			objBPMNTaskDAO.getObjJProvider().begin();
			objBPMNTaskDAO.getObjJProvider().merge(bpmnProcess);
			objBPMNTaskDAO.getObjJProvider().commit();
			
			createNotificationTemplate(companyCode, json);
			BPMNEngine.getInstance().clearBpmnModelInstance(json.getJSONObject("bpmn").getString("BPMNID"));
		} catch (Exception e) {
			e.printStackTrace();
			throw new ApplicationException(e.getMessage());
		}finally{
			//System.out.println("#BpmnTaskDAO#saveDSProcess#End#TT#"+ (System.currentTimeMillis() - t1));
		}
		
	}
	
	public String editMaster(String companyCode, String masterName, String metadata) throws ApplicationException 
	{
		MasterDAO objMasterDAO = null;
		String result=null;
		try 
		{
			objMasterDAO = new MasterDAO(Constants.DB_PUNIT);
			objMasterDAO.begin();
			result=objMasterDAO.updateMaster(companyCode, masterName, metadata);
			objMasterDAO.commit();
		} 
		catch (Exception e) 
		{
			if(objMasterDAO!=null && objMasterDAO.isActive())
			{
				objMasterDAO.rollback();
			}
			e.printStackTrace();
			throw new ApplicationException(e.getMessage());
		}
		finally
		{
			if(objMasterDAO!=null )
			{
				objMasterDAO.close();
			}
		}
		return result;
	}
	
	
	public String createMaster(String companyCode, String masterName, String metadata) throws ApplicationException 
	{
		MasterDAO objMasterDAO = null;
		String result=null;
		try 
		{
			objMasterDAO = new MasterDAO(Constants.DB_PUNIT);
			objMasterDAO.begin();
			result=objMasterDAO.createMaster(companyCode, masterName, metadata);
			objMasterDAO.commit();
		} 
		catch (Exception e) 
		{
			if(objMasterDAO!=null && objMasterDAO.isActive())
			{
				objMasterDAO.rollback();
			}
			e.printStackTrace();
			throw new ApplicationException(e.getMessage());
		}
		finally
		{
			if(objMasterDAO!=null )
			{
				objMasterDAO.close();
			}
		}
		return result;
	}
	
	public ArrayList<String> getMasterList(String companyCode) throws ApplicationException {
		
		try (MasterDAO objMasterDAO = new MasterDAO(Constants.DB_PUNIT)) {
			
			
			return objMasterDAO.getMasterList(companyCode);

		} catch (Exception e) {
			e.printStackTrace();
			throw new ApplicationException(e.getMessage());
		}
	}
	
	public Map<String, String> getMasterMetaData(String companyCode) throws ApplicationException {
		
		try (MasterDAO objMasterDAO = new MasterDAO(Constants.DB_PUNIT)) {
			
			
			return objMasterDAO.getMasterMetaData(companyCode);

		} catch (Exception e) {
			e.printStackTrace();
			throw new ApplicationException(e.getMessage());
		}
	}
	
    public JSONObject getMasterDetail(String companyCode, String mastername) throws ApplicationException {
		
		try (MasterDAO objMasterDAO = new MasterDAO(Constants.DB_PUNIT)) {
			
			
			return objMasterDAO.getMasterDetail(companyCode, mastername);

		} catch (Exception e) {
			e.printStackTrace();
			throw new ApplicationException(e.getMessage());
		}
	}
   
    public Boolean deleteMaster(String companyCode, String mastername) throws ApplicationException {
		
		try (MasterDAO objMasterDAO = new MasterDAO(Constants.DB_PUNIT)) {
			
			objMasterDAO.begin();
			objMasterDAO.deleteMaster(companyCode, mastername);
			objMasterDAO.commit();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ApplicationException(e.getMessage());
		}
	}
    
   public String updateMasterData(String action,  MasterDataDTO masterdto) throws ApplicationException {
		String value=null;
		MasterDAO objMasterDAO =null;
		try  
		{
			objMasterDAO =new MasterDAO(Constants.DB_PUNIT);
			objMasterDAO.begin();
			value= objMasterDAO.updateMasterData(action, masterdto);
			objMasterDAO.commit();
		} 
		catch (Exception e)
		{
			if(objMasterDAO!=null && objMasterDAO.isActive())
			{
				objMasterDAO.rollback();
			}
			e.printStackTrace();
			throw new ApplicationException(e.getMessage());
		}
		finally 
		{
			if(objMasterDAO!=null)
			{
				objMasterDAO.close(Constants.DB_PUNIT);
			}
		}
		return value;
	}
   
   public ComputeDTO computeFields(String companycode,  ComputeDTO computedto) throws ApplicationException {
	   try
		{
			Binding objBinding = new Binding();
			ObjectMapper mapper = new ObjectMapper();
			Map<String, Object> datamap =mapper.readValue(computedto.getProcessdata(), Map.class);
			
			if(datamap!=null && datamap.size()>0)
			{
				objBinding.setVariable("zf", datamap);
				objBinding.setVariable("master", new MasterHelper(companycode));
				objBinding.setVariable("fn", new FunctionsUtil(companycode));
				
				GroovyShell shell = new GroovyShell(objBinding);
				
				if(computedto.getComputeFormulas() != null && computedto.getComputeFormulas().size()>0) {
					ArrayList<Map<String,String>> formulas = computedto.getComputeFormulas();
					for(int i=0;i<formulas.size();i++) {
						String formula = formulas.get(i).get("formula");
						String fieldname = formulas.get(i).get("fieldname");
						String computedValue = (String) shell.evaluate(formula);
						datamap.put(fieldname, computedValue);
					}
				}
				JSONObject obj = new JSONObject(datamap);
				computedto.setProcessdata(obj.toString());
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return computedto;
   }
   
   public ArrayList<String> getMasterDropDown(String companyCode, String mastername, String columnname) throws ApplicationException {
		
	  
	   
		try (MasterDAO objMasterDAO = new MasterDAO(Constants.DB_PUNIT)) {
			if("ZFUSERS".equalsIgnoreCase(mastername) && "ZFUSERS".equalsIgnoreCase(columnname)) {
			   return objMasterDAO.getAllUsers(companyCode);
			}else{
			   return objMasterDAO.getMasterDropDown(companyCode,mastername,columnname);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ApplicationException(e.getMessage());
		}
	}
   
	private String getStringFromJson(JSONObject json,String property) {
	
		try {
			return json.getString(property);
		}catch (Exception e) {
			//System.out.println(e.getMessage());
			return null;
		}
		
	}
}