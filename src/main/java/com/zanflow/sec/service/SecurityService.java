package com.zanflow.sec.service;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.zanflow.auth.jwt.JwtUtils;
import com.zanflow.bpmn.model.Membership;
import com.zanflow.sec.common.Constants;
import com.zanflow.sec.common.exception.ApplicationException;
import com.zanflow.sec.dao.SecServiceDAO;
import com.zanflow.sec.dao.UserMgmtDAO;
import com.zanflow.sec.dto.AppIntegrationDTO;
import com.zanflow.sec.dto.DepartmentDTO;
import com.zanflow.sec.dto.LocationDTO;
import com.zanflow.sec.dto.RegisterDTO;
import com.zanflow.sec.dto.ResponseDTO;
import com.zanflow.sec.dto.RoleDTO;
import com.zanflow.sec.dto.UserDTO;
import com.zanflow.sec.dto.UserRoleDTO;
import com.zanflow.sec.model.AppIntegrationModel;
import com.zanflow.sec.model.CompanyProfile;
import com.zanflow.sec.model.Department;
import com.zanflow.sec.model.Leads;
import com.zanflow.sec.model.Location;
import com.zanflow.sec.model.Role;
import com.zanflow.sec.model.User;

import io.jsonwebtoken.SignatureAlgorithm;



@RestController
@CrossOrigin(origins = "*" ,allowedHeaders ="*")
public class SecurityService {
	

