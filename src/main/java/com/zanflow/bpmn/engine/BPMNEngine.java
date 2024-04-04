package com.zanflow.bpmn.engine;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.stream.Stream;  

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.StartEvent;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;
import org.camunda.bpm.model.xml.type.ModelElementType;

import com.zanflow.bpmn.BPMNData;
import com.zanflow.bpmn.dao.BPMNTaskDAO;
import com.zanflow.bpmn.dto.BPMNCompleterResultDTO;
import com.zanflow.bpmn.dto.BPMNStepDTO;
import com.zanflow.bpmn.elements.BasicElement;
import com.zanflow.bpmn.elements.CallActivityElement;
import com.zanflow.bpmn.elements.EndEventElement;
import com.zanflow.bpmn.elements.ExclusiveGateWayElement;
import com.zanflow.bpmn.elements.InclusiveGateWayElement;
import com.zanflow.bpmn.elements.ParallelGateWayElement;
import com.zanflow.bpmn.elements.ScriptTaskElement;
import com.zanflow.bpmn.elements.ServiceTaskElement;
import com.zanflow.bpmn.elements.StartEventElement;
import com.zanflow.bpmn.elements.TerminateEndEvent;
import com.zanflow.bpmn.elements.UserTaskElement;
import com.zanflow.bpmn.exception.ApplicationException;
import com.zanflow.bpmn.model.BPMNProcess;
import com.zanflow.bpmn.model.BPMNProcessInfo;
import com.zanflow.bpmn.model.BPMNTask;
import com.zanflow.bpmn.util.DateUtility;




public class BPMNEngine {
	static BPMNEngine objBPMNEngine=null;
	private LinkedHashMap<String,BpmnModelInstance> bpmnModelInstanceMap=new LinkedHashMap<String,BpmnModelInstance>();
	
	private BPMNEngine()
	{
		
	}
	
	

	public LinkedHashMap<String, BpmnModelInstance> getBpmnModelInstanceMap() {
		return bpmnModelInstanceMap;
	}



	public void setBpmnModelInstanceMap(
			LinkedHashMap<String, BpmnModelInstance> bpmnModelInstanceMap) {
		this.bpmnModelInstanceMap = bpmnModelInstanceMap;
	}
	
	public void clearBpmnModelInstanceMap()
	{
		this.bpmnModelInstanceMap.clear();
	}

	public void clearBpmnModelInstance(String bpmnId)
	{
		this.bpmnModelInstanceMap.remove(bpmnId);
	}
	
	public static BPMNEngine getInstance()
	{
		if(objBPMNEngine==null)
		{
			objBPMNEngine=new BPMNEngine();
		}
		return objBPMNEngine;
	}
	
	public BpmnModelInstance getBpmnModelInstance(BPMNData objBPMNData,String bpmnId) throws Exception
	{
		BpmnModelInstance objBpmnModelInstance=null;
		String searchKey=null;
		if(bpmnId!=null)
		{
			searchKey=bpmnId;
		}
		else
		{
			searchKey=objBPMNData.getBpmnId();
		}
		if(bpmnModelInstanceMap.containsKey(searchKey))
		{
			objBpmnModelInstance=bpmnModelInstanceMap.get(searchKey);
		}
		else
		{
			BPMNProcess objBPMNProcess=getBPMNContentFromDB(searchKey,objBPMNData.getpUnitName(),objBPMNData.getProcessId(),objBPMNData.getCountryCode(),objBPMNData.getCompanyCode());
			if(objBPMNProcess!=null)
			{
				InputStream objInputStream = new ByteArrayInputStream(objBPMNProcess.getBpmnFileContent().getBytes(StandardCharsets.UTF_8));
				objBpmnModelInstance = Bpmn.readModelFromStream(objInputStream);
			}
			else
			{
				objBpmnModelInstance=getBPMNModelFromFile(searchKey,objBPMNData.getpUnitName(),objBPMNData.getProcessId(),objBPMNData.getCountryCode());
			}
			if(objBpmnModelInstance!=null)
			{
				bpmnModelInstanceMap.put(searchKey,objBpmnModelInstance);
			}
		}
		return objBpmnModelInstance;
	}
	
