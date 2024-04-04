package com.zanflow.bpmn.service;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.zanflow.bpmn.dao.KanbanDAO;
import com.zanflow.bpmn.dto.BPMNCompleterResultDTO;
import com.zanflow.bpmn.dto.ProcessDTO;
import com.zanflow.bpmn.dto.ResponseDTO;
import com.zanflow.bpmn.dto.TaskDTO;
import com.zanflow.bpmn.exception.ApplicationException;
import com.zanflow.bpmn.service.impl.KanbanServiceImpl;
import com.zanflow.bpmn.service.impl.WorkflowServiceImpl;
import com.zanflow.common.db.Constants;
import com.zanflow.kanban.dto.LanesDTO;

@RestController
@CrossOrigin(origins = "*" ,allowedHeaders ="*")
public class KanbanService {

	
	@RequestMapping(value="/getBoardList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<ProcessDTO>> getBoardList(HttpServletRequest request, HttpServletResponse 
			 response) throws ApplicationException {
		
		
		String userId = request.getHeader("userId");
		String companyCode =  getCompanyCode(request);		
		String userType = request.getHeader("userType");		
		List<ProcessDTO> dto = new ArrayList<ProcessDTO>();
		
		try (KanbanDAO kanbanDAO = new KanbanDAO(Constants.DB_PUNIT)) {
			
			if (companyCode == null || userId == null) {
				throw new ApplicationException("Insufficient info: Company code or user Id is empty");
			}
			dto = kanbanDAO.getBoardList(companyCode, userId, userType);
			
			return new ResponseEntity<List<ProcessDTO>>(dto, HttpStatus.OK);
		} catch (ApplicationException e) {
			return new ResponseEntity<List<ProcessDTO>>(
					dto,(e.getErrCode() == 1) ? HttpStatus.NO_CONTENT : HttpStatus.NO_CONTENT);
		}
	}
	
