package com.zanflow.bpmn.service.notification;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.zanflow.bpmn.dao.BPMNTaskDAO;
import com.zanflow.bpmn.dto.BPMNStepDTO;
import com.zanflow.bpmn.exception.JPAIllegalStateException;
import com.zanflow.bpmn.model.BPMNNotification;
import com.zanflow.bpmn.model.pk.BPMNNotificationPK;
import com.zanflow.common.db.Constants;

public class NotificationHelper 
{
	public void sendMail(String bpmnTxRefno,String bpmnId,String stepName,String triggerEvent,Map<String, Object> datamap,String processId,String companyCode)
	{
		BPMNTaskDAO objBPMNTaskDAO=null;
		try
		{
			if(datamap==null)
			{
				datamap=new HashMap<String, Object>();
			}
			objBPMNTaskDAO=new BPMNTaskDAO(Constants.DB_PUNIT);
			datamap.put("TXNREFNO", bpmnTxRefno);
			Notifier objNotifier=Notifier.getNotifier();
			
			BPMNNotificationPK objPk=new BPMNNotificationPK();
			objPk.setCompanyCode(companyCode);
//			objPk.setProcessId(processId);
			objPk.setBpmnId(bpmnId);
			objPk.setStepName(stepName);
			objPk.setTriggerEvent(triggerEvent);
			
			
			datamap.put("STEPNAME", stepName);
			
			BPMNNotification objNotfication=objBPMNTaskDAO.findBPMNNotification(objPk);
			if(objNotfication!=null)
			{
				String subject=objNotfication.getSubject();
				for(String key:datamap.keySet())
				{
					String tag="<<"+key+">>";
					subject=subject.replaceAll(tag, (String) datamap.get(key));
				}
				
				String mailContent=objNotfication.getMailContent();
				for(String key:datamap.keySet())
				{
					String tag="<<"+key+">>";
					mailContent=mailContent.replaceAll(tag, (String) datamap.get(key));
				}
				objNotifier.sendEmail(objNotfication.getToEmail(), objNotfication.getCcEmail(), subject, mailContent);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally 
		{
			if(objBPMNTaskDAO!=null)
			{
				try {
					objBPMNTaskDAO.close(Constants.DB_PUNIT);
				} catch (JPAIllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public void sendMail(String bpmnTxRefno,ArrayList<BPMNStepDTO> bpmnNextSteps,String triggerEvent,Map<String, Object> datamap,String processId,String companyCode,String bpmnId)
	{
		BPMNTaskDAO objBPMNTaskDAO=null;
		try
		{
			if(datamap==null)
			{
				datamap=new HashMap<String, Object>();
			}
			objBPMNTaskDAO=new BPMNTaskDAO(Constants.DB_PUNIT);
			datamap.put("TXNREFNO", bpmnTxRefno);
			Notifier objNotifier=Notifier.getNotifier();
			System.out.println("Notification "+bpmnTxRefno+" bpmnNextSteps "+bpmnNextSteps.size()+" bpmnNextSteps "+bpmnNextSteps);
			if(bpmnNextSteps!=null && bpmnNextSteps.size()>0)
			{
				for(BPMNStepDTO objStep:bpmnNextSteps)
				{
					System.out.println("Notification objStep "+objStep.getStepName());
					
					BPMNNotificationPK objPk=new BPMNNotificationPK();
					objPk.setCompanyCode(companyCode);
//					objPk.setProcessId(processId);
					objPk.setBpmnId(bpmnId);
					objPk.setStepName(objStep.getStepName());
					objPk.setTriggerEvent(triggerEvent);
					
					System.out.println("Notification objStep objPk "+objPk);
					datamap.put("STEPNAME", objStep.getStepName());
					
					BPMNNotification objNotfication=objBPMNTaskDAO.findBPMNNotification(objPk);
					if(objNotfication!=null)
					{
						System.out.println("Notification "+objNotfication);
						String subject=objNotfication.getSubject();
						String toEmail = objNotfication.getToEmail();
						String ccEmail = objNotfication.getCcEmail();
						for(String key:datamap.keySet())
						{
							String tag=" zf."+key+" ";
							if(datamap.get(key) instanceof String) {
								subject=subject.replaceAll(tag, " " + (String) datamap.get(key) + " ");
							}
							if(toEmail!=null && toEmail.startsWith("zf.") && toEmail.equals("zf."+key)) {
								toEmail = (String) datamap.get(key);
							}
							System.out.println("Notification To "+objNotfication.getToEmail() + " | Notification CC " + ccEmail + " " + ccEmail.equals("zf."+key));
							if(ccEmail!=null && ccEmail.startsWith("zf.") && ccEmail.equals("zf."+key)) {
								ccEmail = (String) datamap.get(key);
							}
							
						}
						
						String mailContent=objNotfication.getMailContent();
						for(String key:datamap.keySet())
						{
							String tag=" zf."+key+" ";
							if(datamap.get(key) instanceof String) {
							   mailContent=mailContent.replaceAll(tag, " " + (String) datamap.get(key) + " ");
							}
						}
						
						
						objNotifier.sendEmail(toEmail, ccEmail, subject, mailContent);
					}
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally 
		{
			if(objBPMNTaskDAO!=null)
			{
				try {
					objBPMNTaskDAO.close(Constants.DB_PUNIT);
				} catch (JPAIllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void main(String[] args) throws UnsupportedEncodingException 
	{
		NotificationHelper obj=new NotificationHelper();
		Map<String, Object> datamap=new HashMap<String, Object>();
		datamap.put("LOANNO", "ACDSFJ2342304");
		Notifier objNotifier=Notifier.getNotifier();
		objNotifier.sendEmail("dharma.tv1@gmail.com", "", "Test notification from zanflow" , "Welcome to Zanflow, sent from Java test");
		//obj.sendMail("10293723283","P006_IN_V1", "Maker", "Complete",datamap,null,"zanflow");
	}
}