	private BpmnModelInstance getBPMNModelFromFile(String searchKey,String pUnitName,String processId,String countryCode) throws ApplicationException
	{
		BpmnModelInstance objBpmnModelInstance=null;
		URL path =Thread.currentThread().getContextClassLoader().getResource(searchKey+".bpmn");
		if(path!=null)
		{
			//System.out.println(path.getFile());
			File file = new File(path.getFile());
			objBpmnModelInstance = Bpmn.readModelFromFile(file);
			BPMNProcess objBPMNProcess=new BPMNProcess();
			objBPMNProcess.setBpmnId(searchKey);
			objBPMNProcess.setBpmnFileContent(getBPMNFileContent(file.getPath()));
			objBPMNProcess.setCreatedTime(DateUtility.getCountryTimeStamp(pUnitName,countryCode));
			insertBPMNContentToDB(objBPMNProcess,pUnitName,processId,countryCode);
		}
		else
		{
			throw new ApplicationException(searchKey+".bpmn File Not Found");
		}
		return objBpmnModelInstance;
	}
	
    private static String getBPMNFileContent(String filePath) throws ApplicationException
    {
        StringBuilder contentBuilder = new StringBuilder();
 
        try (Stream<String> stream = Files.lines( Paths.get(filePath), StandardCharsets.UTF_8)) 
        {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        }
        catch (IOException e) 
        {
            e.printStackTrace();
            throw new ApplicationException(e);
        }
        return contentBuilder.toString();
    }
	
	private void insertBPMNContentToDB(BPMNProcess objBPMNProcess,String pUnitName,String processId,String countryCode) throws ApplicationException
	{
		BPMNTaskDAO objBPMNTaskDAO=null;
		try
		{
			objBPMNTaskDAO=new BPMNTaskDAO(pUnitName);
			objBPMNTaskDAO.begin();
			objBPMNTaskDAO.createBPMNProcess(objBPMNProcess);
			objBPMNTaskDAO.commit();
		}
		catch(Exception ex)
		{
			if(objBPMNTaskDAO.isActive())
			{
				objBPMNTaskDAO.rollback();
			}
			ex.printStackTrace();
			throw new ApplicationException(ex);
		}
		finally
		{
			if(objBPMNTaskDAO!=null)
			{
				objBPMNTaskDAO.close();
			}
		}
	}
	private BPMNProcess getBPMNContentFromDB(String searchKey,String pUnitName,String processId,String countryCode,String companyCode) throws ApplicationException
	{
		BPMNTaskDAO objBPMNTaskDAO=null;
		BPMNProcess objBPMNProcess=null;
		try
		{
			objBPMNTaskDAO=new BPMNTaskDAO(pUnitName);
			objBPMNProcess=objBPMNTaskDAO.findBPMNProcess(companyCode, searchKey);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw new ApplicationException(ex);
		}
		finally
		{
			if(objBPMNTaskDAO!=null)
			{
				objBPMNTaskDAO.close();
			}
		}
		return objBPMNProcess;
	}
	
