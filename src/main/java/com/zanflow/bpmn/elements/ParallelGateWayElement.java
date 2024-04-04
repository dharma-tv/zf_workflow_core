package com.zanflow.bpmn.elements;

import java.util.ArrayList;
import java.util.List;

import org.camunda.bpm.model.bpmn.instance.SequenceFlow;

import com.zanflow.bpmn.BPMNData;
import com.zanflow.bpmn.model.BPMNTask;
import com.zanflow.bpmn.util.FlowConditionValidator;




public class ParallelGateWayElement extends BasicElement
{

	@Override
	public ArrayList<String> completeTask(BPMNData  objBPMNData) {
		ArrayList<String> nextSteps=new ArrayList<String>();
		String gateWayToken1=null;
		ArrayList<String> outGoingSeqList=getOutGoingSequence(getObjElementModelInstance());
		if(outGoingSeqList!=null && outGoingSeqList.size()==1)
		{
			String gateWayToken=objBPMNData.getBpmnTask().getGateWayToken();
			if(gateWayToken!=null)
			{
				gateWayToken=gateWayToken.substring(0,gateWayToken.lastIndexOf(","));
				gateWayToken1=getTaskGateWayToken(Long.parseLong(gateWayToken), objBPMNData);
//				updateTaskGateWayToken(objBPMNData,gateWayToken1);
				objBPMNData.getBpmnTask().setGateWayToken(gateWayToken1);
			}
		}
		nextSteps=getNextSteps(outGoingSeqList, objBPMNData);
		if(outGoingSeqList!=null && outGoingSeqList.size()>1)
		{
			ArrayList<String> subStepWithParent = new ArrayList<String>();
			for(String subStep:nextSteps){
				String stepName = subStep + "#P";
				subStepWithParent.add(stepName);
			}
			nextSteps=subStepWithParent;
		}
		else
		{
			updateTaskGateWayToken(objBPMNData,gateWayToken1);
//			String gateWayToken=objBPMNData.getBpmnTask().getGateWayToken();
//			if(gateWayToken!=null)
//			{
//				gateWayToken=gateWayToken.substring(0,gateWayToken.lastIndexOf(","));
//				String gateWayToken1=getTaskGateWayToken(Long.parseLong(gateWayToken), objBPMNData);
//				updateTaskGateWayToken(objBPMNData,gateWayToken1);
//				objBPMNData.getBpmnTask().setGateWayToken(gateWayToken1);
//			}
		}
		return nextSteps;
	}
	
	@Override
	public BPMNTask createTask(BPMNData objBPMNData,String stringParam2) 
	{
		ArrayList<String> inComingSeqList=getIncomingSequence(getObjElementModelInstance());
		boolean isIncomingTaskCompleted=true;
		List<BPMNTask> taskList=getTaskList(objBPMNData);
		if(inComingSeqList!=null && inComingSeqList.size()>0)
		{
			for(String inFlowId:inComingSeqList)
			{
				//System.out.println("objSequenceFlowID#"+inFlowId);
//				SequenceFlow objSequenceFlow=getObjBpmnModelInstance().getModelElementById(inFlowId);
//				//System.out.println("objSequenceFlow.getSource()#"+objSequenceFlow.getSource());
//				//System.out.println("objSequenceFlow.getSource().getId()#"+objSequenceFlow.getSource().getId());
//				if(objSequenceFlow.getSource()!=null && objSequenceFlow.getSource().getId()!=null)
//				{
					isIncomingTaskCompleted=checkTaskStatus(objBPMNData,  inFlowId,taskList);
					if(!isIncomingTaskCompleted)
					{
						break;
					}
//				}
			}
		}
		if(isIncomingTaskCompleted)
		{
			BPMNTask task=createTask(objBPMNData,getObjElementModelInstance().getDomElement().getAttribute("id"),stringParam2,"parallelGateway",1);
			return task;
		}
		else
		{
			return null;
		}
	}
	
	private ArrayList<String> getNextSteps(ArrayList<String> outGoingSeqList,BPMNData objBPMNData)
	{
		ArrayList<String> nextSteps=new ArrayList<String>();
		String gateWayToken=objBPMNData.getBpmnTask().getGateWayToken();
		if(getObjBpmnModelInstance()!=null)
		{
			for(String outFlowId:outGoingSeqList)
			{
				SequenceFlow objSequenceFlow=getObjBpmnModelInstance().getModelElementById(outFlowId);
//				if(objSequenceFlow.getConditionExpression()!=null)
//				{					
//					String conditionExpression = objSequenceFlow.getConditionExpression().getTextContent();
//					//System.out.println("conditionExpression ---> " + conditionExpression);
//					try 
//					{
//						boolean result = FlowConditionValidator.validateCondition(objBPMNData.getDataMap(), conditionExpression);
//						if(result)
//						{
//							nextSteps.add(objSequenceFlow.getTarget().getId());
//						}
//					} 
//					catch (Exception e) 
//					{
//						e.printStackTrace();
//					}
//				}
//				else
//				{
					if(objSequenceFlow.getTarget()!=null && objSequenceFlow.getTarget().getId()!=null)
					{
						//System.out.println("parallel gatewayToken#"+outFlowId+"#"+gateWayToken);
						createTask(objBPMNData,outFlowId,gateWayToken,"Flow",2);
						nextSteps.add(objSequenceFlow.getTarget().getId());
					}
//				}
			}
		}
		return nextSteps;
	}
}
