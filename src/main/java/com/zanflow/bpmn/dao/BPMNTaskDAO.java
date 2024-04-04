package com.zanflow.bpmn.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.persistence.OptimisticLockException;
import javax.persistence.Query;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import com.zanflow.bpmn.dto.TaskDTO;
import com.zanflow.bpmn.exception.ApplicationException;
import com.zanflow.bpmn.exception.JPAPersistenceException;
import com.zanflow.bpmn.model.BPMNComments;
import com.zanflow.bpmn.model.BPMNNotification;
import com.zanflow.bpmn.model.BPMNProcess;
import com.zanflow.bpmn.model.BPMNProcessInfo;
import com.zanflow.bpmn.model.BPMNTask;
import com.zanflow.bpmn.model.TXNDocments;
import com.zanflow.bpmn.model.pk.BPMNNotificationPK;
import com.zanflow.bpmn.util.DateUtility;
import com.zanflow.common.db.JPATransaction;
import com.zanflow.common.db.JPersistenceProvider;



public class BPMNTaskDAO extends JPATransaction
{

	public BPMNTaskDAO(String pJunitName)throws ApplicationException
	{
		//System.out.println("#BpmnTaskDAO#pJunitName#"+pJunitName);
		try {
            if(pJunitName != null && pJunitName.trim().length() > 0){
           	 strJPUnitName = pJunitName;
            }
            else {
           	 throw new ApplicationException("Arguments are NULL, Unable to create JPersistenceProvider.");
            }
            //System.out.println("#pJunitName#"+strJPUnitName);
       	 	objJProvider = new JPersistenceProvider(strJPUnitName);
		} catch (ApplicationException e) {
			e.printStackTrace();
			throw new ApplicationException(new Exception(e));
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
			throw new ApplicationException(new Exception(e));
		} catch (EntityExistsException e) {
			e.printStackTrace();
			throw new ApplicationException(new Exception(e));
		} 
	}	


	public BPMNTaskDAO(JPersistenceProvider objJProvider) throws ApplicationException
	{
		try{
			if (objJProvider != null)
			{
				this.objJProvider = objJProvider;
				strJPUnitName = objJProvider.getpUnitName();
				//System.out.println("# BpmnTaskDAO objJProvider constructor invoked");
			}else{
				throw new ApplicationException("BpmnTaskDAO : JPersistenceProvider is null");
			}
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
			throw new ApplicationException(new Exception(e));
		} catch (EntityExistsException e) {
			e.printStackTrace();
			throw new ApplicationException(new Exception(e));
		} catch (JPAPersistenceException e) {
			e.printStackTrace();
			throw new ApplicationException(new Exception(e));
		}	
	}


