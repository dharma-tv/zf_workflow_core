package com.zanflow.bpmn.elements;

import java.util.ArrayList;
import java.util.List;

import org.camunda.bpm.model.bpmn.instance.SequenceFlow;

import com.zanflow.bpmn.BPMNData;
import com.zanflow.bpmn.model.BPMNTask;
import com.zanflow.bpmn.util.FlowConditionValidator;



public class ExclusiveGateWayElement extends BasicElement{

	@Override
	public ArrayList<String> completeTask(BPMNData  objBPMNData) 
	{
		ArrayList<String> nextSteps=new ArrayList<String>();
		String gateWayToken1=null;
		ArrayList<String> outGoingSeqList=getOutGoingSequence(getObjElementModelInstance());
		if(outGoingSeqList!=null && outGoingSeqList.size()>0)
		{
			if(outGoingSeqList.size()==1)
			{
				String gateWayToken=objBPMNData.getBpmnTask().getGateWayToken();
				if(gateWayToken!=null)
				{
					gateWayToken=gateWayToken.substring(0,gateWayToken.lastIndexOf(","));
					gateWayToken1=getTaskGateWayToken(Long.parseLong(gateWayToken), objBPMNData);
//					updateTaskGateWayToken(objBPMNData,gateWayToken1);
					objBPMNData.getBpmnTask().setGateWayToken(gateWayToken1);
				}
			}
			nextSteps=getNextSteps(outGoingSeqList, objBPMNData);
			if(outGoingSeqList.size()>1)
			{
				ArrayList<String> subStepWithParent = new ArrayList<String>();
				for(String subStep:nextSteps){
					String stepName = subStep + "#E"+nextSteps.size();//+ taskId;
						subStepWithParent.add(stepName);
				}
				nextSteps=subStepWithParent;
			}
			else
			{
				if(gateWayToken1!=null)
				{
					updateTaskGateWayToken(objBPMNData,gateWayToken1);
				}
//				String gateWayToken=objBPMNData.getBpmnTask().getGateWayToken();
//				if(gateWayToken!=null)
//				{
//					gateWayToken=gateWayToken.substring(0,gateWayToken.lastIndexOf(","));
//					String gateWayToken1=getTaskGateWayToken(Long.parseLong(gateWayToken), objBPMNData);
//					updateTaskGateWayToken(objBPMNData,gateWayToken1);
//					objBPMNData.getBpmnTask().setGateWayToken(gateWayToken1);
//				}
			}
		}
		return nextSteps;
	}

	
	@Override
	public BPMNTask createTask(BPMNData   objBPMNData,String stringParam2) 
	{
		ArrayList<String> inComingSeqList=getIncomingSequence(getObjElementModelInstance());
		boolean isIncomingTaskCompleted=true;
		List<BPMNTask> taskList=getTaskList(objBPMNData);
		if(inComingSeqList!=null && inComingSeqList.size()>0)
		{
			if(inComingSeqList.size()>1)
			{
				isIncomingTaskCompleted=false;
				int count=0,actCount=0;
				String gateWayToken=objBPMNData.getBpmnTask().getGateWayToken();
				if(gateWayToken!=null && gateWayToken.length()>0)
				{
					String[] param2Arr=gateWayToken.split(",");
					String lastValue = param2Arr[param2Arr.length-1];
							if(lastValue.startsWith("E"))
							{
								try
								{
									count=Integer.parseInt(lastValue.replaceAll("E", ""));
								}
								catch(Exception ex){}
							}
					
				}
				if(count>0)
				for(String inFlowId:inComingSeqList)
				{
//					//System.out.println("#ExclusiveGateWayElement#"+objBPMNData.getBpmnTask().getBpmnTaskId()+"#"+objBPMNData.getBpmnTask().getElementId()+"#"+objBPMNData.getBpmnTask().getGateWayToken());
//					SequenceFlow objSequenceFlow=getObjBpmnModelInstance().getModelElementById(inFlowId);
//					//System.out.println("ExclusiveGateWayElement#objSequenceFlow.getSource()#"+objSequenceFlow.getSource());
//					//System.out.println("ExclusiveGateWayElement#objSequenceFlow.getSource().getId()#"+objSequenceFlow.getSource().getId());
//					isIncomingTaskCompleted=checkTaskStatus(objBPMNData,  objSequenceFlow.getSource().getId(),taskList);
//					//System.out.println("ExclusiveGateWayElement#objSequenceFlow.getSource().getId()#"+objSequenceFlow.getSource().getId()+"#flag#"+isIncomingTaskCompleted);
					//System.out.println("#ExclusiveGateWayElement#objSequenceFlowID#"+inFlowId);
					isIncomingTaskCompleted=checkTaskStatus(objBPMNData,  inFlowId,taskList);
					//System.out.println("ExclusiveGateWayElement#objSequenceFlowID#"+inFlowId+"#flag#"+isIncomingTaskCompleted);
					if(isIncomingTaskCompleted)
					{
						actCount++;
					}
					if(count==actCount)
					{
						break;
					}
				}
				isIncomingTaskCompleted=(count>0 && count==actCount);
				//System.out.println("#ExclusiveGateWayElement#count#"+count+"#actCount#"+actCount+"#flag#"+isIncomingTaskCompleted);
			}
			else
			{
				for(String inFlowId:inComingSeqList)
				{
					//System.out.println("#ExclusiveGateWayElement#objSequenceFlowID#"+inFlowId);
					isIncomingTaskCompleted=checkTaskStatus(objBPMNData,  inFlowId,taskList);
//					SequenceFlow objSequenceFlow=getObjBpmnModelInstance().getModelElementById(inFlowId);
//					//System.out.println("ExclusiveGateWayElement#objSequenceFlow.getSource()#"+objSequenceFlow.getSource());
//					//System.out.println("ExclusiveGateWayElement#objSequenceFlow.getSource().getId()#"+objSequenceFlow.getSource().getId());
//					if(objSequenceFlow.getSource()!=null && objSequenceFlow.getSource().getId()!=null)
//					{
//						isIncomingTaskCompleted=checkTaskStatus(objBPMNData,  objSequenceFlow.getSource().getId(),taskList);
//					}
				}
			}
		}
		if(isIncomingTaskCompleted)
		{
			BPMNTask task=createTask(objBPMNData,getObjElementModelInstance().getDomElement().getAttribute("id"),stringParam2,"exclusiveGateway",1);
			return task;
		}
		return null;
	}
	
