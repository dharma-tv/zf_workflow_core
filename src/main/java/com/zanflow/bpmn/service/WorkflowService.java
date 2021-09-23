package com.zanflow.bpmn.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.zanflow.bpmn.dao.ProcessDAO;
import com.zanflow.bpmn.dto.BPMNCompleterResultDTO;
import com.zanflow.bpmn.dto.ComputeDTO;
import com.zanflow.bpmn.dto.ProcessDTO;
import com.zanflow.bpmn.dto.ResponseDTO;
import com.zanflow.bpmn.dto.TXNDocumentDTO;
import com.zanflow.bpmn.dto.TXNDocumentListDTO;
import com.zanflow.bpmn.dto.TaskDTO;
import com.zanflow.bpmn.dto.master.MasterDataDTO;
import com.zanflow.bpmn.dto.master.MasterSchemaDTO;
import com.zanflow.bpmn.exception.ApplicationException;
import com.zanflow.bpmn.model.TXNDocments;
import com.zanflow.bpmn.service.impl.WorkflowServiceImpl;
import com.zanflow.cms.serv.AWSS3ServiceImpl;
import com.zanflow.common.db.Constants;

@RestController
@CrossOrigin(origins = "*" ,allowedHeaders ="*")
public class WorkflowService {

	
	@RequestMapping(value="/getTaskList/{companyCode}/{userId}/{filterType}/{filterValue}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ArrayList<TaskDTO> getTaskList(HttpServletRequest request, HttpServletResponse 
			 response,@PathVariable String companyCode, @PathVariable String userId,@PathVariable String filterType, @PathVariable String filterValue) throws ApplicationException {
		WorkflowServiceImpl service = new WorkflowServiceImpl();
		ArrayList<TaskDTO> taskList = null;
		try
		{
			taskList=service.getActiveTaskList(companyCode,userId,filterType,filterValue);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return taskList;
	}
	
	
	@RequestMapping(value="/getLaunchProcessList/{companyCode}/{userId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Map<String,List<ProcessDTO>>> getLaunchProcessList(HttpServletRequest request, HttpServletResponse 
			 response,@PathVariable String companyCode, @PathVariable String userId) throws ApplicationException {
		
		Map<String,List<ProcessDTO>> processMap = new HashMap<String, List<ProcessDTO>>();
		List<ProcessDTO> dto = new ArrayList<ProcessDTO>();
		
		try (ProcessDAO processDAO = new ProcessDAO(Constants.DB_PUNIT)) {
			
			if (companyCode == null || userId == null) {
				throw new ApplicationException("Insufficient info: Company code or user Id is empty");
			}
			dto = processDAO.getProcessList(companyCode,userId,processMap);
			processMap.put("BAM", processDAO.getBAMProcessList(companyCode, userId, "BAM"));
			processMap.put("ENQUIRY", processDAO.getBAMProcessList(companyCode, userId, "ENQUIRY"));
			
			return new ResponseEntity<Map<String,List<ProcessDTO>>>(processMap, HttpStatus.OK);
		} catch (ApplicationException e) {
			return new ResponseEntity<Map<String,List<ProcessDTO>>>(
					processMap,(e.getErrCode() == 1) ? HttpStatus.NO_CONTENT : HttpStatus.NO_CONTENT);
		}
	}
	
	@RequestMapping(value="external/process/{companyCode}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<ProcessDTO>> getLaunchProcessList(HttpServletRequest request, HttpServletResponse 
			 response,@PathVariable String companyCode) throws ApplicationException {
		
		List<ProcessDTO> dto = new ArrayList<ProcessDTO>();
		
		try (ProcessDAO processDAO = new ProcessDAO(Constants.DB_PUNIT)) {
			
			if (companyCode == null ) {
				throw new ApplicationException("Insufficient info: Company code or user Id is empty");
			}
			//dto = processDAO.getProcessList(companyCode,"ALL");
			
			return new ResponseEntity<List<ProcessDTO>>(dto, HttpStatus.OK);
		} catch (ApplicationException e) {
			return new ResponseEntity<List<ProcessDTO>>(
					dto,(e.getErrCode() == 1) ? HttpStatus.NO_CONTENT : HttpStatus.NO_CONTENT);
		}
	}
	
	
	@RequestMapping(value="/getTask", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseDTO> getTask(HttpServletRequest request, HttpServletResponse 
			 response,@RequestBody TaskDTO task) {
		ResponseDTO dto =new TaskDTO();
		WorkflowServiceImpl service = new WorkflowServiceImpl();
		try {
			System.out.println("getTask --> BPMN ID --> " + task.getBpmnId());
			dto = service.getTask(task);
			System.out.println("getTask --> BPMN ID --> dto " + dto);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<ResponseDTO>(dto,HttpStatus.OK);
	}
	
	@RequestMapping(value="/getLaunchTask/{bpmnId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseDTO> getLaunchTask(HttpServletRequest request, HttpServletResponse 
			response,@PathVariable String bpmnId) {
		ResponseDTO dto =new TaskDTO();
		WorkflowServiceImpl service = new WorkflowServiceImpl();
		try {
			System.out.println("getLaunchTask --> BPMN ID --> " + bpmnId);
			dto = service.getLaunchTaskStep(bpmnId, request.getHeader("userId"), request.getHeader("companycode"));
			System.out.println("getLaunchTask --> BPMN ID --> dto " + dto);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<ResponseDTO>(dto,HttpStatus.OK);
	}

	@RequestMapping(value="/getEnquiryDetailTask/{refNo}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseDTO> getEnquiryDetailTask(HttpServletRequest request, HttpServletResponse 
			response,@PathVariable String refNo) {
		ResponseDTO dto =new TaskDTO();
		WorkflowServiceImpl service = new WorkflowServiceImpl();
		try {
			System.out.println("getEnquiryDetailTask --> BPMN ID --> " + refNo);
			dto = service.getEnqDetailStep(refNo);
			System.out.println("getEnquiryDetailTask --> BPMN ID --> dto " + dto);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<ResponseDTO>(dto,HttpStatus.OK);
	}
	
	@RequestMapping(value="/getWidgetDetail/{widgetid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseDTO> getWidgetDetail(HttpServletRequest request, HttpServletResponse 
			response,@PathVariable String widgetid) {
		ResponseDTO dto =new TaskDTO();
		WorkflowServiceImpl service = new WorkflowServiceImpl();
		try {
			System.out.println("getWidgetDetail --> BPMN ID --> " + widgetid);
			dto = service.getWidgetDetail(widgetid, request.getHeader("userId"), request.getHeader("companycode"));
			System.out.println("getWidgetDetail --> BPMN ID --> dto " + dto);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<ResponseDTO>(dto,HttpStatus.OK);
	}
	
	@PostMapping(value="external/process-instance/{interfaceName}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> initiateProcessInstance(HttpServletRequest request, HttpServletResponse 
			response,@PathVariable String interfaceName, @RequestBody String payload ) {
		ResponseDTO dto =new TaskDTO();
		WorkflowServiceImpl service = new WorkflowServiceImpl();
		System.out.println("payload "+payload);
		try {
			String bpmnid = (String)request.getAttribute("bpmnid");
			String processid = (String)request.getAttribute("bpmnid");
			System.out.println("getLaunchTask-->BPMN ID-->" + bpmnid+"#processid#"+processid);
			
			System.out.println("getLaunchTask --> BPMN ID --> dto " + dto);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<String>("Success",HttpStatus.OK);
	}
	
	
	@RequestMapping(value="external/process-details/{bpmnId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseDTO> getLaunchTaskDetails(HttpServletRequest request, HttpServletResponse 
			response,@PathVariable String bpmnId) {
		ResponseDTO dto =new TaskDTO();
		WorkflowServiceImpl service = new WorkflowServiceImpl();
		try {
			System.out.println("getLaunchTask --> BPMN ID --> " + bpmnId);
			dto = service.getLaunchTaskStep(bpmnId, request.getHeader("userId") , request.getHeader("companycode"));
			System.out.println("getLaunchTask --> BPMN ID --> dto " + dto);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<ResponseDTO>(dto,HttpStatus.OK);
	}
	
	 @RequestMapping(value="/completeTask", method = RequestMethod.POST)
	 public BPMNCompleterResultDTO completeTask(HttpServletRequest request, HttpServletResponse 
	 response,@RequestBody TaskDTO objTaskDTO)
	 {
		 WorkflowServiceImpl objWorkflowServiceImpl=new WorkflowServiceImpl();
		 BPMNCompleterResultDTO objBPMNCompleterResultDTO=null;
		 try
			{
				objBPMNCompleterResultDTO=objWorkflowServiceImpl.completeTask(objTaskDTO);
			}
			catch (Exception ex) 
			{
				ex.printStackTrace();
				objBPMNCompleterResultDTO=new BPMNCompleterResultDTO();
				objBPMNCompleterResultDTO.setResponseCode("ERROR");
				objBPMNCompleterResultDTO.setResponsMsg("Unable complete Task#"+ex.getMessage());
			}
			return objBPMNCompleterResultDTO;
	 }
	 
	 @RequestMapping(value="/save-task", method = RequestMethod.POST)
	 public BPMNCompleterResultDTO saveTask(HttpServletRequest request, HttpServletResponse 
	 response,@RequestBody TaskDTO objTaskDTO)
	 {
		 WorkflowServiceImpl objWorkflowServiceImpl=new WorkflowServiceImpl();
		 BPMNCompleterResultDTO objBPMNCompleterResultDTO=null;
		 try
			{
				objBPMNCompleterResultDTO=objWorkflowServiceImpl.saveTask(objTaskDTO);
			}
			catch (Exception ex) 
			{
				ex.printStackTrace();
				objBPMNCompleterResultDTO=new BPMNCompleterResultDTO();
				objBPMNCompleterResultDTO.setResponseCode("ERROR");
				objBPMNCompleterResultDTO.setResponsMsg("Unable complete Task#"+ex.getMessage());
			}
			return objBPMNCompleterResultDTO;
	 }
	 
	 @RequestMapping(value="/cancel-task", method = RequestMethod.POST)
	 public BPMNCompleterResultDTO cancelTask(HttpServletRequest request, HttpServletResponse 
	 response,@RequestBody TaskDTO objTaskDTO)
	 {
		 WorkflowServiceImpl objWorkflowServiceImpl=new WorkflowServiceImpl();
		 BPMNCompleterResultDTO objBPMNCompleterResultDTO=null;
		 try
			{
				objBPMNCompleterResultDTO=objWorkflowServiceImpl.cancelTask(objTaskDTO);
			}
			catch (Exception ex) 
			{
				ex.printStackTrace();
				objBPMNCompleterResultDTO=new BPMNCompleterResultDTO();
				objBPMNCompleterResultDTO.setResponseCode("ERROR");
				objBPMNCompleterResultDTO.setResponsMsg("Unable complete Task#"+ex.getMessage());
			}
			return objBPMNCompleterResultDTO;
	 }
	 
	@RequestMapping(value="/initiateTransaction", method = RequestMethod.POST)
	 public BPMNCompleterResultDTO initiateTransaction(HttpServletRequest request, HttpServletResponse 
	 response,@RequestBody TaskDTO objTaskDTO) throws Exception
	 {
		WorkflowServiceImpl objWorkflowServiceImpl=new WorkflowServiceImpl();	
		BPMNCompleterResultDTO objBPMNCompleterResultDTO=null;
		try
		{
			objBPMNCompleterResultDTO=objWorkflowServiceImpl.initiateTransaction(objTaskDTO);
		}
		catch (Exception ex) 
		{
			ex.printStackTrace();
			objBPMNCompleterResultDTO=new BPMNCompleterResultDTO();
			objBPMNCompleterResultDTO.setResponseCode("ERROR");
			objBPMNCompleterResultDTO.setResponsMsg("Unable Initiate Transaction#"+ex.getMessage());
		}
		return objBPMNCompleterResultDTO;
	 }
	 
	@RequestMapping(value="/uploadDocument/{companyCode}/{userId}/{bpmnTxRefNo}/{stepName}", method = RequestMethod.POST)
	 public TXNDocumentDTO uploadDocument(HttpServletRequest request, HttpServletResponse response,@RequestParam("file") MultipartFile fileObj,
			 @PathVariable String companyCode,@PathVariable String userId,@PathVariable String bpmnTxRefNo,@PathVariable String stepName)
	 {
		 WorkflowServiceImpl objWorkflowServiceImpl=new WorkflowServiceImpl();	
		 TXNDocumentDTO objTXNDocumentDTO=null;
		 try
		 {
			 objTXNDocumentDTO=objWorkflowServiceImpl.uploadDocument(companyCode, bpmnTxRefNo, stepName, userId, fileObj);
			 objTXNDocumentDTO.setResponseCode("SUCCESS");
			 objTXNDocumentDTO.setResponsMsg("File Uploaded successfully");
		 }
		 catch(Exception ex)
		 {
			 ex.printStackTrace();
			 objTXNDocumentDTO=new TXNDocumentDTO();
			 objTXNDocumentDTO.setResponseCode("ERROR");
			 objTXNDocumentDTO.setResponsMsg(ex.getLocalizedMessage());
		 }
		 return objTXNDocumentDTO;
	 }
	 
	 @RequestMapping(value="/getDocument/{documentId}", method = RequestMethod.GET)
	 public ResponseEntity<byte[]> getDocument(HttpServletRequest request, HttpServletResponse response,@PathVariable String documentId)
	 {
		 WorkflowServiceImpl objWorkflowServiceImpl=new WorkflowServiceImpl();	
		 try
		 {
			 long docId=Long.parseLong(documentId);
			 TXNDocments objTxnDocments=objWorkflowServiceImpl.getDocument(docId);
			 /*REtrieve document content from S3 content store*/
			 AWSS3ServiceImpl s3service = new AWSS3ServiceImpl();
			 objTxnDocments.setDocument(s3service.getDocument(objTxnDocments.getCompanyCode(), documentId+objTxnDocments.getDocumentName().substring(objTxnDocments.getDocumentName().indexOf("."))));
			 HttpHeaders header = new HttpHeaders();
			 header.setContentType(MediaType.valueOf(objTxnDocments.getDocumentType()));
			 header.setContentLength(objTxnDocments.getDocument().length);
			 header.set("Access-Control-Expose-Headers", "Content-Disposition");
			 header.set("Content-Disposition", "attachment; filename=" + objTxnDocments.getDocumentName());
			 //ContentDisposition contentDisposition = ContentDisposition.builder("attachment").filename("Filename").build();
			 //header.setContentDisposition(contentDisposition);
			 return new ResponseEntity<>(objTxnDocments.getDocument(), header, HttpStatus.OK);
		 }
		 catch(Exception ex)
		 {
			 ex.printStackTrace();
			 return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		 }
	 }
	 
	 @RequestMapping(value="/deleteDocument/{companyCode}/{documentId}", method = RequestMethod.POST)
	 public ResponseDTO deleteDocument(HttpServletRequest request, HttpServletResponse response,@PathVariable String companyCode, @PathVariable String documentId)
	 {
		 WorkflowServiceImpl objWorkflowServiceImpl=new WorkflowServiceImpl();	
		 ResponseDTO objResponseDTO=new ResponseDTO();
		 try
		 {
			 long docId=Long.parseLong(documentId);
			 TXNDocments objTxnDocments=objWorkflowServiceImpl.getDocument(docId);
			 int docCount=objWorkflowServiceImpl.deleteDocument(companyCode,docId);
			 AWSS3ServiceImpl service = new AWSS3ServiceImpl();
			 service.deleteFileS3Bucket(companyCode, documentId+objTxnDocments.getDocumentName().substring(objTxnDocments.getDocumentName().indexOf(".")));
			 objResponseDTO.setResponseCode("SUCCESS");
			 objResponseDTO.setResponsMsg(docCount+"#File deleted successfully");
		 }
		 catch(Exception ex)
		 {
			 ex.printStackTrace();
			 objResponseDTO.setResponseCode("ERROR");
			 objResponseDTO.setResponsMsg(ex.getLocalizedMessage());
		 }
		 return objResponseDTO;
	 }
	 
	  @RequestMapping(value="/getALLDocuments/{companyCode}/{bpmnTxRefNo}", method = RequestMethod.GET)
	 public TXNDocumentListDTO getALLDocuments(HttpServletRequest request, HttpServletResponse response,@PathVariable String companyCode,@PathVariable String bpmnTxRefNo)
	 {
		 WorkflowServiceImpl objWorkflowServiceImpl=new WorkflowServiceImpl();	
		 TXNDocumentListDTO objTXNDocumentListDTO=new TXNDocumentListDTO();
		 try
		 {
			 objTXNDocumentListDTO=objWorkflowServiceImpl.getALLDocuments(companyCode,bpmnTxRefNo);
			 objTXNDocumentListDTO.setResponseCode("SUCCESS");
		 }
		 catch(Exception ex)
		 {
			 ex.printStackTrace();
			 objTXNDocumentListDTO.setResponseCode("ERROR");
			 objTXNDocumentListDTO.setResponsMsg(ex.getLocalizedMessage());
		 }
		 return objTXNDocumentListDTO;
	 }
	
	 @RequestMapping(value="/processes/{companyCode}/{processtype}", method = RequestMethod.GET , produces = MediaType.APPLICATION_JSON_VALUE)
	 public String getDSProcessList(HttpServletRequest request, HttpServletResponse response, @PathVariable String companyCode,  @PathVariable String processtype) throws Exception
	 {
		WorkflowServiceImpl objWorkflowServiceImpl=new WorkflowServiceImpl();	
		BPMNCompleterResultDTO objBPMNCompleterResultDTO=null;
		JSONObject json = new JSONObject();
		try
		{
			json=objWorkflowServiceImpl.getDSProcessList(companyCode, processtype);
			System.out.println("output "+json.toString());
		}
		catch (Exception ex) 
		{
			ex.printStackTrace();
			objBPMNCompleterResultDTO=new BPMNCompleterResultDTO();
			objBPMNCompleterResultDTO.setResponseCode("ERROR");
			objBPMNCompleterResultDTO.setResponsMsg("Unable Initiate Transaction#"+ex.getMessage());
		}
		return json.toString();
	 }
	 

	 @RequestMapping(value="/process/{companyCode}/{processId}", method = RequestMethod.GET , produces = MediaType.APPLICATION_JSON_VALUE)
	 public String getDSProcess(HttpServletRequest request, HttpServletResponse 
	 response, @PathVariable String processId,@PathVariable String companyCode) throws Exception
	 {
		WorkflowServiceImpl objWorkflowServiceImpl=new WorkflowServiceImpl();	
		BPMNCompleterResultDTO objBPMNCompleterResultDTO=null;
		JSONObject json = new JSONObject();
		try
		{
			json=objWorkflowServiceImpl.getDSProcess(companyCode,processId);
			System.out.println("output "+json.toString());
		}
		catch (Exception ex) 
		{
			ex.printStackTrace();
			objBPMNCompleterResultDTO=new BPMNCompleterResultDTO();
			objBPMNCompleterResultDTO.setResponseCode("ERROR");
			objBPMNCompleterResultDTO.setResponsMsg("Unable Initiate Transaction#"+ex.getMessage());
		}
		return json.toString();
	 }
	 
	 
	 @RequestMapping(value="/createProcess/{processtype}", method = RequestMethod.POST , produces = MediaType.APPLICATION_JSON_VALUE)
	 public ResponseDTO createDSProcess(HttpServletRequest request, HttpServletResponse 
	 response,@RequestBody String processJson, @PathVariable String processtype) throws Exception
	 {
		String companyCode =getCompanyCode(request);
		System.out.println("processJson --- > " + processJson);
		System.out.println("companyCode --- > " + companyCode);
		WorkflowServiceImpl objWorkflowServiceImpl=new WorkflowServiceImpl();	
		ResponseDTO objResponseDTO=new ResponseDTO();
		try
		{
			objWorkflowServiceImpl.createDSProcess(companyCode,processJson, processtype);
			//System.out.println("output "+json.toString());
			objResponseDTO.setResponseCode("SUCCESS");
			objResponseDTO.setResponsMsg("Process Created");
		}
		catch (Exception ex) 
		{
			ex.printStackTrace();
			objResponseDTO.setResponseCode("ERROR");
			objResponseDTO.setResponsMsg("Unable Create Process#"+ex.getMessage());
		}
		return objResponseDTO;
	 }

	 @RequestMapping(value="/save-process/{companyCode}", method = RequestMethod.POST , produces = MediaType.APPLICATION_JSON_VALUE)
	 public ResponseDTO saveDSProcess(HttpServletRequest request, HttpServletResponse 
	 response,@RequestBody String processJson, @PathVariable String companyCode) throws Exception
	 {
		System.out.println("processJson --- > " + processJson);
		System.out.println("companyCode --- > " + companyCode);
		WorkflowServiceImpl objWorkflowServiceImpl=new WorkflowServiceImpl();	
		ResponseDTO objResponseDTO=new ResponseDTO();
		try
		{
			objWorkflowServiceImpl.saveDSProcess(companyCode,processJson);
			//System.out.println("output "+json.toString());
			objResponseDTO.setResponseCode("SUCCESS");
			objResponseDTO.setResponsMsg("Process saved");
		}
		catch (Exception ex) 
		{
			ex.printStackTrace();
			objResponseDTO.setResponseCode("ERROR");
			objResponseDTO.setResponsMsg("Unable save Process due to "+ex.getMessage());
		}
		return objResponseDTO;
	 }
	 
	 /**
	  * Deploy designed process into Core System 
	  * @param request
	  * @param response
	  * @param processJson
	  * @param companyCode
	  * @return
	  * @throws Exception
	  */
	@RequestMapping(value="/deploy-process/{companyCode}/{newVersion}", method = RequestMethod.POST , produces = MediaType.APPLICATION_JSON_VALUE)
	 public ResponseDTO deployDSProcess(HttpServletRequest request, HttpServletResponse 
	 response,@RequestBody String processJson, @PathVariable String companyCode, @PathVariable String newVersion) throws Exception
	 {
		System.out.println("processJson --- > " + processJson);
		System.out.println("companyCode --- > " + companyCode);
		System.out.println("new version  --- > " + newVersion);
		WorkflowServiceImpl objWorkflowServiceImpl=new WorkflowServiceImpl();	
		ResponseDTO objResponseDTO=new ResponseDTO();
		try
		{
			if("yes".equalsIgnoreCase(newVersion)) {
				processJson = objWorkflowServiceImpl.createDSProcessVersion(companyCode,processJson);
				System.out.println("processJson newversion --- > " + processJson);
			}
			objWorkflowServiceImpl.saveDSProcess(companyCode,processJson);
			objWorkflowServiceImpl.deployDSProcess(companyCode,processJson);
			objResponseDTO.setResponseCode("SUCCESS");
			objResponseDTO.setResponsMsg("Process Deployed");
		}
		catch (Exception ex) 
		{
			ex.printStackTrace();
			objResponseDTO.setResponseCode("ERROR");
			objResponseDTO.setResponsMsg("Unable save Process due to "+ex.getMessage());
		}
		return objResponseDTO;
	 }
	
	
	 @RequestMapping(value="/check-processid/{companyCode}/{processid}", method = RequestMethod.GET , produces = MediaType.APPLICATION_JSON_VALUE)
	 public ResponseDTO checkProcessId(HttpServletRequest request, HttpServletResponse 
	 response, @PathVariable String companyCode, @PathVariable String processid) throws Exception
	 {
		System.out.println("companyCode --- > " + companyCode);
		WorkflowServiceImpl objWorkflowServiceImpl=new WorkflowServiceImpl();	
		ResponseDTO objResponseDTO=new ResponseDTO();
		try
		{
			String responsemsg = objWorkflowServiceImpl.checkProcessId(companyCode,processid);
			objResponseDTO.setResponseCode("SUCCESS");
			objResponseDTO.setResponsMsg(responsemsg);
		}
		catch (Exception ex) 
		{
			ex.printStackTrace();
			objResponseDTO.setResponseCode("ERROR");
			objResponseDTO.setResponsMsg("Unable save Process due to "+ex.getMessage());
		}
		return objResponseDTO;
	 }

	 @RequestMapping(value="/create-master", method = RequestMethod.POST , produces = MediaType.APPLICATION_JSON_VALUE)
	 public ResponseDTO createMaster(HttpServletRequest request, HttpServletResponse 
	 response,@RequestBody MasterSchemaDTO masterschema) throws Exception
	 {
		System.out.println("companyCode --- > " + masterschema.getCompanycode());
		System.out.println("mastetrname --- > " + masterschema.getMastername());
		System.out.println("metadata  --- > " + masterschema.getMetadata());
		WorkflowServiceImpl objWorkflowServiceImpl=new WorkflowServiceImpl();	
		ResponseDTO objResponseDTO=new ResponseDTO();
		try
		{
			objWorkflowServiceImpl.createMaster(masterschema.getCompanycode(), masterschema.getMastername(), masterschema.getMetadata());
			objResponseDTO.setResponseCode("SUCCESS");
			objResponseDTO.setResponsMsg("Master created");
		}
		catch (Exception ex) 
		{
			ex.printStackTrace();
			objResponseDTO.setResponseCode("ERROR");
			objResponseDTO.setResponsMsg("Unable to create Master "+ex.getMessage());
		}
		return objResponseDTO;
	 }
	 
	 @RequestMapping(value="/edit-master", method = RequestMethod.POST , produces = MediaType.APPLICATION_JSON_VALUE)
	 public ResponseDTO editMaster(HttpServletRequest request, HttpServletResponse 
	 response,@RequestBody MasterSchemaDTO masterschema) throws Exception
	 {
		System.out.println("companyCode --- > " + masterschema.getCompanycode());
		System.out.println("mastetrname --- > " + masterschema.getMastername());
		System.out.println("metadata  --- > " + masterschema.getMetadata());
		WorkflowServiceImpl objWorkflowServiceImpl=new WorkflowServiceImpl();	
		ResponseDTO objResponseDTO=new ResponseDTO();
		try
		{
			objWorkflowServiceImpl.editMaster(masterschema.getCompanycode(), masterschema.getMastername(), masterschema.getMetadata());
			objResponseDTO.setResponseCode("SUCCESS");
			objResponseDTO.setResponsMsg("Master updated");
		}
		catch (Exception ex) 
		{
			ex.printStackTrace();
			objResponseDTO.setResponseCode("ERROR");
			objResponseDTO.setResponsMsg("Unable to update Master "+ex.getMessage());
		}
		return objResponseDTO;
	 }
	 
	 @RequestMapping(value="/getMasterList/{companyCode}", method = RequestMethod.GET , produces = MediaType.APPLICATION_JSON_VALUE)
	 public ArrayList<String> getMasterList(HttpServletRequest request, HttpServletResponse 
	 response,@PathVariable String companyCode) throws Exception
	 {
		System.out.println("companyCode --- > " + companyCode);
		WorkflowServiceImpl objWorkflowServiceImpl=new WorkflowServiceImpl();	
		try
		{
			return objWorkflowServiceImpl.getMasterList(companyCode);

		}
		catch (Exception ex) 
		{
			ex.printStackTrace();
		}
		return null;
	 }
	 
	 @RequestMapping(value="/getMasterMetaData", method = RequestMethod.GET , produces = MediaType.APPLICATION_JSON_VALUE)
	 public Map<String, String> getMasterMetaData(HttpServletRequest request, HttpServletResponse 
	 response) throws Exception
	 {
		String companyCode = getCompanyCode(request);
		System.out.println("companyCode --- > " + companyCode);
		WorkflowServiceImpl objWorkflowServiceImpl=new WorkflowServiceImpl();	
		try
		{
			return objWorkflowServiceImpl.getMasterMetaData(companyCode);

		}
		catch (Exception ex) 
		{
			ex.printStackTrace();
		}
		return null;
	 }
	 
	 @RequestMapping(value="/getMasterDetail/{companyCode}/{masterName}", method = RequestMethod.GET , produces = MediaType.APPLICATION_JSON_VALUE)
	 public String getMasterDetail(HttpServletRequest request, HttpServletResponse 
	 response,@PathVariable String companyCode, @PathVariable String masterName) throws Exception
	 {
		System.out.println("companyCode --- > " + companyCode);
		WorkflowServiceImpl objWorkflowServiceImpl=new WorkflowServiceImpl();	
		try
		{
			JSONObject json = objWorkflowServiceImpl.getMasterDetail(companyCode, masterName);
			return json.toString();

		}
		catch (Exception ex) 
		{
			ex.printStackTrace();
		}
		return null;
	 }
	 
	 @RequestMapping(value="/deletMaster/{masterName}", method = RequestMethod.GET , produces = MediaType.APPLICATION_JSON_VALUE)
	 public String deleteMaster(HttpServletRequest request, HttpServletResponse 
	 response,@PathVariable String masterName) throws Exception
	 {
		
		WorkflowServiceImpl objWorkflowServiceImpl=new WorkflowServiceImpl();	
		String companyCode = getCompanyCode(request);
		System.out.println("companyCode --- > " + companyCode);
		try
		{
			objWorkflowServiceImpl.deleteMaster(companyCode, masterName);
			return "Delete Success";

		}
		catch (Exception ex) 
		{
			ex.printStackTrace();
		}
		return "Delete Failure";
	 }
	 
	 @RequestMapping(value="/updateMasterData/{action}", method = RequestMethod.POST , produces = MediaType.APPLICATION_JSON_VALUE)
	 public ResponseDTO updateMasterData(HttpServletRequest request, HttpServletResponse 
	 response,@PathVariable String action, @RequestBody MasterDataDTO masterdto) throws Exception
	 {
		System.out.println("companyCode --- > " + masterdto.getCompanycode());
		System.out.println("mastetrname --- > " + masterdto.getMastername());
		System.out.println("key  --- > " + masterdto.getKey());
		WorkflowServiceImpl objWorkflowServiceImpl=new WorkflowServiceImpl();	
		ResponseDTO objResponseDTO=new ResponseDTO();
		try
		{
			objWorkflowServiceImpl.updateMasterData(action, masterdto);
			objResponseDTO.setResponseCode("SUCCESS");
			objResponseDTO.setResponsMsg("Master data updated");
		}
		catch (Exception ex) 
		{
			ex.printStackTrace();
			objResponseDTO.setResponseCode("ERROR");
			objResponseDTO.setResponsMsg("Unable to update Master "+ex.getMessage());
		}
		return objResponseDTO;
	 }
	 
	 
	 @RequestMapping(value="/computeFields/{companycode}", method = RequestMethod.POST , produces = MediaType.APPLICATION_JSON_VALUE)
	 public ComputeDTO computeFields(HttpServletRequest request, HttpServletResponse 
	 response,@PathVariable String companycode, @RequestBody ComputeDTO computedto) throws Exception
	 {
		System.out.println("companyCode --- > " + companycode);
		System.out.println("processdata --- > " + computedto.getProcessdata());
		System.out.println("getComputeFormulas --- > " + computedto.getComputeFormulas());
		WorkflowServiceImpl objWorkflowServiceImpl=new WorkflowServiceImpl();	
		//ResponseDTO objResponseDTO=new ResponseDTO();
		try
		{
			computedto = objWorkflowServiceImpl.computeFields(companycode, computedto);
			computedto.setResponseCode("SUCCESS");
			computedto.setResponsMsg("Master data updated");
		}
		catch (Exception ex) 
		{
			ex.printStackTrace();
			computedto.setResponseCode("ERROR");
			computedto.setResponsMsg("Unable to update Master "+ex.getMessage());
		}
		return computedto;
	 }
	 
	 @RequestMapping(value="/getMasterDropDown/{companycode}/{mastername}/{columnname}", method = RequestMethod.GET , produces = MediaType.APPLICATION_JSON_VALUE)
	 public ArrayList<String> getMasterDropDown(HttpServletRequest request, HttpServletResponse 
	 response,@PathVariable String companycode, @PathVariable String mastername, @PathVariable String columnname) throws Exception
	 {
		System.out.println("companyCode --- > " + companycode);
		System.out.println("mastername --- > " + mastername);
		System.out.println("columnname --- > " + columnname);
		WorkflowServiceImpl objWorkflowServiceImpl=new WorkflowServiceImpl();	
		//ResponseDTO objResponseDTO=new ResponseDTO();
		ArrayList<String> list = new ArrayList<String>();
		try
		{
			list = objWorkflowServiceImpl.getMasterDropDown(companycode, mastername, columnname);
		}
		catch (Exception ex) 
		{
			ex.printStackTrace();
			
		}
		return list;
	 }
	 
	 private String getCompanyCode(HttpServletRequest request) {
		 return request.getHeader("companycode");
		
	 }
}