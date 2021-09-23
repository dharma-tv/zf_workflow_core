package com.zanflow.bpmn.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.RequestBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zanflow.bpmn.BPMNData;
import com.zanflow.bpmn.dao.BPMNTaskDAO;
import com.zanflow.bpmn.dao.KanbanDAO;
import com.zanflow.bpmn.dto.BPMNCompleterResultDTO;
import com.zanflow.bpmn.dto.BPMNStepDTO;
import com.zanflow.bpmn.dto.CommentsDTO;
import com.zanflow.bpmn.dto.TXNDocumentDTO;
import com.zanflow.bpmn.dto.TaskDTO;
import com.zanflow.bpmn.exception.ApplicationException;
import com.zanflow.bpmn.exception.JPAIllegalStateException;
import com.zanflow.bpmn.model.BPMNComments;
import com.zanflow.bpmn.model.BPMNProcess;
import com.zanflow.bpmn.model.BPMNProcessInfo;
import com.zanflow.bpmn.model.BPMNTask;
import com.zanflow.bpmn.model.TXNDocments;
import com.zanflow.bpmn.util.screen.MasterHelper;
import com.zanflow.common.db.Constants;
import com.zanflow.kanban.dto.LanesDTO;
import com.zanflow.kanban.engine.KanbanEngine;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;



public class KanbanServiceImpl 
{

	//@Autowired
	//private AWSS3Service service;
	
	public BPMNCompleterResultDTO initiateCard(@RequestBody TaskDTO objTaskDTO) throws Exception
	{
		BPMNCompleterResultDTO objBPMNCompleterResultDTO=null;
		KanbanDAO objKanbanDAO=null;
		try
		{
			ObjectMapper mapper = new ObjectMapper();
			KanbanEngine objKanbanEngine = KanbanEngine.getInstance();
			BPMNData objBPMNData=new BPMNData();
			objBPMNData.setCompanyCode(objTaskDTO.getCompanyCode());
			objBPMNData.setBpmnId(objTaskDTO.getBpmnId());
			objBPMNData.setBpmnTaskId(objTaskDTO.getTaskId());
			objBPMNData.setpUnitName(Constants.DB_PUNIT); 
			objBPMNData.setCompanyCode(objTaskDTO.getCompanyCode());
			JSONObject formData = new JSONObject(objTaskDTO.getFormData());
			
			Map<String, Object> datamap =mapper.readValue(formData.get("processdata").toString(), Map.class);
			objBPMNData.setDataMap(datamap);
			objBPMNData.setStepName(objTaskDTO.getTaskName());
			
			objKanbanDAO=new KanbanDAO(Constants.DB_PUNIT);
			JSONObject boardconfig = objKanbanDAO.getBoardConfig(objTaskDTO.getCompanyCode(), objTaskDTO.getBpmnId());
			String cardtitle = boardconfig.getJSONObject("boardconfig").getJSONObject("process").getString("CARDTITLE");
			String carddescription = boardconfig.getJSONObject("boardconfig").getJSONObject("process").getString("CARDDESCRIPTION");
			JSONArray columns = boardconfig.getJSONObject("boardconfig").getJSONArray("columns");
			String stepname = columns.getJSONObject(0).getString("id");
			
		    ArrayList<String> columnNames = new ArrayList<String>();
			objBPMNCompleterResultDTO=objKanbanEngine.initiateCard(objBPMNData,objTaskDTO.getLockedUser(), stepname, cardtitle, carddescription);
			
			BPMNProcessInfo objBPMNProcessInfo=objKanbanDAO.findBPMNProcessInfo(objBPMNCompleterResultDTO.getBpmnTxRefNo());
			JSONObject obj = new JSONObject(datamap);
			objBPMNProcessInfo.setProcessdata(obj.toString());
			objKanbanDAO.begin();
			
			
			
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
			objKanbanDAO.updateBPMNProcessInfo(objBPMNProcessInfo);
			if(objTaskDTO.getTxnDocList()!=null && objTaskDTO.getTxnDocList().size()>0)
			{
				List<String> docIds=new ArrayList<String>();
				for(TXNDocumentDTO objTXNDocumentDTO:objTaskDTO.getTxnDocList())
				{
					docIds.add(String.valueOf(objTXNDocumentDTO.getDocumentId()));
				}
				int updateCount=objKanbanDAO.updateDocuments(objBPMNCompleterResultDTO.getBpmnTxRefNo(), docIds);
				System.out.println("no of documents update -----------> " +updateCount);
			}
			
			objTaskDTO.setRefId(objBPMNCompleterResultDTO.getBpmnTxRefNo());
			if(objTaskDTO.getComments()!=null) {
				for(int i=0; i < objTaskDTO.getComments().size(); i++) {
					objTaskDTO.getComments().get(i).setRefId(objBPMNCompleterResultDTO.getBpmnTxRefNo());
				}
			}
			/**SaveComments*/
			if(objTaskDTO.getComments()!=null) {
			   updateComments(objTaskDTO,objKanbanDAO);
			}
			objKanbanDAO.commit();
			
			//NotificationHelper objNotificationHelper=new NotificationHelper();
			//System.out.println("Notification helpper current step");
			//objNotificationHelper.sendMail(objBPMNCompleterResultDTO.getBpmnTxRefNo(), objTaskDTO.getBpmnId(), objTaskDTO.getTaskName(), "Complete", datamap,null,objTaskDTO.getCompanyCode());
			//System.out.println("Notification helpper next step ");
			//objNotificationHelper.sendMail(objBPMNCompleterResultDTO.getBpmnTxRefNo(), objBPMNCompleterResultDTO.getBpmnNextSteps(), "Create", datamap,objTaskDTO.getBpmnId(),objTaskDTO.getCompanyCode(),objTaskDTO.getBpmnId());
	
		}
		catch(Exception ex)
		{
			if(objKanbanDAO!=null && objKanbanDAO.isActive())
			{
				objKanbanDAO.rollback();
			}
			throw new Exception(ex);
		}
		finally {
			if(objKanbanDAO!=null)
			{
				objKanbanDAO.close(Constants.DB_PUNIT);
			}
		}
		return objBPMNCompleterResultDTO;
	}
	
