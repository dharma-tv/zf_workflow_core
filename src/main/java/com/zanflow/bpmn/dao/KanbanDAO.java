package com.zanflow.bpmn.dao;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.persistence.OptimisticLockException;
import javax.persistence.Query;

import org.json.JSONArray;
import org.json.JSONObject;

import com.zanflow.bpmn.dto.ProcessDTO;
import com.zanflow.bpmn.dto.TaskDTO;
import com.zanflow.bpmn.exception.ApplicationException;
import com.zanflow.bpmn.exception.JPAPersistenceException;
import com.zanflow.bpmn.model.BPMNProcess;
import com.zanflow.bpmn.model.BPMNProcessInfo;
import com.zanflow.bpmn.model.BPMNTask;
import com.zanflow.bpmn.service.impl.KanbanServiceImpl;
import com.zanflow.common.db.JPATransaction;
import com.zanflow.common.db.JPersistenceProvider;
import com.zanflow.kanban.dto.LanesDTO;



public class KanbanDAO extends JPATransaction
{

	public KanbanDAO(String pJunitName)throws ApplicationException
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


	public KanbanDAO(JPersistenceProvider objJProvider) throws ApplicationException
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
				q=objJProvider.createQuery("update BPMNTask set statusCode=:statusCode,lockeduser=:lockeduser,tasklockedtime=now() where bpmnTaskId=:bpmnTaskId");
			else
				q=objJProvider.createQuery("update BPMNTask set statusCode=:statusCode,lockeduser=null where bpmnTaskId=:bpmnTaskId and lockeduser=:lockeduser");
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
	
