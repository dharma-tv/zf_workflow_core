package com.zanflow.bpmn.service;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.zanflow.bpmn.dto.BPMNCompleterResultDTO;
import com.zanflow.bpmn.dto.TXNDocumentDTO;
import com.zanflow.bpmn.dto.TaskDTO;
import com.zanflow.bpmn.exception.ApplicationException;
import com.zanflow.bpmn.model.BPMNProcessInfo;
import com.zanflow.bpmn.service.impl.WorkflowServiceImpl;
import com.zanflow.integration.dao.IntegrationDAO;
import com.zanflow.integration.dto.DropDownDTO;
import com.zanflow.integration.dto.FieldSchemaDTO;
import com.zanflow.sec.common.Constants;

@RestController
@CrossOrigin(origins = "*" ,allowedHeaders ="*")
public class IntegrationService {

	
	@RequestMapping(value="/integration/validate-api-key", method = RequestMethod.GET , produces = MediaType.APPLICATION_JSON_VALUE)
	public boolean validateApiKey(HttpServletRequest request, HttpServletResponse 
			response) throws Exception{
	
		String companyCode =  request.getHeader("X-ACCOUNT-ID"); 
		String apikey = request.getHeader("X-API-KEY");
		
		try(IntegrationDAO dao = new IntegrationDAO(Constants.DB_PUNIT)){
			return dao.validateApiKey(companyCode, apikey);
		} 
		
	}
	
	
	@RequestMapping(value="/integration/get-process-list", method = RequestMethod.GET , produces = MediaType.APPLICATION_JSON_VALUE)
	public List<DropDownDTO> getProcessList(HttpServletRequest request, HttpServletResponse 
			response) throws Exception{
	
		String companyCode =  request.getHeader("X-ACCOUNT-ID"); 
		String apikey = request.getHeader("X-API-KEY");
		
		try(IntegrationDAO dao = new IntegrationDAO(Constants.DB_PUNIT)){
			if(dao.validateApiKey(companyCode, apikey)) {
				return dao.getProcessList(companyCode);
			}else {
				throw new ApplicationException("Not valid credentials");
			}
		} 
		//return null;	
	}
	
	@RequestMapping(value="/integration/get-step-list/{processId}", method = RequestMethod.GET , produces = MediaType.APPLICATION_JSON_VALUE)
	public List<DropDownDTO> getStepList(HttpServletRequest request, HttpServletResponse 
			response,@PathVariable String processId) throws Exception{
	
		String companyCode =  request.getHeader("X-ACCOUNT-ID"); 
		String apikey = request.getHeader("X-API-KEY");
		
		try(IntegrationDAO dao = new IntegrationDAO(Constants.DB_PUNIT)){
			if(dao.validateApiKey(companyCode, apikey)) {
				return dao.getstepList(companyCode, processId);
			}else {
				throw new ApplicationException("Not valid credentials");
			}
		} 
		//return null;	
	}
	
	@RequestMapping(value="/integration/subscription/{processId}/{stepName}", method = RequestMethod.POST , produces = MediaType.APPLICATION_JSON_VALUE)
	public DropDownDTO createSubscription(HttpServletRequest request, HttpServletResponse 
			response,@PathVariable String processId,@PathVariable String stepName , @RequestBody Map<String, Object> payload) throws Exception{
	
		String companyCode =  request.getHeader("X-ACCOUNT-ID"); 
		String apikey = request.getHeader("X-API-KEY");
		String url = (String)payload.get("hookUrl");
		
		try(IntegrationDAO dao = new IntegrationDAO(Constants.DB_PUNIT)){
			if(dao.validateApiKey(companyCode, apikey)) {
				return dao.createSubscription(companyCode, processId, stepName, url);
			}else {
				throw new ApplicationException("Not valid credentials");
			}
		} 
	}
	