	public BPMNCompleterResultDTO moveCard(TaskDTO objTaskDTO) throws Exception
	{
		BPMNCompleterResultDTO objBPMNCompleterResultDTO=null;
		KanbanDAO objKanbanDAO=null;
		try
		{
			objKanbanDAO=new KanbanDAO(Constants.DB_PUNIT);
			BPMNProcessInfo objBPMNProcessInfo=objKanbanDAO.findBPMNProcessInfo(objTaskDTO.getRefId());
			BPMNTask task = objKanbanDAO.findBPMNTaskById(objTaskDTO.getTaskId());
			
			task.setElementId(objTaskDTO.getTaskName());
			objBPMNProcessInfo.setCurrentStepName(objTaskDTO.getTaskName());
			objKanbanDAO.begin();
			objKanbanDAO.updateBPMNTask(task);
			/**Save/Update BPMNProcessDAta*/
			objKanbanDAO.updateBPMNProcessInfo(objBPMNProcessInfo);
			
			objKanbanDAO.commit();
		}
		catch(Exception ex)
		{
			if(objKanbanDAO!=null && objKanbanDAO.isActive())
			{
				objKanbanDAO.rollback();
			}
			throw new Exception(ex);
		}
		finally 
		{
			if(objKanbanDAO!=null)
			{
				objKanbanDAO.close(Constants.DB_PUNIT);
			}
		}
		return objBPMNCompleterResultDTO;
	}
	
	public BPMNCompleterResultDTO saveCard(TaskDTO objTaskDTO) throws Exception
	{
		BPMNCompleterResultDTO objBPMNCompleterResultDTO=null;
		KanbanDAO objKanbanDAO=null;
		try
		{
			ObjectMapper mapper = new ObjectMapper();
			JSONObject formData = new JSONObject(objTaskDTO.getFormData());
			Map<String, String> datamap =mapper.readValue(formData.get("processdata").toString(), Map.class);
			objKanbanDAO=new KanbanDAO(Constants.DB_PUNIT);
			BPMNProcessInfo objBPMNProcessInfo=objKanbanDAO.findBPMNProcessInfo(objTaskDTO.getRefId());
			JSONObject obj = new JSONObject(datamap);
			objBPMNProcessInfo.setProcessdata(obj.toString());
			BPMNTask task = objKanbanDAO.findBPMNTaskById(objTaskDTO.getTaskId());
			JSONObject boardconfig = objKanbanDAO.getBoardConfig(objTaskDTO.getCompanyCode(), objTaskDTO.getBpmnId());
			String cardtitle = boardconfig.getJSONObject("boardconfig").getJSONObject("process").getString("CARDTITLE");
			String carddescription = boardconfig.getJSONObject("boardconfig").getJSONObject("process").getString("CARDDESCRIPTION");
			
			task.setStepLabel(KanbanEngine.getCardTitle(datamap, cardtitle));
			task.setTaskSubject(KanbanEngine.getCardTitle(datamap, carddescription));
			task.setElementId(objTaskDTO.getTaskName());
			
			objBPMNProcessInfo.setCurrentStepName(objTaskDTO.getTaskName());
			objKanbanDAO.begin();
			objKanbanDAO.updateBPMNTask(task);
			
			/**SaveComments*/
			updateComments(objTaskDTO,objKanbanDAO);
			
			/**Save/Update BPMNProcessDAta*/
			objKanbanDAO.updateBPMNProcessInfo(objBPMNProcessInfo);
			
			objKanbanDAO.commit();
		}
		catch(Exception ex)
		{
			if(objKanbanDAO!=null && objKanbanDAO.isActive())
			{
				objKanbanDAO.rollback();
			}
			throw new Exception(ex);
		}
		finally 
		{
			if(objKanbanDAO!=null)
			{
				objKanbanDAO.close(Constants.DB_PUNIT);
			}
		}
		return objBPMNCompleterResultDTO;
	}
	
