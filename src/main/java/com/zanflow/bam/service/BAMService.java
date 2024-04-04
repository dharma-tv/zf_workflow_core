package com.zanflow.bam.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.zanflow.bam.dto.BAMDetailDTO;
import com.zanflow.bam.dto.MyReqDTO;
import com.zanflow.bam.dto.MyReqTXNListDTO;
import com.zanflow.bam.dto.ProcessListDTO;
import com.zanflow.bam.dto.ResponseDTO;
import com.zanflow.bam.dto.SearchResultListDTO;
import com.zanflow.bam.service.impl.BAMServiceImpl;

@RestController
@CrossOrigin(origins = "*" ,allowedHeaders ="*")
public class BAMService 
{
	 @CrossOrigin(origins = {"http://localhost:3000","http://localhost:6075","http://ec2-54-221-88-139.compute-1.amazonaws.com:6075"})
	 @RequestMapping(value="/txn-metrics/{companyCode}/{userid}/{processId}/{startDate}/{endDate}", method = RequestMethod.GET)
	 public ResponseDTO statusWiseTxnMetrics(HttpServletRequest request, HttpServletResponse response,@PathVariable String companyCode, @PathVariable String userid,
			 @PathVariable String processId, @PathVariable String startDate,@PathVariable String endDate)
	 {
		 ResponseDTO objResponseDTO=null;
		 BAMServiceImpl objBAMServiceImpl=new BAMServiceImpl();
		 try
		 {
			 //System.out.println(companyCode+"#"+processId+"#"+startDate+"#"+endDate);
			 BAMDetailDTO objBAMDetailDTO=objBAMServiceImpl.getDetails(companyCode,userid,processId,startDate,endDate);
			 BAMDetailDTO objBAMDetailDTOBar=objBAMServiceImpl.getStatusWeiseMetrics_chart(companyCode,userid,processId,startDate,endDate);
			 objBAMDetailDTO.setChartData(objBAMDetailDTOBar.getChartData());
			 return objBAMDetailDTO;
		 }
		 catch(Exception ex)
		 {
			 ex.printStackTrace();
			 objResponseDTO=new ResponseDTO();
			 objResponseDTO.setResponsMsg(ex.getMessage());
			 objResponseDTO.setResponseCode("ERR");
		 }
		 return objResponseDTO;
	 }
	 
	 @CrossOrigin(origins = {"http://localhost:3000","http://localhost:6075","http://ec2-54-221-88-139.compute-1.amazonaws.com:6075"})
	 @RequestMapping(value="/txn-metrics/chart/{companyCode}/{userid}", method = RequestMethod.GET)
	 public ResponseDTO statusWiseMetrics_chart(HttpServletRequest request, HttpServletResponse response,@PathVariable String companyCode, @PathVariable String userid
			)
	 {
		 ResponseDTO objResponseDTO=null;
		 BAMServiceImpl objBAMServiceImpl=new BAMServiceImpl();
		 try
		 {
			 //System.out.println(companyCode+"#"+userid);
			 BAMDetailDTO objBAMDetailDTO= null;
			 //BAMDetailDTO objBAMDetailDTO=objBAMServiceImpl.getStatusWeiseMetrics_chart(companyCode,userid,processId,startDate,endDate);
			 return objBAMDetailDTO;
		 }
		 catch(Exception ex)
		 {
			 ex.printStackTrace();
			 objResponseDTO=new ResponseDTO();
			 objResponseDTO.setResponsMsg(ex.getMessage());
			 objResponseDTO.setResponseCode("ERR");
		 }
		 return objResponseDTO;
	 }
	 
	 @RequestMapping(value="/getProcessList/{companyCode}/{moduleName}/{userId}", method = RequestMethod.GET)
	 public ResponseDTO getProcessList(HttpServletRequest request, HttpServletResponse response,@PathVariable String companyCode,
			 @PathVariable String moduleName,@PathVariable String userId)
	 {
		 ResponseDTO objResponseDTO=null	;
		 BAMServiceImpl objBAMServiceImpl=new BAMServiceImpl();
		 try
		 {
			 //System.out.println(companyCode+"#"+moduleName);
			 ProcessListDTO objProcessListDTO=objBAMServiceImpl.getProcessList(companyCode,userId,moduleName);
			 return objProcessListDTO;
		 }
		 catch(Exception ex)
		 {
			 ex.printStackTrace();
			 objResponseDTO=new ResponseDTO();
			 objResponseDTO.setResponsMsg(ex.getMessage());
			 objResponseDTO.setResponseCode("ERR");
		 }
		 return objResponseDTO;
	 }
	 
	 
	 @RequestMapping(value="/getSearchDetails/{companyCode}/{processId}/{startDate}/{endDate}", method = RequestMethod.GET)
	 public ResponseDTO getSearchDetails(HttpServletRequest request, HttpServletResponse response,@PathVariable String companyCode,
			 @PathVariable String processId,@PathVariable String startDate,@PathVariable String endDate)
	 {
		 ResponseDTO objResponseDTO=null;
		 BAMServiceImpl objBAMServiceImpl=new BAMServiceImpl();
		 try
		 {
			 //System.out.println(companyCode+"#"+processId+"#"+startDate+"#"+endDate);
			 SearchResultListDTO objSearchResultListDTO=objBAMServiceImpl.getSearchResult(companyCode,processId,startDate,endDate);
			//System.out.println("Result "+objSearchResultListDTO);
			 return objSearchResultListDTO;
		 }
		 catch(Exception ex)
		 {
			 ex.printStackTrace();
			 objResponseDTO=new ResponseDTO();
			 objResponseDTO.setResponsMsg(ex.getMessage());
			 objResponseDTO.setResponseCode("ERR");
		 }
		 return objResponseDTO;
	 }
	 @RequestMapping(value={"/getMyReqTXNList/{companyCode}/{moduleName}/{userId}","/getMyReqTXNList/{companyCode}/{moduleName}/{userId}/{processId}"}, method = RequestMethod.GET)
	 
	 public ResponseDTO getMyReqTXNList(HttpServletRequest request, HttpServletResponse response,@PathVariable String companyCode,
			 @PathVariable String moduleName,@PathVariable String userId,@PathVariable(required = false) String processId)
	 {
		 ResponseDTO objResponseDTO=null;
		 BAMServiceImpl objBAMServiceImpl=new BAMServiceImpl();
		 try
		 {
			 //System.out.println(companyCode+"#"+moduleName+"#"+userId);
			 MyReqTXNListDTO objMyReqTXNListDTO=new MyReqTXNListDTO();
			 List<MyReqDTO> myReqList=objBAMServiceImpl.getMyReqDtls(companyCode,userId, processId);
			 objMyReqTXNListDTO.setMyReqList(myReqList);
			 return objMyReqTXNListDTO;
		 }
		 catch(Exception ex)
		 {
			 ex.printStackTrace();
			 objResponseDTO=new ResponseDTO();
			 objResponseDTO.setResponsMsg(ex.getMessage());
			 objResponseDTO.setResponseCode("ERR");
		 }
		 return objResponseDTO;
	 }

}
