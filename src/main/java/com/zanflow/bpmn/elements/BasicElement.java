package com.zanflow.bpmn.elements;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.Documentation;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;

import com.zanflow.bpmn.BPMNData;
import com.zanflow.bpmn.dao.BPMNTaskDAO;
import com.zanflow.bpmn.exception.ApplicationException;
import com.zanflow.bpmn.model.BPMNTask;
import com.zanflow.sec.dao.UserMgmtDAO;
import com.zanflow.sec.model.User;

//import groovy.lang.Binding;
//import groovy.lang.GroovyShell;


public abstract class BasicElement 
{
	private BpmnModelInstance objBpmnModelInstance=null;
	private ModelElementInstance objElementModelInstance=null;
	private String elementType;
	
	public static BasicElement getElement(BpmnModelInstance objBpmnModelInstance,ModelElementInstance objElementModelInstance,String elementType)
	{
		BasicElement objBaseElement=null;
		if(elementType!=null)
		{
			if(elementType.equalsIgnoreCase("userTask"))
			{
				objBaseElement=new UserTaskElement();
			}
			else if(elementType.equalsIgnoreCase("serviceTask"))
			{
				objBaseElement=new ServiceTaskElement();
			}
			else if(elementType.equalsIgnoreCase("scriptTask"))
			{
				objBaseElement=new ScriptTaskElement();
			}
			else if(elementType.equalsIgnoreCase("exclusiveGateway"))
			{
				objBaseElement=new ExclusiveGateWayElement();
			}
			else if(elementType.equalsIgnoreCase("parallelGateway"))
			{
				objBaseElement=new ParallelGateWayElement();
			}
			else if(elementType.equalsIgnoreCase("inclusiveGateway"))
			{
				objBaseElement=new InclusiveGateWayElement();
			}
			else if(elementType.equalsIgnoreCase("startEvent"))
			{
				objBaseElement=new StartEventElement();
			}
			else if(elementType.equalsIgnoreCase("callActivity"))
			{
				objBaseElement=new CallActivityElement();
			}
			else if(elementType.equalsIgnoreCase("endEvent"))
			{
				boolean isTerminate=false;
				for(DomElement child:objElementModelInstance.getDomElement().getChildElements())
				{
					if(child.getLocalName().equalsIgnoreCase("terminateEventDefinition"))
					{
						isTerminate=true; break;
					}
				}
				if(isTerminate)
				{
					objBaseElement=new TerminateEndEvent();
				}
				else
				{
					objBaseElement=new EndEventElement();
				}
			}
			if(objBaseElement!=null)
			{
				objBaseElement.setObjBpmnModelInstance(objBpmnModelInstance);
				objBaseElement.setObjElementModelInstance(objElementModelInstance);
				objBaseElement.setElementType(elementType);
			}
			
		}
		return objBaseElement;
	}
	
	public abstract ArrayList<String> completeTask(BPMNData objBPMNData);
	
	public abstract BPMNTask createTask(BPMNData objBPMNData,String stringParam2);
	