	public List<LanesDTO> getActiveCardList(String companyCode, String boardid)throws ApplicationException
	{
		long t1 = System.currentTimeMillis();
		//System.out.println("#KanbanDAO#getActiveCardList#");
		List<BPMNTask> bmpnTaskList=null;
		List<LanesDTO> lanelistDTO = new ArrayList<LanesDTO>();
		try
		{
			String query="select t from BPMNTask t where bpmnid = :boardid and companycode = :companyCode";
			
			Query objQuery=objJProvider.createQuery(query);
			objQuery.setParameter("boardid", boardid);
			objQuery.setParameter("companyCode",companyCode);
			
			bmpnTaskList=objQuery.getResultList();
			
		
	        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	        
	        JSONObject boardconfig = getBoardConfig(companyCode, boardid);
	        
	        JSONArray columns = boardconfig.getJSONObject("boardconfig").getJSONArray("columns");
	        ArrayList<String> columnNames = new ArrayList<String>();
	        for(int i = 0; i < columns.length(); i++)
	        {
	              JSONObject column = columns.getJSONObject(i);
	              LanesDTO lane = new LanesDTO();
	              lane.setId(column.getString("id"));
	              lane.setLabel(column.getString("name"));
	              //lane.setTitle(column.getString("id"));
	              lanelistDTO.add(lane);
	              columnNames.add(column.getString("name"));
	        }
	    HashMap<String, ArrayList<TaskDTO>> cardsMap = new HashMap<String, ArrayList<TaskDTO>>();
		if (bmpnTaskList != null && bmpnTaskList.size()>0) {
			for(BPMNTask task:bmpnTaskList) {
				TaskDTO dto = new TaskDTO(task.getBpmnTaskId(),task.getBpmnTxRefNo(),task.getElementId(),"",task.getTaskSubject()
						//,simpleDateFormat.format(task.getTaskCreatedDate()),simpleDateFormat.format(task.getDueDate()),
						, "", "",
						task.getLockedUser(),task.getBpmnId(),task.getCompanyCode());
				dto.setStepLabel(task.getStepLabel());
				dto.setTaskSubject(task.getTaskSubject());
				dto.setTaskType(task.getElementType());
				
				dto.setAssigneduser(task.getAssignedUser());
				dto.setPriority(task.getPriority());
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				if(task.getDueDate() != null){
					dto.setDueDate(df.format(new Date(task.getDueDate().getTime())));
				}
				if(task.getTaskCreatedDate() != null){
					dto.setCreatedDate(df.format(new Date(task.getTaskCreatedDate().getTime())));
				}
				if(task.getTaskCompleteDate() != null){
					dto.setCompletedDate(df.format(new Date(task.getTaskCompleteDate().getTime())));
				}
				if(task.getLastModifiedDate() != null){
					dto.setLastModifiedDate(df.format(new Date(task.getLastModifiedDate().getTime())));
				}
				String columnname = task.getElementId();
				if(!columnNames.contains(columnname)) {
					columnname = lanelistDTO.get(0).getLabel();
				}
				if(!cardsMap.containsKey(columnname)) {
					cardsMap.put(columnname, new ArrayList<TaskDTO>());
				}
				ArrayList<TaskDTO> cardsList = cardsMap.get(columnname);
				cardsList.add(dto);
				cardsMap.put(columnname, cardsList);
			}
		}
		
		 for (LanesDTO lane : lanelistDTO) { 		      
	           lane.setCards(cardsMap.get(lane.getLabel()));		
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
		return lanelistDTO;
	  }
	
	public List<TaskDTO> getTaskList(String companyCode, String userId)throws ApplicationException
	{
		long t1 = System.currentTimeMillis();
		//System.out.println("#KanbanDAO#getActiveCardList#");
		List<BPMNTask> bmpnTaskList=null;
		ArrayList<TaskDTO> taskList = new ArrayList<TaskDTO>();
		try
		{
			String query="select t from BPMNTask t, BPMNProcess p where p.rendertype = 'BOARD' and t.bpmnId = p.bpmnId and p.companycode = :companyCode and t.companyCode = :companyCode and t.assignedUser= :assignedUser";
			
			Query objQuery=objJProvider.createQuery(query);
			objQuery.setParameter("assignedUser", userId);
			objQuery.setParameter("companyCode",companyCode);
			
			bmpnTaskList=objQuery.getResultList();
			
		
	        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	        
		if (bmpnTaskList != null && bmpnTaskList.size()>0) {
			for(BPMNTask task:bmpnTaskList) {
				TaskDTO dto = new TaskDTO(task.getBpmnTaskId(),task.getBpmnTxRefNo(),task.getElementId(),"",task.getTaskSubject()
						//,simpleDateFormat.format(task.getTaskCreatedDate()),simpleDateFormat.format(task.getDueDate()),
						, "", "",
						task.getLockedUser(),task.getBpmnId(),task.getCompanyCode());
				dto.setStepLabel(task.getStepLabel());
				dto.setTaskSubject(task.getTaskSubject());
				dto.setTaskType(task.getElementType());
				
				dto.setAssigneduser(task.getAssignedUser());
				dto.setPriority(task.getPriority());
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				if(task.getDueDate() != null){
					dto.setDueDate(df.format(new Date(task.getDueDate().getTime())));
				}
				if(task.getTaskCreatedDate() != null){
					dto.setCreatedDate(df.format(new Date(task.getTaskCreatedDate().getTime())));
				}
				if(task.getTaskCompleteDate() != null){
					dto.setCompletedDate(df.format(new Date(task.getTaskCompleteDate().getTime())));
				}
				if(task.getLastModifiedDate() != null){
					dto.setLastModifiedDate(df.format(new Date(task.getLastModifiedDate().getTime())));
				}
				taskList.add(dto);
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
		return taskList;
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
	
	public JSONObject findStepDetails(String bpmnId) throws ApplicationException 
	{
		long t1 = System.currentTimeMillis();
		List<Object[]> obj=null;
		JSONArray steps = null;
		JSONObject json =new JSONObject();
		try {
			//System.out.println("#BpmnTaskDAO#findBPMNProcess#TaskId#" + bpmnId);
			if (bpmnId !=null) {
			 obj=  objJProvider.createNativeQuery("select processconfig ->> 'steps' as Steps, processconfig ->> 'fields' as fields, processconfig ->> 'choices' as choices, processconfig ->> 'fieldSet' as fieldSet,  processconfig ->> 'tabs' as tabs, processconfig ->> 'bpmn' as bpmn, companyCode from {h-schema}zf_cfg_bpmnprocess where bpmnid = :bpmnid").setParameter("bpmnid", bpmnId).getResultList();
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
			json.put("fields", new JSONArray(String.valueOf(obj.get(0)[1])));
			json.put("choices", new JSONArray(String.valueOf(obj.get(0)[2])));
			json.put("fieldSet", new JSONArray(String.valueOf(obj.get(0)[3])));
			json.put("tabs", new JSONArray(String.valueOf(obj.get(0)[4])));
			json.put("bpmn", new JSONObject(String.valueOf(obj.get(0)[5])));
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
			//System.out.println("#BpmnTaskDAO#findBPMNProcess#End#TT#"+ (System.currentTimeMillis() - t1));
		}
		return json;
	}
	
	public JSONObject findTaskDetails(TaskDTO task) throws ApplicationException 
	{
		String bpmnId = task.getBpmnId();
		long t1 = System.currentTimeMillis();
		List<Object[]> obj=null;
		JSONArray steps = null;
		JSONObject json =new JSONObject();
		try {
			//System.out.println("#BpmnTaskDAO#findBPMNProcess#TaskId#" + bpmnId);
			if (bpmnId !=null) {
			 obj=  objJProvider.createNativeQuery("select processconfig ->> 'steps' as Steps, processconfig ->> 'fields' as fields, processconfig ->> 'choices' as choices, processconfig ->> 'fieldSet' as fieldSet,  processconfig ->> 'tabs' as tabs, processconfig ->> 'bpmn' as bpmn from {h-schema}zf_cfg_bpmnprocess where bpmnid = :bpmnid").setParameter("bpmnid", bpmnId).getResultList();
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
			json.put("fields", new JSONArray(String.valueOf(obj.get(0)[1])));
			json.put("choices", new JSONArray(String.valueOf(obj.get(0)[2])));
			json.put("fieldSet", new JSONArray(String.valueOf(obj.get(0)[3])));
			json.put("tabs", new JSONArray(String.valueOf(obj.get(0)[4])));
			json.put("bpmn", new JSONObject(String.valueOf(obj.get(0)[5])));
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
			//System.out.println("#BpmnTaskDAO#findBPMNProcess#End#TT#"+ (System.currentTimeMillis() - t1));
		}
		return json;
	}
	
	
	
    
	public JSONObject getDSProcessList(String companyCode) throws ApplicationException {

		long t1 = System.currentTimeMillis();
		List<Object[]> obj=null;
		JSONObject json =new JSONObject();
		try {
			 obj=  objJProvider.createNativeQuery("select PROCESSID, COUNTRYCODE, PROCESSNAME as NAME, BPMNID, '3456' as TAT, 'test' as DESCRIPTION from {h-schema}zf_cfg_bpmnworkflowdtl where companycode=:companyCode").setParameter("companyCode", companyCode).getResultList();
			//System.out.println("obj "+obj);
			JSONArray array = new JSONArray();
			for(int i=0 ; i < obj.size() ; i++) {
				JSONObject node = new JSONObject();
				
					node.put("processid", obj.get(i)[0]);
					node.put("countrycode", obj.get(i)[1]);
					node.put("name", obj.get(i)[2]);
					node.put("bpmnid", obj.get(i)[3]);
					node.put("tat", obj.get(i)[4]);
					node.put("description", obj.get(i)[5]);
				
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
			//System.out.println("#BpmnTaskDAO#findBPMNProcess#End#TT#"+ (System.currentTimeMillis() - t1));
		}
		return json;
	
	 }
	
	public String checkBoardId(String companyCode, String boardid) throws ApplicationException {

		long t1 = System.currentTimeMillis();
		List<Object[]> obj=null;
		String response="Available";
		try {
			if(boardid!=null && boardid.toUpperCase().startsWith("FP")) {
				response="Not Available";
			}
			obj=  objJProvider.createNativeQuery("select PROCESSNAME as NAME from {h-schema}zf_cfg_bpmnworkflowdtl where companycode=:companyCode and processid=:boardid").setParameter("companyCode", companyCode).setParameter("boardid", boardid).getResultList();
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
			//System.out.println("#BpmnTaskDAO#findBPMNProcess#End#TT#"+ (System.currentTimeMillis() - t1));
		}
		return response;
	
	 }
	
	
	public JSONObject getBoardConfig(String companyCode, String boardid) throws ApplicationException {

		long t1 = System.currentTimeMillis();
		List<Object[]> obj=null;
		JSONObject json =new JSONObject();
		try {
			 obj =  objJProvider.createNativeQuery("select processid,CAST(json as TEXT) from {h-schema}zf_cfg_bpmnworkflowdtl where companyCode = :companyCode and processId= :processId").setParameter("companyCode", companyCode).setParameter("processId", boardid).getResultList();
			
			 
			//System.out.println("obj "+obj);
			for(int i=0 ; i < obj.size() ; i++) {
				json.put("boardid", obj.get(i)[0]);
				json.put("companycode", companyCode);
				String boardconfig = obj.get(i)[1].toString().replace("\"","\"");				
				json.put("boardconfig", new JSONObject(boardconfig));
			}
			
			//System.out.println("json "+json.toString());
			}	
		catch (Exception e) {
			e.printStackTrace();
			//log.printErrorMessage(e);
			throw new ApplicationException(new Exception(e));
		}
		finally
		{
			//System.out.println("#BpmnTaskDAO#findBPMNProcess#End#TT#"+ (System.currentTimeMillis() - t1));
		}
		return json;
	
	}
	
	public void deleteBoard(String companyCode, String boardid) throws ApplicationException {

		long t1 = System.currentTimeMillis();
		
		try {
			 
			 if(boardid.length()==5) {
				List<Object> obj =  objJProvider.createNativeQuery("select processtype from {h-schema}zf_cfg_bpmnworkflowdtl where companycode=:companyCode and processid=:processId").setParameter("companyCode", companyCode).setParameter("processId", boardid).getResultList();
				if(obj.size()>0) {
					String idtype = String.valueOf(obj.get(0));
					objJProvider.createNativeQuery("insert into {h-schema}zf_cfg_available_ids (companycode, idtype, id) values (:companyCode, :idtype, :id)").setParameter("companyCode", companyCode).setParameter("idtype", idtype).setParameter("id", boardid).executeUpdate();
				}
			 }
			 objJProvider.createNativeQuery("delete from {h-schema}zf_cfg_bpmnworkflowdtl where companycode = :companyCode and processid= :processId").setParameter("companyCode", companyCode).setParameter("processId", boardid).executeUpdate();
			 objJProvider.createNativeQuery("delete from {h-schema}zf_cfg_bpmnprocess where companycode = :companyCode and processid= :processId").setParameter("companyCode", companyCode).setParameter("processId", boardid).executeUpdate();
			 objJProvider.createNativeQuery("delete from {h-schema}zf_txn_userdocs where companycode = :companyCode and bpmntxrefno in (select bpmntxrefno from  {h-schema}zf_txn_bpmnprocessinfo where companycode = :companyCode and processid= :processId)").setParameter("companyCode", companyCode).setParameter("processId", boardid).executeUpdate();
			 objJProvider.createNativeQuery("delete from {h-schema}zf_txn_comments where companycode = :companyCode and bpmntxrefno in (select bpmntxrefno from  {h-schema}zf_txn_bpmnprocessinfo where companycode = :companyCode and processid= :processId)").setParameter("companyCode", companyCode).setParameter("processId", boardid).executeUpdate();
			 objJProvider.createNativeQuery("delete from {h-schema}zf_txn_bpmntask where companycode = :companyCode and bpmntxrefno in (select bpmntxrefno from  {h-schema}zf_txn_bpmnprocessinfo where companycode = :companyCode and processid= :processId)").setParameter("companyCode", companyCode).setParameter("processId", boardid).executeUpdate();
			 objJProvider.createNativeQuery("delete from {h-schema}zf_txn_bpmnprocessinfo where companycode = :companyCode and processid= :processId").setParameter("companyCode", companyCode).setParameter("processId", boardid).executeUpdate();
			
		}	
		catch (Exception e) {
			e.printStackTrace();
			//log.printErrorMessage(e);
			throw new ApplicationException(new Exception(e));
		}
		finally
		{
			//System.out.println("#KanbanDAO#deleteBoard#End#TT#"+ (System.currentTimeMillis() - t1));
		}
	
	}
	
	public String createBoard(String companyCode, JSONObject processjson) throws ApplicationException {

		long t1 = System.currentTimeMillis();
		int count=0;
	    String message="Board created successfully";
	    JSONObject process = (JSONObject) processjson.get("process");
	    
		try {
			objJProvider.begin();
			String boardid = "B0001";
			String processtype = "BOARD";
			List<Object> obj1 = objJProvider.createNativeQuery("select id from {h-schema}zf_cfg_available_ids where companycode = :companycode and idtype = :processtype order by id asc LIMIT 1").setParameter("companycode", companyCode).setParameter("processtype", processtype).getResultList();
			if(obj1.size()>0) {
				String tempProcessId = String.valueOf(obj1.get(0));
				if(tempProcessId.length()==5) {
					//String intprocess = tempProcessId.substring(1);
					boardid = tempProcessId;
				}
				objJProvider.createNativeQuery("delete from {h-schema}zf_cfg_available_ids where companycode=:companyCode and idtype=:processtype  and id=:id ").setParameter("companyCode", companyCode).setParameter("processtype", processtype).setParameter("id", tempProcessId).executeUpdate();
				
			}else{
			List<Object> obj = objJProvider.createNativeQuery("select processid from {h-schema}zf_cfg_bpmnworkflowdtl where companycode = :companycode and processtype = :processtype order by processid desc LIMIT 1").setParameter("companycode", companyCode).setParameter("processtype", processtype).getResultList();
			if(obj.size()>0) {
				String tempProcessId = String.valueOf(obj.get(0));
				if(tempProcessId.length()==5) {
					String intprocess = tempProcessId.substring(1);
					boardid = processtype.substring(0, 1) + String.format("%04d", Integer.valueOf(intprocess)+1);		
				}
			}
			}
			
			process.put("BOARDID", boardid);
			processjson.put("process", process);
			
			 count =  objJProvider.createNativeQuery("insert into {h-schema}zf_cfg_bpmnworkflowdtl (companycode, processid, processname, bpmnid, countrycode, json, branchname, processtype) values (:1,:2,:3,:4,:5,:6,:7,:8)")
					 .setParameter("1", companyCode)
					 .setParameter("2", boardid)
					 .setParameter("3", (String)process.get("NAME"))
					 .setParameter("4", "NA")
					 .setParameter("5", "NA")
					 .setParameter("6", processjson.toString())
					 .setParameter("7", "MASTER")
					 .setParameter("8", "BOARD")
					 .executeUpdate();
			objJProvider.commit();
			if(count == 0) {
				message="Unable to create process";
			}else {
				KanbanServiceImpl objKanbanServiceImpl=new KanbanServiceImpl();	
				//System.out.println("#BpmnTaskDAO#deployBoard#processjson#"+ processjson.toString());
				objKanbanServiceImpl.deployBoard(companyCode,processjson.toString());
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
			//System.out.println("#BpmnTaskDAO#findBPMNProcess#End#TT#"+ (System.currentTimeMillis() - t1));
		}
		return message;
	
	}
	/**
	 * Save the Board Design
	 * @param companyCode
	 * @param processjson
	 * @return
	 * @throws ApplicationException
	 */
	public String saveBoard(String companyCode, String processjson) throws ApplicationException {
		long t1 = System.currentTimeMillis();
		int count=0;
	    String message="Process Saved successfully";
	    JSONObject json = new JSONObject(processjson);
	    JSONObject process = (JSONObject) json.get("process");
	    
	    //System.out.println("companyCode#"+companyCode+"#process"+process.get("BOARDID"));
	   try {
			objJProvider.begin();
			count =  objJProvider.createNativeQuery("UPDATE {h-schema}zf_cfg_bpmnworkflowdtl set json=:json, processname=:processname where companycode=:companycode and  processid=:processid ")
					 .setParameter("companycode", companyCode)
					 .setParameter("processid", (String)process.get("BOARDID"))
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
	
	
	public List<ProcessDTO> getBoardList(String companyCode,String userId, String userType)throws ApplicationException
	{
		long t1 = System.currentTimeMillis();
		//System.out.println("#kanbabDAO#getBoardList#"+companyCode+"#"+userId);
		List<Object[]> boardList=null;
		List<ProcessDTO> dto = new ArrayList<ProcessDTO>();
		try
		{
			String query=null;
			if(userType!=null && userType.equalsIgnoreCase("admin"))
			{
				query="select t.processid, t.processname from {h-schema}zf_cfg_bpmnprocess t where isactive='Y' and rendertype='BOARD' and companyCode=:companyCode";
			}
			else
			{
				query="select t.processid, t.processname from {h-schema}zf_cfg_bpmnprocess t where companyCode=:companyCode and isactive='Y' and rendertype='BOARD' and (processconfig -> 'process' ->'MEMBERLIST') \\?\\? :userId ";
			}
			Query objQuery=objJProvider.createNativeQuery(query);
			if(!(userType!=null && userType.equalsIgnoreCase("admin"))) {
			   objQuery.setParameter("companyCode", companyCode);
			   objQuery.setParameter("userId", userId);
		    }else {
		    	objQuery.setParameter("companyCode", companyCode);
		    }
			boardList=objQuery.getResultList();
			//System.out.println(boardList.size() + " -------- " + boardList);
			for (int i = 0; i < boardList.size(); i++) {
				 ProcessDTO processDTO = new ProcessDTO();
			     processDTO.setProcessId((String) boardList.get(i)[0]);
			     processDTO.setProcessname((String) boardList.get(i)[1]);
			     dto.add(processDTO);
			}
			//System.out.println(dto.size() + " -------- " + dto);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			//log.printErrorMessage(ex);
			throw new ApplicationException(ex);
		}
		finally
		{
			//System.out.println("#kanbanDAO#getBoardList#End#TT#" + (System.currentTimeMillis() - t1));
		}
		return dto;
	  }

	/**
	 * Remove All comments mapped to taskid
	 * @param refNo
	 * @param taskid
	 * @return
	 * @throws ApplicationException
	 */
	//TODO - used in kanban
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
	
	//TODO - used in kanban
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
}