	@RequestMapping(value="/integration/subscription", method = RequestMethod.DELETE , produces = MediaType.APPLICATION_JSON_VALUE)
	public DropDownDTO deleteSubscription(HttpServletRequest request, HttpServletResponse 
			response , @RequestBody Map<String, Object> payload) throws Exception{
	
		String companyCode =  request.getHeader("X-ACCOUNT-ID"); 
		String apikey = request.getHeader("X-API-KEY");
		String url = (String)payload.get("hookUrl");
		try(IntegrationDAO dao = new IntegrationDAO(Constants.DB_PUNIT)){
			if(dao.validateApiKey(companyCode, apikey)) {
				return dao.deleteSubscription(companyCode, url);
			}else {
				throw new ApplicationException("Not valid credentials");
			}
		} 
	}
	
	@RequestMapping(value="/integration/get-task-data/{processId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Map<String, Object>>  getTaskData(HttpServletRequest request, HttpServletResponse 
			response,@PathVariable String processId) throws Exception{
		String companyCode =  request.getHeader("X-ACCOUNT-ID"); 
		String apikey = request.getHeader("X-API-KEY");
		
		try(IntegrationDAO dao = new IntegrationDAO(Constants.DB_PUNIT)){
			if(dao.validateApiKey(companyCode, apikey)) {
				return dao.getTaskData(processId, companyCode);
			}else {
				throw new ApplicationException("Not valid credentials");
			}
		} 
	}
	
	@RequestMapping(value="/integration/process-field-meta-data/{processId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<FieldSchemaDTO>  getFieldMetaData(HttpServletRequest request, HttpServletResponse 
			response,@PathVariable String processId) throws Exception{
		String companyCode =  request.getHeader("X-ACCOUNT-ID"); 
		String apikey = request.getHeader("X-API-KEY");
		try(IntegrationDAO dao = new IntegrationDAO(Constants.DB_PUNIT)){
			if(dao.validateApiKey(companyCode, apikey)) {
				return dao.getMetaData(processId, companyCode);
			}else {
				throw new ApplicationException("Not valid credentials");
			}
		} 
	}
	
	@RequestMapping(value="/integration/initiateTransaction/{processId}", method = RequestMethod.POST)
	 public BPMNCompleterResultDTO initiateTransaction(HttpServletRequest request, HttpServletResponse 
	 response,@RequestBody String payload,@PathVariable String processId) throws Exception
	 {
		String companyCode =  request.getHeader("X-ACCOUNT-ID"); 
		String apikey = request.getHeader("X-API-KEY");
		try(IntegrationDAO dao = new IntegrationDAO(Constants.DB_PUNIT)){
			if(dao.validateApiKey(companyCode, apikey)) {
				
				WorkflowServiceImpl objWorkflowServiceImpl=new WorkflowServiceImpl();	
				BPMNCompleterResultDTO objBPMNCompleterResultDTO=null;
				try
				{
					TaskDTO objTaskDTO = objWorkflowServiceImpl.getLaunchTaskStep(processId+"_V1", "INTEGRATION", companyCode);
					//System.out.println("objTaskDTO"+ objTaskDTO.toString());
					JSONObject json = new JSONObject(objTaskDTO.getFormData());
					JSONObject processdata =new JSONObject(payload);
					json.put("processdata", processdata);		
					objTaskDTO.setFormData(json.toString());
					objTaskDTO.setBpmnId(processId+"_V1");
					objTaskDTO.setCompanyCode(companyCode);
					objBPMNCompleterResultDTO=objWorkflowServiceImpl.initiateTransaction(objTaskDTO);
					
					//int updateCount=objBPMNTaskDAO.updateDocuments(objBPMNCompleterResultDTO.getBpmnTxRefNo(), docIds);
					dao.saveDocuments(processdata, companyCode, objBPMNCompleterResultDTO.getBpmnTxRefNo());
				
				}
				catch (Exception ex) 
				{
					ex.printStackTrace();
					objBPMNCompleterResultDTO=new BPMNCompleterResultDTO();
					objBPMNCompleterResultDTO.setResponseCode("ERROR");
					objBPMNCompleterResultDTO.setResponsMsg("Unable Initiate Transaction#"+ex.getMessage());
				}
				return objBPMNCompleterResultDTO;
			}else {
				throw new ApplicationException("Not valid credentials");
			}
		} 
		
	 }
	
}