	public BPMNCompleterResultDTO initiateTransaction(BPMNData objBPMNData,String userId)throws Exception
	{
		BPMNTaskDAO objBPMNTaskDAO = null;
//		ArrayList<BPMNStepDTO> bpmnNextSteps=new ArrayList<BPMNStepDTO>();
		BPMNCompleterResultDTO objBPMNCompleterResultDTO=null;
		try
		{
			BpmnModelInstance objBpmnModelInstance=getBpmnModelInstance(objBPMNData,null);
			objBPMNTaskDAO = new BPMNTaskDAO(objBPMNData.getpUnitName());
			ModelElementType startType = objBpmnModelInstance.getModel().getType(StartEvent.class);
			Collection<ModelElementInstance> startInstances = objBpmnModelInstance.getModelElementsByType(startType);
			if(startInstances!=null && startInstances.size()==1)
			{
				for (ModelElementInstance element : startInstances) 
				{
					//System.out.println(element.getElementType().getTypeName());
					//System.out.println(element.getDomElement().getAttribute("id"));
					//System.out.println(element.getDomElement().getAttribute("name"));
					
					StartEventElement objStartEventElement=new StartEventElement();
					objStartEventElement.setObjBpmnModelInstance(objBpmnModelInstance);
					objStartEventElement.setObjElementModelInstance(element);
					BPMNProcess objBPMNProcess=objBPMNTaskDAO.findBPMNProcess(objBPMNData.getCompanyCode(), objBPMNData.getBpmnId());
					String bpmnTxrefNo=generateBPMNTXREFNO(objBPMNTaskDAO,objBPMNProcess.getProcessid());
					BPMNTask objBPMNTask=new BPMNTask();
					objBPMNTask.setBpmnId(objBPMNData.getBpmnId());
					objBPMNTask.setStatusCode(1);
					objBPMNTask.setElementId(element.getDomElement().getAttribute("id"));
					objBPMNTask.setElementType(element.getElementType().getTypeName());
					objBPMNTask.setBpmnTxRefNo(bpmnTxrefNo);
					objBPMNTask.setCompanyCode(objBPMNData.getCompanyCode());
					BPMNProcessInfo objBPMNProcessInfo=new BPMNProcessInfo();
					objBPMNProcessInfo.setBpmnTxRefNo(bpmnTxrefNo);
					objBPMNProcessInfo.setCompamnyCode(objBPMNData.getCompanyCode());
					objBPMNProcessInfo.setStatusCode(3);
					objBPMNProcessInfo.setInitatedBy(userId);
					objBPMNProcessInfo.setProcessid(objBPMNProcess.getProcessid());
					objBPMNProcessInfo.setBpmnId(objBPMNData.getBpmnId());
					objBPMNProcessInfo.setCreatedTime(DateUtility.getCountryTimeStamp(objBPMNData.getpUnitName(), null));
					objBPMNTaskDAO.begin();
					objBPMNTask=objBPMNTaskDAO.createBPMNTask(objBPMNTask);
					objBPMNTaskDAO.createBPMNProcessInfo(objBPMNProcessInfo);
					objBPMNTaskDAO.commit();
					objBPMNData.setBpmnTaskId(objBPMNTask.getBpmnTaskId());
					objBPMNData.setStepName(objBPMNTask.getElementId());
					objBPMNData.setInitatedBy(userId);
					objBPMNCompleterResultDTO=completeTask(objBPMNData);
					objBPMNCompleterResultDTO.setBpmnTxRefNo(bpmnTxrefNo);
//					ArrayList<String> nextSteps=objStartEventElement.completeTask(null);
//					if(nextSteps!=null && nextSteps.size()>0)
//					{
//						String bpmnTxrefNo=generateBPMNTXREFNO(objBPMNTaskDAO,objBPMNData.getBpmnId());
//						BPMNTask objBPMNTask=new BPMNTask();
//						objBPMNTask.setBpmnId(objBPMNData.getBpmnId());
//						objBPMNTask.setStatusCode(1);
//						objBPMNTask.setElementId(nextSteps.get(0));
//						BasicElement objBaseElement=getStepElement(objBpmnModelInstance, nextSteps.get(0));
//						if(objBaseElement!=null)
//						{
//							objBPMNTask.setElementType(objBaseElement.getElementType());
//						}
//						objBPMNTask.setBpmnTxRefNo(bpmnTxrefNo);
//						BPMNProcessInfo objBPMNProcessInfo=new BPMNProcessInfo();
//						objBPMNProcessInfo.setBpmnTxRefNo(bpmnTxrefNo);
//						objBPMNProcessInfo.setStatusCode(3);
//						objBPMNProcessInfo.setBpmnId(objBPMNData.getBpmnId());
//						objBPMNProcessInfo.setCreatedTime(DateUtility.getCountryTimeStamp(objBPMNData.getpUnitName(), null));
//						objBPMNTaskDAO.begin();
//						objBPMNTask=objBPMNTaskDAO.createBPMNTask(objBPMNTask);
//						objBPMNTaskDAO.createBPMNProcessInfo(objBPMNProcessInfo);
//						objBPMNTaskDAO.commit();
//						BPMNStepDTO stepDTO=new BPMNStepDTO();
//						stepDTO.setStepName(nextSteps.get(0));
//						stepDTO.setBpmnTaskId(objBPMNTask.getBpmnTaskId());
//						stepDTO.setBpmnId(objBPMNData.getBpmnId());
//						stepDTO.setBpmnTxRefNo(objBPMNTask.getBpmnTxRefNo());
//						bpmnNextSteps.add(stepDTO);
//					}
				}
			}
			else
			{
				throw new Exception("More than one StartEvent");
			}
		}
		catch(Exception ex)
		{
			if(objBPMNTaskDAO.isActive())
			{
				objBPMNTaskDAO.rollback();
			}
			throw new Exception(ex);
			
		}
		finally
		{
			if(objBPMNTaskDAO!=null && objBPMNTaskDAO.isOpen())
			{
				objBPMNTaskDAO.close();
			}
		}
		return objBPMNCompleterResultDTO;
	}
	

