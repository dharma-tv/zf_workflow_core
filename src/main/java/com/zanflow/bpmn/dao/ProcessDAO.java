package com.zanflow.bpmn.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;

import com.zanflow.bpmn.dto.ProcessDTO;
import com.zanflow.bpmn.exception.ApplicationException;
import com.zanflow.bpmn.exception.JPAPersistenceException;
import com.zanflow.common.db.JPATransaction;
import com.zanflow.common.db.JPersistenceProvider;


public class ProcessDAO extends JPATransaction{

	public ProcessDAO(String pJunitName)throws ApplicationException
	{
		//System.out.println("#ProcessDAO#pJunitName#"+pJunitName);
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


	public ProcessDAO(JPersistenceProvider objJProvider) throws ApplicationException
	{
		try{
			if (objJProvider != null)
			{
				this.objJProvider = objJProvider;
				strJPUnitName = objJProvider.getpUnitName();
				//System.out.println("# ProcessDAO objJProvider constructor invoked");
			}else{
				throw new ApplicationException("ProcessDAO : JPersistenceProvider is null");
			}
		} catch (EntityNotFoundException e) {
			//log.printErrorMessage("# EntityNotFoundException #"+e.getMessage());
			e.printStackTrace();
			throw new ApplicationException(new Exception(e));
		} catch (EntityExistsException e) {
			//log.printErrorMessage("# EntityExistsException #"+e.getMessage());
			e.printStackTrace();
			throw new ApplicationException(new Exception(e));
		} catch (JPAPersistenceException e) {
			//log.printErrorMessage("# JPAPersistenceException #"+e.getMessage());
			e.printStackTrace();
			throw new ApplicationException(new Exception(e));
		}	
	}
	
	
	public Map<String,String> getDashboardData(String companyCode) throws ApplicationException	{
		long t1 = System.currentTimeMillis();
		//System.out.println("#ProcessDAO#getDashboardData#"+companyCode);
		List<Object> result=null;
		Map<String,String> dto = new HashMap<String,String>();
		
		String process_query = "select count(1) from {h-schema}zf_cfg_bpmnworkflowdtl where companyCode=:companyCode and processtype = 'PROCESS'";
		String board_query = "select count(1) from {h-schema}zf_cfg_bpmnworkflowdtl where companyCode=:companyCode and processtype = 'BOARD'";
		String widget_query = "select count(1) from {h-schema}zf_cfg_bpmnworkflowdtl where companyCode=:companyCode and processtype = 'WIDGET'";
		String master_query = "select count(1) from {h-schema}zf_mstr_metadata zmm where companyCode=:companyCode";
		String user_query = "select count(1) from {h-schema}zf_id_user  where companyCode=:companyCode";
		String role_query = "select count(1) from {h-schema}zf_id_role  where companyCode=:companyCode";
		try
		{
			
			Query objQuery=objJProvider.createNativeQuery(process_query);
			objQuery.setParameter("companyCode", companyCode);
			result=objQuery.getResultList();
			dto.put("process",String.valueOf(result.get(0)));
			
			objQuery=objJProvider.createNativeQuery(board_query);
			objQuery.setParameter("companyCode", companyCode);
			result=objQuery.getResultList();
			dto.put("board",String.valueOf(result.get(0)));
			
			objQuery=objJProvider.createNativeQuery(widget_query);
			objQuery.setParameter("companyCode", companyCode);
			result=objQuery.getResultList();
			dto.put("widget",String.valueOf(result.get(0)));
			
			objQuery=objJProvider.createNativeQuery(master_query);
			objQuery.setParameter("companyCode", companyCode);
			result=objQuery.getResultList();
			dto.put("master",String.valueOf(result.get(0)));
			
			objQuery=objJProvider.createNativeQuery(user_query);
			objQuery.setParameter("companyCode", companyCode);
			result=objQuery.getResultList();
			dto.put("user",String.valueOf(result.get(0)));
			
			objQuery=objJProvider.createNativeQuery(role_query);
			objQuery.setParameter("companyCode", companyCode);
			result=objQuery.getResultList();
			dto.put("role",String.valueOf(result.get(0)));
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			//log.printErrorMessage(ex);
			throw new ApplicationException(ex);
		}
		finally
		{
			//System.out.println("#ProcessDAO#getDashboardData#End#TT#" + (System.currentTimeMillis() - t1));
		}
		return dto;
	  }
	
	public List<ProcessDTO> getProcessList(String companyCode,String userId, Map<String, List<ProcessDTO>> processMap) throws ApplicationException	{
		long t1 = System.currentTimeMillis();
		//System.out.println("#ProcessDAO#getProcessList#"+companyCode+"#"+userId);
		List<Object[]> processList=null;
		List<ProcessDTO> dto = new ArrayList<ProcessDTO>();
		String query = "select t.bpmnid, t.processname,t.processid,t.rendertype from {h-schema}zf_cfg_bpmnprocess t where isactive='Y' and companyCode=:companyCode1 and ((t.initiaterole in (select roleid from  {h-schema}zf_id_membership where companyCode=:companyCode and userId=:userId and status='A')) OR t.initiaterole ='ALL')";
		try
		{
			if(userId.equalsIgnoreCase("ALL")) {
				query = query.replaceAll("and userId=:userId", "");
			}
			Query objQuery=objJProvider.createNativeQuery(query);
			objQuery.setParameter("companyCode1", companyCode);
			objQuery.setParameter("companyCode", companyCode);
			if(!userId.equalsIgnoreCase("ALL")) {
			objQuery.setParameter("userId", userId);
			}
			processList=objQuery.getResultList();
			//System.out.println(processList.size() + " -------- " + processList);
			for (int i = 0; i < processList.size(); i++) {
				String renderType=(String) processList.get(i)[3];
				 ProcessDTO processDTO = new ProcessDTO();
			     processDTO.setBpmnId((String) processList.get(i)[0]);
			     processDTO.setProcessname((String) processList.get(i)[1]);
			     processDTO.setProcessId((String) processList.get(i)[2]);
			    if(processMap.containsKey(renderType)) {
			    	processMap.get(renderType).add(processDTO);
				}else {
					List<ProcessDTO> listDto = new ArrayList<ProcessDTO>();
					listDto.add(processDTO);
					processMap.put(renderType, listDto);
				}
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
			//System.out.println("#ProcessDAO#getProcessList#End#TT#" + (System.currentTimeMillis() - t1));
		}
		return dto;
	  }
	
	
	public boolean isExternalProcess(String companyCode,String bpmnid) throws ApplicationException	{
		long t1 = System.currentTimeMillis();
		List<Object[]> processList=null;
		List<ProcessDTO> dto = new ArrayList<ProcessDTO>();
		String query = "select t.bpmnid, t.processname,t.processid,t.rendertype from {h-schema}zf_cfg_bpmnprocess t where isactive='Y' and companyCode=:companyCode and t.initiaterole ='External link' and t.bpmnid=:bpmnid";
		try
		{

			Query objQuery=objJProvider.createNativeQuery(query);
			objQuery.setParameter("companyCode", companyCode);
			objQuery.setParameter("bpmnid", bpmnid);
			processList=objQuery.getResultList();
			if(processList!=null && processList.size()>0) {
				return true;
			}else {
				return false;
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
			//System.out.println("#ProcessDAO#getProcessList#End#TT#" + (System.currentTimeMillis() - t1));
		}
	  }
	
	public List<ProcessDTO> getBAMProcessList(String companyCode,String userId,String moduleName)throws ApplicationException
	{
		long t1 = System.currentTimeMillis();
		//System.out.println("#ProcessDAO#getProcessList#"+companyCode+"#"+userId);
		List<Object[]> processList=null;
		List<ProcessDTO> dto = new ArrayList<ProcessDTO>();
		try
		{
			String query=null;
			if(moduleName!=null && moduleName.equalsIgnoreCase("BAM"))
			{
				query="select t.bpmnid, t.processname from {h-schema}zf_cfg_bpmnprocess t where companyCode=:companyCode1 and (monitorrole in (select roleid from  {h-schema}zf_id_membership where companyCode=:companyCode and userId=:userId and status='A') OR monitorrole ='ALL') and isactive='Y' and rendertype='PROCESS'";
			}
			else
			{
				query="select t.bpmnid, t.processname from {h-schema}zf_cfg_bpmnprocess t where companyCode=:companyCode1 and (enquiryrole in (select roleid from  {h-schema}zf_id_membership where companyCode=:companyCode and userId=:userId and status='A') OR enquiryrole ='ALL') and isactive='Y' and rendertype='PROCESS'";
			}
			Query objQuery=objJProvider.createNativeQuery(query);
			objQuery.setParameter("companyCode1", companyCode);
			objQuery.setParameter("companyCode", companyCode);
			objQuery.setParameter("userId", userId);
			processList=objQuery.getResultList();
			//System.out.println(processList.size() + " -------- " + processList);
			for (int i = 0; i < processList.size(); i++) {
				 ProcessDTO processDTO = new ProcessDTO();
			     processDTO.setBpmnId((String) processList.get(i)[0]);
			     processDTO.setProcessname((String) processList.get(i)[1]);
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
			//System.out.println("#ProcessDAO#getProcessList#End#TT#" + (System.currentTimeMillis() - t1));
		}
		return dto;
	  }
}
