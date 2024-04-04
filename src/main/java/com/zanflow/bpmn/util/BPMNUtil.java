package com.zanflow.bpmn.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zanflow.bpmn.BPMNData;
import com.zanflow.bpmn.dao.BPMNTaskDAO;
import com.zanflow.bpmn.dto.BPMNCompleterResultDTO;
import com.zanflow.bpmn.dto.BPMNStepDTO;
import com.zanflow.bpmn.dto.TXNDocumentDTO;
import com.zanflow.bpmn.dto.TaskDTO;
import com.zanflow.bpmn.engine.BPMNEngine;
import com.zanflow.bpmn.model.BPMNProcessInfo;
import com.zanflow.bpmn.service.notification.NotificationHelper;
import com.zanflow.common.db.Constants;

public class BPMNUtil {

	protected String companyCode;

	public BPMNUtil(String companyCode)
	{
		this.companyCode=companyCode;
	}
	
	public String initiateTransaction(String processName,Map<String,Object> datamap) throws Exception
	{
		BPMNCompleterResultDTO objBPMNCompleterResultDTO=null;
		BPMNTaskDAO objBPMNTaskDAO=null;
		try
		{
			objBPMNTaskDAO=new BPMNTaskDAO(Constants.DB_PUNIT);
			String bpmnid = objBPMNTaskDAO.findBPMNID(processName, companyCode);
			if(bpmnid != null) {
			JSONObject json = objBPMNTaskDAO.findStepDetails(bpmnid, companyCode);
			String taskname = json.getJSONObject("step").getJSONObject("stepDefinition").getString("NAME");
			BPMNEngine objBPMNEngine = BPMNEngine.getInstance();
			BPMNData objBPMNData=new BPMNData();
			objBPMNData.setCompanyCode(companyCode);
			objBPMNData.setBpmnId(bpmnid);
			objBPMNData.setBpmnTaskId(-1);
			objBPMNData.setpUnitName(Constants.DB_PUNIT); 
			objBPMNData.setCompanyCode(companyCode);
			//JSONObject formData = new JSONObject(objTaskDTO.getFormData());
			////System.out.println("processdata -----------> " +formData.get("processdata").toString());
			//Map<String, Object> datamap =mapper.readValue(formData.get("processdata").toString(), Map.class);
			objBPMNData.setDataMap(datamap);
			objBPMNData.setStepName(taskname);
			objBPMNCompleterResultDTO=objBPMNEngine.initiateTransaction(objBPMNData,"SYS");
			
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
			/*if(objTaskDTO.getTxnDocList()!=null && objTaskDTO.getTxnDocList().size()>0)
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
		
			if(objTaskDTO.getComments()!=null) {
			   //updateComments(objTaskDTO,objBPMNTaskDAO);
			}*/
			objBPMNTaskDAO.commit();
			
			NotificationHelper objNotificationHelper=new NotificationHelper();
			//System.out.println("Notification helpper current step");
			objNotificationHelper.sendMail(objBPMNCompleterResultDTO.getBpmnTxRefNo(), bpmnid, taskname, "Complete", datamap,null,companyCode, processName);
			//System.out.println("Notification helpper next step ");
			objNotificationHelper.sendMail(objBPMNCompleterResultDTO.getBpmnTxRefNo(), objBPMNCompleterResultDTO.getBpmnNextSteps(), "Create", datamap,bpmnid,companyCode,bpmnid , processName);
			}
	
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
		if(objBPMNCompleterResultDTO != null) {
			return objBPMNCompleterResultDTO.getBpmnTxRefNo();
		}else {
			return "";
		}
	}
	
	public BPMNCompleterResultDTO saveTask(String refId,Map<String,Object> datamap) throws Exception
	{
		BPMNCompleterResultDTO objBPMNCompleterResultDTO=null;
		BPMNTaskDAO objBPMNTaskDAO=null;
		try
		{
			objBPMNTaskDAO=new BPMNTaskDAO(Constants.DB_PUNIT);
			BPMNProcessInfo objBPMNProcessInfo=objBPMNTaskDAO.findBPMNProcessInfo(refId);
			JSONObject obj = new JSONObject(datamap);
			objBPMNProcessInfo.setProcessdata(obj.toString());
			
			objBPMNTaskDAO.begin();
			
			/**SaveComments*/
			//updateComments(objTaskDTO,objBPMNTaskDAO);
			
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
	
	
	public Map<String,Object> getProcessData(String refId) throws Exception
	{
		BPMNTaskDAO objBPMNTaskDAO=null;
		try
		{
			objBPMNTaskDAO=new BPMNTaskDAO(Constants.DB_PUNIT);
			BPMNProcessInfo objBPMNProcessInfo=objBPMNTaskDAO.findBPMNProcessInfo(refId);
			
			ObjectMapper mapper = new ObjectMapper();
			Map<String, Object> datamap =mapper.readValue(objBPMNProcessInfo.getProcessdata(), Map.class);
			return datamap;
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
		
	}
	
}