	@PostMapping(value="/login")
	public ResponseEntity<?> authenticate(HttpServletRequest request, HttpServletResponse 
	response, @RequestBody UserDTO objUserDTO) throws Exception {
		
		
		try (UserMgmtDAO objUserMgmtDAO = new UserMgmtDAO(Constants.DB_PUNIT)) {
			
			
			if (objUserDTO == null || objUserDTO.getUserId() == null || objUserDTO.getUserId() == null ) {
				throw new ApplicationException("Insufficient info: Userid/password  is empty",1);
			}
			User objRole = objUserMgmtDAO.validateUSer(objUserDTO);
			UserDTO dto = prepareUserDto(objRole);
			
			
			System.out.println(request.getAttribute("request_uri") + " ended , time taken " + (System.currentTimeMillis() - (long) request.getAttribute("start_time")) + " milliseconds");
			
			return new ResponseEntity<ResponseDTO>(dto, HttpStatus.OK);
		} catch (ApplicationException e) {
			return new ResponseEntity<ResponseDTO>(new ResponseDTO(e.getMessage(), e.getErrorCode()),
					(e.getErrCode() == 1) ? HttpStatus.UNAUTHORIZED : HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping(value="/google-login")
	public ResponseEntity<?> googleauthenticate(HttpServletRequest request, HttpServletResponse 
	response, @RequestBody UserDTO objUserDTO) throws Exception {
		try (UserMgmtDAO objUserMgmtDAO = new UserMgmtDAO(Constants.DB_PUNIT)) {
			
			if (objUserDTO == null || objUserDTO.getUserId() == null) {
				throw new ApplicationException("Insufficient info: Userid/password  is empty",1);
			}
			User objRole = objUserMgmtDAO.validateGoogleUser(objUserDTO);
			UserDTO dto = prepareUserDto(objRole);
			
			System.out.println(request.getAttribute("request_uri") + " ended , time taken " + (System.currentTimeMillis() - (long) request.getAttribute("start_time")) + " milliseconds");
			
			return new ResponseEntity<ResponseDTO>(dto, HttpStatus.OK);
		} catch (ApplicationException e) {
			return new ResponseEntity<ResponseDTO>(new ResponseDTO(e.getMessage(), e.getErrorCode()),
					(e.getErrCode() == 1) ? HttpStatus.UNAUTHORIZED : HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping(value="/logout/{userid}")
	public ResponseEntity<?> signout(HttpServletRequest request, HttpServletResponse 
	response, @PathVariable String userid) throws Exception {
		try (UserMgmtDAO objUserMgmtDAO = new UserMgmtDAO(Constants.DB_PUNIT)) {
	
			if (userid == null) {
				throw new ApplicationException("Insufficient info: Userid/password  is empty",1);
			}
			User objUser = objUserMgmtDAO.findUser(userid);
			objUser.setLogInStatus("N");
			objUserMgmtDAO.begin();
			objUserMgmtDAO.updateUser(objUser);
			objUserMgmtDAO.commit();
			
			System.out.println(request.getAttribute("request_uri") + " ended , time taken " + (System.currentTimeMillis() - (long) request.getAttribute("start_time")) + " milliseconds");
			
			return new ResponseEntity<String>("Success", HttpStatus.OK);
		} catch (ApplicationException e) {
			return new ResponseEntity<ResponseDTO>(new ResponseDTO(e.getMessage(), e.getErrorCode()),
					(e.getErrCode() == 1) ? HttpStatus.UNAUTHORIZED : HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping(value="/resetPassword/{companyCode}/{userid}/{password}")
	public ResponseEntity<?> signout(HttpServletRequest request, HttpServletResponse 
	response,@PathVariable String companyCode, @PathVariable String userid,@PathVariable String password) throws Exception {
		try (UserMgmtDAO objUserMgmtDAO = new UserMgmtDAO(Constants.DB_PUNIT)) {
	
			if (userid == null) {
				throw new ApplicationException("Insufficient info: Userid/password  is empty",1);
			}
			User objUser = objUserMgmtDAO.findUser(userid);
			objUser.setPassword(password);
			objUserMgmtDAO.begin();
			objUserMgmtDAO.updateUser(objUser);
			objUserMgmtDAO.commit();
			
			System.out.println(request.getAttribute("request_uri") + " ended , time taken " + (System.currentTimeMillis() - (long) request.getAttribute("start_time")) + " milliseconds");
			
			return new ResponseEntity<String>("Success", HttpStatus.OK);
		} catch (ApplicationException e) {
			return new ResponseEntity<ResponseDTO>(new ResponseDTO(e.getMessage(), e.getErrorCode()),
					(e.getErrCode() == 1) ? HttpStatus.UNAUTHORIZED : HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	private UserDTO prepareUserDto(User objRole) {
		JwtUtils jwtUtils = new JwtUtils();
		String jwt = jwtUtils.generateJwtToken(objRole.getUserId(),Constants.JWT_AUTH_SUBJ,15);
		String refreshKey = jwtUtils.generateJwtRefreshToken(objRole.getUserId(),Constants.JWT_REFRESH_SUBJ,30);
		//System.out.println("token#"+jwt+"refreshKey#"+refreshKey);
		UserDTO dto = new UserDTO();
		String ignore[] = {"password"};
		BeanUtils.copyProperties(objRole, dto, ignore);
		dto.setAuthToken(jwt);
		dto.setAuthRefreshKey(refreshKey);
		return dto;
	}
	
	@RequestMapping(value="/addUser", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public String addUser(HttpServletRequest request, HttpServletResponse response,@RequestBody UserDTO objUserDTO) throws Exception{
		String responseStr="user created";

		UserMgmtDAO objUserMgmtDAO = null;
		if(objUserDTO!=null)
		{
			//System.out.println("UserDTO#"+objUserDTO);
			try {

				if(objUserDTO.getUserId()!=null && objUserDTO.getFirstName()!=null && objUserDTO.getPassword()!=null && objUserDTO.getManagerId()!=null)
				{
					objUserMgmtDAO=new UserMgmtDAO(Constants.DB_PUNIT);
					User objUser=new User();
					String password = PasswordGenerator.generateSecureRandomPassword();
					objUser.setUserId(objUserDTO.getUserId());
					objUser.setFirstName(objUserDTO.getFirstName());
					objUser.setLastName(objUserDTO.getLastName());
					objUser.setPassword(password);
					objUser.setStatus("A");
					objUser.setCompanyCode(objUserDTO.getCompanyCode());
					objUser.setEmailId(objUserDTO.getEmailId());
					objUser.setManagerId(objUserDTO.getManagerId());
					objUserMgmtDAO.begin();
					objUser=objUserMgmtDAO.createUser(objUser);
					String bodyMsg = "";
					//bodyMsg="<h4>Welcome " + objUserDTO.getEmailId() + "</h4> <h4>You are invited for zanflow </h4><h4>Login id - " + objUserDTO.getEmailId()+ "</h4><h4>Password - " + password + "</h4><h4>App link - https://app.zanflow.com </h4>" ;
					//bodyMsg=bodyMsg+"<br> <h2>Your account Login Credentials: login id : "+appIntModel.getCompanyMailId()+" and password : "+user.getPassword()+"</h2>";
					
					bodyMsg = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"><html xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:o=\"urn:schemas-microsoft-com:office:office\" style=\"width:100%;font-family:arial, 'helvetica neue', helvetica, sans-serif;-webkit-text-size-adjust:100%;-ms-text-size-adjust:100%;padding:0;Margin:0\"><head><meta charset=\"UTF-8\"><meta content=\"width=device-width, initial-scale=1\" name=\"viewport\"><meta name=\"x-apple-disable-message-reformatting\"><meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\"><meta content=\"telephone=no\" name=\"format-detection\"><title>Copy of New email template 2021-11-15</title> <!--[if (mso 16)]><style type=\"text/css\"> a {text-decoration: none;} </style><![endif]--> <!--[if gte mso 9]><style>sup { font-size: 100% !important; }</style><![endif]--> <!--[if gte mso 9]><xml> <o:OfficeDocumentSettings> <o:AllowPNG></o:AllowPNG> <o:PixelsPerInch>96</o:PixelsPerInch> </o:OfficeDocumentSettings> </xml><![endif]--><style type=\"text/css\">#outlook a { padding:0;}.ExternalClass { width:100%;}.ExternalClass,.ExternalClass p,.ExternalClass span,.ExternalClass font,.ExternalClass td,.ExternalClass div { line-height:100%;}.es-button { mso-style-priority:100!important; text-decoration:none!important;}a[x-apple-data-detectors] { color:inherit!important; text-decoration:none!important; font-size:inherit!important; font-family:inherit!important; font-weight:inherit!important; line-height:inherit!important;}.es-desk-hidden { display:none; float:left; overflow:hidden; width:0; max-height:0; line-height:0; mso-hide:all;}[data-ogsb] .es-button { border-width:0!important; padding:10px 20px 10px 20px!important;}@media only screen and (max-width:600px) {p, ul li, ol li, a { line-height:150%!important } h1, h2, h3, h1 a, h2 a, h3 a { line-height:120%!important } h1 { font-size:42px!important; text-align:left } h2 { font-size:26px!important; text-align:left } h3 { font-size:20px!important; text-align:left } h1 a { text-align:left } .es-header-body h1 a, .es-content-body h1 a, .es-footer-body h1 a { font-size:42px!important } h2 a { text-align:left } .es-header-body h2 a, .es-content-body h2 a, .es-footer-body h2 a { font-size:26px!important } h3 a { text-align:left } .es-header-body h3 a, .es-content-body h3 a, .es-footer-body h3 a { font-size:20px!important } .es-menu td a { font-size:14px!important } .es-header-body p, .es-header-body ul li, .es-header-body ol li, .es-header-body a { font-size:14px!important } .es-content-body p, .es-content-body ul li, .es-content-body ol li, .es-content-body a { font-size:14px!important } .es-footer-body p, .es-footer-body ul li, .es-footer-body ol li, .es-footer-body a { font-size:14px!important } .es-infoblock p, .es-infoblock ul li, .es-infoblock ol li, .es-infoblock a { font-size:12px!important } *[class=\"gmail-fix\"] { display:none!important } .es-m-txt-c, .es-m-txt-c h1, .es-m-txt-c h2, .es-m-txt-c h3 { text-align:center!important } .es-m-txt-r, .es-m-txt-r h1, .es-m-txt-r h2, .es-m-txt-r h3 { text-align:right!important } .es-m-txt-l, .es-m-txt-l h1, .es-m-txt-l h2, .es-m-txt-l h3 { text-align:left!important } .es-m-txt-r img, .es-m-txt-c img, .es-m-txt-l img { display:inline!important } .es-button-border { display:block!important } a.es-button, button.es-button { font-size:20px!important; display:block!important; border-left-width:0px!important; border-right-width:0px!important } .es-btn-fw { border-width:10px 0px!important; text-align:center!important } .es-adaptive table, .es-btn-fw, .es-btn-fw-brdr, .es-left, .es-right { width:100%!important } .es-content table, .es-header table, .es-footer table, .es-content, .es-footer, .es-header { width:100%!important; max-width:600px!important } .es-adapt-td { display:block!important; width:100%!important } .adapt-img { width:100%!important; height:auto!important } .es-m-p0 { padding:0px!important } .es-m-p0r { padding-right:0px!important } .es-m-p0l { padding-left:0px!important } .es-m-p0t { padding-top:0px!important } .es-m-p0b { padding-bottom:0!important } .es-m-p20b { padding-bottom:20px!important } .es-mobile-hidden, .es-hidden { display:none!important } tr.es-desk-hidden, td.es-desk-hidden, table.es-desk-hidden { width:auto!important; overflow:visible!important; float:none!important; max-height:inherit!important; line-height:inherit!important } tr.es-desk-hidden { display:table-row!important } table.es-desk-hidden { display:table!important } td.es-desk-menu-hidden { display:table-cell!important } .es-menu td { width:1%!important } table.es-table-not-adapt, .esd-block-html table { width:auto!important } table.es-social { display:inline-block!important } table.es-social td { display:inline-block!important } }</style></head>\r\n"
							+ "<body style=\"width:100%;font-family:arial, 'helvetica neue', helvetica, sans-serif;-webkit-text-size-adjust:100%;-ms-text-size-adjust:100%;padding:0;Margin:0\"><div class=\"es-wrapper-color\" style=\"background-color:#EFEFEF\"> <!--[if gte mso 9]><v:background xmlns:v=\"urn:schemas-microsoft-com:vml\" fill=\"t\"> <v:fill type=\"tile\" color=\"#efefef\"></v:fill> </v:background><![endif]--><table class=\"es-wrapper\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;padding:0;Margin:0;width:100%;height:100%;background-repeat:repeat;background-position:center top\"><tr style=\"border-collapse:collapse\"><td valign=\"top\" style=\"padding:0;Margin:0\"><table cellpadding=\"0\" cellspacing=\"0\" class=\"es-content\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%\"><tr style=\"border-collapse:collapse\"><td class=\"es-adaptive\" align=\"center\" style=\"padding:0;Margin:0\"><table class=\"es-content-body\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:transparent;width:600px\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\"><tr style=\"border-collapse:collapse\"><td align=\"left\" style=\"padding:10px;Margin:0\"> <!--[if mso]><table style=\"width:580px\"><tr><td style=\"width:280px\" valign=\"top\"><![endif]--><table class=\"es-left\" cellspacing=\"0\" cellpadding=\"0\" align=\"left\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;float:left\"><tr style=\"border-collapse:collapse\"><td align=\"left\" style=\"padding:0;Margin:0;width:280px\"><table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\"><tr style=\"border-collapse:collapse\"><td align=\"center\" style=\"padding:0;Margin:0;display:none\"></td>\r\n"
							+ "</tr></table></td></tr></table> <!--[if mso]></td><td style=\"width:20px\"></td><td style=\"width:280px\" valign=\"top\"><![endif]--><table class=\"es-right\" cellspacing=\"0\" cellpadding=\"0\" align=\"right\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;float:right\"><tr style=\"border-collapse:collapse\"><td align=\"left\" style=\"padding:0;Margin:0;width:280px\"><table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\"><tr style=\"border-collapse:collapse\"><td align=\"center\" style=\"padding:0;Margin:0;display:none\"></td></tr></table></td></tr></table> <!--[if mso]></td></tr></table><![endif]--></td></tr></table></td>\r\n"
							+ "</tr></table><table cellpadding=\"0\" cellspacing=\"0\" class=\"es-header\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%;background-color:transparent;background-repeat:repeat;background-position:center top\"><tr style=\"border-collapse:collapse\"><td align=\"center\" style=\"padding:0;Margin:0\"><table class=\"es-header-body\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#E6EBEF;width:600px\"><tr style=\"border-collapse:collapse\"><td align=\"left\" bgcolor=\"#ffffff\" style=\"padding:0;Margin:0;background-color:#ffffff\"><table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\"><tr style=\"border-collapse:collapse\"><td valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;width:600px\"><table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\"><tr style=\"border-collapse:collapse\"><td align=\"center\" style=\"padding:0;Margin:0;font-size:0px\"><a href=\"https://app.zanflow.com\" target=\"_blank\" style=\"-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;text-decoration:none;color:#677D9E;font-size:14px\"><img src=\"https://app.zanflow.com/zanflow/signin_logo.png\" alt width=\"259\" style=\"display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic\"></a></td>\r\n"
							+ "</tr></table></td></tr></table></td></tr></table></td>\r\n"
							+ "</tr></table><table class=\"es-content\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%\"><tr style=\"border-collapse:collapse\"><td align=\"center\" style=\"padding:0;Margin:0\"><table class=\"es-content-body\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#ffffff\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#FFFFFF;width:600px\"><tr style=\"border-collapse:collapse\"><td align=\"left\" style=\"Margin:0;padding-top:15px;padding-left:30px;padding-right:30px;padding-bottom:40px\"><table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\"><tr style=\"border-collapse:collapse\"><td valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;width:540px\"><table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\"><tr style=\"border-collapse:collapse\"><td align=\"left\" style=\"padding:0;Margin:0\"><h3 style=\"Margin:0;line-height:24px;mso-line-height-rule:exactly;font-family:arial, 'helvetica neue', helvetica, sans-serif;font-size:20px;font-style:normal;font-weight:normal;color:#666666\">Welcome,</h3>\r\n"
							+ "</td></tr><tr style=\"border-collapse:collapse\"><td align=\"left\" style=\"padding:0;Margin:0;padding-top:15px\"><p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:arial, 'helvetica neue', helvetica, sans-serif;line-height:21px;color:#999999;font-size:14px\">You are invited for Zanflow. Please find your login credentials below<br></p></td>\r\n"
							+ "</tr><tr style=\"border-collapse:collapse\"><td align=\"center\" bgcolor=\"#ffffff\" style=\"padding:0;Margin:0\"><h1 style=\"Margin:0;line-height:17px;mso-line-height-rule:exactly;font-family:arial, 'helvetica neue', helvetica, sans-serif;font-size:14px;font-style:normal;font-weight:normal;color:#333333;text-align:left\"><strong>User id </strong>- {userid}<br><strong>Password</strong> - {password}<br><strong>App link </strong>- <a target=\"_blank\" href=\"https://app.zanflow.com\" style=\"-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;text-decoration:underline;color:#3E8EB8;font-size:14px;text-align:left\">https://app.zanflow.com</a></h1></td>\r\n"
							+ "</tr><tr style=\"border-collapse:collapse\"><td align=\"left\" style=\"padding:0;Margin:0;padding-top:25px\"><p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:arial, 'helvetica neue', helvetica, sans-serif;line-height:21px;color:#999999;font-size:14px\">Please change your password after login. <br><br>Contact us at support@zanflow.com for&nbsp;support</p></td></tr><tr style=\"border-collapse:collapse\"><td align=\"left\" style=\"padding:0;Margin:0;padding-top:15px\"><p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:arial, 'helvetica neue', helvetica, sans-serif;line-height:21px;color:#999999;font-size:14px\">Regards,</p>\r\n"
							+ "<p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:arial, 'helvetica neue', helvetica, sans-serif;line-height:21px;color:#999999;font-size:14px\">The Zanflow team</p></td></tr></table></td></tr></table></td></tr></table></td>\r\n"
							+ "</tr></table><table cellpadding=\"0\" cellspacing=\"0\" class=\"es-footer\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%;background-color:transparent;background-repeat:repeat;background-position:center top\"><tr style=\"border-collapse:collapse\"><td align=\"center\" style=\"padding:0;Margin:0\"><table class=\"es-footer-body\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#E6EBEF;width:600px\"><tr style=\"border-collapse:collapse\"><td align=\"left\" style=\"Margin:0;padding-top:20px;padding-bottom:20px;padding-left:20px;padding-right:20px\"><table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\"><tr style=\"border-collapse:collapse\"><td valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;width:560px\"><table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\"><tr style=\"border-collapse:collapse\"><td align=\"center\" style=\"Margin:0;padding-bottom:10px;padding-top:15px;padding-left:15px;padding-right:15px\"><p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:arial, 'helvetica neue', helvetica, sans-serif;line-height:20px;color:#999999;font-size:13px\">You are receiving this email because you have been invited for Zanflow. If you didn't expect this email, someone might have entered your address by mistake.</p>\r\n"
							+ "</td></tr><tr style=\"border-collapse:collapse\"><td align=\"center\" style=\"padding:0;Margin:0;padding-top:15px;font-size:0\"><table class=\"es-table-not-adapt es-social\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\"><tr style=\"border-collapse:collapse\"><td valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;padding-right:10px\"><a target=\"_blank\" href=\"https://twitter.com/zanflow1?lang=en\" style=\"-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;text-decoration:none;color:#999999;font-size:13px\"><img title=\"Twitter\" src=\"https://uhvgvh.stripocdn.email/content/assets/img/social-icons/logo-black/twitter-logo-black.png\" alt=\"Tw\" width=\"32\" style=\"display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic\"></a></td>\r\n"
							+ "<td valign=\"top\" align=\"center\" style=\"padding:0;Margin:0\"><a target=\"_blank\" href=\"https://www.youtube.com/channel/UCvYhuKGQJ0nQEZYUXlAGcXw\" style=\"-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;text-decoration:none;color:#999999;font-size:13px\"><img title=\"Youtube\" src=\"https://uhvgvh.stripocdn.email/content/assets/img/social-icons/logo-black/youtube-logo-black.png\" alt=\"Yt\" width=\"32\" style=\"display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic\"></a></td></tr></table></td></tr></table></td></tr></table></td></tr></table></td>\r\n"
							+ "</tr></table><table class=\"es-content\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%\"><tr style=\"border-collapse:collapse\"><td align=\"center\" style=\"padding:0;Margin:0\"><table class=\"es-content-body\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:transparent;width:600px\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\"><tr style=\"border-collapse:collapse\"><td align=\"left\" style=\"Margin:0;padding-left:20px;padding-right:20px;padding-top:30px;padding-bottom:30px\"><table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\"><tr style=\"border-collapse:collapse\"><td valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;width:560px\"><table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\"><tr style=\"border-collapse:collapse\"><td align=\"center\" style=\"padding:0;Margin:0;display:none\"></td>\r\n"
							+ "</tr></table></td></tr></table></td></tr></table></td></tr></table></td></tr></table></div></body></html>";
					
					bodyMsg = bodyMsg.replace("{userid}", objUserDTO.getEmailId());
					bodyMsg = bodyMsg.replace("{password}", password);
					
					Notifier.getNotifier().sendEmail(objUserDTO.getEmailId(),"", "You are invited to Zanflow", bodyMsg);
					//System.out.println(bodyMsg);
					objUserMgmtDAO.commit();
				}
				else
				{
					responseStr="Insufficient information";
				}
			}
			catch(Exception e) 
			{
				if(objUserMgmtDAO!=null && objUserMgmtDAO.isActive())
				{
					objUserMgmtDAO.rollback();
				}
				responseStr="ERROR#"+e.getMessage();
				e.printStackTrace();
			}
			finally
			{
				if(objUserMgmtDAO!=null)
				{
					try {
						objUserMgmtDAO.close();
					} catch (ApplicationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		else
		{
			responseStr="Insufficient information";
		}
		
		System.out.println(request.getAttribute("request_uri") + " ended , time taken " + (System.currentTimeMillis() - (long) request.getAttribute("start_time")) + " milliseconds");

		return responseStr;
	}
	
	@RequestMapping(value="/forgotPassword", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public String forgotPassword(HttpServletRequest request, HttpServletResponse response,@RequestBody UserDTO objUserDTO) throws Exception{
		String responseStr="Password sent to email";

		UserMgmtDAO objUserMgmtDAO = null;
		if(objUserDTO!=null)
		{
			//System.out.println("UserDTO#"+objUserDTO);
			try {

				if(objUserDTO.getUserId()!=null)
				{
					objUserMgmtDAO=new UserMgmtDAO(Constants.DB_PUNIT);
					User objUser=objUserMgmtDAO.findUser(objUserDTO.getUserId());
					if(objUser == null) {
						responseStr="User Id does not exist";
					}else {

					String bodyMsg = "";
					
					bodyMsg = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"><html xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:o=\"urn:schemas-microsoft-com:office:office\" style=\"width:100%;font-family:arial, 'helvetica neue', helvetica, sans-serif;-webkit-text-size-adjust:100%;-ms-text-size-adjust:100%;padding:0;Margin:0\"><head><meta charset=\"UTF-8\"><meta content=\"width=device-width, initial-scale=1\" name=\"viewport\"><meta name=\"x-apple-disable-message-reformatting\"><meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\"><meta content=\"telephone=no\" name=\"format-detection\"><title>Copy of New email template 2021-11-15</title> <!--[if (mso 16)]><style type=\"text/css\"> a {text-decoration: none;} </style><![endif]--> <!--[if gte mso 9]><style>sup { font-size: 100% !important; }</style><![endif]--> <!--[if gte mso 9]><xml> <o:OfficeDocumentSettings> <o:AllowPNG></o:AllowPNG> <o:PixelsPerInch>96</o:PixelsPerInch> </o:OfficeDocumentSettings> </xml><![endif]--><style type=\"text/css\">#outlook a { padding:0;}.ExternalClass { width:100%;}.ExternalClass,.ExternalClass p,.ExternalClass span,.ExternalClass font,.ExternalClass td,.ExternalClass div { line-height:100%;}.es-button { mso-style-priority:100!important; text-decoration:none!important;}a[x-apple-data-detectors] { color:inherit!important; text-decoration:none!important; font-size:inherit!important; font-family:inherit!important; font-weight:inherit!important; line-height:inherit!important;}.es-desk-hidden { display:none; float:left; overflow:hidden; width:0; max-height:0; line-height:0; mso-hide:all;}[data-ogsb] .es-button { border-width:0!important; padding:10px 20px 10px 20px!important;}@media only screen and (max-width:600px) {p, ul li, ol li, a { line-height:150%!important } h1, h2, h3, h1 a, h2 a, h3 a { line-height:120%!important } h1 { font-size:42px!important; text-align:left } h2 { font-size:26px!important; text-align:left } h3 { font-size:20px!important; text-align:left } h1 a { text-align:left } .es-header-body h1 a, .es-content-body h1 a, .es-footer-body h1 a { font-size:42px!important } h2 a { text-align:left } .es-header-body h2 a, .es-content-body h2 a, .es-footer-body h2 a { font-size:26px!important } h3 a { text-align:left } .es-header-body h3 a, .es-content-body h3 a, .es-footer-body h3 a { font-size:20px!important } .es-menu td a { font-size:14px!important } .es-header-body p, .es-header-body ul li, .es-header-body ol li, .es-header-body a { font-size:14px!important } .es-content-body p, .es-content-body ul li, .es-content-body ol li, .es-content-body a { font-size:14px!important } .es-footer-body p, .es-footer-body ul li, .es-footer-body ol li, .es-footer-body a { font-size:14px!important } .es-infoblock p, .es-infoblock ul li, .es-infoblock ol li, .es-infoblock a { font-size:12px!important } *[class=\"gmail-fix\"] { display:none!important } .es-m-txt-c, .es-m-txt-c h1, .es-m-txt-c h2, .es-m-txt-c h3 { text-align:center!important } .es-m-txt-r, .es-m-txt-r h1, .es-m-txt-r h2, .es-m-txt-r h3 { text-align:right!important } .es-m-txt-l, .es-m-txt-l h1, .es-m-txt-l h2, .es-m-txt-l h3 { text-align:left!important } .es-m-txt-r img, .es-m-txt-c img, .es-m-txt-l img { display:inline!important } .es-button-border { display:block!important } a.es-button, button.es-button { font-size:20px!important; display:block!important; border-left-width:0px!important; border-right-width:0px!important } .es-btn-fw { border-width:10px 0px!important; text-align:center!important } .es-adaptive table, .es-btn-fw, .es-btn-fw-brdr, .es-left, .es-right { width:100%!important } .es-content table, .es-header table, .es-footer table, .es-content, .es-footer, .es-header { width:100%!important; max-width:600px!important } .es-adapt-td { display:block!important; width:100%!important } .adapt-img { width:100%!important; height:auto!important } .es-m-p0 { padding:0px!important } .es-m-p0r { padding-right:0px!important } .es-m-p0l { padding-left:0px!important } .es-m-p0t { padding-top:0px!important } .es-m-p0b { padding-bottom:0!important } .es-m-p20b { padding-bottom:20px!important } .es-mobile-hidden, .es-hidden { display:none!important } tr.es-desk-hidden, td.es-desk-hidden, table.es-desk-hidden { width:auto!important; overflow:visible!important; float:none!important; max-height:inherit!important; line-height:inherit!important } tr.es-desk-hidden { display:table-row!important } table.es-desk-hidden { display:table!important } td.es-desk-menu-hidden { display:table-cell!important } .es-menu td { width:1%!important } table.es-table-not-adapt, .esd-block-html table { width:auto!important } table.es-social { display:inline-block!important } table.es-social td { display:inline-block!important } }</style></head>\r\n"
							+ "<body style=\"width:100%;font-family:arial, 'helvetica neue', helvetica, sans-serif;-webkit-text-size-adjust:100%;-ms-text-size-adjust:100%;padding:0;Margin:0\"><div class=\"es-wrapper-color\" style=\"background-color:#EFEFEF\"> <!--[if gte mso 9]><v:background xmlns:v=\"urn:schemas-microsoft-com:vml\" fill=\"t\"> <v:fill type=\"tile\" color=\"#efefef\"></v:fill> </v:background><![endif]--><table class=\"es-wrapper\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;padding:0;Margin:0;width:100%;height:100%;background-repeat:repeat;background-position:center top\"><tr style=\"border-collapse:collapse\"><td valign=\"top\" style=\"padding:0;Margin:0\"><table cellpadding=\"0\" cellspacing=\"0\" class=\"es-content\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%\"><tr style=\"border-collapse:collapse\"><td class=\"es-adaptive\" align=\"center\" style=\"padding:0;Margin:0\"><table class=\"es-content-body\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:transparent;width:600px\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\"><tr style=\"border-collapse:collapse\"><td align=\"left\" style=\"padding:10px;Margin:0\"> <!--[if mso]><table style=\"width:580px\"><tr><td style=\"width:280px\" valign=\"top\"><![endif]--><table class=\"es-left\" cellspacing=\"0\" cellpadding=\"0\" align=\"left\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;float:left\"><tr style=\"border-collapse:collapse\"><td align=\"left\" style=\"padding:0;Margin:0;width:280px\"><table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\"><tr style=\"border-collapse:collapse\"><td align=\"center\" style=\"padding:0;Margin:0;display:none\"></td>\r\n"
							+ "</tr></table></td></tr></table> <!--[if mso]></td><td style=\"width:20px\"></td><td style=\"width:280px\" valign=\"top\"><![endif]--><table class=\"es-right\" cellspacing=\"0\" cellpadding=\"0\" align=\"right\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;float:right\"><tr style=\"border-collapse:collapse\"><td align=\"left\" style=\"padding:0;Margin:0;width:280px\"><table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\"><tr style=\"border-collapse:collapse\"><td align=\"center\" style=\"padding:0;Margin:0;display:none\"></td></tr></table></td></tr></table> <!--[if mso]></td></tr></table><![endif]--></td></tr></table></td>\r\n"
							+ "</tr></table><table cellpadding=\"0\" cellspacing=\"0\" class=\"es-header\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%;background-color:transparent;background-repeat:repeat;background-position:center top\"><tr style=\"border-collapse:collapse\"><td align=\"center\" style=\"padding:0;Margin:0\"><table class=\"es-header-body\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#E6EBEF;width:600px\"><tr style=\"border-collapse:collapse\"><td align=\"left\" bgcolor=\"#ffffff\" style=\"padding:0;Margin:0;background-color:#ffffff\"><table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\"><tr style=\"border-collapse:collapse\"><td valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;width:600px\"><table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\"><tr style=\"border-collapse:collapse\"><td align=\"center\" style=\"padding:0;Margin:0;font-size:0px\"><a href=\"https://app.zanflow.com\" target=\"_blank\" style=\"-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;text-decoration:none;color:#677D9E;font-size:14px\"><img src=\"https://app.zanflow.com/zanflow/signin_logo.png\" alt width=\"259\" style=\"display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic\"></a></td>\r\n"
							+ "</tr></table></td></tr></table></td></tr></table></td>\r\n"
							+ "</tr></table><table class=\"es-content\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%\"><tr style=\"border-collapse:collapse\"><td align=\"center\" style=\"padding:0;Margin:0\"><table class=\"es-content-body\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#ffffff\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#FFFFFF;width:600px\"><tr style=\"border-collapse:collapse\"><td align=\"left\" style=\"Margin:0;padding-top:15px;padding-left:30px;padding-right:30px;padding-bottom:40px\"><table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\"><tr style=\"border-collapse:collapse\"><td valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;width:540px\"><table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\"><tr style=\"border-collapse:collapse\"><td align=\"left\" style=\"padding:0;Margin:0\"><h3 style=\"Margin:0;line-height:24px;mso-line-height-rule:exactly;font-family:arial, 'helvetica neue', helvetica, sans-serif;font-size:20px;font-style:normal;font-weight:normal;color:#666666\">Welcome,</h3>\r\n"
							+ "</td></tr><tr style=\"border-collapse:collapse\"><td align=\"left\" style=\"padding:0;Margin:0;padding-top:15px\"><p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:arial, 'helvetica neue', helvetica, sans-serif;line-height:21px;color:#999999;font-size:14px\">Please find your login credentials below<br></p></td>\r\n"
							+ "</tr><tr style=\"border-collapse:collapse\"><td align=\"center\" bgcolor=\"#ffffff\" style=\"padding:0;Margin:0\"><h1 style=\"Margin:0;line-height:17px;mso-line-height-rule:exactly;font-family:arial, 'helvetica neue', helvetica, sans-serif;font-size:14px;font-style:normal;font-weight:normal;color:#333333;text-align:left\"><strong>User id </strong>- {userid}<br><strong>Password</strong> - {password}<br><strong>App link </strong>- <a target=\"_blank\" href=\"https://app.zanflow.com\" style=\"-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;text-decoration:underline;color:#3E8EB8;font-size:14px;text-align:left\">https://app.zanflow.com</a></h1></td>\r\n"
							+ "</tr><tr style=\"border-collapse:collapse\"><td align=\"left\" style=\"padding:0;Margin:0;padding-top:25px\"><p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:arial, 'helvetica neue', helvetica, sans-serif;line-height:21px;color:#999999;font-size:14px\">Please change your password after login. <br><br>Contact us at support@zanflow.com for&nbsp;support</p></td></tr><tr style=\"border-collapse:collapse\"><td align=\"left\" style=\"padding:0;Margin:0;padding-top:15px\"><p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:arial, 'helvetica neue', helvetica, sans-serif;line-height:21px;color:#999999;font-size:14px\">Regards,</p>\r\n"
							+ "<p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:arial, 'helvetica neue', helvetica, sans-serif;line-height:21px;color:#999999;font-size:14px\">The Zanflow team</p></td></tr></table></td></tr></table></td></tr></table></td>\r\n"
							+ "</tr></table><table cellpadding=\"0\" cellspacing=\"0\" class=\"es-footer\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%;background-color:transparent;background-repeat:repeat;background-position:center top\"><tr style=\"border-collapse:collapse\"><td align=\"center\" style=\"padding:0;Margin:0\"><table class=\"es-footer-body\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#E6EBEF;width:600px\"><tr style=\"border-collapse:collapse\"><td align=\"left\" style=\"Margin:0;padding-top:20px;padding-bottom:20px;padding-left:20px;padding-right:20px\"><table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\"><tr style=\"border-collapse:collapse\"><td valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;width:560px\"><table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\"><tr style=\"border-collapse:collapse\"><td align=\"center\" style=\"Margin:0;padding-bottom:10px;padding-top:15px;padding-left:15px;padding-right:15px\"><p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:arial, 'helvetica neue', helvetica, sans-serif;line-height:20px;color:#999999;font-size:13px\">You are receiving this email because you have requested forgot password for Zanflow.</p>\r\n"
							+ "</td></tr><tr style=\"border-collapse:collapse\"><td align=\"center\" style=\"padding:0;Margin:0;padding-top:15px;font-size:0\"><table class=\"es-table-not-adapt es-social\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\"><tr style=\"border-collapse:collapse\"><td valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;padding-right:10px\"><a target=\"_blank\" href=\"https://twitter.com/zanflow1?lang=en\" style=\"-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;text-decoration:none;color:#999999;font-size:13px\"><img title=\"Twitter\" src=\"https://uhvgvh.stripocdn.email/content/assets/img/social-icons/logo-black/twitter-logo-black.png\" alt=\"Tw\" width=\"32\" style=\"display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic\"></a></td>\r\n"
							+ "<td valign=\"top\" align=\"center\" style=\"padding:0;Margin:0\"><a target=\"_blank\" href=\"https://www.youtube.com/channel/UCvYhuKGQJ0nQEZYUXlAGcXw\" style=\"-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;text-decoration:none;color:#999999;font-size:13px\"><img title=\"Youtube\" src=\"https://uhvgvh.stripocdn.email/content/assets/img/social-icons/logo-black/youtube-logo-black.png\" alt=\"Yt\" width=\"32\" style=\"display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic\"></a></td></tr></table></td></tr></table></td></tr></table></td></tr></table></td>\r\n"
							+ "</tr></table><table class=\"es-content\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%\"><tr style=\"border-collapse:collapse\"><td align=\"center\" style=\"padding:0;Margin:0\"><table class=\"es-content-body\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:transparent;width:600px\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\"><tr style=\"border-collapse:collapse\"><td align=\"left\" style=\"Margin:0;padding-left:20px;padding-right:20px;padding-top:30px;padding-bottom:30px\"><table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\"><tr style=\"border-collapse:collapse\"><td valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;width:560px\"><table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\"><tr style=\"border-collapse:collapse\"><td align=\"center\" style=\"padding:0;Margin:0;display:none\"></td>\r\n"
							+ "</tr></table></td></tr></table></td></tr></table></td></tr></table></td></tr></table></div></body></html>";
					
					bodyMsg = bodyMsg.replace("{userid}", objUserDTO.getUserId());
					bodyMsg = bodyMsg.replace("{password}", objUser.getPassword());
					
					Notifier.getNotifier().sendEmail(objUserDTO.getUserId(),"", "Zanflow - Password reset", bodyMsg);
					//System.out.println(bodyMsg);
					}
					//objUserMgmtDAO.commit();
				}
				else
				{
					responseStr="Insufficient information";
				}
			}
			catch(Exception e) 
			{
				if(objUserMgmtDAO!=null && objUserMgmtDAO.isActive())
				{
					objUserMgmtDAO.rollback();
				}
				responseStr="ERROR#"+e.getMessage();
				e.printStackTrace();
			}
			finally
			{
				if(objUserMgmtDAO!=null)
				{
					try {
						objUserMgmtDAO.close();
					} catch (ApplicationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		else
		{
			responseStr="Insufficient information";
		}

		System.out.println(request.getAttribute("request_uri") + " ended , time taken " + (System.currentTimeMillis() - (long) request.getAttribute("start_time")) + " milliseconds");
		
		return responseStr;
	}
	 
	@RequestMapping(value="/deleteUser/{companyCode}/{userId}",method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	 public String deleteUser(HttpServletRequest request, HttpServletResponse 
			 response,@PathVariable String companyCode,@PathVariable String userId)
	 {
		 String responseStr="user deleted";
		 //System.out.println("deleteRole --> companyCode#" + companyCode+"#userId#"+userId);
		 UserMgmtDAO objUserMgmtDAO = null;
		 
		 try {
			 objUserMgmtDAO=new UserMgmtDAO(Constants.DB_PUNIT);	
			 objUserMgmtDAO.begin();
			objUserMgmtDAO.deleteUser(companyCode, userId);
			objUserMgmtDAO.commit();
		 }
		 catch(Exception e) 
		 {
			 responseStr="ERROR#"+e.getMessage();
			 if(objUserMgmtDAO!=null && objUserMgmtDAO.isActive())
			 {
				 objUserMgmtDAO.rollback();
			 }
			 e.printStackTrace();
		}
		 finally
		 {
			 if(objUserMgmtDAO!=null)
			 {
				 try {
					objUserMgmtDAO.close();
				} catch (ApplicationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 }
		 }
		 
		 System.out.println(request.getAttribute("request_uri") + " ended , time taken " + (System.currentTimeMillis() - (long) request.getAttribute("start_time")) + " milliseconds");
		 return responseStr;
	 }

	@RequestMapping(value="/updateUser", method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
	public String updateUser(HttpServletRequest request, HttpServletResponse 
			response,@RequestBody UserDTO objUserDTO) throws Exception{
	 String responseStr="user updated";
	 
	 UserMgmtDAO objUserMgmtDAO = null;
	 if(objUserDTO!=null)
	 {
		 try {
				
			if(objUserDTO.getUserId()!=null && objUserDTO.getFirstName()!=null && objUserDTO.getPassword()!=null && objUserDTO.getManagerId()!=null)
			{
				 objUserMgmtDAO=new UserMgmtDAO(Constants.DB_PUNIT);
				 User objUser=objUserMgmtDAO.findUser(objUserDTO.getUserId());
				 objUser.setUserId(objUserDTO.getUserId());
				 objUser.setFirstName(objUserDTO.getFirstName());
				 objUser.setLastName(objUserDTO.getLastName());
				 //objUser.setPassword(objUserDTO.getPassword());
				 objUser.setStatus("A");
				 objUser.setUserType(objUserDTO.getUserType());
				 objUser.setCompanyCode(objUserDTO.getCompanyCode());
				 objUser.setEmailId(objUserDTO.getEmailId());
				 objUser.setManagerId(objUserDTO.getManagerId());
				 objUserMgmtDAO.begin();
				 objUserMgmtDAO.updateUser(objUser);
				 objUserMgmtDAO.commit();
			}
			else
			{
				 responseStr="Insufficient information";
			}
		 }
		 catch(Exception e) 
		 {
			 if(objUserMgmtDAO!=null && objUserMgmtDAO.isActive())
			 {
				 objUserMgmtDAO.rollback();
			 }
			 responseStr="ERROR#"+e.getMessage();
			 e.printStackTrace();
		}
		 finally
		 {
			 if(objUserMgmtDAO!=null)
			 {
				 try {
					objUserMgmtDAO.close();
				} catch (ApplicationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 }
		 }
	 }
	 else
	 {
		 responseStr="Insufficient information";
	 }
	 System.out.println(request.getAttribute("request_uri") + " ended , time taken " + (System.currentTimeMillis() - (long) request.getAttribute("start_time")) + " milliseconds");
	 return responseStr;
}

	@RequestMapping(value="/getAllUsers/{companyCode}",method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<User> getUserDetail(HttpServletRequest request, HttpServletResponse 
		 response,@PathVariable String companyCode)
	{
	 //System.out.println("getUserDetail --> companyCode --> " + companyCode);
	 UserMgmtDAO objUserMgmtDAO = null;
	 List<User> userList=null;
	 try {
		 objUserMgmtDAO=new UserMgmtDAO(Constants.DB_PUNIT);	
		 userList=objUserMgmtDAO.getAllUsers(companyCode);
	 }
	 catch(Exception e) {
		 e.printStackTrace();
	}
	 finally
	 {
		 if(objUserMgmtDAO!=null)
		 {
			 try {
				objUserMgmtDAO.close();
			} catch (ApplicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 }
	 }
	 System.out.println(request.getAttribute("request_uri") + " ended , time taken " + (System.currentTimeMillis() - (long) request.getAttribute("start_time")) + " milliseconds");
	 return userList;
	}

	@RequestMapping(value="/addRole", method = RequestMethod.POST)
	public String addRole(HttpServletRequest request, HttpServletResponse 
			response,@RequestBody RoleDTO objRoleDTO) throws Exception{
	 String responseStr="role created";
	 
	 UserMgmtDAO objUserMgmtDAO = null;
	 if(objRoleDTO!=null)
	 {
		 try {
				
			if(objRoleDTO.getRoleId()!=null && objRoleDTO.getRoleName()!=null && objRoleDTO.getCompanyCode()!=null)
			{
				 objUserMgmtDAO=new UserMgmtDAO(Constants.DB_PUNIT);
				 Role objRole=new Role();
				 objRole.setRoleId(objRoleDTO.getRoleId());
				 objRole.setRoleName(objRoleDTO.getRoleId());
				 objRole.setRoleDescription(objRoleDTO.getRoleDescription());
				 objRole.setStatus("A");
				 objRole.setCompanyCode(objRoleDTO.getCompanyCode());
				 objRole.setCreatedBy(objRoleDTO.getCreatedBy());
				 objUserMgmtDAO.begin();
				 objRole=objUserMgmtDAO.createRole(objRole);
				 objUserMgmtDAO.commit();
			}
			else
			{
				 responseStr="Insufficient information";
			}
		 }
		 catch(Exception e) 
		 {
			 if(objUserMgmtDAO!=null && objUserMgmtDAO.isActive())
			 {
				 objUserMgmtDAO.rollback();
			 }
			 responseStr="ERROR#"+e.getMessage();
			 e.printStackTrace();
		}
		 finally
		 {
			 if(objUserMgmtDAO!=null)
			 {
				 try {
					objUserMgmtDAO.close();
				} catch (ApplicationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 }
		 }
	 }
	 else
	 {
		 responseStr="Insufficient information";
	 }
	 System.out.println(request.getAttribute("request_uri") + " ended , time taken " + (System.currentTimeMillis() - (long) request.getAttribute("start_time")) + " milliseconds");
	 return responseStr;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param roleId
	 * @param companyCode
	 * @return
	 * @throws Exception
	 */
	@GetMapping(value="/role/{roleId}")
	public ResponseEntity<ResponseDTO> getRole(HttpServletRequest request, HttpServletResponse 
	response, @PathVariable String roleId, @RequestHeader("x-market") String companyCode) throws Exception {
		try (UserMgmtDAO objUserMgmtDAO = new UserMgmtDAO(Constants.DB_PUNIT)) {
	
			if (companyCode == null) {
				throw new ApplicationException("Insufficient info: Company code is empty");
			}
			Role objRole = objUserMgmtDAO.getRole(companyCode, roleId);
			ResponseDTO dto = new RoleDTO();
			BeanUtils.copyProperties(objRole, dto);
			System.out.println(request.getAttribute("request_uri") + " ended , time taken " + (System.currentTimeMillis() - (long) request.getAttribute("start_time")) + " milliseconds");
			return new ResponseEntity<ResponseDTO>(dto, HttpStatus.OK);
		} catch (ApplicationException e) {
			return new ResponseEntity<ResponseDTO>(new ResponseDTO(e.getMessage(), e.getErrorCode()),
					(e.getErrCode() == 1) ? HttpStatus.NO_CONTENT : HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping(value="/user-roles/")
	public ResponseEntity<List<UserRoleDTO>> getRole(HttpServletRequest request, HttpServletResponse 
	response, @RequestHeader("companycode") String companyCode) throws Exception {
		List<UserRoleDTO> dto = new ArrayList<UserRoleDTO>();
		
		try (UserMgmtDAO objUserMgmtDAO = new UserMgmtDAO(Constants.DB_PUNIT)) {
	
			if (companyCode == null) {
				throw new ApplicationException("Insufficient info: Company code is empty");
			}
			List<Membership>  objRole = objUserMgmtDAO.getCompUsersRole(companyCode);
			//System.out.println(objRole.toString());
			for (Membership membership : objRole) {
				UserRoleDTO usrDto = new UserRoleDTO();
				BeanUtils.copyProperties(membership, usrDto);
				dto.add(usrDto);
				
			}
			//System.out.println(objRole.toString()+"#"+dto.toString());
			System.out.println(request.getAttribute("request_uri") + " ended , time taken " + (System.currentTimeMillis() - (long) request.getAttribute("start_time")) + " milliseconds");
			return new ResponseEntity<List<UserRoleDTO>>(dto, HttpStatus.OK);
		} catch (ApplicationException e) {
			return new ResponseEntity<List<UserRoleDTO>>(
					dto,(e.getErrCode() == 1) ? HttpStatus.NO_CONTENT : HttpStatus.NO_CONTENT);
		}
	}
	
	
	@RequestMapping(value="/roles/{companyCode}",method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<RoleDTO>> getAllRoles(HttpServletRequest request, HttpServletResponse 
	response,  @PathVariable String companyCode) throws Exception {
		List<RoleDTO> dto = new ArrayList<RoleDTO>();
		//System.out.println("companyCode "+companyCode);
		try (UserMgmtDAO objUserMgmtDAO = new UserMgmtDAO(Constants.DB_PUNIT)) {
	
			if (companyCode == null) {
				throw new ApplicationException("Insufficient info: Company code is empty");
			}
			List<Role>  objRole = objUserMgmtDAO.getCompRoles(companyCode);
			//System.out.println(objRole.toString());
			for (Role role : objRole) {
				RoleDTO usrDto = new RoleDTO();
				BeanUtils.copyProperties(role, usrDto);
				dto.add(usrDto);
				
			}
			//System.out.println(objRole.toString()+"#"+dto.toString());
			System.out.println(request.getAttribute("request_uri") + " ended , time taken " + (System.currentTimeMillis() - (long) request.getAttribute("start_time")) + " milliseconds");
			return new ResponseEntity<List<RoleDTO>>(dto, HttpStatus.OK);
		} catch (ApplicationException e) {
			return new ResponseEntity<List<RoleDTO>>(
					dto,(e.getErrCode() == 1) ? HttpStatus.NO_CONTENT : HttpStatus.NO_CONTENT);
		}
	}
	
	@RequestMapping(value="/departments/{companyCode}",method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<DepartmentDTO>> getAllDepartments(HttpServletRequest request, HttpServletResponse 
	response,  @PathVariable String companyCode) throws Exception {
		List<DepartmentDTO> dto = new ArrayList<DepartmentDTO>();
		//System.out.println("companyCode "+companyCode);
		try (UserMgmtDAO objUserMgmtDAO = new UserMgmtDAO(Constants.DB_PUNIT)) {
	
			if (companyCode == null) {
				throw new ApplicationException("Insufficient info: Company code is empty");
			}
			List<Department>  objDepartmet = objUserMgmtDAO.getCompDepartments(companyCode);
			//System.out.println(objRole.toString());
			for (Department department : objDepartmet) {
				DepartmentDTO depDto = new DepartmentDTO();
				BeanUtils.copyProperties(department, depDto);
				dto.add(depDto);
				
			}
			//System.out.println(objRole.toString()+"#"+dto.toString());
			System.out.println(request.getAttribute("request_uri") + " ended , time taken " + (System.currentTimeMillis() - (long) request.getAttribute("start_time")) + " milliseconds");
			return new ResponseEntity<List<DepartmentDTO>>(dto, HttpStatus.OK);
		} catch (ApplicationException e) {
			return new ResponseEntity<List<DepartmentDTO>>(
					dto,(e.getErrCode() == 1) ? HttpStatus.NO_CONTENT : HttpStatus.NO_CONTENT);
		}
	}
	
	@RequestMapping(value="/addDepartment", method = RequestMethod.POST)
	public String addDepartment(HttpServletRequest request, HttpServletResponse 
			response,@RequestBody DepartmentDTO objDepartmentDTO) throws Exception{
	 String responseStr="role created";
	 
	 UserMgmtDAO objUserMgmtDAO = null;
	 if(objDepartmentDTO!=null)
	 {
		 try {
				
			if(objDepartmentDTO.getDepartmentId()!=null && objDepartmentDTO.getDepartmentName()!=null && objDepartmentDTO.getCompanyCode()!=null)
			{
				 objUserMgmtDAO=new UserMgmtDAO(Constants.DB_PUNIT);
				 Department objDepartment=new Department();
				 objDepartment.setDepartmentId(objDepartmentDTO.getDepartmentId());
				 objDepartment.setDepartmentName(objDepartmentDTO.getDepartmentName());
				 objDepartment.setDepartmentHead(objDepartmentDTO.getDepartmentHead());
				 objDepartment.setStatus("A");
				 objDepartment.setCompanyCode(objDepartmentDTO.getCompanyCode());
				 objDepartment.setCreatedBy(objDepartmentDTO.getCreatedBy());
				 objUserMgmtDAO.begin();
				 objDepartment=objUserMgmtDAO.createDepartment(objDepartment);
				 objUserMgmtDAO.commit();
			}
			else
			{
				 responseStr="Insufficient information";
			}
		 }
		 catch(Exception e) 
		 {
			 if(objUserMgmtDAO!=null && objUserMgmtDAO.isActive())
			 {
				 objUserMgmtDAO.rollback();
			 }
			 responseStr="ERROR#"+e.getMessage();
			 e.printStackTrace();
		}
		 finally
		 {
			 if(objUserMgmtDAO!=null)
			 {
				 try {
					objUserMgmtDAO.close();
				} catch (ApplicationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 }
		 }
	 }
	 else
	 {
		 responseStr="Insufficient information";
	 }
	 System.out.println(request.getAttribute("request_uri") + " ended , time taken " + (System.currentTimeMillis() - (long) request.getAttribute("start_time")) + " milliseconds");
	 return responseStr;
	}
	
	
	@RequestMapping(value="/deleteDepartment/{companyCode}/{departmentId}",method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public String deleteDepartment(HttpServletRequest request, HttpServletResponse 
		 response,@PathVariable String companyCode,@PathVariable String departmentId)
	{
	 String responseStr="department deleted";
	 //System.out.println("deleteDepartment --> companyCode#" + companyCode+"#departmentId#"+departmentId);
	 UserMgmtDAO objUserMgmtDAO = null;
	 
	 try {
		 objUserMgmtDAO=new UserMgmtDAO(Constants.DB_PUNIT);	
		 objUserMgmtDAO.begin();
		objUserMgmtDAO.deleteDepartment(companyCode, departmentId);
		objUserMgmtDAO.commit();
	 }
	 catch(Exception e) 
	 {
		 responseStr="ERROR#"+e.getMessage();
		 if(objUserMgmtDAO!=null && objUserMgmtDAO.isActive())
		 {
			 objUserMgmtDAO.rollback();
		 }
		 e.printStackTrace();
	}
	 finally
	 {
		 if(objUserMgmtDAO!=null)
		 {
			 try {
				objUserMgmtDAO.close();
			} catch (ApplicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 }
	 }
	 System.out.println(request.getAttribute("request_uri") + " ended , time taken " + (System.currentTimeMillis() - (long) request.getAttribute("start_time")) + " milliseconds");
	 return responseStr;
	}
	
	@RequestMapping(value="/addLocation", method = RequestMethod.POST)
	public String addLocation(HttpServletRequest request, HttpServletResponse 
			response,@RequestBody LocationDTO objLocationDTO) throws Exception{
	 String responseStr="role created";
	 
	 UserMgmtDAO objUserMgmtDAO = null;
	 if(objLocationDTO!=null)
	 {
		 try {
				
			if(objLocationDTO.getLocationId()!=null && objLocationDTO.getLocationName()!=null && objLocationDTO.getCompanyCode()!=null)
			{
				 objUserMgmtDAO=new UserMgmtDAO(Constants.DB_PUNIT);
				 Location objLocation=new Location();
				 objLocation.setLocationId(objLocationDTO.getLocationId());
				 objLocation.setLocationName(objLocationDTO.getLocationName());
				 objLocation.setLocationHead(objLocationDTO.getLocationHead());
				 objLocation.setStatus("A");
				 objLocation.setCompanyCode(objLocationDTO.getCompanyCode());
				 objLocation.setCreatedBy(objLocationDTO.getCreatedBy());
				 objUserMgmtDAO.begin();
				 objLocation=objUserMgmtDAO.createLocation(objLocation);
				 objUserMgmtDAO.commit();
			}
			else
			{
				 responseStr="Insufficient information";
			}
		 }
		 catch(Exception e) 
		 {
			 if(objUserMgmtDAO!=null && objUserMgmtDAO.isActive())
			 {
				 objUserMgmtDAO.rollback();
			 }
			 responseStr="ERROR#"+e.getMessage();
			 e.printStackTrace();
		}
		 finally
		 {
			 if(objUserMgmtDAO!=null)
			 {
				 try {
					objUserMgmtDAO.close();
				} catch (ApplicationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 }
		 }
	 }
	 else
	 {
		 responseStr="Insufficient information";
	 }
	 System.out.println(request.getAttribute("request_uri") + " ended , time taken " + (System.currentTimeMillis() - (long) request.getAttribute("start_time")) + " milliseconds");
	 return responseStr;
	}
	
	@RequestMapping(value="/deleteLocation/{companyCode}/{locationId}",method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public String deleteLocation(HttpServletRequest request, HttpServletResponse 
		 response,@PathVariable String companyCode,@PathVariable String locationId)
	{
	 String responseStr="location deleted";
	 //System.out.println("deleteLocation --> companyCode#" + companyCode+"#locationId#"+locationId);
	 UserMgmtDAO objUserMgmtDAO = null;
	 
	 try {
		 objUserMgmtDAO=new UserMgmtDAO(Constants.DB_PUNIT);	
		 objUserMgmtDAO.begin();
		 objUserMgmtDAO.deleteLocation(companyCode, locationId);
		 objUserMgmtDAO.commit();
	 }
	 catch(Exception e) 
	 {
		 responseStr="ERROR#"+e.getMessage();
		 if(objUserMgmtDAO!=null && objUserMgmtDAO.isActive())
		 {
			 objUserMgmtDAO.rollback();
		 }
		 e.printStackTrace();
	}
	 finally
	 {
		 if(objUserMgmtDAO!=null)
		 {
			 try {
				objUserMgmtDAO.close();
			} catch (ApplicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 }
	 }
	 System.out.println(request.getAttribute("request_uri") + " ended , time taken " + (System.currentTimeMillis() - (long) request.getAttribute("start_time")) + " milliseconds");
	 return responseStr;
	}
	
	@RequestMapping(value="/locations/{companyCode}",method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<LocationDTO>> getAllLocations(HttpServletRequest request, HttpServletResponse 
	response,  @PathVariable String companyCode) throws Exception {
		List<LocationDTO> dto = new ArrayList<LocationDTO>();
		//System.out.println("companyCode "+companyCode);
		try (UserMgmtDAO objUserMgmtDAO = new UserMgmtDAO(Constants.DB_PUNIT)) {
	
			if (companyCode == null) {
				throw new ApplicationException("Insufficient info: Company code is empty");
			}
			List<Location>  objLocation = objUserMgmtDAO.getCompLocations(companyCode);
			//System.out.println(objRole.toString());
			for (Location location : objLocation) {
				LocationDTO locationDto = new LocationDTO();
				BeanUtils.copyProperties(location, locationDto);
				dto.add(locationDto);
				
			}
			//System.out.println(objRole.toString()+"#"+dto.toString());
			System.out.println(request.getAttribute("request_uri") + " ended , time taken " + (System.currentTimeMillis() - (long) request.getAttribute("start_time")) + " milliseconds");
			return new ResponseEntity<List<LocationDTO>>(dto, HttpStatus.OK);
		} catch (ApplicationException e) {
			return new ResponseEntity<List<LocationDTO>>(
					dto,(e.getErrCode() == 1) ? HttpStatus.NO_CONTENT : HttpStatus.NO_CONTENT);
		}
	}
	
	@GetMapping(value="/user-roles/{roleId}/{companyCode}")
	public ResponseEntity<List<UserRoleDTO>> getAllRoles(HttpServletRequest request, HttpServletResponse 
	response, @PathVariable String roleId ,@PathVariable String companyCode) throws Exception {
		List<UserRoleDTO> dto = new ArrayList<UserRoleDTO>();
		
		try (UserMgmtDAO objUserMgmtDAO = new UserMgmtDAO(Constants.DB_PUNIT)) {
	
			if (companyCode == null) {
				throw new ApplicationException("Insufficient info: Company code is empty");
			}
			List<Membership>  objRole = objUserMgmtDAO.getCompUsersRole(roleId,companyCode);
			//System.out.println(objRole.toString());
			for (Membership membership : objRole) {
				UserRoleDTO usrDto = new UserRoleDTO();
				BeanUtils.copyProperties(membership, usrDto);
				dto.add(usrDto);
				
			}
			//System.out.println(objRole.toString()+"#"+dto.toString());
			System.out.println(request.getAttribute("request_uri") + " ended , time taken " + (System.currentTimeMillis() - (long) request.getAttribute("start_time")) + " milliseconds");
			return new ResponseEntity<List<UserRoleDTO>>(dto, HttpStatus.OK);
		} catch (ApplicationException e) {
			return new ResponseEntity<List<UserRoleDTO>>(
					dto,(e.getErrCode() == 1) ? HttpStatus.NO_CONTENT : HttpStatus.NO_CONTENT);
		}
	}
	@RequestMapping(value="/deleteRole/{companyCode}/{roleId}",method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public String deleteRole(HttpServletRequest request, HttpServletResponse 
		 response,@PathVariable String companyCode,@PathVariable String roleId)
	{
	 String responseStr="role deleted";
	 //System.out.println("deleteRole --> companyCode#" + companyCode+"#roleId#"+roleId);
	 UserMgmtDAO objUserMgmtDAO = null;
	 
	 try {
		 objUserMgmtDAO=new UserMgmtDAO(Constants.DB_PUNIT);	
		 objUserMgmtDAO.begin();
		objUserMgmtDAO.deleteRole(companyCode, roleId);
		objUserMgmtDAO.commit();
	 }
	 catch(Exception e) 
	 {
		 responseStr="ERROR#"+e.getMessage();
		 if(objUserMgmtDAO!=null && objUserMgmtDAO.isActive())
		 {
			 objUserMgmtDAO.rollback();
		 }
		 e.printStackTrace();
	}
	 finally
	 {
		 if(objUserMgmtDAO!=null)
		 {
			 try {
				objUserMgmtDAO.close();
			} catch (ApplicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 }
	 }
	 System.out.println(request.getAttribute("request_uri") + " ended , time taken " + (System.currentTimeMillis() - (long) request.getAttribute("start_time")) + " milliseconds");
	 return responseStr;
	}

	@RequestMapping(value="/addUserRole", method = RequestMethod.POST)
	public ResponseEntity<ResponseDTO> addUserRole(HttpServletRequest request, HttpServletResponse 
			response,@RequestBody UserRoleDTO objUserDTO) throws Exception{
	 String responseStr="user role added";
	 ResponseDTO dto = new ResponseDTO();
	 UserMgmtDAO objUserMgmtDAO = null;
	 if(objUserDTO!=null)
	 {
		 try {
				
			if(objUserDTO.getUserId()!=null && objUserDTO.getRoleId()!=null && objUserDTO.getAccessGivenBy()!=null)
			{
				 objUserMgmtDAO=new UserMgmtDAO(Constants.DB_PUNIT);
				 Membership objMembership=new Membership();
				 objMembership.setUserId(objUserDTO.getUserId());
				 objMembership.setAccessGivenBy(objUserDTO.getAccessGivenBy());
				 objMembership.setRoleId(objUserDTO.getRoleId());
				 objMembership.setCompanyCode(objUserDTO.getCompanyCode());
				 objMembership.setStatus("A");
				 objUserMgmtDAO.begin();
				 objMembership=objUserMgmtDAO.createUserRole(objMembership);
				 objUserMgmtDAO.commit();
				 dto.setResponsMsg("user role added");
			}
			else
			{
				dto.setResponseCode("500");
				dto.setResponsMsg("Insufficient information");
			}
		 }
		 catch(Exception e) 
		 {
			 if(objUserMgmtDAO!=null && objUserMgmtDAO.isActive())
			 {
				 objUserMgmtDAO.rollback();
			 }
			 responseStr="ERROR#"+e.getMessage();
			 e.printStackTrace();
		}
		 finally
		 {
			 if(objUserMgmtDAO!=null)
			 {
				 try {
					objUserMgmtDAO.close();
				} catch (ApplicationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 }
		 }
	 }
	 else
	 {
		 dto.setResponseCode("500");
		dto.setResponsMsg("Insufficient information");
	 }
	 System.out.println(request.getAttribute("request_uri") + " ended , time taken " + (System.currentTimeMillis() - (long) request.getAttribute("start_time")) + " milliseconds");
	 return new ResponseEntity<ResponseDTO>(dto, HttpStatus.OK);
	}

	@RequestMapping(value="/deleteUserRole/{roleId}/{userId}",method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseDTO> deleteUserRole(HttpServletRequest request, HttpServletResponse 
		 response,@PathVariable String roleId,@PathVariable String userId)
	{
	 String responseStr="user Role deleted";
	 ResponseDTO dto = new ResponseDTO();
	 //System.out.println("deleteUserRole --> roleId#" + roleId+"#userId#"+userId);
	 UserMgmtDAO objUserMgmtDAO = null;
	 
	 try {
		 objUserMgmtDAO=new UserMgmtDAO(Constants.DB_PUNIT);	
		 objUserMgmtDAO.begin();
		objUserMgmtDAO.deleteUserRole(roleId, userId);
		objUserMgmtDAO.commit();
		dto.setResponsMsg(responseStr);
	 }
	 catch(Exception e) 
	 {
		 responseStr="ERROR#"+e.getMessage();
		 if(objUserMgmtDAO!=null && objUserMgmtDAO.isActive())
		 {
			 objUserMgmtDAO.rollback();
		 }
		 e.printStackTrace();
	}
	 finally
	 {
		 if(objUserMgmtDAO!=null)
		 {
			 try {
				objUserMgmtDAO.close();
			} catch (ApplicationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 }
	 }
	 System.out.println(request.getAttribute("request_uri") + " ended , time taken " + (System.currentTimeMillis() - (long) request.getAttribute("start_time")) + " milliseconds");
	 return new ResponseEntity<ResponseDTO>(dto, HttpStatus.OK);
	}
	
	@RequestMapping(value="/updateUserRole", method = RequestMethod.POST)
	public String updateUserRole(HttpServletRequest request, HttpServletResponse 
		response,@RequestBody UserRoleDTO objUserDTO) throws Exception{
			 String responseStr="user role updated";
			 
			 UserMgmtDAO objUserMgmtDAO = null;
			 if(objUserDTO!=null)
			 {
				 try {
						
					if(objUserDTO.getUserId()!=null && objUserDTO.getRoleId()!=null && objUserDTO.getAccessGivenBy()!=null)
					{
						 objUserMgmtDAO=new UserMgmtDAO(Constants.DB_PUNIT);
						 Membership objMembership=new Membership();
						 objMembership.setUserId(objUserDTO.getUserId());
						 objMembership.setAccessGivenBy(objUserDTO.getAccessGivenBy());
						 objMembership.setRoleId(objUserDTO.getRoleId());
						 objMembership.setStatus(objUserDTO.getStatus());
						 objUserMgmtDAO.begin();
						 objMembership=objUserMgmtDAO.createUserRole(objMembership);
						 objUserMgmtDAO.commit();
					}
					else
					{
						 responseStr="Insufficient information";
					}
				 }
				 catch(Exception e) 
				 {
					 if(objUserMgmtDAO!=null && objUserMgmtDAO.isActive())
					 {
						 objUserMgmtDAO.rollback();
					 }
					 responseStr="ERROR#"+e.getMessage();
					 e.printStackTrace();
				}
				 finally
				 {
					 if(objUserMgmtDAO!=null)
					 {
						 try {
							objUserMgmtDAO.close();
						} catch (ApplicationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					 }
				 }
			 }
			 else
			 {
				 responseStr="Insufficient information";
			 }
			 System.out.println(request.getAttribute("request_uri") + " ended , time taken " + (System.currentTimeMillis() - (long) request.getAttribute("start_time")) + " milliseconds");
			 return responseStr;
		}
	
	@PostMapping (value="/api-auth-key/",  produces = MediaType.APPLICATION_JSON_VALUE)
	public String apiIntegrationKey(HttpServletRequest request, HttpServletResponse 
			response,@RequestBody AppIntegrationDTO integDto){
	
		JwtUtils utils = new JwtUtils();
		integDto.setApiauthkey(utils.generateAPIIntegrationKey(integDto.getCompanycode(), integDto.getProcessid(), integDto.getValidityseconds(), SignatureAlgorithm.HS256));
		try(SecServiceDAO dao = new SecServiceDAO(Constants.DB_PUNIT)){
			AppIntegrationModel appIntModel = new AppIntegrationModel();
			BeanUtils.copyProperties(integDto, appIntModel);
			dao.getObjJProvider().begin();
			dao.getObjJProvider().merge(appIntModel);
			dao.getObjJProvider().commit();
		} catch (ApplicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(request.getAttribute("request_uri") + " ended , time taken " + (System.currentTimeMillis() - (long) request.getAttribute("start_time")) + " milliseconds");
		return null;
		
	}
	
	@RequestMapping(value="/gen-api-key", method = RequestMethod.POST , produces = MediaType.APPLICATION_JSON_VALUE)
	public String generateApiKey(HttpServletRequest request, HttpServletResponse 
			response) throws Exception{
	
		
		String apikey = "";
		String companyCode =getCompanyCode(request);
		//System.out.println("#integration/gen-api-key#"+companyCode + " -- " + getCompanyCode(request));
		try(SecServiceDAO dao = new SecServiceDAO(Constants.DB_PUNIT)){
		    apikey = RandomAESKeyGen.generate();
			AppIntegrationModel appIntModel = new AppIntegrationModel();
			appIntModel.setAction("ALL");
			appIntModel.setProcessid("ALL");
			appIntModel.setApiauthkey(apikey);
			appIntModel.setCompanycode(companyCode);
			
			//BeanUtils.copyProperties(integDto, appIntModel);
			dao.genAPIkey(companyCode, apikey);
		} catch (ApplicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			throw e1;
		}
		System.out.println(request.getAttribute("request_uri") + " ended , time taken " + (System.currentTimeMillis() - (long) request.getAttribute("start_time")) + " milliseconds");
		return apikey;
		
	}
	
	@RequestMapping(value="/get-api-key", method = RequestMethod.GET , produces = MediaType.APPLICATION_JSON_VALUE)
	public String getApiKey(HttpServletRequest request, HttpServletResponse 
			response) throws Exception{

		String companyCode =getCompanyCode(request);
		try(SecServiceDAO dao = new SecServiceDAO(Constants.DB_PUNIT)){
			System.out.println(request.getAttribute("request_uri") + " ended , time taken " + (System.currentTimeMillis() - (long) request.getAttribute("start_time")) + " milliseconds");
			return dao.getAPIkey(companyCode);
		} catch (ApplicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} 
		//return apikey;
		
	}

	

	@RequestMapping(value="/account-details", method = RequestMethod.GET , produces = MediaType.APPLICATION_JSON_VALUE)
	public CompanyProfile  getAccountDetails(HttpServletRequest request, HttpServletResponse 
			response) throws Exception{
	
		String companyCode =getCompanyCode(request);
		//System.out.println("#/account-details#"+companyCode + " -- " + getCompanyCode(request));
		 UserMgmtDAO objUserMgmtDAO = null;
		 try {
			 objUserMgmtDAO=new UserMgmtDAO(Constants.DB_PUNIT);	
			 System.out.println(request.getAttribute("request_uri") + " ended , time taken " + (System.currentTimeMillis() - (long) request.getAttribute("start_time")) + " milliseconds");
			 return objUserMgmtDAO.getCompany(companyCode);
		 }
		 catch(Exception e) {
			 e.printStackTrace();
			 throw e;
		}
		 finally
		 {
			 if(objUserMgmtDAO!=null)
			 {
				 try {
					objUserMgmtDAO.close();
				} catch (ApplicationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 }
		 }
	}

	
	
	@PostMapping (value="/sign-up",  produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseDTO> registerCompany(HttpServletRequest request, HttpServletResponse 
			response,@RequestBody RegisterDTO profDto){
	
		
		try(UserMgmtDAO dao = new UserMgmtDAO(Constants.DB_PUNIT)){
			CompanyProfile appIntModel = new CompanyProfile();
			BeanUtils.copyProperties(profDto, appIntModel);
			appIntModel.setStatus("N");
			if(dao.validateCompany(appIntModel).size() != 0){
				throw new ApplicationException("Zanflow account is already setup with this email id");
			}
			
			//appIntModel.setActivationkey(Util.getRandomAlphanumeric(4));
			//appIntModel.setCompanyCode(profDto.getCompanyName());
			//dao.getObjJProvider().begin();
			//dao.getObjJProvider().merge(appIntModel);
			//dao.getObjJProvider().commit();
			
			Leads lead =  new Leads();
			lead.setUserId(appIntModel.getCompanyMailId());
			lead.setFirstName(profDto.getFirstName());
			lead.setLastName(profDto.getLastName());
			lead.setActivationcode(Util.getRandomAlphanumeric(4));
			lead.setAttempts("0");
			dao.getObjJProvider().begin();
			dao.getObjJProvider().merge(lead);
			dao.getObjJProvider().commit();
			
			String bodyMsg = "";///"<a href='http://zanflow.in/fp-security/activate-profile?activationkey="+appIntModel+"'>Click to Activate</a><br>";
			//bodyMsg="<h4>Welcome " + profDto.getFirstName() + "</h4><br> <h4>Your account activation code: "+lead.getActivationcode()+"</h4><br>";
			//bodyMsg=bodyMsg+"<br> <h2>Your account Login Credentials: login id : "+appIntModel.getCompanyMailId()+" and password : "+user.getPassword()+"</h2>";
			//alt=\"Zanflow\" title=\"Zanflow\"
			bodyMsg = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"><html xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:o=\"urn:schemas-microsoft-com:office:office\" style=\"width:100%;font-family:arial, 'helvetica neue', helvetica, sans-serif;-webkit-text-size-adjust:100%;-ms-text-size-adjust:100%;padding:0;Margin:0\"><head><meta charset=\"UTF-8\"><meta content=\"width=device-width, initial-scale=1\" name=\"viewport\"><meta name=\"x-apple-disable-message-reformatting\"><meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\"><meta content=\"telephone=no\" name=\"format-detection\"><title>New email template 2021-11-15</title> <!--[if (mso 16)]><style type=\"text/css\"> a {text-decoration: none;} </style><![endif]--> <!--[if gte mso 9]><style>sup { font-size: 100% !important; }</style><![endif]--> <!--[if gte mso 9]><xml> <o:OfficeDocumentSettings> <o:AllowPNG></o:AllowPNG> <o:PixelsPerInch>96</o:PixelsPerInch> </o:OfficeDocumentSettings> </xml><![endif]--><style type=\"text/css\">#outlook a { padding:0;}.ExternalClass { width:100%;}.ExternalClass,.ExternalClass p,.ExternalClass span,.ExternalClass font,.ExternalClass td,.ExternalClass div { line-height:100%;}.es-button { mso-style-priority:100!important; text-decoration:none!important;}a[x-apple-data-detectors] { color:inherit!important; text-decoration:none!important; font-size:inherit!important; font-family:inherit!important; font-weight:inherit!important; line-height:inherit!important;}.es-desk-hidden { display:none; float:left; overflow:hidden; width:0; max-height:0; line-height:0; mso-hide:all;}[data-ogsb] .es-button { border-width:0!important; padding:10px 20px 10px 20px!important;}@media only screen and (max-width:600px) {p, ul li, ol li, a { line-height:150%!important } h1, h2, h3, h1 a, h2 a, h3 a { line-height:120%!important } h1 { font-size:42px!important; text-align:left } h2 { font-size:26px!important; text-align:left } h3 { font-size:20px!important; text-align:left } h1 a { text-align:left } .es-header-body h1 a, .es-content-body h1 a, .es-footer-body h1 a { font-size:42px!important } h2 a { text-align:left } .es-header-body h2 a, .es-content-body h2 a, .es-footer-body h2 a { font-size:26px!important } h3 a { text-align:left } .es-header-body h3 a, .es-content-body h3 a, .es-footer-body h3 a { font-size:20px!important } .es-menu td a { font-size:14px!important } .es-header-body p, .es-header-body ul li, .es-header-body ol li, .es-header-body a { font-size:14px!important } .es-content-body p, .es-content-body ul li, .es-content-body ol li, .es-content-body a { font-size:14px!important } .es-footer-body p, .es-footer-body ul li, .es-footer-body ol li, .es-footer-body a { font-size:14px!important } .es-infoblock p, .es-infoblock ul li, .es-infoblock ol li, .es-infoblock a { font-size:12px!important } *[class=\"gmail-fix\"] { display:none!important } .es-m-txt-c, .es-m-txt-c h1, .es-m-txt-c h2, .es-m-txt-c h3 { text-align:center!important } .es-m-txt-r, .es-m-txt-r h1, .es-m-txt-r h2, .es-m-txt-r h3 { text-align:right!important } .es-m-txt-l, .es-m-txt-l h1, .es-m-txt-l h2, .es-m-txt-l h3 { text-align:left!important } .es-m-txt-r img, .es-m-txt-c img, .es-m-txt-l img { display:inline!important } .es-button-border { display:block!important } a.es-button, button.es-button { font-size:20px!important; display:block!important; border-left-width:0px!important; border-right-width:0px!important } .es-btn-fw { border-width:10px 0px!important; text-align:center!important } .es-adaptive table, .es-btn-fw, .es-btn-fw-brdr, .es-left, .es-right { width:100%!important } .es-content table, .es-header table, .es-footer table, .es-content, .es-footer, .es-header { width:100%!important; max-width:600px!important } .es-adapt-td { display:block!important; width:100%!important } .adapt-img { width:100%!important; height:auto!important } .es-m-p0 { padding:0px!important } .es-m-p0r { padding-right:0px!important } .es-m-p0l { padding-left:0px!important } .es-m-p0t { padding-top:0px!important } .es-m-p0b { padding-bottom:0!important } .es-m-p20b { padding-bottom:20px!important } .es-mobile-hidden, .es-hidden { display:none!important } tr.es-desk-hidden, td.es-desk-hidden, table.es-desk-hidden { width:auto!important; overflow:visible!important; float:none!important; max-height:inherit!important; line-height:inherit!important } tr.es-desk-hidden { display:table-row!important } table.es-desk-hidden { display:table!important } td.es-desk-menu-hidden { display:table-cell!important } .es-menu td { width:1%!important } table.es-table-not-adapt, .esd-block-html table { width:auto!important } table.es-social { display:inline-block!important } table.es-social td { display:inline-block!important } }</style></head>\r\n"
					+ "<body style=\"width:100%;font-family:arial, 'helvetica neue', helvetica, sans-serif;-webkit-text-size-adjust:100%;-ms-text-size-adjust:100%;padding:0;Margin:0\"><div class=\"es-wrapper-color\" style=\"background-color:#EFEFEF\"> <!--[if gte mso 9]><v:background xmlns:v=\"urn:schemas-microsoft-com:vml\" fill=\"t\"> <v:fill type=\"tile\" color=\"#efefef\"></v:fill> </v:background><![endif]--><table class=\"es-wrapper\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;padding:0;Margin:0;width:100%;height:100%;background-repeat:repeat;background-position:center top\"><tr style=\"border-collapse:collapse\"><td valign=\"top\" style=\"padding:0;Margin:0\"><table cellpadding=\"0\" cellspacing=\"0\" class=\"es-content\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%\"><tr style=\"border-collapse:collapse\"><td class=\"es-adaptive\" align=\"center\" style=\"padding:0;Margin:0\"><table class=\"es-content-body\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:transparent;width:600px\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\"><tr style=\"border-collapse:collapse\"><td align=\"left\" style=\"padding:10px;Margin:0\"> <!--[if mso]><table style=\"width:580px\"><tr><td style=\"width:280px\" valign=\"top\"><![endif]--><table class=\"es-left\" cellspacing=\"0\" cellpadding=\"0\" align=\"left\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;float:left\"><tr style=\"border-collapse:collapse\"><td align=\"left\" style=\"padding:0;Margin:0;width:280px\"><table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\"><tr style=\"border-collapse:collapse\"><td align=\"center\" style=\"padding:0;Margin:0;display:none\"></td>\r\n"
					+ "</tr></table></td></tr></table> <!--[if mso]></td><td style=\"width:20px\"></td><td style=\"width:280px\" valign=\"top\"><![endif]--><table class=\"es-right\" cellspacing=\"0\" cellpadding=\"0\" align=\"right\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;float:right\"><tr style=\"border-collapse:collapse\"><td align=\"left\" style=\"padding:0;Margin:0;width:280px\"><table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\"><tr style=\"border-collapse:collapse\"><td align=\"center\" style=\"padding:0;Margin:0;display:none\"></td></tr></table></td></tr></table> <!--[if mso]></td></tr></table><![endif]--></td></tr></table></td>\r\n"
					+ "</tr></table><table cellpadding=\"0\" cellspacing=\"0\" class=\"es-header\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%;background-color:transparent;background-repeat:repeat;background-position:center top\"><tr style=\"border-collapse:collapse\"><td align=\"center\" style=\"padding:0;Margin:0\"><table class=\"es-header-body\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#E6EBEF;width:600px\"><tr style=\"border-collapse:collapse\"><td align=\"left\" bgcolor=\"#ffffff\" style=\"padding:0;Margin:0;background-color:#ffffff\"><table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\"><tr style=\"border-collapse:collapse\"><td valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;width:600px\"><table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\"><tr style=\"border-collapse:collapse\"><td align=\"center\" style=\"padding:0;Margin:0;font-size:0px\"><a href=\"https://www.zanflow.com/\" target=\"_blank\" style=\"-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;text-decoration:none;color:#677D9E;font-size:14px\"><img src=\"https://app.zanflow.com/zanflow/signin_logo.png\"  width=\"259\" style=\"display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic\"></a></td>\r\n"
					+ "</tr></table></td></tr></table></td></tr></table></td>\r\n"
					+ "</tr></table><table class=\"es-content\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%\"><tr style=\"border-collapse:collapse\"><td align=\"center\" style=\"padding:0;Margin:0\"><table class=\"es-content-body\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#ffffff\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#FFFFFF;width:600px\"><tr style=\"border-collapse:collapse\"><td align=\"left\" style=\"Margin:0;padding-top:15px;padding-left:30px;padding-right:30px;padding-bottom:40px\"><table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\"><tr style=\"border-collapse:collapse\"><td valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;width:540px\"><table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\"><tr style=\"border-collapse:collapse\"><td align=\"left\" style=\"padding:0;Margin:0\"><h3 style=\"Margin:0;line-height:24px;mso-line-height-rule:exactly;font-family:arial, 'helvetica neue', helvetica, sans-serif;font-size:20px;font-style:normal;font-weight:normal;color:#666666\">Welcome {firstname},</h3>\r\n"
					+ "</td></tr><tr style=\"border-collapse:collapse\"><td align=\"center\" style=\"padding:0;Margin:0;padding-top:15px\"><p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:arial, 'helvetica neue', helvetica, sans-serif;line-height:39px;color:#666666;font-size:26px\">Complete registration</p></td></tr><tr style=\"border-collapse:collapse\"><td align=\"left\" style=\"padding:0;Margin:0;padding-top:15px\"><p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:arial, 'helvetica neue', helvetica, sans-serif;line-height:21px;color:#999999;font-size:14px\">Please enter this activation code in the window where you started creating account<br><br></p></td>\r\n"
					+ "</tr><tr style=\"border-collapse:collapse\"><td align=\"center\" bgcolor=\"#efefef\" style=\"padding:0;Margin:0\"><h1 style=\"Margin:0;line-height:59px;mso-line-height-rule:exactly;font-family:arial, 'helvetica neue', helvetica, sans-serif;font-size:49px;font-style:normal;font-weight:normal;color:#333333;text-align:center\">{activationcode}</h1></td></tr><tr style=\"border-collapse:collapse\"><td align=\"left\" style=\"padding:0;Margin:0;padding-top:25px\"><p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:arial, 'helvetica neue', helvetica, sans-serif;line-height:21px;color:#999999;font-size:14px\">If you need any support post activation, Please contact us at support@zanflow.com&nbsp;</p></td>\r\n"
					+ "</tr><tr style=\"border-collapse:collapse\"><td align=\"left\" style=\"padding:0;Margin:0;padding-top:15px\"><p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:arial, 'helvetica neue', helvetica, sans-serif;line-height:21px;color:#999999;font-size:14px\">Regards,</p><p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:arial, 'helvetica neue', helvetica, sans-serif;line-height:21px;color:#999999;font-size:14px\">The Zanflow team</p></td></tr></table></td></tr></table></td></tr></table></td>\r\n"
					+ "</tr></table><table cellpadding=\"0\" cellspacing=\"0\" class=\"es-footer\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%;background-color:transparent;background-repeat:repeat;background-position:center top\"><tr style=\"border-collapse:collapse\"><td align=\"center\" style=\"padding:0;Margin:0\"><table class=\"es-footer-body\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:#E6EBEF;width:600px\"><tr style=\"border-collapse:collapse\"><td align=\"left\" style=\"Margin:0;padding-top:20px;padding-bottom:20px;padding-left:20px;padding-right:20px\"><table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\"><tr style=\"border-collapse:collapse\"><td valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;width:560px\"><table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\"><tr style=\"border-collapse:collapse\"><td align=\"center\" style=\"Margin:0;padding-bottom:10px;padding-top:15px;padding-left:15px;padding-right:15px\"><p style=\"Margin:0;-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;font-family:arial, 'helvetica neue', helvetica, sans-serif;line-height:20px;color:#999999;font-size:13px\">You are receiving this email because you have requested for Zanflow signup. If you didn't expect this email, someone might have entered your address by mistake.</p>\r\n"
					+ "</td></tr><tr style=\"border-collapse:collapse\"><td align=\"center\" style=\"padding:0;Margin:0;padding-top:15px;font-size:0\"><table class=\"es-table-not-adapt es-social\" cellspacing=\"0\" cellpadding=\"0\" role=\"presentation\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\"><tr style=\"border-collapse:collapse\"><td valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;padding-right:10px\"><a target=\"_blank\" href=\"https://twitter.com/zanflow1?lang=en\" style=\"-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;text-decoration:none;color:#999999;font-size:13px\"><img title=\"Twitter\" src=\"https://uhvgvh.stripocdn.email/content/assets/img/social-icons/logo-black/twitter-logo-black.png\" alt=\"Tw\" width=\"32\" style=\"display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic\"></a></td>\r\n"
					+ "<td valign=\"top\" align=\"center\" style=\"padding:0;Margin:0\"><a target=\"_blank\" href=\"https://www.youtube.com/channel/UCvYhuKGQJ0nQEZYUXlAGcXw\" style=\"-webkit-text-size-adjust:none;-ms-text-size-adjust:none;mso-line-height-rule:exactly;text-decoration:none;color:#999999;font-size:13px\"><img title=\"Youtube\" src=\"https://uhvgvh.stripocdn.email/content/assets/img/social-icons/logo-black/youtube-logo-black.png\" alt=\"Yt\" width=\"32\" style=\"display:block;border:0;outline:none;text-decoration:none;-ms-interpolation-mode:bicubic\"></a></td></tr></table></td></tr></table></td></tr></table></td></tr></table></td>\r\n"
					+ "</tr></table><table class=\"es-content\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;table-layout:fixed !important;width:100%\"><tr style=\"border-collapse:collapse\"><td align=\"center\" style=\"padding:0;Margin:0\"><table class=\"es-content-body\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px;background-color:transparent;width:600px\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\"><tr style=\"border-collapse:collapse\"><td align=\"left\" style=\"Margin:0;padding-left:20px;padding-right:20px;padding-top:30px;padding-bottom:30px\"><table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\"><tr style=\"border-collapse:collapse\"><td valign=\"top\" align=\"center\" style=\"padding:0;Margin:0;width:560px\"><table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" style=\"mso-table-lspace:0pt;mso-table-rspace:0pt;border-collapse:collapse;border-spacing:0px\"><tr style=\"border-collapse:collapse\"><td align=\"center\" style=\"padding:0;Margin:0;display:none\"></td>\r\n"
					+ "</tr></table></td></tr></table></td></tr></table></td></tr></table></td></tr></table></div></body></html>";
			
			bodyMsg = bodyMsg.replace("{firstname}", profDto.getFirstName());
			bodyMsg = bodyMsg.replace("{activationcode}", lead.getActivationcode());
			Notifier.getNotifier().sendActEmail(appIntModel.getCompanyMailId(),"", "Activation Code from Zanflow", bodyMsg);
			//System.out.println(lead.getActivationcode());
			profDto.setResponsMsg("Acitvation Code sent to email");
			profDto.setResponseCode("Success");
			System.out.println(request.getAttribute("request_uri") + " ended , time taken " + (System.currentTimeMillis() - (long) request.getAttribute("start_time")) + " milliseconds");
			return new ResponseEntity<ResponseDTO>(profDto, HttpStatus.OK);
		} catch (ApplicationException e) {
			e.printStackTrace();
			profDto.setResponsMsg(e.getMessage());
			profDto.setResponseCode("Failure");
			return new ResponseEntity<ResponseDTO>(profDto, HttpStatus.UNPROCESSABLE_ENTITY);
		}
		
	}
	
	@PostMapping (value="/activate-profile/{activationKey}",  produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> activateProfile(HttpServletRequest request, HttpServletResponse 
			response,@PathVariable String activationKey, @RequestBody RegisterDTO profDto){
		
		try(UserMgmtDAO dao = new UserMgmtDAO(Constants.DB_PUNIT)){
			CompanyProfile appIntModel = new CompanyProfile();
			BeanUtils.copyProperties(profDto, appIntModel);
			if(dao.validateCompany(appIntModel).size() != 0){
				throw new ApplicationException("Zanflow account is already setup with this email id");
			}
			
		 if(dao.checkActivationCode(activationKey, profDto.getCompanyMailId())) {
			appIntModel.setActivationkey(Util.getRandomAlphanumeric(4));
			
			dao.getObjJProvider().begin();
			String accountid = "A0000001";
			String processtype = "ACCOUNT";
			List<Object> obj1 = dao.getObjJProvider().createNativeQuery("select id from {h-schema}zf_cfg_available_ids where companycode = :companycode and idtype = :processtype order by id asc LIMIT 1").setParameter("companycode", "ZF_ACCOUNTID").setParameter("processtype", processtype).getResultList();
			if(obj1.size()>0) {
				String tempProcessId = String.valueOf(obj1.get(0));
				if(tempProcessId.length()==8) {
					//String intprocess = tempProcessId.substring(1);
					accountid = tempProcessId;
				}
				dao.getObjJProvider().createNativeQuery("delete from {h-schema}zf_cfg_available_ids where companycode=:companyCode and idtype=:processtype  and id=:id ").setParameter("companyCode", "ZF_ACCOUNTID").setParameter("processtype", processtype).setParameter("id", tempProcessId).executeUpdate();
				
			}else{
			List<Object> obj = dao.getObjJProvider().createNativeQuery("select companycode from {h-schema}zf_id_company where companycode like 'A%' order by companycode desc LIMIT 1").getResultList();
			if(obj.size()>0) {
				String tempProcessId = String.valueOf(obj.get(0));
				System.out.println("BEGIN tempProcessId - "+ tempProcessId);
				if(tempProcessId.length()==8) {
					
					String intprocess = tempProcessId.substring(1);
					System.out.println("INSIDE tempProcessId - "+ intprocess);
					accountid = processtype.substring(0, 1) + String.format("%07d", Integer.valueOf(intprocess)+1);		
				}
			}
			}
			dao.getObjJProvider().commit();
			
			appIntModel.setCompanyCode(accountid);
			appIntModel.setStatus("A");
			dao.getObjJProvider().begin();
			dao.getObjJProvider().merge(appIntModel);
			System.out.println("BEGIN accountid - "+ accountid);
			dao.getObjJProvider().commit();
			System.out.println("END accountid - "+ accountid);
			
			User user =  new User();
			user.setCompanyCode(accountid);
			user.setUserId(appIntModel.getCompanyMailId());
			user.setFirstName(profDto.getFirstName());
			user.setLastName(profDto.getLastName());
			user.setEmailId(appIntModel.getCompanyMailId());
			user.setStatus("A");
			user.setUserType("admin");
			user.setPassword(profDto.getPassword());
			dao.getObjJProvider().begin();
			dao.getObjJProvider().merge(user);
			dao.getObjJProvider().commit();
			
			UserDTO dto = prepareUserDto(user);
			
			System.out.println(request.getAttribute("request_uri") + " ended , time taken " + (System.currentTimeMillis() - (long) request.getAttribute("start_time")) + " milliseconds");
			return new ResponseEntity<ResponseDTO>(dto, HttpStatus.OK);
		 }else {
			return new ResponseEntity<String>("Unable to activate account", HttpStatus.UNPROCESSABLE_ENTITY);
		 }
		} catch (ApplicationException e) {
			e.printStackTrace();
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
		}
	}
	
	 private String getCompanyCode(HttpServletRequest request) {
		 return request.getHeader("companycode");
		
	 }

}
