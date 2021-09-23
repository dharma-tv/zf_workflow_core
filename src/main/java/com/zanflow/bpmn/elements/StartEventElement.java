package com.zanflow.bpmn.elements;

import java.util.ArrayList;

import org.camunda.bpm.model.bpmn.instance.SequenceFlow;

import com.zanflow.bpmn.BPMNData;
import com.zanflow.bpmn.model.BPMNTask;


public class StartEventElement extends BasicElement
{

	@Override
	public ArrayList<String> completeTask(BPMNData objBPMNData) 
	{
		ArrayList<String> nextSteps=null;
		ArrayList<String> outGoingSeqList=getOutGoingSequence(getObjElementModelInstance());
		nextSteps=getNextSteps(outGoingSeqList, objBPMNData);
		return nextSteps;
	}

	@Override
	public BPMNTask createTask(BPMNData objBPMNData,String stringParam2) {
		BPMNTask task=createTask(objBPMNData,getObjElementModelInstance().getDomElement().getAttribute("id"),stringParam2,"startEvent",1);
		return task;
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
				if(objSequenceFlow.getTarget()!=null && objSequenceFlow.getTarget().getId()!=null)
				{
					createTask(objBPMNData,outFlowId,gateWayToken,"Flow",2);
					nextSteps.add(objSequenceFlow.getTarget().getId());
					break;
				}
			}
		}
		return nextSteps;
	}
	
}
