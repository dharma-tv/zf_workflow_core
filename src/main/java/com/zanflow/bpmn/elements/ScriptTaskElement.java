package com.zanflow.bpmn.elements;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.xml.instance.DomElement;

import com.zanflow.bpmn.BPMNData;
import com.zanflow.bpmn.model.BPMNTask;
import com.zanflow.bpmn.util.FlowConditionValidator;
import com.zanflow.bpmn.util.MasterDataUpdater;

public class ScriptTaskElement extends BasicElement
{

	@Override
	public ArrayList<String> completeTask(BPMNData objBPMNData) 
	{
		ArrayList<String> nextSteps=null;
		objBPMNData=executeRule(objBPMNData);
		ArrayList<String> outGoingSeqList=getOutGoingSequence(getObjElementModelInstance());
		nextSteps=this.getNextSteps(outGoingSeqList, objBPMNData);
		return nextSteps;
	}

	@Override
	public BPMNTask createTask(BPMNData objBPMNData, String stringParam2) 
	{
		BPMNTask task=createTask(objBPMNData,getObjElementModelInstance().getDomElement().getAttribute("id"),stringParam2,"scriptTask",1);
		return task;
	}
	
	public ArrayList<String> getNextSteps(ArrayList<String> outGoingSeqList,BPMNData objBPMNData)
	{
		ArrayList<String> nextSteps=new ArrayList<String>();
		String gateWayToken=objBPMNData.getBpmnTask().getGateWayToken();
		if(getObjBpmnModelInstance()!=null)
		{
			for(String outFlowId:outGoingSeqList)
			{
				SequenceFlow objSequenceFlow=getObjBpmnModelInstance().getModelElementById(outFlowId);
				if(objSequenceFlow.getConditionExpression()!=null)
				{					
					String conditionExpression = objSequenceFlow.getConditionExpression().getTextContent();
					System.out.println("conditionExpression ---> " + conditionExpression);
					try 
					{
						boolean result = FlowConditionValidator.validateCondition(objBPMNData.getDataMap(), conditionExpression);
						if(result)
						{
							createTask(objBPMNData,outFlowId,gateWayToken,"Flow",2);
							nextSteps.add(objSequenceFlow.getTarget().getId());
						}
					} 
					catch (Exception e) 
					{
						e.printStackTrace();
					}
				}
				else
				{
					if(objSequenceFlow.getTarget()!=null && objSequenceFlow.getTarget().getId()!=null)
					{
						createTask(objBPMNData,outFlowId,gateWayToken,"Flow",2);
						nextSteps.add(objSequenceFlow.getTarget().getId()); 
						break;
					}
				}
			}
		}
		return nextSteps;
	}
	
	public BPMNData executeRule(BPMNData objBPMNData) 
	{
		try
		{
			Binding objBinding = new Binding();
			System.out.println("objBPMNData.getDataMap()#"+objBPMNData.getDataMap());
			if(objBPMNData.getDataMap()!=null && objBPMNData.getDataMap().size()>0)
			{
//				Set<String> keySet=new HashSet<String>();
//				for(Object key:objBPMNData.getDataMap().keySet())
//				{
//					Object value=objBPMNData.getDataMap().get(key);
//					if(value!=null && value instanceof String)
//					{
//						keySet.add(key.toString());
//						objBinding.setVariable(key.toString(),value.toString());
//					}
//				}
				objBinding.setVariable("zf", objBPMNData.getDataMap());
				objBinding.setVariable("masterObj", new MasterDataUpdater(objBPMNData.getCompanyCode()));
				
				GroovyShell shell = new GroovyShell(objBinding);
				String script=null;
				for(DomElement objDomElement:getObjElementModelInstance().getDomElement().getChildElements())
				{
					if(objDomElement.getLocalName().equalsIgnoreCase("script"))
					{
						script=objDomElement.getTextContent();
						break;
					}
				}
				System.out.println("script----->>>"+script);
				if(script!=null)
				{
					shell.parse(script).run();
					objBPMNData.setDataMap((Map<String, Object>) objBinding.getVariable("zf"));
//					for(String key:keySet)
//					{
//						Object ob=objBinding.getVariable(key);
//						if(ob!=null && ob instanceof String)
//						{
//							objBPMNData.getDataMap().put(key, ob.toString());
//						}
//					}
				}
			}
			System.out.println("objBPMNData.getDataMap()#"+objBPMNData.getDataMap());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return objBPMNData;
	}

}