	@RequestMapping(value="/getCards/{boardid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ArrayList<LanesDTO> getCardList(HttpServletRequest request, HttpServletResponse 
			 response,@PathVariable String boardid) throws ApplicationException {
		KanbanServiceImpl service = new KanbanServiceImpl();
		String companyCode = getCompanyCode(request);
		ArrayList<LanesDTO> lanes = null;
		try
		{
			lanes=service.getActiveCardList(companyCode,boardid);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return lanes;
	}
	
	
	@RequestMapping(value="/getTasks", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ArrayList<TaskDTO> getTaskList(HttpServletRequest request, HttpServletResponse 
			 response) throws ApplicationException {
		KanbanServiceImpl service = new KanbanServiceImpl();
		String companyCode = getCompanyCode(request);
		String userId = getUserId(request);
		ArrayList<TaskDTO> taskList = null;
		try
		{
			taskList=service.getTaskList(companyCode,userId);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return taskList;
	}
	
	@RequestMapping(value="/getCard", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseDTO> getCard(HttpServletRequest request, HttpServletResponse 
			 response,@RequestBody TaskDTO card) {
		ResponseDTO dto =new TaskDTO();
		KanbanServiceImpl service = new KanbanServiceImpl();
		try {
			//System.out.println("getTask --> BPMN ID --> " + card.getBpmnId());
			dto = service.getCard(card); 
			//System.out.println("getTask --> BPMN ID --> dto " + dto);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<ResponseDTO>(dto,HttpStatus.OK);
	}
	
	@RequestMapping(value="/getLaunchCard/{boardid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseDTO> getLaunchCard(HttpServletRequest request, HttpServletResponse 
			response,@PathVariable String boardid) {
		ResponseDTO dto =new TaskDTO();
		KanbanServiceImpl service = new KanbanServiceImpl();
		try {
			//System.out.println("getLaunchCard --> Board ID --> " + boardid);
			dto = service.getLaunchCard(boardid, request.getHeader("userId"), getCompanyCode(request));
			//System.out.println("getLaunchCard --> Board ID --> dto " + dto);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<ResponseDTO>(dto,HttpStatus.OK);
	}
	
	 @RequestMapping(value="/move-card", method = RequestMethod.POST)
	 public BPMNCompleterResultDTO moveCard(HttpServletRequest request, HttpServletResponse 
	 response,@RequestBody TaskDTO objTaskDTO)
	 {
		 KanbanServiceImpl objKanbanServiceImpl=new KanbanServiceImpl();
		 BPMNCompleterResultDTO objBPMNCompleterResultDTO=null;
		 try
			{
				objBPMNCompleterResultDTO=objKanbanServiceImpl.moveCard(objTaskDTO);
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
	 
	 @RequestMapping(value="/save-card", method = RequestMethod.POST)
	 public BPMNCompleterResultDTO saveCard(HttpServletRequest request, HttpServletResponse 
	 response,@RequestBody TaskDTO objTaskDTO)
	 {
		 KanbanServiceImpl objKanbanServiceImpl=new KanbanServiceImpl();
		 BPMNCompleterResultDTO objBPMNCompleterResultDTO=null;
		 try
			{
				objBPMNCompleterResultDTO=objKanbanServiceImpl.saveCard(objTaskDTO);
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
	 
	 @RequestMapping(value="/cancel-card", method = RequestMethod.POST)
	 public BPMNCompleterResultDTO cancelCard(HttpServletRequest request, HttpServletResponse 
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
	 
	@RequestMapping(value="/initiateCard", method = RequestMethod.POST)
	 public BPMNCompleterResultDTO initiateCard(HttpServletRequest request, HttpServletResponse 
	 response,@RequestBody TaskDTO objTaskDTO) throws Exception
	 {
		KanbanServiceImpl objKanbanServiceImpl=new KanbanServiceImpl();	
		BPMNCompleterResultDTO objBPMNCompleterResultDTO=null;
		try
		{
			objBPMNCompleterResultDTO=objKanbanServiceImpl.initiateCard(objTaskDTO);
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
	 

	 @RequestMapping(value="/board/{boardid}", method = RequestMethod.GET , produces = MediaType.APPLICATION_JSON_VALUE)
	 public String getBoardConfig(HttpServletRequest request, HttpServletResponse 
	 response, @PathVariable String boardid) throws Exception
	 {
		
		String companyCode =  getCompanyCode(request);		
		KanbanServiceImpl objKanbanServiceImpl=new KanbanServiceImpl();	
		JSONObject json = new JSONObject();
		try
		{
			json=objKanbanServiceImpl.getBoardConfig(companyCode,boardid);
			//System.out.println("output "+json.toString());
		}
		catch (Exception ex) 
		{
			ex.printStackTrace();
		}
		return json.toString();
	 }
	 
	 @RequestMapping(value="/deleteBoard/{boardid}", method = RequestMethod.POST , produces = MediaType.APPLICATION_JSON_VALUE)
	 public String deleteBoard(HttpServletRequest request, HttpServletResponse 
	 response, @PathVariable String boardid) throws Exception
	 {
		
		String companyCode =  getCompanyCode(request);		
		KanbanServiceImpl objKanbanServiceImpl=new KanbanServiceImpl();	
		try
		{
			objKanbanServiceImpl.deleteBoard(companyCode,boardid);
		}
		catch (Exception ex) 
		{
			ex.printStackTrace();
			return "Failed";
		}
		return "Success";
	 }
	 
	 @RequestMapping(value="/createBoard", method = RequestMethod.POST , produces = MediaType.APPLICATION_JSON_VALUE)
	 public ResponseDTO createBoard(HttpServletRequest request, HttpServletResponse 
	 response,@RequestBody String boardJson) throws Exception
	 {

		String companyCode =  getCompanyCode(request);		
		//System.out.println("processJson --- > " + boardJson);
		//System.out.println("companyCode --- > " + companyCode);
		KanbanServiceImpl objKanbanServiceImpl=new KanbanServiceImpl();	
		ResponseDTO objResponseDTO=new ResponseDTO();
		try
		{
			objKanbanServiceImpl.createBoard(companyCode,boardJson);
			//objKanbanServiceImpl.deployBoard(companyCode,boardJson);
			objResponseDTO.setResponseCode("SUCCESS");
			objResponseDTO.setResponsMsg("Board Created");
		}
		catch (Exception ex) 
		{
			ex.printStackTrace();
			objResponseDTO.setResponseCode("ERROR");
			objResponseDTO.setResponsMsg("Unable Create Process#"+ex.getMessage());
		}
		return objResponseDTO;
	 }

	 @RequestMapping(value="/save-board", method = RequestMethod.POST , produces = MediaType.APPLICATION_JSON_VALUE)
	 public ResponseDTO saveBoard(HttpServletRequest request, HttpServletResponse 
	 response,@RequestBody String processJson) throws Exception
	 {
		String companyCode =  getCompanyCode(request);		
		//System.out.println("processJson --- > " + processJson);
		//System.out.println("companyCode --- > " + companyCode);
		KanbanServiceImpl objKanbanServiceImpl=new KanbanServiceImpl();	
		ResponseDTO objResponseDTO=new ResponseDTO();
		try
		{
			objKanbanServiceImpl.saveBoard(companyCode,processJson);
			objResponseDTO.setResponseCode("SUCCESS");
			objResponseDTO.setResponsMsg("Board saved");
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
	  * Deploy designed board into Core System 
	  * @param request
	  * @param response
	  * @param processJson
	  * @param companyCode
	  * @return
	  * @throws Exception
	  */
	@RequestMapping(value="/deploy-board", method = RequestMethod.POST , produces = MediaType.APPLICATION_JSON_VALUE)
	 public ResponseDTO deployBoard(HttpServletRequest request, HttpServletResponse 
	 response,@RequestBody String processJson) throws Exception
	 {
		String companyCode =  getCompanyCode(request);		
		//System.out.println("processJson --- > " + processJson);
		//System.out.println("companyCode --- > " + companyCode);
		KanbanServiceImpl objKanbanServiceImpl=new KanbanServiceImpl();	
		ResponseDTO objResponseDTO=new ResponseDTO();
		try
		{
			objKanbanServiceImpl.saveBoard(companyCode,processJson);
			objKanbanServiceImpl.deployBoard(companyCode,processJson);
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
	
	
	 @RequestMapping(value="/check-boardid/{boardid}", method = RequestMethod.GET , produces = MediaType.APPLICATION_JSON_VALUE)
	 public ResponseDTO checkBoardId(HttpServletRequest request, HttpServletResponse 
	 response, @PathVariable String boardid) throws Exception
	 {
		String companyCode =  getCompanyCode(request);		
		KanbanServiceImpl objKanbanServiceImpl=new KanbanServiceImpl();	
		ResponseDTO objResponseDTO=new ResponseDTO();
		try
		{
			String responsemsg = objKanbanServiceImpl.checkBoardId(companyCode,boardid);
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
	 
	 private String getCompanyCode(HttpServletRequest request) {
		 return request.getHeader("companycode");
		
	 }
	 
	 private String getUserId(HttpServletRequest request) {
		 return request.getHeader("userId");
		
	 }
	 
}