	public void updateComments(TaskDTO objTaskDTO,KanbanDAO objKanbanDAO) throws ApplicationException {

		try{
			/**Clean all Comments of this task and then Save all new comments of this task.*/
			int commnetCleanCnt = objKanbanDAO.removeComments(objTaskDTO.getRefId(), objTaskDTO.getTaskId());
			int commentCount=0;
			System.out.println("CleanComments#cnt#"+commnetCleanCnt);
			for (CommentsDTO commentsDTO : objTaskDTO.getComments()) {
				if(objTaskDTO.getTaskId()==commentsDTO.getTaskId()) {
					commentCount++;
					BPMNComments comments = new BPMNComments();
					BeanUtils.copyProperties(commentsDTO, comments);
					comments.setCommentSeq(commentCount);
					objKanbanDAO.getObjJProvider().save(comments);
				}
			}
			System.out.println("CleanComments#cnt#"+commnetCleanCnt+"#"+commentCount);
		}catch (ApplicationException e) {
			throw e;
		}finally {
			
		}
	}
	
	public TaskDTO getLaunchCard(String boardid, String userId, String companycode) throws Exception{
		KanbanDAO objKanbanDAO = null;
		JSONObject json = null;
		TaskDTO dto = new TaskDTO();
		try {
			objKanbanDAO = new KanbanDAO(Constants.DB_PUNIT);
			json=objKanbanDAO.getBoardConfig(companycode, boardid);
			LinkedHashMap<String,String> computeFieldMap=new LinkedHashMap<String,String>();
			Map<String, Object> dataMap = new HashMap<String, Object>();
			//String companyCode=json.getString("companycode");
			//System.out.println("companyCode#"+companyCode);
			
			JSONObject jsonschema = json.getJSONObject("boardconfig").getJSONObject("jsonschema");
			dto.setTaskName("card task");
			dto.setCompanyCode(companycode);
			dto.setTaskType("CARD");
			String[] fieldArr=JSONObject.getNames(jsonschema);
			if(fieldArr!=null && fieldArr.length>0)
			{
				for(int j=0;j<fieldArr.length;j++)
				{
					JSONObject fieldJSON= jsonschema.getJSONObject(fieldArr[j]);
					String fieldName=fieldJSON.getString("NAME");
					String defaultValue=fieldJSON.getString("DEFAULTVALUE");
					System.out.println("fieldName#"+fieldName+"#defaultValue#"+defaultValue);
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
				if(computeFieldMap.size()>0)
				{
					Binding objBinding = new Binding();
					objBinding.setVariable("zf", dataMap);
					objBinding.setVariable("master", new MasterHelper(companycode));
					GroovyShell shell = new GroovyShell(objBinding);
					
					ScriptEngineManager manager = new ScriptEngineManager();
					ScriptEngine engine = manager.getEngineByName("js");
					Bindings scope = engine.createBindings();
					scope.put("zf", dataMap);
					
					for(String fieldName:computeFieldMap.keySet())
					{
						String formula=computeFieldMap.get(fieldName);
						System.out.println("fieldName#"+fieldName+"#formula#"+formula);
						String computedValue = "";
						if(formula.startsWith("eval(")) {
						   computedValue = String.valueOf(engine.eval(formula, scope));
						}else{
					       computedValue = (String) shell.evaluate(formula);
						}
						System.out.println("fieldName#"+fieldName+"#formula#"+formula+"#computedValue#"+computedValue);
						dataMap.put(fieldName, computedValue);
					}
				}
				System.out.println("dataMap#"+dataMap);
				json.put("processdata", dataMap);
			}
			dto.setFormData(json.toString());
			dto.setBpmnId(boardid);
			List<TXNDocumentDTO> docListDTO=new ArrayList<TXNDocumentDTO>();
			dto.setTxnDocList(docListDTO);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally 
		{
			if(objKanbanDAO!=null)
			{
				objKanbanDAO.close(Constants.DB_PUNIT);
			}
		}
		return dto;
	}
	
	public TaskDTO getCard(TaskDTO card) throws Exception{
		BPMNTaskDAO objBPMNTaskDAO = null;
		KanbanDAO objKanbanDAO = null;
		JSONObject json = null;
		BPMNTask bpmntask =null;
		boolean unlock= false;
		try {
			objBPMNTaskDAO = new BPMNTaskDAO(Constants.DB_PUNIT);
			objKanbanDAO = new KanbanDAO(Constants.DB_PUNIT);
			bpmntask =objBPMNTaskDAO.findBPMNTaskById(card.getTaskId());
			
			if(bpmntask != null && bpmntask.getBpmnTaskId() >0 && (bpmntask.getLockedUser() ==null || bpmntask.getLockedUser().equalsIgnoreCase(card.getLockedUser()) || unlock) ) {
				
				json=objKanbanDAO.getBoardConfig(card.getCompanyCode(), card.getBpmnId());
				JSONObject processData = null;
				BPMNProcessInfo objBPMNProcessInfo = objBPMNTaskDAO.findBPMNProcessInfo(card.getRefId());
				if(objBPMNProcessInfo.getProcessdata() == null || objBPMNProcessInfo.getProcessdata()=="") {
					processData = new JSONObject();
				}else {
					processData = new JSONObject(objBPMNProcessInfo.getProcessdata());
				}
				
				json.put("processdata", processData);
				card.setFormData(json.toString());
				
				
				card.setStepLabel(bpmntask.getStepLabel());
				/**
				 * Prepare comments List for the Txn
				 */
				ArrayList<CommentsDTO> commentLst = new ArrayList<CommentsDTO>();
				for (BPMNComments comments : (ArrayList<BPMNComments>) objBPMNTaskDAO.getComments(card.getRefId())) {
					CommentsDTO commentdto = new CommentsDTO();
					BeanUtils.copyProperties(comments, commentdto);
					commentLst.add(commentdto);
				}
				card.setComments(commentLst);
				List<TXNDocments> objDocList=objBPMNTaskDAO.getDocumentList(card.getCompanyCode(),card.getRefId());
				List<TXNDocumentDTO> docListDTO=new ArrayList<TXNDocumentDTO>();
				if(objDocList!=null && objDocList.size()>0)
				{
					for(TXNDocments objTXNDocments:objDocList)
					{
						TXNDocumentDTO objTXNDocumentDTO=new TXNDocumentDTO();
						objTXNDocumentDTO.setBpmnTxRefNo(card.getRefId());
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
				card.setTxnDocList(docListDTO);			
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
			if(objKanbanDAO!=null)
			{
				objKanbanDAO.close(Constants.DB_PUNIT);
			}
		}
		return card;
	}
	
   public String checkBoardId(String companyCode, String boardid) throws JPAIllegalStateException {
 		KanbanDAO objKanbanDAO  = null;
 		String response = null;
 		try {
 			objKanbanDAO = new KanbanDAO(Constants.DB_PUNIT);
 			response=objKanbanDAO.checkBoardId(companyCode,boardid);
 			}
 		catch(Exception e) {
 			e.printStackTrace();
 		}
 		finally 
 		{
 			if(objKanbanDAO!=null)
 			{
 				objKanbanDAO.close(Constants.DB_PUNIT);
 			}
 		}
 		return response;
 	}
    
   public JSONObject getBoardConfig(String companyCode, String boardid) throws JPAIllegalStateException {
		KanbanDAO objKanbanDAO = null;
		JSONObject json = null;
		try {
			objKanbanDAO = new KanbanDAO(Constants.DB_PUNIT);
			json=objKanbanDAO.getBoardConfig(companyCode, boardid);
			}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally 
		{
			if(objKanbanDAO!=null)
			{
				objKanbanDAO.close(Constants.DB_PUNIT);
			}
		}
		return json;
	}
   
   public void deleteBoard(String companyCode, String boardid) throws JPAIllegalStateException {
		KanbanDAO objKanbanDAO = null;
		JSONObject json = null;
		try {
			objKanbanDAO = new KanbanDAO(Constants.DB_PUNIT);
			objKanbanDAO.begin();
			objKanbanDAO.deleteBoard(companyCode, boardid);
			objKanbanDAO.commit();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally 
		{
			if(objKanbanDAO!=null)
			{
				objKanbanDAO.close(Constants.DB_PUNIT);
			}
		}
	}
   
   public String createBoard(String companyCode,String processJson) throws JPAIllegalStateException {
		KanbanDAO objKanbanDAO = null;
		JSONObject json = new JSONObject(processJson);
		String message = null;
		try {
			    objKanbanDAO = new KanbanDAO(Constants.DB_PUNIT);
				message=objKanbanDAO.createBoard(companyCode, json);
			}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally 
		{
			if(objKanbanDAO!=null)
			{
				objKanbanDAO.close(Constants.DB_PUNIT);
			}
		}
		return message;
	}
   

	/**
	 * Save saveBoard details
	 * @param companyCode
	 * @param processJson
	 * @return
	 * @throws ApplicationException
	 */
	public String saveBoard(String companyCode, String processJson) throws ApplicationException {
		
		try (KanbanDAO objKanbanDAO = new KanbanDAO(Constants.DB_PUNIT)) {
			
			
			return objKanbanDAO.saveBoard(companyCode, processJson);

		} catch (Exception e) {
			e.printStackTrace();
			throw new ApplicationException(e.getMessage());
		}
	}
	  
	public ArrayList<LanesDTO> getActiveCardList(String companyCode,String boardid) throws Exception
	{
		KanbanDAO objKanbanDAO = null;
		ArrayList<LanesDTO> laneList = null;
		try
		{
			objKanbanDAO = new KanbanDAO(Constants.DB_PUNIT);
			laneList = (ArrayList<LanesDTO>) objKanbanDAO.getActiveCardList(companyCode, boardid);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw new Exception(ex);
		}
		finally
		{
			if(objKanbanDAO!=null)
			{		
				objKanbanDAO.close(Constants.DB_PUNIT);
			}
		}
		return laneList;
	}

	/**
	 * 
	 * @param companyCode
	 * @param processJson
	 * @throws ApplicationException 
	 */
	public void deployBoard(String companyCode, String processJson) throws ApplicationException {
		long t1 = System.currentTimeMillis();
		
		try (KanbanDAO objKanbanDAO = new KanbanDAO(Constants.DB_PUNIT)) {
			
			JSONObject json = new JSONObject(processJson);
			/**Save Designed Process into DS Table*/
			objKanbanDAO.saveBoard(companyCode, processJson);
			
			/**Merge Process details and BPMN content into workflow process table*/ 
			BPMNProcess bpmnProcess = new BPMNProcess();
			bpmnProcess.setBpmnId(getStringFromJson(json.getJSONObject("process"),"BOARDID"));
			//bpmnProcess.setBpmnFileContent(null);
			bpmnProcess.setInitiaterole(getStringFromJson(json.getJSONObject("process"),"INITIATEROLE"));
			bpmnProcess.setProcessname(json.getJSONObject("process").getString("NAME"));
			bpmnProcess.setProcessConfig(json.toString());
			//bpmnProcess.setMonitorrole(getStringFromJson(json.getJSONObject("process"),"MONITORROLE"));
			//bpmnProcess.setEnquiryrole(getStringFromJson(json.getJSONObject("process"),"ENQUIRYROLE"));
			bpmnProcess.setCompanycode(companyCode);
			bpmnProcess.setIsactive("Y");
			bpmnProcess.setProcessid(getStringFromJson(json.getJSONObject("process"),"BOARDID"));
			bpmnProcess.setRendertype(getStringFromJson(json.getJSONObject("process"),"RENDERTYPE"));
			objKanbanDAO.getObjJProvider().begin();
			objKanbanDAO.getObjJProvider().merge(bpmnProcess);
			objKanbanDAO.getObjJProvider().commit();
			
			//createNotificationTemplate(companyCode, json);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new ApplicationException(e.getMessage());
		}finally{
			System.out.println("#BpmnTaskDAO#saveDSProcess#End#TT#"+ (System.currentTimeMillis() - t1));
		}
		
	}

	private String getStringFromJson(JSONObject json,String property) {
	
		try {
			return json.getString(property);
		}catch (Exception e) {
			System.out.println(e.getMessage());
			return null;
		}
		
	}
	
	
	
}