package com.zanflow.auth.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zanflow.auth.jwt.JwtUtils;
import com.zanflow.sec.common.Constants;
import com.zanflow.sec.common.exception.ApplicationException;
import com.zanflow.sec.dao.UserMgmtDAO;
import com.zanflow.sec.dto.ResponseDTO;
import com.zanflow.sec.dto.UserDTO;
import com.zanflow.sec.model.User;

@CrossOrigin(origins = "*", allowedHeaders ="*")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@PostMapping("/signin")
	public ResponseEntity<?> authenticate(HttpServletRequest request, HttpServletResponse 
	response, @RequestBody UserDTO objUserDTO) throws Exception {
		try (UserMgmtDAO objUserMgmtDAO = new UserMgmtDAO(Constants.DB_PUNIT)) {
	
			if (objUserDTO == null || objUserDTO.getUserId() == null || objUserDTO.getUserId() == null ) {
				throw new ApplicationException("Insufficient info: Userid/password  is empty",1);
			}
			User objRole = objUserMgmtDAO.validateUSer(objUserDTO);
			JwtUtils jwtUtils = new JwtUtils();
			String jwt = jwtUtils.generateJwtToken(objRole.getUserId(),Constants.JWT_AUTH_SUBJ,15);
			String refreshKey = jwtUtils.generateJwtRefreshToken(objRole.getUserId(),Constants.JWT_REFRESH_SUBJ,30);
			
			UserDTO dto = new UserDTO();
			String ignore[] = {"password"};
			BeanUtils.copyProperties(objRole, dto, ignore);
			dto.setAuthToken(jwt);
			dto.setAuthRefreshKey(refreshKey);
			return new ResponseEntity<ResponseDTO>(dto, HttpStatus.OK);
		} catch (ApplicationException e) {
			return new ResponseEntity<ResponseDTO>(new ResponseDTO(e.getMessage(), e.getErrorCode()),
					(e.getErrCode() == 1) ? HttpStatus.UNAUTHORIZED : HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/refresh-token")
	public ResponseEntity<?> authenticate(HttpServletRequest request, HttpServletResponse 
	response, @RequestHeader("Authorization") String refreshKey,@RequestHeader("companycode") String company) throws Exception {
		try (UserMgmtDAO objUserMgmtDAO = new UserMgmtDAO(Constants.DB_PUNIT)) {
			refreshKey = refreshKey.replace("Bearer ", "");
			//User objRole = objUserMgmtDAO.validateUSer(objUserDTO);
			//System.out.println("refreshKey#"+refreshKey);
			JwtUtils jwtUtils = new JwtUtils();
			jwtUtils.validateJwtRefreshToken(refreshKey);
			String userId = jwtUtils.getUserNameFromJwtToken(refreshKey,true);
			User user = objUserMgmtDAO.findUser(userId);
			UserDTO dto = new UserDTO();
			
			if(user ==  null || !"A".equalsIgnoreCase(user.getStatus()) || !user.getCompanyCode().equalsIgnoreCase(company))
				throw new ApplicationException("Invalid User",1);

			//if("Y".equalsIgnoreCase(user.getLogInStatus())){
				String jwt = jwtUtils.generateJwtToken(user.getUserId(),Constants.JWT_AUTH_SUBJ,15);
				String refreshToken = jwtUtils.generateJwtRefreshToken(user.getUserId(),Constants.JWT_REFRESH_SUBJ,30);
				dto.setAuthToken(jwt);
				dto.setAuthRefreshKey(refreshToken);
				//System.out.println("GeneratedToken#"+jwt+"#refreshKey#"+refreshToken);
					
			//}else {
			//	throw new ApplicationException("User not logged in or user session got expired");
			//}
			
			BeanUtils.copyProperties(user, dto);
			return new ResponseEntity<ResponseDTO>(dto, HttpStatus.OK);
		} catch (ApplicationException e) {
			e.printStackTrace();
			return new ResponseEntity<ResponseDTO>(new ResponseDTO(e.getMessage(), e.getErrorCode()),
					(e.getErrCode() == 1) ? HttpStatus.UNAUTHORIZED : HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}