	private ArrayList<String> getNextSteps(ArrayList<String> outGoingSeqList,BPMNData objBPMNData)
	{
		ArrayList<String> nextSteps=new ArrayList<String>();
		ArrayList<SequenceFlow> nonCondSequenceList=new ArrayList<SequenceFlow>();
		SequenceFlow objDefSequenceFlow=null;
		String gateWayToken=objBPMNData.getBpmnTask().getGateWayToken();
		if(getObjBpmnModelInstance()!=null)
		{
			String defaultFlowId=getObjElementModelInstance().getAttributeValue("default");
			for(String outFlowId:outGoingSeqList)
			{
				SequenceFlow objSequenceFlow=getObjBpmnModelInstance().getModelElementById(outFlowId);
				if(objSequenceFlow.getConditionExpression()!=null)
				{					
					String conditionExpression = objSequenceFlow.getConditionExpression().getTextContent();
					//System.out.println("conditionExpression ---> " + conditionExpression);
					try 
					{
					   boolean result = FlowConditionValidator.validateCondition(objBPMNData.getDataMap(), conditionExpression);
					   if(result){
						   createTask(objBPMNData,outFlowId,gateWayToken,"Flow",2);
						   nextSteps.add(objSequenceFlow.getTarget().getId());
						   break;
					   }
					} 
					catch (Exception e) 
					{
						e.printStackTrace();
					}
				}
				else
				{
					if(defaultFlowId!=null && objDefSequenceFlow==null)
					{
						if(defaultFlowId.equalsIgnoreCase(outFlowId))
						{
							objDefSequenceFlow=objSequenceFlow;
						}
						else
						{
							nonCondSequenceList.add(objSequenceFlow);
						}
					}
					else
					{
						nonCondSequenceList.add(objSequenceFlow);
					}
				}
			}
			if(nextSteps.size()==0)
			{
				if(objDefSequenceFlow!=null && objDefSequenceFlow.getTarget()!=null && objDefSequenceFlow.getTarget().getId()!=null)
				{
					createTask(objBPMNData,objDefSequenceFlow.getId(),gateWayToken,"Flow",2);
					nextSteps.add(objDefSequenceFlow.getTarget().getId());
				}
			}
			if(nextSteps.size()==0)
			{
				for(SequenceFlow objSequenceFlow:nonCondSequenceList)
				{
					if(objSequenceFlow.getTarget()!=null && objSequenceFlow.getTarget().getId()!=null)
					{
						createTask(objBPMNData,objSequenceFlow.getId(),gateWayToken,"Flow",2);
						nextSteps.add(objSequenceFlow.getTarget().getId());
						break;	
					}
				}
			}
		}
		return nextSteps;
	}
}