	public BPMNTask findBPMNTaskById(long id) throws ApplicationException 
	{
		long t1 = System.currentTimeMillis();
		BPMNTask task = new BPMNTask();

		try {
			//System.out.println("#BpmnTaskDAO#findBPMNTaskById#TaskId#" + id);
			if (id >= 0) {

				task = (BPMNTask) objJProvider.find(BPMNTask.class, id);
			} else {
				throw new ApplicationException("Task Id is invalid");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ApplicationException(new Exception(e));
		}
		finally
		{
			//System.out.println("#BpmnTaskDAO#findBPMNTaskById#End#TT#"+ (System.currentTimeMillis() - t1));
		}
		return task;
	}


	public int findLastLockedTimeDiff(long id) throws ApplicationException 
	{
		long t1 = System.currentTimeMillis();
		int timeDiff=0;

		try {
			//System.out.println("#BpmnTaskDAO#findLastLockedTimeDiff#TaskId#" + id);
			if (id >= 0) {

				Object	obj = objJProvider.createNativeQuery("SELECT extract(epoch from (now() - ex.tasklockedtime )) / 60 FROM {h-schema}zf_txn_bpmntask ex where ex.bpmntaskid=:bpmntaskid").setParameter("bpmntaskid", id).getResultList().get(0);
				if(obj != null) {
				Double	diff = Double.valueOf((String) obj);
				if(diff >0)
					timeDiff = diff.intValue();
				}
			} else {
				throw new ApplicationException("Task Id is invalid");
			}
		//System.out.println("diff "+timeDiff );
		} catch (Exception e) {
			e.printStackTrace();
			throw new ApplicationException(new Exception(e));
		}
		finally
		{
			//System.out.println("#BpmnTaskDAO#findBPMNTaskById#End#TT#"+ (System.currentTimeMillis() - t1));
		}
		return timeDiff;
	}
	
	public int deleteBPMNTask(long bpmnTaskId) throws ApplicationException 
	{	
		long t1 = System.currentTimeMillis();
		//System.out.println("#BpmnTaskDAO#deleteBPMNTask#"+bpmnTaskId);
		int rows = 0;
		try {
			if (bpmnTaskId <= 0)
				throw new ApplicationException("Invalid BPMN Task Id");
			BPMNTask task = (BPMNTask) objJProvider.find(BPMNTask.class, bpmnTaskId);
			objJProvider.delete(task);
			rows = 1;
		} catch (Exception e) {
			rows = -1;
			e.printStackTrace();
			throw new ApplicationException(new Exception(e));

		}
		finally
		{
			//System.out.println("#BpmnTaskDAO#deleteBPMNTask#End#TT#"+ (System.currentTimeMillis() - t1));
		}
		return rows;

	}


	public BPMNTask createBPMNTask(BPMNTask task) throws ApplicationException 
	{
		long t1 = System.currentTimeMillis();
		BPMNTask newTask = null;
		try {
			 newTask =(BPMNTask) objJProvider.merge((BPMNTask) task);
		} catch (Exception e) {
			e.printStackTrace();
			//log.printErrorMessage(e);
			throw new ApplicationException(e.getMessage());
		}
		finally
		{
			//System.out.println("#BpmnTaskDAO#createBPMNTask#End#TT#"+ (System.currentTimeMillis() - t1));
		}
		return newTask;
	}

	
	public int updateBPMNTask(BPMNTask task) throws ApplicationException {
		long t1 = System.currentTimeMillis();
		int rows = 0;
		try {
			if (task != null && task.getBpmnTaskId() > 0) {
				objJProvider.merge((BPMNTask) task);
				rows = 1;
			} else {
				rows = -1;
				throw new ApplicationException("Failed to update");
			}

		} catch (Exception e) {
			rows = -1;
			e.printStackTrace();
			throw new ApplicationException(new Exception(e));
		}
		finally
		{
			//System.out.println( "#BpmnTaskDAO#updateBPMNTask#End#TT#"+ (System.currentTimeMillis() - t1));
		}
		return rows;
	}

	
	public BPMNProcessInfo createBPMNProcessInfo(BPMNProcessInfo objBPMNProcessInfo) throws ApplicationException 
	{
		long t1 = System.currentTimeMillis();
		BPMNProcessInfo newTask = null;
		try {
			 newTask =(BPMNProcessInfo) objJProvider.merge((BPMNProcessInfo) objBPMNProcessInfo);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ApplicationException(e.getMessage());
		}
		finally
		{
			//System.out.println("#BpmnTaskDAO#createBPMNProcessInfo#End#TT#"+ (System.currentTimeMillis() - t1));
		}
		return newTask;
	}
	
	public BPMNProcessInfo findBPMNProcessInfo(String bpmnTxRefNo) throws ApplicationException 
	{
		long t1 = System.currentTimeMillis();
		BPMNProcessInfo objBPMNProcessInfo = null;

		try {
			//System.out.println("#BpmnTaskDAO#findBPMNTaskById#TaskId#" + bpmnTxRefNo);
			if (bpmnTxRefNo!=null && bpmnTxRefNo.length()>0) {

				objBPMNProcessInfo = (BPMNProcessInfo) objJProvider.find(BPMNProcessInfo.class, bpmnTxRefNo);
			} else {
				throw new ApplicationException("bpmnTxRefNo is invalid");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ApplicationException(new Exception(e));
		}
		finally
		{
			//System.out.println("#BpmnTaskDAO#findBPMNProcessInfo#End#TT#"+ (System.currentTimeMillis() - t1));
		}
		return objBPMNProcessInfo;
	}
	
	public int updateBPMNProcessInfo(BPMNProcessInfo objBPMNProcessInfo) throws ApplicationException 
	{
		long t1 = System.currentTimeMillis();
		int rows = 0;
		try {
			if (objBPMNProcessInfo != null && objBPMNProcessInfo.getBpmnTxRefNo()!=null) {
				objJProvider.merge((BPMNProcessInfo) objBPMNProcessInfo);
				rows = 1;
			} else {
				rows = -1;
				throw new ApplicationException("Failed to update");
			}

		}
		catch(javax.persistence.OptimisticLockException ex)
		{
			rows = -1;
			ex.printStackTrace();
			//log.printErrorMessage(e);
			throw new OptimisticLockException(ex);
		}
		catch (Exception e) {
			rows = -1;
			e.printStackTrace();
			//log.printErrorMessage(e);
			throw new ApplicationException(new Exception(e));
		}
		finally
		{
			//System.out.println( "#BpmnTaskDAO#updateBPMNProcessInfo#End#TT#"+ (System.currentTimeMillis() - t1));
		}
		return rows;
	}
	
	public int updateBPMNTaskStatus(long bpmnTaskId,int status) throws ApplicationException
	{
		long t1 = System.currentTimeMillis();
		//System.out.println("#BpmnTaskDAO#updateBPMNTaskStatus#"+bpmnTaskId+"#"+status);
		int iupdateCount=0;
		try{
			Query q=objJProvider.createQuery("update BPMNTask set statusCode=?1 where bpmnTaskId=?2");
			q.setParameter(1,status);
			q.setParameter(2,bpmnTaskId);
			iupdateCount=q.executeUpdate();
		}catch(Exception e){
			e.printStackTrace();
			//log.printErrorMessage(e);
			throw new ApplicationException(e);
		}
		finally
		{
			//System.out.println("#BpmnTaskDAO#updateTaskStatus#End#TT#" + (System.currentTimeMillis() - t1));
		}
		return iupdateCount;
	}
	
	public int updateCompleteBPMNTaskStatus(long bpmnTaskId,int status,String userId) throws ApplicationException
	{
		long t1 = System.currentTimeMillis();
		//System.out.println("#BpmnTaskDAO#updateBPMNTaskStatus#"+bpmnTaskId+"#"+status);
		int iupdateCount=0;
		try{
			Query q=objJProvider.createQuery("update BPMNTask set statusCode=:statusCode,completedBy=:completedBy,taskcompletedate=now() where bpmnTaskId=:bpmnTaskId");
			q.setParameter("statusCode",status);
			q.setParameter("bpmnTaskId",bpmnTaskId);
			q.setParameter("completedBy",userId);
			iupdateCount=q.executeUpdate();
		}catch(Exception e){
			e.printStackTrace();
			//log.printErrorMessage(e);
			throw new ApplicationException(e);
		}
		finally
		{
			//System.out.println("#BpmnTaskDAO#updateTaskStatus#End#TT#" + (System.currentTimeMillis() - t1));
		}
		return iupdateCount;
	}
	
	public int updateBPMNTaskStatus(long bpmnTaskId,int status,String userId) throws ApplicationException
	{
		long t1 = System.currentTimeMillis();
		//System.out.println("#BpmnTaskDAO#updateBPMNTaskStatus#"+bpmnTaskId+"#"+status);
		int iupdateCount=0;
		Query q;
		try{
			if(status == 3)
				q=objJProvider.createQuery("update BPMNTask set statusCode=:statusCode,lockeduser=:lockeduser,tasklockedtime=now() where bpmnTaskId=:bpmnTaskId and statusCode!=2");
			else
				q=objJProvider.createQuery("update BPMNTask set statusCode=:statusCode,lockeduser=null where bpmnTaskId=:bpmnTaskId and lockeduser=:lockeduser and statusCode!=2");
			q.setParameter("statusCode",status);
			q.setParameter("bpmnTaskId",bpmnTaskId);
			q.setParameter("lockeduser",userId);
			iupdateCount=q.executeUpdate();
		}catch(Exception e){
			e.printStackTrace();
			//log.printErrorMessage(e);
			throw new ApplicationException(e);
		}
		finally
		{
			//System.out.println("#BpmnTaskDAO#updateTaskStatus#End#TT#" + (System.currentTimeMillis() - t1));
		}
		return iupdateCount;
	}
	
	public int updateBPMNTaskResponse(long bpmnTaskId,String selectedResponse) throws ApplicationException
	{
		long t1 = System.currentTimeMillis();
		//System.out.println("#BpmnTaskDAO#updateBPMNTaskResponse#"+bpmnTaskId+"#"+selectedResponse);
		int iupdateCount=0;
		try{
			Query q=objJProvider.createQuery("update BPMNTask set selectedResponse=?1 where bpmnTaskId=?2");
			q.setParameter(1,selectedResponse);
			q.setParameter(2,bpmnTaskId);
			iupdateCount=q.executeUpdate();
		}catch(Exception e){
			e.printStackTrace();
			//log.printErrorMessage(e);
			throw new ApplicationException(e);
		}
		finally
		{
			//System.out.println("#BpmnTaskDAO#updateBPMNTaskResponse#End#TT#" + (System.currentTimeMillis() - t1));
		}
		return iupdateCount;
	}
	
	public int updateBPMNTaskGateWayToken(long bpmnTaskId,String gateWayToken)throws ApplicationException
	{
		long t1 = System.currentTimeMillis();
		//System.out.println("#BpmnTaskDAO#updateBPMNTaskGateWayToken#"+bpmnTaskId+"#"+gateWayToken);
		int iupdateCount=0;
		try
		{
			Query objQuery=objJProvider.createQuery("update BPMNTask set gateWayToken=:gateWayToken where bpmnTaskId=:bpmnTaskId");
			objQuery.setParameter("gateWayToken", gateWayToken);
			objQuery.setParameter("bpmnTaskId", bpmnTaskId);
			iupdateCount=objQuery.executeUpdate();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			//log.printErrorMessage(ex);
			throw new ApplicationException(ex);
		}
		finally
		{
			//System.out.println("#BpmnTaskDAO#updateBPMNTaskGateWayToken#End#TT#" + (System.currentTimeMillis() - t1));
		}
		return iupdateCount;
	}
	
	public List<BPMNTask> getBPMNTasksByTXREFNO(String txrefno)throws ApplicationException
	{
		long t1 = System.currentTimeMillis();
		//System.out.println("#BpmnTaskDAO#getBPMNTasksByTXREFNO#"+txrefno);
		List<BPMNTask> bmpnTaskList=null;
		try
		{
			Query objQuery=objJProvider.createQuery("select t from BPMNTask t where bpmnTxRefNo=:bpmnTxRefNo order by bpmnTaskId desc");
			objQuery.setParameter("bpmnTxRefNo", txrefno);
			bmpnTaskList=objQuery.getResultList();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			//log.printErrorMessage(ex);
			throw new ApplicationException(ex);
		}
		finally
		{
			//System.out.println("#BpmnTaskDAO#getBPMNTasksByTXREFNO#End#TT#" + (System.currentTimeMillis() - t1));
		}
		return bmpnTaskList;
	  }

	public List<BPMNTask> getActiveTasks(String txrefno)throws ApplicationException
	{
		long t1 = System.currentTimeMillis();
		//System.out.println("#BpmnTaskDAO#getBPMNTasksByTXREFNO#"+txrefno);
		List<BPMNTask> bmpnTaskList=null;
		try
		{
			Query objQuery=objJProvider.createQuery("select t from BPMNTask t where bpmnTxRefNo=:bpmnTxRefNo and statusCode=1  order by bpmnTaskId desc");
			objQuery.setParameter("bpmnTxRefNo", txrefno);
			bmpnTaskList=objQuery.getResultList();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			//log.printErrorMessage(ex);
			throw new ApplicationException(ex);
		}
		finally
		{
			//System.out.println("#BpmnTaskDAO#getBPMNTasksByTXREFNO#End#TT#" + (System.currentTimeMillis() - t1));
		}
		return bmpnTaskList;
	  }
	public List<TaskDTO> getActiveTaskList(String companyCode, String userId, String filterType, String filterValue, String bpmnid) throws ApplicationException
	{
		long t1 = System.currentTimeMillis();
		//System.out.println("#BpmnTaskDAO#getBPMNTasksByTXREFNO#");
		List<BPMNTask> bmpnTaskList=null;
		List<TaskDTO> listDTO = new ArrayList<TaskDTO>();
		try
		{
			String query="select t from BPMNTask t where (statusCode=1 or statusCode=3) and elementType = :elementType and companyCode=:companyCode ";
			String query3= " and (assignedUser=:assignedUser or assignedRole IN (select roleId from Membership where status='A' and userId=:userId and companyCode=:companyCode)) order by bpmnTaskId desc";
			String query2=null;
			
			if((filterType!=null && filterType.equalsIgnoreCase("ALL")) && (bpmnid!=null && bpmnid.equalsIgnoreCase("ALL")))//filterValue==null || filterValue.length()==0)
			{
				query=query+query3;
			}
			else
			{
				if(!bpmnid.equalsIgnoreCase("ALL"))
				{
					query2="and bpmnId='"+bpmnid+"'";
				}
				
				if(filterType.equalsIgnoreCase("TASKSUBJECT"))
				{
					query2="and taskSubject like '%"+filterValue+"%'";
				}
				else if(bpmnid.equalsIgnoreCase("ALL") && filterType.equalsIgnoreCase("PROCESSNAME"))
				{
					query2="and bpmnId='"+filterValue+"'";
				}
				else if(filterType.equalsIgnoreCase("STEPNAME"))
				{
					query2="and elementId='"+filterValue+"'";
				}
				else if(filterType.equalsIgnoreCase("INITDATE"))
				{
					query2="and taskCreatedDate='"+filterValue+"'";
				}
				else if(filterType.equalsIgnoreCase("BPMNTXREFNO"))
				{
					query2="and bpmnTxRefNo='"+filterValue+"'";
				}
				if(query2!=null && query2.length()>0)
				{
					query=query+query2+query3;
				}
				else
				{
					query=query+query3;
				}
			}
			Query objQuery=objJProvider.createQuery(query);
			
			
			objQuery.setParameter("elementType", "userTask");
			objQuery.setParameter("assignedUser",userId);
			objQuery.setParameter("userId",userId);
			objQuery.setParameter("companyCode",companyCode);
			
			bmpnTaskList=objQuery.getResultList();
			Query processQuery=objJProvider.createNativeQuery("select bpmnid, processname from {h-schema}zf_cfg_bpmnprocess");
			Map<String, String> processMap = new HashMap<String, String>();
			List<Object[]> processList = processQuery.getResultList();
			for(int i=0;i<processList.size();i++) {
				processMap.put((String)processList.get(i)[0], (String)processList.get(i)[1]);
			}
	        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		if (bmpnTaskList != null && bmpnTaskList.size()>0) {
			for(BPMNTask task:bmpnTaskList) {
				String createdDate = "";
				if(task.getTaskCreatedDate()!=null) {
					createdDate = simpleDateFormat.format(task.getTaskCreatedDate());
				}
				String dueDate = "";
				if(task.getDueDate()!=null) {
					dueDate = simpleDateFormat.format(task.getDueDate());
				}
				
				TaskDTO dto = new TaskDTO(task.getBpmnTaskId(),task.getBpmnTxRefNo(),task.getElementId(),(String) processMap.get(task.getBpmnId()),task.getTaskSubject(),createdDate,dueDate,task.getLockedUser(),task.getBpmnId(),task.getCompanyCode());
				dto.setStepLabel(task.getStepLabel());
				listDTO.add(dto);
			}
		}
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			//log.printErrorMessage(ex);
			throw new ApplicationException(ex);
		}
		finally
		{
			//System.out.println("#BpmnTaskDAO#getBPMNTasksByTXREFNO#End#TT#" + (System.currentTimeMillis() - t1));
		}
		return listDTO;
	  }
	
	
	public List<TaskDTO> getTaskHistory(String bpmntxrefno)throws ApplicationException
	{
		long t1 = System.currentTimeMillis();
		//System.out.println("#BpmnTaskDAO#getBPMNTasksByTXREFNO#");
		List<BPMNTask> bmpnTaskList=null;
		List<TaskDTO> listDTO = new ArrayList<TaskDTO>();
		try
		{
			//Query objQuery=objJProvider.createQuery("select t from BPMNTask t where bpmntxrefno = :bpmntxrefno and elementtype !='Flow' order by bpmnTaskId desc");
//			Query objQuery=objJProvider.createQuery("select t from BPMNTask t where bpmntxrefno = :bpmntxrefno AND (elementType = 'userTask' OR elementType = 'startEvent') order by bpmnTaskId desc");
			
			Query objQuery=objJProvider.createQuery("select t from BPMNTask t where bpmntxrefno = :bpmntxrefno order by bpmnTaskId desc");
			
			objQuery.setParameter("bpmntxrefno", bpmntxrefno);
			
			
			bmpnTaskList=objQuery.getResultList();
	        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy");
			if (bmpnTaskList != null && bmpnTaskList.size()>0) {
				for(BPMNTask task:bmpnTaskList) {
					String createdDate = (task.getTaskCreatedDate()!=null)?simpleDateFormat.format(task.getTaskCreatedDate()):"";
					String dueDate = (task.getDueDate()!=null)?simpleDateFormat.format(task.getDueDate()):"";
					TaskDTO dto = new TaskDTO(task.getBpmnTaskId(),task.getBpmnTxRefNo(),task.getElementId(),task.getBpmnId(),task.getTaskSubject(),createdDate,dueDate,task.getLockedUser(),task.getBpmnId(),task.getCompanyCode());
					dto.setStatusCode(task.getStatusCode());
					dto.setStepLabel(task.getStepLabel());
					listDTO.add(dto);
				}
			}
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			//log.printErrorMessage(ex);
			throw new ApplicationException(ex);
		}
		finally
		{
			//System.out.println("#BpmnTaskDAO#getBPMNTasksByTXREFNO#End#TT#" + (System.currentTimeMillis() - t1));
		}
		return listDTO;
	  }
	
	public List<BPMNComments> getComments(String bpmntxrefno)throws ApplicationException
	{
		long t1 = System.currentTimeMillis();
		//System.out.println("#BpmnTaskDAO#getBPMNTasksByTXREFNO#");
		List<BPMNComments> commentsLst=null;
		try
		{
			Query objQuery=objJProvider.createQuery("select t from BPMNComments t where bpmntxrefno = :bpmntxrefno order by bpmnTaskId,commentseq asc");
			objQuery.setParameter("bpmntxrefno", bpmntxrefno);
			commentsLst=objQuery.getResultList();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw new ApplicationException(ex);
		}
		finally
		{
			//System.out.println("#BpmnTaskDAO#getBPMNTasksByTXREFNO#End#TT#" + (System.currentTimeMillis() - t1));
		}
		return commentsLst;
	  }
	
	
	public void createBPMNProcess (BPMNProcess objBPMNProcess) throws ApplicationException 
	{
		long t1 = System.currentTimeMillis();
		try {
			BPMNProcess objNewBPMNProcess = (BPMNProcess) objJProvider.merge((BPMNProcess) objBPMNProcess);

		} catch (Exception e) {
			e.printStackTrace();
			//log.printErrorMessage(e);
			throw new ApplicationException(e.getMessage());
		}
		finally
		{
			//System.out.println("#BpmnTaskDAO#createBPMNTask#End#TT#"+ (System.currentTimeMillis() - t1));
		}
	}
	
	public BPMNProcess findBPMNProcess(String  companyCode,String  bpmnId) throws ApplicationException 
	{
		long t1 = System.currentTimeMillis();
		BPMNProcess objBPMNProcess = new BPMNProcess();

		try {
			//System.out.println("#BpmnTaskDAO#findBPMNProcess#bpmnId#"+ bpmnId+"#companyCode#"+companyCode);
			if (bpmnId !=null) {
//			objBPMNProcess = (BPMNProcess) objJProvider.find(BPMNProcess.class, bpmnId);
				Query objQuery=objJProvider.createQuery("select ob from BPMNProcess ob where bpmnId=:bpmnId and companycode=:companyCode");
				objQuery.setParameter("bpmnId", bpmnId);
				objQuery.setParameter("companyCode", companyCode);
				List<BPMNProcess> processList=objQuery.getResultList();
				if(processList!=null && processList.size()>0)
				{
					objBPMNProcess=processList.get(0);
				}
				else
				{
					throw new ApplicationException(bpmnId+"#"+companyCode+"#unable to find process");
				}
			} else {
				throw new ApplicationException(bpmnId+"#"+companyCode+"#is invalid");
			}
		} catch (Exception e) {
			e.printStackTrace();
			//log.printErrorMessage(e);
			throw new ApplicationException(new Exception(e));
		}
		finally
		{
			//System.out.println("#BpmnTaskDAO#findBPMNProcess#End#TT#"+ (System.currentTimeMillis() - t1));
		}
		return objBPMNProcess;
	}
	
	public  String getSeqVal(String seqName,int size,int value)throws ApplicationException
	{
		//System.out.println("#BpmnTaskDAO#getSeqVal#"+seqName+"#"+size+"#"+value);
 		String seqVal = null;
		long t1 = System.currentTimeMillis();
		try{
			//System.out.println("am in try");
			if(seqName == null || size<=0){
				throw new ApplicationException("SeqName may be null or size < then 0 in GenTxnRefnoSeqVal()");
			}
			seqVal = (String)objJProvider.createNativeQuery("SELECT LPAD(CAST(nextval('{h-schema}"+seqName+"') AS VARCHAR),"+size+",'"+value+"')").getSingleResult();
			//System.out.println("seqVal = "+seqVal);
		}catch (Exception e) {
			e.printStackTrace();
			throw new ApplicationException(e);
		}	
		//System.out.println("#BpmnTaskDAO#getSeqVal#E#TT#"+(System.currentTimeMillis()-t1));
		//System.out.println("#BpmnTaskDAO#getSeqVal#seqval#"+seqVal);
		return seqVal;
	}
	
	public  String getProcessList(String seqName,int size,int value)throws ApplicationException
	{
		//System.out.println("#BpmnTaskDAO#getSeqVal#"+seqName+"#"+size+"#"+value);
 		String seqVal = null;
		long t1 = System.currentTimeMillis();
		try{
			//System.out.println("am in try");
			if(seqName == null || size<=0){
				throw new ApplicationException("SeqName may be null or size < then 0 in GenTxnRefnoSeqVal()");
			}
			seqVal = (String)objJProvider.createNativeQuery("SELECT LPAD(CAST(nextval('{h-schema}"+seqName+"') AS VARCHAR),"+size+",'"+value+"')").getSingleResult();
			//System.out.println("seqVal = "+seqVal);
		}catch (Exception e) {
			e.printStackTrace();
			throw new ApplicationException(e);
		}	
		//System.out.println("#BpmnTaskDAO#getSeqVal#E#TT#"+(System.currentTimeMillis()-t1));
		//System.out.println("#BpmnTaskDAO#getSeqVal#seqval#"+seqVal);
		return seqVal;
	}
	
	public JSONObject findStepDetails(String bpmnId, String companycode) throws ApplicationException 
	{
		long t1 = System.currentTimeMillis();
		List<Object[]> obj=null;
		JSONArray steps = null;
		JSONObject json =new JSONObject();
		try {
			//System.out.println("#BpmnTaskDAO#findStepDetails#TaskId#" + bpmnId);
			if (bpmnId !=null) {
			 obj=  objJProvider.createNativeQuery("select processconfig ->> 'steps' as Steps, processconfig ->> 'choices' as choices, processconfig ->> 'jsonschema' as jsonschema,  processconfig ->> 'uischema' as uischema, processconfig ->> 'bpmn' as bpmn,processconfig ->> 'displayrule' as displayrule, companyCode from {h-schema}zf_cfg_bpmnprocess where bpmnid = :bpmnid and companyCode=:companycode").setParameter("bpmnid", bpmnId).setParameter("companycode", companycode).getResultList();
			//System.out.println("obj "+obj.get(0)[0]);
			steps = new  JSONArray(String.valueOf(obj.get(0)[0]));
			//steps = obj.get(0).toJSONArray(steps);
			JSONObject step = null;
			for (Iterator<Object> i = steps.iterator(); i.hasNext(); ){
				   step = (JSONObject)i.next();
				   if("FLEXI".equalsIgnoreCase(((JSONObject)step.get("stepDefinition")).getString("BPMNSTEPTYPE"))) {
					   break;
				   }
			}
			
			json.put("step", step);
			json.put("choices", new JSONArray(String.valueOf(obj.get(0)[1])));
			json.put("jsonschema", new JSONObject(String.valueOf(obj.get(0)[2])));
			json.put("uischema", new JSONObject(String.valueOf(obj.get(0)[3])));
			json.put("bpmn", new JSONObject(String.valueOf(obj.get(0)[4])));
			json.put("displayrule",  new JSONArray(String.valueOf(obj.get(0)[5])));
			json.put("companyCode", String.valueOf(obj.get(0)[6]));
			JSONObject processData = new JSONObject();
			json.put("processdata", processData);

			//System.out.println("json "+json.toString());
			}	
		} catch (Exception e) {
			e.printStackTrace();
			//log.printErrorMessage(e);
			throw new ApplicationException(new Exception(e));
		}
		finally
		{
			//System.out.println("#BpmnTaskDAO#findStepDetails#End#TT#"+ (System.currentTimeMillis() - t1));
		}
		return json;
	}
	
	public String findBPMNID(String processName, String companycode) throws ApplicationException 
	{
		long t1 = System.currentTimeMillis();
		List<Object[]> obj=null;
		String bpmnid = null;
		try {
			//System.out.println("#BpmnTaskDAO#findBPMNID#processName#" + processName);
			if (processName !=null) {
			 obj=  objJProvider.createNativeQuery("select bpmnid from {h-schema}zf_cfg_bpmnprocess where processname = :processname and companyCode=:companycode").setParameter("processname", processName).setParameter("companycode", companycode).getResultList();
				if(obj.size() >  0) {
					bpmnid = String.valueOf(obj.get(0));
				}
			}	
		} catch (Exception e) {
			e.printStackTrace();
			//log.printErrorMessage(e);
			throw new ApplicationException(new Exception(e));
		}
		finally
		{
			//System.out.println("#BpmnTaskDAO#findBPMNID#processName#End#TT#"+ (System.currentTimeMillis() - t1));
		}
		return bpmnid;
	}
	
	public JSONObject findWidgetDetails(String widgetid, String companycode) throws ApplicationException 
	{
		long t1 = System.currentTimeMillis();
		List<Object[]> obj=null;
		JSONArray steps = null;
		JSONObject json =new JSONObject();
		try {
			//System.out.println("#BpmnTaskDAO#findWidgetDetails#TaskId#" + widgetid);
			if (widgetid !=null) {
			 obj=  objJProvider.createNativeQuery("select processconfig ->> 'choices' as choices, processconfig ->> 'jsonschema' as jsonschema,  processconfig ->> 'uischema' as uischema, processconfig ->> 'bpmn' as bpmn, processconfig ->> 'displayrule' as displayrule, companyCode, processname from {h-schema}zf_cfg_bpmnprocess where bpmnid = :bpmnid and companyCode=:companycode").setParameter("bpmnid", widgetid).setParameter("companycode", companycode).getResultList();
		
			//steps = obj.get(0).toJSONArray(steps);
			JSONObject step = new JSONObject();
			JSONObject stepDefinition = new JSONObject();
			stepDefinition.put("showComments", false);
			stepDefinition.put("showAttachment", false);
			stepDefinition.put("showHistory", false);
			stepDefinition.put("ISENQUIRY", false);
			stepDefinition.put("NAME", "WIDGET");
			stepDefinition.put("LABEL", String.valueOf(obj.get(0)[6]));
			step.put("stepDefinition", stepDefinition);
			json.put("step", step);
			json.put("choices", new JSONArray(String.valueOf(obj.get(0)[0])));
			

			JSONObject jsonschemaobj = new JSONObject(String.valueOf(obj.get(0)[1]));
			Iterator<String> keys = jsonschemaobj.keys();

			while(keys.hasNext()) {
			    String key = keys.next();
			    JSONObject fieldJSON= jsonschemaobj.getJSONObject(key);
			    if(fieldJSON.has("VISIBILITY")) {
			    	fieldJSON.getJSONObject("VISIBILITY").put("WIDGET","ReadOnly");
			    }else {
			    	JSONObject visibility = new JSONObject();
			    	visibility.put("WIDGET","ReadOnly");
			    	fieldJSON.put("VISIBILITY", visibility);
			    }
				
	
			}
			json.put("jsonschema", jsonschemaobj);
			json.put("uischema", new JSONObject(String.valueOf(obj.get(0)[2])));
			json.put("bpmn", new JSONObject(String.valueOf(obj.get(0)[3])));
			json.put("displayrule",  new JSONArray(String.valueOf(obj.get(0)[4])));
			json.put("companyCode", String.valueOf(obj.get(0)[5]));
			//json.put("displayrule",  new JSONArray());
			JSONObject processData = new JSONObject();
			json.put("processdata", processData);

			//System.out.println("json "+json.toString());
			}	
		} catch (Exception e) {
			e.printStackTrace();
			//log.printErrorMessage(e);
			throw new ApplicationException(new Exception(e));
		}
		finally
		{
			//System.out.println("#BpmnTaskDAO#findWidgetDetails#End#TT#"+ (System.currentTimeMillis() - t1));
		}
		return json;
	}
	
	public TaskDTO findEnqTaskDetails(TaskDTO dto) throws ApplicationException{

		long t1 = System.currentTimeMillis();
		List<Object[]> obj=null;
		JSONArray steps = null;
		JSONObject json =new JSONObject();
		JSONObject processData = null;
		try {
			//System.out.println("#BpmnTaskDAO#findEnqTaskDetails#refNo#" + dto);
			if (dto !=null) {
				BPMNProcessInfo objBPMNProcessInfo = findBPMNProcessInfo(dto.getRefId());
				if(objBPMNProcessInfo.getProcessdata() == null || objBPMNProcessInfo.getProcessdata()=="") {
					processData = new JSONObject();
				}else {
					processData = new JSONObject(objBPMNProcessInfo.getProcessdata());
				}
				
				
			obj=  objJProvider.createNativeQuery("select processconfig ->> 'steps' as Steps, processconfig ->> 'choices' as choices, processconfig ->> 'jsonschema' as jsonschema,  processconfig ->> 'uischema' as uischema, processconfig ->> 'bpmn' as bpmn from {h-schema}zf_cfg_bpmnprocess where bpmnid = :bpmnid").setParameter("bpmnid", objBPMNProcessInfo.getBpmnId()).getResultList();
			//System.out.println("obj "+obj.get(0)[0]);
			steps = new  JSONArray(String.valueOf(obj.get(0)[0]));
			JSONObject step = null;
			
			for (Iterator<Object> i = steps.iterator(); i.hasNext(); ){
				   step = (JSONObject)i.next();
				  if(((JSONObject)step.get("stepDefinition")).has("ISENQUIRY") &&
				      (((JSONObject)step.get("stepDefinition")).getBoolean("ISENQUIRY"))) {
					   break;
				   }
			}
			
			
			
			JSONObject stepObj =step;
			stepObj.getJSONObject("stepDefinition").put("NAME", "ENQUIRY");
			stepObj.getJSONObject("stepDefinition").put("LABEL", "ENQUIRY");
			
			
			json.put("step", stepObj);
			
			JSONObject jsonschemaobj = new JSONObject(String.valueOf(obj.get(0)[2]));
			Iterator<String> keys = jsonschemaobj.keys();

			while(keys.hasNext()) {
			    String key = keys.next();
			    JSONObject fieldJSON= jsonschemaobj.getJSONObject(key);
			    if(fieldJSON.has("VISIBILITY")) {
			    	fieldJSON.getJSONObject("VISIBILITY").put("ENQUIRY","ReadOnly");
			    }else {
			    	JSONObject visibility = new JSONObject();
			    	visibility.put("ENQUIRY","ReadOnly");
			    	fieldJSON.put("VISIBILITY", visibility);
			    }
				
	
			}
			json.put("choices", new JSONArray(String.valueOf(obj.get(0)[1])));
			json.put("jsonschema", jsonschemaobj);
			json.put("uischema", new JSONObject(String.valueOf(obj.get(0)[3])));
			json.put("bpmn", new JSONObject(String.valueOf(obj.get(0)[4])));
			
			
			json.put("processdata", processData);

			//System.out.println("json "+json.toString());
			dto.setCompanyCode(objBPMNProcessInfo.getCompamnyCode());
			dto.setBpmnId(objBPMNProcessInfo.getBpmnId());
			dto.setFormData(json.toString());
			}	
		} catch (Exception e) {
			e.printStackTrace();
			//log.printErrorMessage(e);
			throw new ApplicationException(new Exception(e));
		}
		finally
		{
			//System.out.println("#BpmnTaskDAO#findEnqTaskDetails#End#TT#"+ (System.currentTimeMillis() - t1));
		}
		return dto;
	
	}
	
	public JSONObject findTaskDetails(TaskDTO task) throws ApplicationException 
	{
		String bpmnId = task.getBpmnId();
		String companycode = task.getCompanyCode();
		long t1 = System.currentTimeMillis();
		List<Object[]> obj=null;
		JSONArray steps = null;
		JSONObject json =new JSONObject();
		try {
			//System.out.println("#BpmnTaskDAO#findTaskDetails#TaskId#" + bpmnId);
			if (bpmnId !=null) {
			 obj=  objJProvider.createNativeQuery("select processconfig ->> 'steps' as Steps, processconfig ->> 'choices' as choices, processconfig ->> 'jsonschema' as jsonschema,  processconfig ->> 'uischema' as uischema, processconfig ->> 'bpmn' as bpmn, processconfig ->> 'displayrule' as displayrule from {h-schema}zf_cfg_bpmnprocess where bpmnid = :bpmnid and companyCode=:companycode").setParameter("bpmnid", bpmnId).setParameter("companycode", companycode).getResultList();
			//System.out.println("obj "+obj.get(0)[0]);
			steps = new  JSONArray(String.valueOf(obj.get(0)[0]));
			//steps = obj.get(0).toJSONArray(steps);
			JSONObject step = null;
			String stepName = task.getTaskName();
			for (Iterator<Object> i = steps.iterator(); i.hasNext(); ){
				   step = (JSONObject)i.next();
				  if(stepName.equals(((JSONObject)step.get("stepDefinition")).getString("NAME"))) {
					   break;
				   }
			}
			
			
			json.put("step", step);
			json.put("choices", new JSONArray(String.valueOf(obj.get(0)[1])));
			json.put("jsonschema", new JSONObject(String.valueOf(obj.get(0)[2])));
			json.put("uischema", new JSONObject(String.valueOf(obj.get(0)[3])));
			json.put("bpmn", new JSONObject(String.valueOf(obj.get(0)[4])));
			json.put("displayrule", new JSONArray(String.valueOf(obj.get(0)[5])));
			JSONObject processData = null;
			BPMNProcessInfo objBPMNProcessInfo = findBPMNProcessInfo(task.getRefId());
			if(objBPMNProcessInfo.getProcessdata() == null || objBPMNProcessInfo.getProcessdata()=="") {
				processData = new JSONObject();
			}else {
				processData = new JSONObject(objBPMNProcessInfo.getProcessdata());
			}
			
			json.put("processdata", processData);
			
			//System.out.println("json "+json.toString());
			}	
		} catch (Exception e) {
			e.printStackTrace();
			//log.printErrorMessage(e);
			throw new ApplicationException(new Exception(e));
		}
		finally
		{
			//System.out.println("#BpmnTaskDAO#findTaskDetails#End#TT#"+ (System.currentTimeMillis() - t1));
		}
		return json;
	}
	
	public TXNDocments createDocument(String bpmnTxRefNo,String stepName,String documentName,byte[] docData,String userId,
			String companyCode,String documentType,String countryCode) throws ApplicationException
	{
		TXNDocments objTxnDocments=null;
		try
		{
			objTxnDocments=new TXNDocments();
			objTxnDocments.setBpmnTxRefNo(bpmnTxRefNo);
			objTxnDocments.setStepName(stepName);
			objTxnDocments.setDocumentName(documentName);
			objTxnDocments.setDocument(docData);
			objTxnDocments.setCompanyCode(companyCode);
			objTxnDocments.setUserId(userId);
			objTxnDocments.setDocumentType(documentType);
			objTxnDocments.setCreatedTime(DateUtility.getCountryTimeStamp(getStrJPUnitName(), countryCode));
			objTxnDocments=(TXNDocments) objJProvider.merge(objTxnDocments);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw new ApplicationException(ex);
		}
		return objTxnDocments;
	}
	
	public TXNDocments getDocument(long documentId)throws Exception
	{
		TXNDocments objTxnDocments=null;
		try
		{
			
			objTxnDocments=(TXNDocments) objJProvider.find(TXNDocments.class,documentId);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw new Exception(ex);
		}
		return objTxnDocments;
	}
	
	public int deleteDocument(String companyCode,long documentId)throws Exception
	{
		int delCount=0;
		try
		{
			String deleteQuery="DELETE FROM {h-schema}zf_txn_userdocs where companyCode=?2 and documentId=?1";
			Query objQuery=objJProvider.createNativeQuery(deleteQuery);
			objQuery.setParameter(1, documentId);
			objQuery.setParameter(2, companyCode);
			delCount=objQuery.executeUpdate();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw new Exception(ex);
		}
		return delCount;
	}
	
	public List<TXNDocments> getDocumentList(String companyCode,String bpmnTxRefNo) throws Exception
	{
		List<TXNDocments> docList=null;
		try
		{
			//String deleteQuery="SELECT ob FROM TXNDocments ob where companyCode=?2 and bpmnTxRefNo=?1";
			String deleteQuery="SELECT ob FROM TXNDocments ob where bpmnTxRefNo=?1";
			Query objQuery=objJProvider.createQuery(deleteQuery);
			objQuery.setParameter(1, bpmnTxRefNo);
			//objQuery.setParameter(2, companyCode);
			docList=objQuery.getResultList();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw new Exception(ex);
		}
		return docList;
	}
	
	public int updateDocuments(String bpmnTxnRefNo,List<String> docIds) throws Exception
	{
		int updateCount=0;
		try
		{
			String updateQuery="UPDATE {h-schema}zf_txn_userdocs SET bpmntxrefno=?1 WHERE  documentid in(?2)";
			Query objQuery=objJProvider.createNativeQuery(updateQuery);
			objQuery.setParameter(1, bpmnTxnRefNo);
			objQuery.setParameter(2, docIds);
			updateCount=objQuery.executeUpdate();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw new Exception(ex);
		}
		return updateCount;
	}
    
	public JSONObject getDSProcessList(String companyCode, String processtype) throws ApplicationException {

		long t1 = System.currentTimeMillis();
		List<Object[]> obj=null;
		JSONObject json =new JSONObject();
		try {
			 obj=  objJProvider.createNativeQuery("select PROCESSID, COUNTRYCODE, PROCESSNAME as NAME, BPMNID, '3456' as TAT, 'test' as DESCRIPTION, category  from {h-schema}zf_cfg_bpmnworkflowdtl where companycode=:companyCode and processtype=:processtype").setParameter("companyCode", companyCode).setParameter("processtype", processtype).getResultList();
			//System.out.println("obj : "+obj + " | companyCode : " + companyCode + " | processtype : " + processtype);
			JSONArray array = new JSONArray();
			for(int i=0 ; i < obj.size() ; i++) {
				JSONObject node = new JSONObject();
				
					node.put("processid", obj.get(i)[0]);
					node.put("countrycode", obj.get(i)[1]);
					node.put("name", obj.get(i)[2]);
					node.put("bpmnid", obj.get(i)[3]);
					node.put("tat", obj.get(i)[4]);
					node.put("description", obj.get(i)[5]);
					node.put("category", obj.get(i)[6]);
				array.put(node);
			}
			
			json.put("processList", array);
			
			//System.out.println("json "+json.toString());
			}	
		catch (Exception e) {
			e.printStackTrace();
			//log.printErrorMessage(e);
			throw new ApplicationException(new Exception(e));
		}
		finally
		{
			//System.out.println("#BpmnTaskDAO#getDSProcessList#End#TT#"+ (System.currentTimeMillis() - t1));
		}
		return json;
	
	 }
	
	public String checkProcessId(String companyCode, String processid) throws ApplicationException {

		long t1 = System.currentTimeMillis();
		List<Object[]> obj=null;
		String response="Available";
		try {
			if(processid!=null && processid.toUpperCase().startsWith("FP")) {
				response="Not Available";
			}
			obj=  objJProvider.createNativeQuery("select PROCESSNAME as NAME from {h-schema}zf_cfg_bpmnworkflowdtl where companycode=:companyCode and processid=:processid").setParameter("companyCode", companyCode).setParameter("processid", processid).getResultList();
			//System.out.println("obj "+obj);
			if(obj!= null && obj.size()>0) {
				response="Not Available";
			}
			}	
		catch (Exception e) {
			e.printStackTrace();
			//log.printErrorMessage(e);
			throw new ApplicationException(new Exception(e));
		}
		finally
		{
			//System.out.println("#BpmnTaskDAO#checkProcessId#End#TT#"+ (System.currentTimeMillis() - t1));
		}
		return response;
	
	 }
	
	
	public JSONObject getDSProcess(String companyCode, String processId) throws ApplicationException {

		long t1 = System.currentTimeMillis();
		List<Object[]> obj=null;
		JSONObject json =new JSONObject();
		try {
			 obj =  objJProvider.createNativeQuery("select processid,countrycode,bpmnid,CAST(json as TEXT) from {h-schema}zf_cfg_bpmnworkflowdtl where companyCode = :companyCode and processId= :processId").setParameter("companyCode", companyCode).setParameter("processId", processId).getResultList();
			
			 
			 //System.out.println("obj : "+obj + " | companyCode : " + companyCode + " | processId : " + processId);
				
			JSONObject node = new JSONObject();
			for(int i=0 ; i < obj.size() ; i++) {
					node.put("processid", obj.get(i)[0]);
					node.put("countrycode", obj.get(i)[1]);
					node.put("bpmnid", obj.get(i)[2]);
					//JSONObject processnode = new JSONObject();
					//processnode.put("process", new JSONObject(String.valueOf(obj.get(i)[3])));
					node.put("json", obj.get(i)[3]);
			}
			
			json.put("process", node);
			
			//System.out.println("json "+json.toString());
			}	
		catch (Exception e) {
			e.printStackTrace();
			//log.printErrorMessage(e);
			throw new ApplicationException(new Exception(e));
		}
		finally
		{
			//System.out.println("#BpmnTaskDAO#getDSProcess#End#TT#"+ (System.currentTimeMillis() - t1));
		}
		return json;
	
	}
	
	
	public String createDSProcess(String companyCode, JSONObject processjson, String processtype) throws ApplicationException {

		long t1 = System.currentTimeMillis();
		int count=0;
	    String message="Process created successfully";
	    JSONObject process = (JSONObject) processjson.get("process");
	    
//	    String defaultBPMN = "<?xml version='1.0' encoding='UTF-8'?> <bpmn:definitions xmlns:bpmn='http://www.omg.org/spec/BPMN/20100524/MODEL' xmlns:bpmndi='http://www.omg.org/spec/BPMN/20100524/DI' xmlns:dc='http://www.omg.org/spec/DD/20100524/DC' xmlns:camunda='http://camunda.org/schema/1.0/bpmn' id='Definitions_1bweu69' targetNamespace='http://bpmn.io/schema/bpmn' exporter='Camunda Modeler' exporterVersion='3.0.0-dev'> <bpmn:process id='Process_1or6mfn' isExecutable='true'> <bpmn:startEvent id='StartEvent_1' /> </bpmn:process> <bpmndi:BPMNDiagram id='BPMNDiagram_1'> <bpmndi:BPMNPlane id='BPMNPlane_1' bpmnElement='Process_1or6mfn'> <bpmndi:BPMNShape id='_BPMNShape_StartEvent_2' bpmnElement='StartEvent_1'> <dc:Bounds x='179' y='159' width='36' height='36' /> </bpmndi:BPMNShape> </bpmndi:BPMNPlane> </bpmndi:BPMNDiagram> </bpmn:definitions>";
//	    JSONObject bpmn = new JSONObject();
//	    bpmn.put("BPMNID", (String)process.get("BPMNID"));
//	    bpmn.put("BPMNFILECONTENT", defaultBPMN);
//	    bpmn.put("RELOAD", "N");
//	    processjson.put("bpmn", bpmn);
	    
		try {
			objJProvider.begin();
			String processid = processtype.substring(0, 1) + "0001";
			
			List<Object> obj1 = objJProvider.createNativeQuery("select id from {h-schema}zf_cfg_available_ids where companycode = :companycode and idtype = :processtype order by id asc LIMIT 1").setParameter("companycode", companyCode).setParameter("processtype", processtype).getResultList();
			if(obj1.size()>0) {
				String tempProcessId = String.valueOf(obj1.get(0));
				if(tempProcessId.length()==5) {
					//String intprocess = tempProcessId.substring(1);
					processid = tempProcessId;
				}
				objJProvider.createNativeQuery("delete from {h-schema}zf_cfg_available_ids where companycode=:companyCode and idtype=:processtype  and id=:id ").setParameter("companyCode", companyCode).setParameter("processtype", processtype).setParameter("id", tempProcessId).executeUpdate();
				
			}else{
			List<Object> obj = objJProvider.createNativeQuery("select processid from {h-schema}zf_cfg_bpmnworkflowdtl where companycode = :companycode and processtype = :processtype order by processid desc LIMIT 1").setParameter("companycode", companyCode).setParameter("processtype", processtype).getResultList();
			if(obj.size()>0) {
				String tempProcessId = String.valueOf(obj.get(0));
				//System.out.println("#BpmnTaskDAO#createDSProcess#tempProcessId#"+ tempProcessId);
				if(tempProcessId.length()==5) {
					String intprocess = tempProcessId.substring(1);
					processid = processtype.substring(0, 1) + String.format("%04d", Integer.valueOf(intprocess)+1);
					//System.out.println("#BpmnTaskDAO#createDSProcess#processid#"+ tempProcessId);
				}
			}
			}
			//updating auto generated process id
			process.put("PROCESSID", processid);
			process.put("BPMNID", processid+"_V1");
			JSONObject bpmn = (JSONObject) processjson.get("bpmn");
			bpmn.put("BPMNID", processid+"_V1");
			processjson.put("process", process);
			processjson.put("bpmn", bpmn);
			
			count =  objJProvider.createNativeQuery("insert into {h-schema}zf_cfg_bpmnworkflowdtl (companycode, processid, processname, bpmnid, countrycode, json, branchname, processtype) values (:1,:2,:3,:4,:5,:6,:7,:8)")
					 .setParameter("1", companyCode)
					 //.setParameter("2", (String)process.get("PROCESSID"))
					 .setParameter("2", processid)
					 .setParameter("3", (String)process.get("NAME"))
					 .setParameter("4", (String)process.get("BPMNID"))
					 .setParameter("5", "IN")
					 .setParameter("6", processjson.toString())
					 .setParameter("7", "MASTER")
					 .setParameter("8", processtype)
					 .executeUpdate();
			objJProvider.commit();
			if(count == 0) {
				message="Unable to create process";
			}
			}	
		catch (Exception e) {
			e.printStackTrace();
			//log.printErrorMessage(e);
			//throw new ApplicationException(new Exception(e));
			message="Process id already taken";
		}
		finally
		{
			//System.out.println("#BpmnTaskDAO#createDSProcess#End#TT#"+ (System.currentTimeMillis() - t1));
		}
		return message;
	
	}
	
	
	public String deleteDSProcess(String companyCode, String processId) throws ApplicationException {

		long t1 = System.currentTimeMillis();
		int count=0;
	    String message="Process deleted successfully";
	
	    
		try {
			objJProvider.begin();
			if(processId.length()==5) {
				List<Object> obj =  objJProvider.createNativeQuery("select processtype from {h-schema}zf_cfg_bpmnworkflowdtl where companycode=:companyCode and processid=:processId").setParameter("companyCode", companyCode).setParameter("processId", processId).getResultList();
				if(obj.size()>0) {
					String idtype = String.valueOf(obj.get(0));
					objJProvider.createNativeQuery("insert into {h-schema}zf_cfg_available_ids (companycode, idtype, id) values (:companyCode, :idtype, :id)").setParameter("companyCode", companyCode).setParameter("idtype", idtype).setParameter("id", processId).executeUpdate();
				}
			}
			count = objJProvider.createNativeQuery("delete from {h-schema}zf_cfg_bpmnworkflowdtl where companycode=:companyCode and processid=:processId").setParameter("companyCode", companyCode).setParameter("processId", processId).executeUpdate();
			objJProvider.createNativeQuery("delete from {h-schema}zf_cfg_bpmnprocess where companycode=:companyCode and processid=:processId").setParameter("companyCode", companyCode).setParameter("processId", processId).executeUpdate();
			objJProvider.createNativeQuery("delete from {h-schema}zf_cfg_bpmnnotification where companycode=:companyCode and processid=:processId").setParameter("companyCode", companyCode).setParameter("processId", processId).executeUpdate();
			objJProvider.createNativeQuery("delete from {h-schema}zf_txn_userdocs where companycode = :companyCode and bpmntxrefno in (select bpmntxrefno from  {h-schema}zf_txn_bpmnprocessinfo where companycode = :companyCode and processid= :processId)").setParameter("companyCode", companyCode).setParameter("processId", processId).executeUpdate();
			objJProvider.createNativeQuery("delete from {h-schema}zf_txn_comments where companycode = :companyCode and bpmntxrefno in (select bpmntxrefno from  {h-schema}zf_txn_bpmnprocessinfo where companycode = :companyCode and processid= :processId)").setParameter("companyCode", companyCode).setParameter("processId", processId).executeUpdate();
			objJProvider.createNativeQuery("delete from {h-schema}zf_txn_bpmntask where companycode = :companyCode and bpmntxrefno in (select bpmntxrefno from  {h-schema}zf_txn_bpmnprocessinfo where companycode = :companyCode and processid= :processId)").setParameter("companyCode", companyCode).setParameter("processId", processId).executeUpdate();
			objJProvider.createNativeQuery("delete from {h-schema}zf_txn_bpmnprocessinfo where companycode = :companyCode and processid= :processId").setParameter("companyCode", companyCode).setParameter("processId", processId).executeUpdate();
		
			objJProvider.commit();
			if(count == 0) {
				message="Unable to delete process";
			}
			}	
		catch (Exception e) {
			e.printStackTrace();
			//log.printErrorMessage(e);
			//throw new ApplicationException(new Exception(e));
			message="Process id already deleted";
		}
		finally
		{
			//System.out.println("#BpmnTaskDAO#deleteDSProcess#End#TT#"+ (System.currentTimeMillis() - t1));
		}
		return message;
	
	}
	/**
	 * Save the Process Design
	 * @param companyCode
	 * @param processjson
	 * @return
	 * @throws ApplicationException
	 */
	public String saveDSProcess(String companyCode, String processjson) throws ApplicationException {
		long t1 = System.currentTimeMillis();
		int count=0;
	    String message="Process Saved successfully";
	    JSONObject json = new JSONObject(processjson);
	    JSONObject process = (JSONObject) json.get("process");
	    
	    //System.out.println("companyCode#"+companyCode+"#process"+process.get("PROCESSID"));
	   try {
			objJProvider.begin();
			count =  objJProvider.createNativeQuery("UPDATE {h-schema}zf_cfg_bpmnworkflowdtl set json=:json, processname=:processname where companycode=:companycode and  processid=:processid ")
					 .setParameter("companycode", companyCode)
					 .setParameter("processid", (String)process.get("PROCESSID"))
					 .setParameter("json", processjson)
					 .setParameter("processname", (String)process.get("NAME"))
					 .executeUpdate();
			objJProvider.commit();
			if(count == 0) {
				message="Unable to save process";
			}
			}	
		catch (Exception e) {
			e.printStackTrace();
			throw new ApplicationException("Process save filed due to "+e.getMessage());
		}
		finally
		{
			//System.out.println("#BpmnTaskDAO#saveDSProcess#End#TT#"+ (System.currentTimeMillis() - t1));
		}
		return message;
	
	}
	
	
	public String createDSProcessVersion(String companyCode, String processjson) throws ApplicationException {
		long t1 = System.currentTimeMillis();
		int count=0;
	   
	    JSONObject json = new JSONObject(processjson);
	    JSONObject process = (JSONObject) json.get("process");
	    
	    //System.out.println("companyCode#"+companyCode+"#process"+process.get("PROCESSID"));
	   try {
			objJProvider.begin();
			count =  objJProvider.createNativeQuery("UPDATE {h-schema}zf_cfg_bpmnprocess set isactive='N' where companycode=:companycode and  processid=:processid ")
					 .setParameter("companycode", companyCode)
					 .setParameter("processid", (String)process.get("PROCESSID"))
					 .executeUpdate();
			objJProvider.commit();
			if(count > 0) {
				String bpmnId = json.getJSONObject("bpmn").getString("BPMNID");
				String version = bpmnId.split("_V")[1];
				String processid = process.getString("PROCESSID");
				int versionno = Integer.parseInt(version) + 1;
				if(versionno >999) {
					versionno = 1;
				}
				json.getJSONObject("bpmn").put("BPMNID", processid+"_V"+versionno);
			}
			}	
		catch (Exception e) {
			e.printStackTrace();
			throw new ApplicationException("Process save filed due to "+e.getMessage());
		}
		finally
		{
			//System.out.println("#BpmnTaskDAO#saveDSProcess#End#TT#"+ (System.currentTimeMillis() - t1));
		}
		return json.toString();
	
	}
	
	/**
	 * Remove All comments mapped to taskid
	 * @param refNo
	 * @param taskid
	 * @return
	 * @throws ApplicationException
	 */
	public int removeComments(String refNo, long taskid) throws ApplicationException {
		try{
			//System.out.println("refNo ---> "+refNo+" taskid  ---> "+taskid );
			String query = "delete from BPMNComments where taskId=:taskId and refId=:refId";
			int commentsCnt = objJProvider.createQuery(query).setParameter("taskId", taskid).setParameter("refId", refNo).executeUpdate();
			return commentsCnt;
		}catch (Exception e) {
			e.printStackTrace();
			throw new ApplicationException("Unable to Clean Comments");
		} 
		
	}
	
	
	public BPMNNotification findBPMNNotification(BPMNNotificationPK objBPMNNotificationPK) throws ApplicationException 
	{
		long t1 = System.currentTimeMillis();
		BPMNNotification objBPMNNotification = new BPMNNotification();

		try {
				objBPMNNotification = (BPMNNotification) objJProvider.find(BPMNNotification.class, objBPMNNotificationPK);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new ApplicationException(new Exception(e));
		}
		finally
		{
			//System.out.println("#BpmnTaskDAO#findBPMNNotification#End#TT#"+ (System.currentTimeMillis() - t1));
		}
		return objBPMNNotification;
	}
	
	public int deleteNotification(String companyCode,String processid,String bpmnId) throws ApplicationException
	{
		try{
			//System.out.println("companyCode ---> "+companyCode+" processid  ---> "+processid+" bpmnId  ---> "+bpmnId );
			String query = "delete from BPMNNotification where  companycode=:companycode and bpmnId=:bpmnId";//and processId=:processId
			int notificationCount = objJProvider.createQuery(query).setParameter("companycode", companyCode).setParameter("bpmnId", bpmnId).executeUpdate();//.setParameter("processId", processid)
			return notificationCount;
		}catch (Exception e) {
			e.printStackTrace();
			throw new ApplicationException("Unable to delete Notifications");
		} 
	}
	
	public BPMNNotification createBPMNNotification(BPMNNotification objBPMNNotification) throws ApplicationException 
	{
		long t1 = System.currentTimeMillis();
		BPMNNotification newBPMNNotification = null;
		try {
			newBPMNNotification =(BPMNNotification) objJProvider.merge((BPMNNotification) objBPMNNotification);
		} catch (Exception e) {
			e.printStackTrace();
			//log.printErrorMessage(e);
			throw new ApplicationException(e.getMessage());
		}
		finally
		{
			//System.out.println("#BpmnTaskDAO#createBPMNNotification#End#TT#"+ (System.currentTimeMillis() - t1));
		}
		return newBPMNNotification;
	}
	
	public void callSubscriptions(String companyCode, String processId, String stepName, String processData) throws Exception 
	{
		//long t1 = System.currentTimeMillis();
		//int rows = 0;
		try {
		//System.out.println(" callSubscriptions begin");
		List<Object> obj = objJProvider.createNativeQuery("select hookurl from {h-schema}zf_cfg_subscription where companycode = :companycode and processid = :processid and stepname=:stepname order by processid desc LIMIT 1").setParameter("companycode", companyCode).setParameter("processid", processId.split("_")[0]).setParameter("stepname", stepName).getResultList();
		if(obj.size()>0) {
			for(int i=0;i<obj.size();i++) {
			String hookurl = String.valueOf(obj.get(i));
			//System.out.println(" callSubscriptions url " + hookurl);
			//System.out.println("#BpmnTaskDAO#createDSProcess#tempProcessId#"+ hookurl);
			  HttpClient client = new DefaultHttpClient();
			  HttpPost post = new HttpPost(hookurl);
			  StringEntity input = new StringEntity(processData);
			  input.setContentType("application/json");
			  post.setEntity(input);
			  HttpResponse response = client.execute(post);
			}
		}
		//System.out.println(" callSubscriptions end");
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	
}