	public BPMNTask createTask(BPMNData objBPMNData,String stepName,String gateWayToken,String elementType,int statuscode) 
	{
		BPMNTask objBPMNTask=null;
		BPMNTaskDAO objBPMNTaskDAO=null;
		UserMgmtDAO objUserMgmtDAO=null;
		try
		{
			
			objBPMNTaskDAO=new BPMNTaskDAO(objBPMNData.getpUnitName());
			objBPMNTask=new BPMNTask();
			objBPMNTask.setBpmnId(objBPMNData.getBpmnId());
			objBPMNTask.setElementId(stepName);
			objBPMNTask.setElementType(elementType);
			objBPMNTask.setGateWayToken(gateWayToken);
			objBPMNTask.setStatusCode(statuscode);
			objBPMNTask.setCompanyCode(objBPMNData.getCompanyCode());
			objBPMNTask.setBpmnTxRefNo(objBPMNData.getBpmnTask().getBpmnTxRefNo());
			objBPMNTask.setCallActivityToken(objBPMNData.getBpmnTask().getCallActivityToken());
			objBPMNTask.setTaskCreatedDate(new Timestamp(System.currentTimeMillis()));
			objBPMNTask.setDueDate(new Timestamp(System.currentTimeMillis()));
			objBPMNTask.setStepLabel( getObjElementModelInstance().getDomElement().getAttribute("name"));
			
			if(elementType == "userTask") {
				UserTask userTaskObj = (UserTask) getObjElementModelInstance();
				String assignedUser = userTaskObj.getCamundaCandidateUsers();
				String assignedRole = userTaskObj.getCamundaCandidateGroups();
				
				System.out.println("----------------assignedUser-- "+ assignedUser + " -------------getInitatedBygetInitatedBy-- " + objBPMNData.getInitatedBy());
				if(assignedUser!=null && assignedUser != "" ) 
				{
					if(assignedUser.startsWith("User:"))
					{
						assignedUser=assignedUser.replaceAll("User:", "");
						assignedUser=(String)objBPMNData.getDataMap().get(assignedUser);
					}else if(assignedUser.equalsIgnoreCase("Initiator")){
						assignedUser= objBPMNData.getInitatedBy();
					}else if(assignedUser.equalsIgnoreCase("Initiators_Manager")){
						objUserMgmtDAO=new UserMgmtDAO(objBPMNData.getpUnitName());
						User user = objUserMgmtDAO.findUser(objBPMNData.getInitatedBy());
						if(user !=null) {
							assignedUser= user.getManagerId();
						}
					}
					objBPMNTask.setAssignedUser(assignedUser);
				}
				else if(assignedRole!=null && assignedRole != "" ) 
				{
					if(assignedRole.startsWith("Role:"))
					{
						assignedRole=assignedRole.replaceAll("Role:", "");
						assignedRole=(String)objBPMNData.getDataMap().get(assignedRole);
					}
					objBPMNTask.setAssignedRole(assignedRole);
				}
				
				Collection<Documentation> documentations = userTaskObj.getDocumentations();
				if(documentations.size() >0) {
					Documentation doc = documentations.iterator().next();
					String taskSubject = doc.getTextContent();
					objBPMNTask.setTaskSubject(getTaskSubject(objBPMNData.getDataMap(), taskSubject));
				}
			}
			
			objBPMNTaskDAO.begin();
			objBPMNTask=objBPMNTaskDAO.createBPMNTask(objBPMNTask);
			objBPMNTaskDAO.commit();
		}
		catch(Exception ex)
		{
			objBPMNTask=null;
			if(objBPMNTaskDAO.isActive())
			{
				objBPMNTaskDAO.rollback();
			}
			ex.printStackTrace();
		}
		finally
		{
			if(objBPMNTaskDAO!=null)
			{
				try {
					objBPMNTaskDAO.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(objUserMgmtDAO!=null)
			{
				try {
					objUserMgmtDAO.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return objBPMNTask;
	}
	
//	public TaskJPAImpl createTask(EopsData objEopsData,String stepName,String gateWayToken) 
//	{
//		TaskJPAImpl task=null;
//		TaskDAOJPAImpl taskDAO=null;
//		try
//		{
//			taskDAO=new TaskDAOJPAImpl(objEopsData.getpUnitName(), objEopsData.getTask().getProcessId(), objEopsData.getTask().getCountryCode());
//			task=new TaskJPAImpl();
//			task.setTxRefNo(objEopsData.getTask().getTxRefNo());
//			task.setProcessId(objEopsData.getTask().getProcessId());
//			task.setCountryCode(objEopsData.getTask().getCountryCode());
//			task.setBpmnId(objEopsData.getTask().getBpmnId());
//			task.setCallActivityToken(objEopsData.getTask().getCallActivityToken());
////			task.setStringParam2(stringParam2);
//			task.setGateWayToken(gateWayToken);
//			task.setStepName(stepName);
//			task.setExternalTaskId("bpmn");
//			task.setStepType("M");
//			task.setQueueName("bpmnQue");
//			task.setCanReassign("Y");
//			task.setResponsecode(0);
//			task.setPriority(1);
//			task.setIsolatedRegion("bpmn");
//			task.setElapsedTime(0);
//			task.setIsmodified("N");
//			task.setStatusCode(1);
//			taskDAO.begin();
//			long taskId=taskDAO.createTask(task);
//			task.setTaskId(taskId);
//			taskDAO.commit();
//		}
//		catch(Exception ex)
//		{
//			task=null;
//			if(taskDAO.isActive())
//			{
//				taskDAO.rollback();
//			}
//			ex.printStackTrace();
//		}
//		finally
//		{
//			if(taskDAO!=null)
//			{
//				try {
//					taskDAO.close();
//				} catch (ApplicationException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//		return task;
//	}

	public List<BPMNTask> getTaskList(BPMNData objBPMNData)
	{
		BPMNTaskDAO taskDAO=null;
		List<BPMNTask> taskList=null;
		try
		{
			taskDAO=new BPMNTaskDAO(objBPMNData.getpUnitName());
			taskList=taskDAO.getBPMNTasksByTXREFNO(objBPMNData.getBpmnTask().getBpmnTxRefNo());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			if(taskDAO!=null)
			{
				try {
					taskDAO.close();
				} catch (ApplicationException e) {
					e.printStackTrace();
				}
			}
		}
		return taskList;
	}
	public String getTaskGateWayToken(long taskId,BPMNData objBPMNData)
	{
		String gateWayToken=null;
		BPMNTaskDAO taskDAO=null;
		try
		{
			taskDAO=new BPMNTaskDAO(objBPMNData.getpUnitName());
			BPMNTask task=taskDAO.findBPMNTaskById(taskId);
			if(task!=null)
			{
				gateWayToken=task.getGateWayToken();
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			if(taskDAO!=null)
			{
				try {
					taskDAO.close();
				} catch (ApplicationException e) {
					e.printStackTrace();
				}
			}
		}
		return gateWayToken;
	}
	
	public boolean checkTaskStatus(BPMNData objBPMNData,String stepName,List<BPMNTask> taskList)
	{
		boolean flag=false;
		if(taskList!=null && taskList.size()>0)
		{
			for(BPMNTask objTaskJPAImpl:taskList)
			{
				//System.out.println("checkTaskStatus#"+stepName+"#"+objTaskJPAImpl.getElementId()+"#"+objTaskJPAImpl.getStatusCode()+"#"+objTaskJPAImpl.getGateWayToken()+"#"+objBPMNData.getBpmnTask().getGateWayToken());
				if(objBPMNData.getBpmnTask().getBpmnId().equals(objTaskJPAImpl.getBpmnId())&&objTaskJPAImpl.getElementId().equals(stepName) && objTaskJPAImpl.getStatusCode()==2 && 
						(objTaskJPAImpl.getGateWayToken()==null || objTaskJPAImpl.getGateWayToken().equals(objBPMNData.getBpmnTask().getGateWayToken())))
				{
					flag=true;
					break;
				}
			}
		}
		return flag;
	}
	
	
	public void updateTaskGateWayToken(BPMNData objBPMNData, String gateWayToken) 
	{
		BPMNTaskDAO taskDAO=null;
		try
		{
			taskDAO=new BPMNTaskDAO(objBPMNData.getpUnitName());
			taskDAO.begin();
			taskDAO.updateBPMNTaskGateWayToken(objBPMNData.getBpmnTask().getBpmnTaskId(), gateWayToken);
			taskDAO.commit();
		}
		catch(Exception ex)
		{
			if(taskDAO.isActive())
			{
				taskDAO.rollback();
			}
			ex.printStackTrace();
		}
		finally
		{
			if(taskDAO!=null)
			{
				try {
					taskDAO.close();
				} catch (ApplicationException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public ArrayList<String> getOutGoingSequence(ModelElementInstance element)
	{
		ArrayList<String> outGoingSeqList=new ArrayList<String>();
		if(element!=null && element.getDomElement().getChildElements()!=null)
		{
			for(DomElement child: element.getDomElement().getChildElements())
			{
				if(child.getLocalName().equalsIgnoreCase("outgoing"))
				{
					outGoingSeqList.add(child.getTextContent());
				}
			}
		}
		return outGoingSeqList;
	}
	
	
	public ArrayList<String> getIncomingSequence(ModelElementInstance element)
	{
		ArrayList<String> inComingSeqList=new ArrayList<String>();
		if(element!=null && element.getDomElement().getChildElements()!=null)
		{
			for(DomElement child: element.getDomElement().getChildElements())
			{
				if(child.getLocalName().equalsIgnoreCase("incoming"))
				{
					inComingSeqList.add(child.getTextContent());
				}
			}
		}
		return inComingSeqList;
	}
	

	public BpmnModelInstance getObjBpmnModelInstance() {
		return objBpmnModelInstance;
	}

	public void setObjBpmnModelInstance(BpmnModelInstance objBpmnModelInstance) {
		this.objBpmnModelInstance = objBpmnModelInstance;
	}

	public ModelElementInstance getObjElementModelInstance() {
		return objElementModelInstance;
	}

	public void setObjElementModelInstance(
			ModelElementInstance objElementModelInstance) {
		this.objElementModelInstance = objElementModelInstance;
	}

	public String getElementType() {
		return elementType;
	}

	public void setElementType(String elementType) {
		this.elementType = elementType;
	}
	
	
	private String getTaskSubject(Map dataMap,String taskSubject)throws Exception
	{
		String resultTaskSubject = taskSubject;
		try
		{
			HashSet<String> keys=new HashSet<String>();
			if(resultTaskSubject!=null)
			{
//				Pattern p=Pattern.compile("#"+"(.*?)"+"#");
//				Matcher m = p.matcher(resultTaskSubject);
//				 while (m.find()) {
//					 //System.out.println(m.group(1));
//					 keys.add(m.group(1));
//				 }
				String []keywords=resultTaskSubject.split(" ");
				if(keywords!=null && keywords.length>0)
				{
					for(String key:keywords)
					{
						if(key.startsWith("zf."))
						{
							key = key.replaceAll("zf.", "");
							keys.add(key);
						}
					}
				}
			}
			for(Object key:keys)
			{
				Object value=dataMap.get(key);
				//System.out.println(key + " ------------- " + value);
				if(value!=null && value instanceof String)
				{
					resultTaskSubject = resultTaskSubject.replaceAll("zf."+key, value.toString());
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw new Exception(ex);
		}
		if(resultTaskSubject != null && resultTaskSubject.length()>200) {
			resultTaskSubject = resultTaskSubject.substring(0, 200);
		}
		return resultTaskSubject;
	}
	
}