	public BPMNCompleterResultDTO initiateTransaction1(BPMNData objBPMNData, String bpmnId, String txrefNo,String callToken)throws Exception
	{
		BPMNTaskDAO objBPMNTaskDAO = null;
//		ArrayList<BPMNStepDTO> bpmnNextSteps=new ArrayList<BPMNStepDTO>();
		BPMNCompleterResultDTO objBPMNCompleterResultDTO=null;
		try
		{
			BpmnModelInstance objBpmnModelInstance=getBpmnModelInstance(objBPMNData,bpmnId);
			objBPMNTaskDAO = new BPMNTaskDAO(objBPMNData.getpUnitName());
			ModelElementType startType = objBpmnModelInstance.getModel().getType(StartEvent.class);
			Collection<ModelElementInstance> startInstances = objBpmnModelInstance.getModelElementsByType(startType);
			if(startInstances!=null && startInstances.size()==1)
			{
				for (ModelElementInstance element : startInstances) 
				{
					//System.out.println(element.getElementType().getTypeName());
					//System.out.println(element.getDomElement().getAttribute("id"));
					//System.out.println(element.getDomElement().getAttribute("name"));
					StartEventElement objStartEventElement=new StartEventElement();
					objStartEventElement.setObjBpmnModelInstance(objBpmnModelInstance);
					objStartEventElement.setObjElementModelInstance(element);
					BPMNTask objBPMNTask=new BPMNTask();
					objBPMNTask.setBpmnId(bpmnId);
					objBPMNTask.setStatusCode(1);
					objBPMNTask.setElementId(element.getDomElement().getAttribute("id"));
					objBPMNTask.setElementType(element.getElementType().getTypeName());
					objBPMNTask.setBpmnTxRefNo(txrefNo);
					objBPMNTask.setCompanyCode(objBPMNData.getCompanyCode());
					objBPMNTask.setGateWayToken(objBPMNData.getBpmnTask().getGateWayToken());
					objBPMNTask.setCallActivityToken(callToken);
					objBPMNTaskDAO.begin();
					objBPMNTask=objBPMNTaskDAO.createBPMNTask(objBPMNTask);
					objBPMNTaskDAO.commit();
					objBPMNData.setBpmnTaskId(objBPMNTask.getBpmnTaskId());
					objBPMNData.setStepName(objBPMNTask.getElementId());
					objBPMNData.setBpmnId(bpmnId);
					objBPMNCompleterResultDTO=completeTask(objBPMNData);
//					ArrayList<String> nextSteps=objStartEventElement.completeTask(null);
//					if(nextSteps!=null && nextSteps.size()>0)
//					{
//						BPMNTask objBPMNTask=new BPMNTask();
//						objBPMNTask.setBpmnId(bpmnId);
//						objBPMNTask.setStatusCode(1);
//						objBPMNTask.setElementId(nextSteps.get(0));
//						BasicElement objBaseElement=getStepElement(objBpmnModelInstance, nextSteps.get(0));
//						if(objBaseElement!=null)
//						{
//							objBPMNTask.setElementType(objBaseElement.getElementType());
//						}
//						objBPMNTask.setBpmnTxRefNo(txrefNo);
//						objBPMNTask.setCallActivityToken(callToken);
//						objBPMNTaskDAO.begin();
//						objBPMNTask=objBPMNTaskDAO.createBPMNTask(objBPMNTask);
//						objBPMNTaskDAO.commit();
//						BPMNStepDTO stepDTO=new BPMNStepDTO();
//						stepDTO.setStepName(nextSteps.get(0));
//						stepDTO.setBpmnTaskId(objBPMNTask.getBpmnTaskId());
//						stepDTO.setBpmnId(bpmnId);
//						bpmnNextSteps.add(stepDTO);
//					}
				}
			}
			else
			{
				throw new Exception("More than one StartEvent");
			}
		}
		catch(Exception ex)
		{
			if(objBPMNTaskDAO.isActive())
			{
				objBPMNTaskDAO.rollback();
			}
			throw new Exception(ex);
			
		}
		finally
		{
			if(objBPMNTaskDAO!=null && objBPMNTaskDAO.isOpen())
			{
				objBPMNTaskDAO.close();
			}
		}
		return objBPMNCompleterResultDTO;
	}
	
	
	public BPMNCompleterResultDTO completeTask(BPMNData objBPMNData)throws Exception
	{
		BPMNTaskDAO objBPMNTaskDAO = null;
		ArrayList<BPMNStepDTO> bpmnNextSteps=new ArrayList<BPMNStepDTO>();
		BPMNCompleterResultDTO objBPMNCompleterResultDTO=new BPMNCompleterResultDTO();
		objBPMNCompleterResultDTO.setDataMap(objBPMNData.getDataMap());
		try
		{
			objBPMNTaskDAO = new BPMNTaskDAO(objBPMNData.getpUnitName());
			BPMNTask objBpmnTask=objBPMNTaskDAO.findBPMNTaskById(objBPMNData.getBpmnTaskId());
			BpmnModelInstance objBpmnModelInstance=getBpmnModelInstance(objBPMNData,objBpmnTask.getBpmnId());
			BasicElement objBaseElement=getStepElement(objBpmnModelInstance, objBPMNData.getStepName());
			objBPMNData.setBpmnTask(objBpmnTask);
			if(objBaseElement!=null && (objBPMNData.getBpmnTask().getStatusCode()==3 || objBPMNData.getBpmnTask().getStatusCode()==1||objBPMNData.getBpmnTask().getStatusCode()==20))
			{
				BPMNProcessInfo objBPMNProcessInfo=objBPMNTaskDAO.findBPMNProcessInfo(objBPMNData.getBpmnTask().getBpmnTxRefNo());
				objBPMNProcessInfo.setLastStepName(objBPMNData.getStepName());
				objBPMNTaskDAO.begin();
				objBPMNTaskDAO.updateCompleteBPMNTaskStatus(objBPMNData.getBpmnTaskId(),2,objBPMNData.getCompletedBy());
				if(objBaseElement.getElementType()!=null && 
				  (objBaseElement.getElementType().equalsIgnoreCase("userTask")||objBaseElement.getElementType().equalsIgnoreCase("serviceTask")))
				{
					objBPMNTaskDAO.updateBPMNTaskResponse(objBPMNData.getBpmnTaskId(), (String)objBPMNData.getDataMap().get("selectedresponse"));
				}
				objBPMNData.setInitatedBy(objBPMNProcessInfo.getInitatedBy());
				objBPMNTaskDAO.updateBPMNProcessInfo(objBPMNProcessInfo);
				objBPMNTaskDAO.commit();
				ArrayList<String> nextSteps=objBaseElement.completeTask(objBPMNData);
				//System.out.println("#completeTask#nextSteps#"+nextSteps);
				if(nextSteps!=null && nextSteps.size()>0)
				{
					for(String stepName:nextSteps)
					{
						String gateWayToken=objBPMNData.getBpmnTask().getGateWayToken();
						if(stepName.contains("#"))
						{
							gateWayToken = objBPMNData.getBpmnTask().getBpmnTaskId() + ","+stepName.split("#")[1];
							stepName=stepName.split("#")[0];
						}
						BasicElement objBaseElement1=getStepElement(objBpmnModelInstance,stepName);
						if(objBaseElement1 instanceof UserTaskElement)
						{
							BPMNTask task=objBaseElement1.createTask(objBPMNData,gateWayToken);
							BPMNStepDTO stepDTO=new BPMNStepDTO();
							stepDTO.setStepName(stepName);
							stepDTO.setBpmnTaskId(task.getBpmnTaskId());
							bpmnNextSteps.add(stepDTO);
						}
						else if(objBaseElement1 instanceof ServiceTaskElement)
						{
							BPMNTask task=objBaseElement1.createTask(objBPMNData,gateWayToken);
							BPMNStepDTO stepDTO=new BPMNStepDTO();
							stepDTO.setStepName(stepName);
							stepDTO.setBpmnTaskId(task.getBpmnTaskId());
							bpmnNextSteps.add(stepDTO);
						}
						else if(objBaseElement1 instanceof ScriptTaskElement)
						{
							BPMNTask task=objBaseElement1.createTask(objBPMNData,gateWayToken);
							if(task!=null)
							{
								BPMNData objBPMNData1=new BPMNData();
								objBPMNData1.setpUnitName(objBPMNData.getpUnitName());
								objBPMNData1.setDataMap(objBPMNData.getDataMap());
								objBPMNData1.setBpmnId(task.getBpmnId());
								objBPMNData1.setStepName(task.getElementId());
								objBPMNData1.setBpmnTaskId(task.getBpmnTaskId());
								objBPMNData1.setCompanyCode(task.getCompanyCode());
								objBPMNData1.setBpmnTask(task);
								//System.out.println("objBPMNData1.getDataMap()#before#"+objBPMNData1.getDataMap());
								BPMNCompleterResultDTO objBPMNCompleterResultDTO1=completeTask(objBPMNData1);
								//System.out.println("objBPMNData1.getDataMap()#after#"+objBPMNData1.getDataMap());
								if(objBPMNCompleterResultDTO1.getBpmnNextSteps()!=null && objBPMNCompleterResultDTO1.getBpmnNextSteps().size()>0)
								{
									bpmnNextSteps.addAll(objBPMNCompleterResultDTO1.getBpmnNextSteps());
								}
								BPMNStepDTO stepDTO=new BPMNStepDTO();
								stepDTO.setStepName(stepName);
								stepDTO.setBpmnTaskId(task.getBpmnTaskId());
								objBPMNCompleterResultDTO.getBpmnScriptSteps().add(stepDTO);
								objBPMNCompleterResultDTO.setDataMap(objBPMNData1.getDataMap());
								objBPMNData.setDataMap(objBPMNData1.getDataMap());
//								bpmnNextSteps.addAll(nextStepsDTOList);
							}
						}
						else if(objBaseElement1 instanceof ParallelGateWayElement)
						{
							BPMNTask task=objBaseElement1.createTask(objBPMNData,gateWayToken);
							if(task!=null)
							{
								BPMNData objBPMNData1=new BPMNData();
								objBPMNData1.setpUnitName(objBPMNData.getpUnitName());
								objBPMNData1.setDataMap(objBPMNData.getDataMap());
								objBPMNData1.setBpmnId(task.getBpmnId());
								objBPMNData1.setStepName(task.getElementId());
								objBPMNData1.setBpmnTaskId(task.getBpmnTaskId());
								objBPMNData1.setCompanyCode(task.getCompanyCode());
								objBPMNData1.setBpmnTask(task);
								BPMNCompleterResultDTO objBPMNCompleterResultDTO1=completeTask(objBPMNData1);
								if(objBPMNCompleterResultDTO1.getBpmnNextSteps()!=null && objBPMNCompleterResultDTO1.getBpmnNextSteps().size()>0)
								{
									bpmnNextSteps.addAll(objBPMNCompleterResultDTO1.getBpmnNextSteps());
								}
								if(objBPMNCompleterResultDTO1.getBpmnScriptSteps()!=null && objBPMNCompleterResultDTO1.getBpmnScriptSteps().size()>0)
								{
									objBPMNCompleterResultDTO.getBpmnScriptSteps().addAll(objBPMNCompleterResultDTO1.getBpmnScriptSteps());
								}
							}
						}
						else if(objBaseElement1 instanceof InclusiveGateWayElement)
						{
							BPMNTask task=objBaseElement1.createTask(objBPMNData,gateWayToken);
							if(task!=null)
							{
								BPMNData objBPMNData1=new BPMNData();
								objBPMNData1.setpUnitName(objBPMNData.getpUnitName());
								objBPMNData1.setDataMap(objBPMNData.getDataMap());
								objBPMNData1.setBpmnId(task.getBpmnId());
								objBPMNData1.setStepName(task.getElementId());
								objBPMNData1.setBpmnTaskId(task.getBpmnTaskId());
								objBPMNData1.setCompanyCode(task.getCompanyCode());
								objBPMNData1.setBpmnTask(task);
								BPMNCompleterResultDTO objBPMNCompleterResultDTO1=completeTask(objBPMNData1);
								if(objBPMNCompleterResultDTO1.getBpmnNextSteps()!=null && objBPMNCompleterResultDTO1.getBpmnNextSteps().size()>0)
								{
									bpmnNextSteps.addAll(objBPMNCompleterResultDTO1.getBpmnNextSteps());
								}
								if(objBPMNCompleterResultDTO1.getBpmnScriptSteps()!=null && objBPMNCompleterResultDTO1.getBpmnScriptSteps().size()>0)
								{
									objBPMNCompleterResultDTO.getBpmnScriptSteps().addAll(objBPMNCompleterResultDTO1.getBpmnScriptSteps());
								}
							}
						}
						else if(objBaseElement1 instanceof ExclusiveGateWayElement)
						{
							BPMNTask task=objBaseElement1.createTask(objBPMNData,gateWayToken);
							if(task!=null)
							{
								BPMNData objBPMNData1=new BPMNData();
								objBPMNData1.setpUnitName(objBPMNData.getpUnitName());
								objBPMNData1.setDataMap(objBPMNData.getDataMap());
								objBPMNData1.setBpmnId(task.getBpmnId());
								objBPMNData1.setStepName(task.getElementId());
								objBPMNData1.setBpmnTaskId(task.getBpmnTaskId());
								objBPMNData1.setCompanyCode(task.getCompanyCode());
								objBPMNData1.setBpmnTask(task);
								BPMNCompleterResultDTO objBPMNCompleterResultDTO1=completeTask(objBPMNData1);
								if(objBPMNCompleterResultDTO1.getBpmnNextSteps()!=null && objBPMNCompleterResultDTO1.getBpmnNextSteps().size()>0)
								{
									bpmnNextSteps.addAll(objBPMNCompleterResultDTO1.getBpmnNextSteps());
								}
								if(objBPMNCompleterResultDTO1.getBpmnScriptSteps()!=null && objBPMNCompleterResultDTO1.getBpmnScriptSteps().size()>0)
								{
									objBPMNCompleterResultDTO.getBpmnScriptSteps().addAll(objBPMNCompleterResultDTO1.getBpmnScriptSteps());
								}
							}
						}
						else if(objBaseElement1 instanceof CallActivityElement)
						{
							BPMNTask task=objBaseElement1.createTask(objBPMNData,gateWayToken);
							if(task!=null)
							{
								BPMNData objBPMNData1=new BPMNData();
								objBPMNData1.setpUnitName(objBPMNData.getpUnitName());
								objBPMNData1.setDataMap(objBPMNData.getDataMap());
								objBPMNData1.setBpmnId(task.getBpmnId());
								objBPMNData1.setStepName(task.getElementId());
								objBPMNData1.setBpmnTaskId(task.getBpmnTaskId());
								objBPMNData1.setCompanyCode(task.getCompanyCode());
								objBPMNData1.setBpmnTask(task);
								String calledElement=objBaseElement1.getObjElementModelInstance().getAttributeValue("calledElement");
								BPMNCompleterResultDTO objBPMNCompleterResultDTO1=initiateTransaction1(objBPMNData1,calledElement,task.getBpmnTxRefNo(),String.valueOf(task.getBpmnTaskId()));
								if(objBPMNCompleterResultDTO1.getBpmnNextSteps()!=null && objBPMNCompleterResultDTO1.getBpmnNextSteps().size()>0)
								{
									bpmnNextSteps.addAll(objBPMNCompleterResultDTO1.getBpmnNextSteps());
								}
								if(objBPMNCompleterResultDTO1.getBpmnScriptSteps()!=null && objBPMNCompleterResultDTO1.getBpmnScriptSteps().size()>0)
								{
									objBPMNCompleterResultDTO.getBpmnScriptSteps().addAll(objBPMNCompleterResultDTO1.getBpmnScriptSteps());
								}
							}
						}
						else if(objBaseElement1 instanceof TerminateEndEvent)
						{
							objBPMNTaskDAO.begin();
							objBPMNProcessInfo.setStatusCode(7);
							objBPMNProcessInfo.setCurrentStepName(null);
							objBPMNTaskDAO.updateBPMNProcessInfo(objBPMNProcessInfo);
							objBPMNTaskDAO.commit();
						}
						else if(objBaseElement1 instanceof EndEventElement)
						{
							if(objBPMNData.getBpmnTask().getCallActivityToken()!=null)
							{
								long taskId=Long.parseLong(objBPMNData.getBpmnTask().getCallActivityToken());
								BPMNTask task=objBPMNTaskDAO.findBPMNTaskById(taskId);
								if(task!=null)
								{
									BPMNData objBPMNData1=new BPMNData();
									objBPMNData1.setpUnitName(objBPMNData.getpUnitName());
									objBPMNData1.setDataMap(objBPMNData.getDataMap());
									objBPMNData1.setBpmnId(task.getBpmnId());
									objBPMNData1.setStepName(task.getElementId());
									objBPMNData1.setBpmnTaskId(task.getBpmnTaskId());
									objBPMNData1.setCompanyCode(task.getCompanyCode());
									objBPMNData1.setBpmnTask(task);
									BPMNCompleterResultDTO objBPMNCompleterResultDTO1=completeTask(objBPMNData1);
									if(objBPMNCompleterResultDTO1.getBpmnNextSteps()!=null && objBPMNCompleterResultDTO1.getBpmnNextSteps().size()>0)
									{
										bpmnNextSteps.addAll(objBPMNCompleterResultDTO1.getBpmnNextSteps());
									}
									if(objBPMNCompleterResultDTO1.getBpmnScriptSteps()!=null && objBPMNCompleterResultDTO1.getBpmnScriptSteps().size()>0)
									{
										objBPMNCompleterResultDTO.getBpmnScriptSteps().addAll(objBPMNCompleterResultDTO1.getBpmnScriptSteps());
									}
								}
							}
							else
							{
								objBPMNTaskDAO.begin();
								objBPMNProcessInfo.setStatusCode(2);
								objBPMNProcessInfo.setCurrentStepName(null);
								objBPMNTaskDAO.updateBPMNProcessInfo(objBPMNProcessInfo);
								objBPMNTaskDAO.commit();
							}
						}
					}
					objBPMNCompleterResultDTO.setBpmnNextSteps(bpmnNextSteps);
				}
				objBPMNCompleterResultDTO.setBpmnTxRefNo(objBPMNData.getBpmnTask().getBpmnTxRefNo());
			}
		}
//		catch(javax.persistence.OptimisticLockException ex)
//		{
//			if(objBPMNTaskDAO.isActive())
//			{
//				objBPMNTaskDAO.rollback();
//			}
//			objBPMNTaskDAO.close();
//			//System.out.println("javax.persistence.OptimisticLockException################"+objBPMNData.getBpmnTaskId()+"#"+objBPMNData.getStepName());
//			Thread.currentThread().sleep(3000);
//			completeTask(objBPMNData);
//		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			//System.out.println(ex.getMessage());
			if(ex.getMessage().startsWith("Row was updated or deleted by another transaction"))
			{
				//System.out.println("javax.persistence.OptimisticLockException################"+objBPMNData.getBpmnTaskId()+"#"+objBPMNData.getStepName());
				if(objBPMNTaskDAO.isActive())
				{
					objBPMNTaskDAO.rollback();
				}
				objBPMNTaskDAO.close();
				Thread.currentThread().sleep(3000);
				completeTask(objBPMNData);
			}
			else
			{
				if(objBPMNTaskDAO.isActive())
				{
					objBPMNTaskDAO.rollback();
				}
				throw new Exception(ex);
			}
		}
		finally
		{
			objBPMNTaskDAO.close();
		}
		return objBPMNCompleterResultDTO;
		
	}
	public BasicElement getStepElement(BpmnModelInstance objBpmnModelInstance,String stepName)
	{
		ModelElementInstance element=objBpmnModelInstance.getModelElementById(stepName);
		//System.out.println(element.getDomElement().getAttribute("id"));
		//System.out.println(element.getDomElement().getAttribute("name"));
		//System.out.println(element.getElementType().getTypeName());
		//System.out.println(element.getDomElement().getChildElements());
		BasicElement objBaseElement= BasicElement.getElement(objBpmnModelInstance, element,element.getElementType().getTypeName());
		return objBaseElement;	
	}
	
	
	private String generateBPMNTXREFNO(BPMNTaskDAO objBPMNTaskDAO,String bpmnId) throws ApplicationException
	{
		String	gentxno = null;
		try
		{
			String date  = DateUtility.getCurrentTimeStamp(objBPMNTaskDAO.getObjJProvider().getpUnitName(),"DDMMYY");
			String scanDate = "";
			if(date!=null){
				scanDate = 	date.replaceAll("[/,-]+", "");
			}
			String seqVal=objBPMNTaskDAO.getSeqVal("bpmnprocessseq",6,0);
			gentxno=bpmnId+scanDate+seqVal;
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new ApplicationException(e);
		}
		return gentxno;
	}
	
}
