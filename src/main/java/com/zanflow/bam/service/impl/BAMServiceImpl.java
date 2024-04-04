package com.zanflow.bam.service.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.zanflow.bam.dao.BAMDAO;
import com.zanflow.bam.dto.BAMDetailDTO;
import com.zanflow.bam.dto.BarChartData;
import com.zanflow.bam.dto.BarChartDataSubset;
import com.zanflow.bam.dto.DataSets;
import com.zanflow.bam.dto.MyReqDTO;
import com.zanflow.bam.dto.ProcessDTO;
import com.zanflow.bam.dto.ProcessListDTO;
import com.zanflow.bam.dto.SearchResultDTO;
import com.zanflow.bam.dto.SearchResultListDTO;
import com.zanflow.common.db.Constants;

public class BAMServiceImpl 
{
	/**
	 * 
	 * @param companyCode
	 * @param userid
	 * @param bpmnId
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws Exception
	 */
	public BAMDetailDTO getDetails(String companyCode, String userid, String processId, String startDate, String endDate) throws Exception
	{
		BAMDetailDTO objBAMDetailDTO=new BAMDetailDTO();
		BAMDAO objBAMDAO=null;
		try
		{
			objBAMDAO=new BAMDAO(Constants.DB_PUNIT);
			List<Object[]> resultList= null;
			if("ALL".equalsIgnoreCase(processId)) {
				resultList=objBAMDAO.getStatusWiseTxnCount_All(companyCode,userid,startDate, endDate);
			}else {
				resultList=objBAMDAO.getStatusWiseTxnCount(companyCode,processId,startDate, endDate);
			}
			if(resultList!=null && resultList.size()>0)
			{
				prepareBamResponseDTO(objBAMDetailDTO, resultList);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw new Exception(ex);
		}
		finally
		{
			if(objBAMDAO!=null)
			{
				objBAMDAO.close();
			}
		}
		return objBAMDetailDTO;
	}
	
	public BAMDetailDTO getStatusWeiseMetrics_chart(String companyCode, String userid, String processId, String startDate, String endDate) throws Exception {
		BAMDetailDTO objBAMDetailDTO=new BAMDetailDTO();
		try(BAMDAO objBAMDAO = new BAMDAO(Constants.DB_PUNIT) )
		{
			List<Object[]> resultList= null;
	
				resultList=objBAMDAO.getStatusWiseTxnCount_Chart(companyCode,userid,processId,startDate,endDate);
			
			if(resultList!=null && resultList.size()>0)
			{
				prepareBamChartResponseDTO(objBAMDetailDTO, resultList);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw new Exception(ex);
		}
		
		return objBAMDetailDTO;
	}

	private void prepareBamResponseDTO(BAMDetailDTO objBAMDetailDTO, List<Object[]> resultList) {
		int terminate=0,inProgress=0,completed=0;
		for(Object []ob:resultList)
		{
			String status=ob[1].toString();
			//System.out.println(ob[0].toString()+"#"+ob[1].toString());
			if(status.equalsIgnoreCase("2"))
			{
				completed=Integer.parseInt(ob[0].toString());
			}
			else if(status.equalsIgnoreCase("3"))
			{
				inProgress=Integer.parseInt(ob[0].toString());
			}
			else if(status.equalsIgnoreCase("7"))
			{
				terminate=Integer.parseInt(ob[0].toString());
			}
		}
		//System.out.println(completed+"#"+inProgress+"#"+terminate);
		objBAMDetailDTO.setCompletedCount(completed);
		objBAMDetailDTO.setInProgressCount(inProgress);
		objBAMDetailDTO.setTerminatedCount(terminate);
		objBAMDetailDTO.setTotalCount(completed+inProgress+terminate);
	}
	
	private void prepareBamChartResponseDTO(BAMDetailDTO objBAMDetailDTO, List<Object[]> resultList) {
		String[] labels= new String[resultList.size()];
		LinkedHashSet<String> lablesSet = new LinkedHashSet<String>();
		LinkedHashMap<String, BarChartDataSubset> dataSetmap = new LinkedHashMap<String, BarChartDataSubset>();
		 
		for(Object []ob:resultList)
		{
			String status = ob[1].toString();
			//System.out.println(ob[0].toString() + "#" + ob[2].toString());
			lablesSet.add(ob[0].toString());
			if (!dataSetmap.containsKey(ob[0].toString())) {
				BarChartDataSubset barchartSuset = new BarChartDataSubset();
				barchartSuset.setLabel(ob[0].toString());
				dataSetmap.put(ob[0].toString(), barchartSuset);
			}

			if (status.equalsIgnoreCase("2")) {
				dataSetmap.get(ob[0].toString()).setCompleted(Integer.parseInt(ob[2].toString()));
			} else if (status.equalsIgnoreCase("3")) {
				dataSetmap.get(ob[0].toString()).setInprogress(Integer.parseInt(ob[2].toString()));

			} else if (status.equalsIgnoreCase("7")) {
				dataSetmap.get(ob[0].toString()).setTerminated(Integer.parseInt(ob[2].toString()));
			}
			
		}		
		
		int[] completed = new int[dataSetmap.size()],terminate = new int[dataSetmap.size()],inProgress = new int[dataSetmap.size()];
		String[] lablesStr = new String[dataSetmap.size()];
		int index = 0;
		
		for(String key:dataSetmap.keySet()) {
			completed[index] = dataSetmap.get(key).getCompleted();
			terminate[index] = dataSetmap.get(key).getTerminated();
			inProgress[index] = dataSetmap.get(key).getInprogress();
			lablesStr[index] = key;
			index++;
			//System.out.println("key#"+key);
		}
		
		DataSets datasetDto = new DataSets("Completed","#0bbb7c",completed);
		DataSets datasetDto_inpro = new DataSets("InProgress","#f57e27",inProgress);
		DataSets datasetDto_termi = new DataSets("Terminated","#ef062f",terminate);
		
		DataSets[] dataset = new DataSets[3];
		dataset[0]=datasetDto;
		dataset[1]=datasetDto_inpro;
		dataset[2]=datasetDto_termi;
		
		BarChartData dataDto = new BarChartData();
		dataDto.setLabels(lablesStr);
		dataDto.setDatasets(dataset);
		objBAMDetailDTO.setChartData(dataDto);
		//System.out.println("#"+lablesStr.toString()+"#"+dataset.toString());
		
	}

	public ProcessListDTO getProcessList(String companyCode,String userId, String moduleName) throws Exception
	{
		BAMDAO objBAMDAO=null;
		ProcessListDTO objProcessListDTO=new ProcessListDTO();
		try
		{
			objBAMDAO=new BAMDAO(Constants.DB_PUNIT);
			List<ProcessDTO> processList=objBAMDAO.getProcessList(companyCode, userId, moduleName);
			objProcessListDTO.setProcessListDTO(processList);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw new Exception(ex);
		}
		finally
		{
			if(objBAMDAO!=null)
			{
				objBAMDAO.close();
			}
		}
		return objProcessListDTO;
	}

	public SearchResultListDTO getSearchResult(String companyCode, String processId, String startDate, String endDate) throws Exception
	{
		SearchResultListDTO objSearchResultListDTO=new SearchResultListDTO();
		BAMDAO objBAMDAO=null;
		try
		{
			objBAMDAO=new BAMDAO(Constants.DB_PUNIT);
			List<String> indexFields = objBAMDAO.getReporableFields(companyCode,processId);
			List<String> columns = new ArrayList<String>();
			columns.add("bpmntxrefno");
			columns.add("createdtime");
			columns.add("laststepname");
			columns.add("statuscode");
			//System.out.println("columns "+columns);
			List<Map<String,Object>> resultList=objBAMDAO.getSearchDtl(companyCode, processId, startDate, endDate,indexFields,columns);
			//System.out.println("columns "+columns);
			if(resultList!=null && resultList.size()>0)
			{
				objSearchResultListDTO.setColumns(columns);	
				objSearchResultListDTO.setResultList(resultList);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw new Exception(ex);
		}
		finally
		{
			if(objBAMDAO!=null)
			{
				objBAMDAO.close();
			}
		}
		return objSearchResultListDTO;
	}

	
	
	
	public List<MyReqDTO> getMyReqDtls(String companyCode,String userId, String processId) throws Exception
	{
		List<MyReqDTO> myReqList=null;
		BAMDAO objBAMDAO=null;
		try
		{
			objBAMDAO=new BAMDAO(Constants.DB_PUNIT);
			myReqList=objBAMDAO.getMyRequests(companyCode, userId, processId);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw new Exception(ex);
		}
		finally
		{
			if(objBAMDAO!=null)
			{
				objBAMDAO.close();
			}
		}
		return myReqList;
	}

}
