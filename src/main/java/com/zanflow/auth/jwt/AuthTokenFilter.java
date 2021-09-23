package com.zanflow.auth.jwt;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;

@Component
@Order(1)

public class AuthTokenFilter implements Filter {

	private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		System.out.println("Starting Transaction for req :{}" + req.getRequestURI() + "#IsLogin#"
				+ req.getRequestURI().contains("/login") + "#IsSwagger#" + req.getRequestURI().contains("/swagger")
				+ "#IsApi-docs#" + req.getRequestURI().contains("/api-docs"));
		String jwt = parseJwt(req);
		JwtUtils jwtUtils = new JwtUtils();
		System.out.println("doFilterInternal#jwt#" + jwt);

	/*	if (true) {
		chain.doFilter(request, response);

	}
	else */if (req.getMethod().equalsIgnoreCase("OPTIONS")) {
			//res.setHeader("Access-Control-Allow-Origin", "*");
			//res.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
			chain.doFilter(request, response);
			

		} 
		else if(req.getRequestURI().contains("/external/")){
			Claims claim = jwtUtils.validateAPIIntegrationKey(jwt, "zanflowapi");
			req.setAttribute("bpmnid", claim.getSubject());
			req.setAttribute("companycode", claim.getAudience());
			chain.doFilter(request, response);
		}
		else if(req.getRequestURI().contains("/externalform/authenticate")){
			Claims claim = jwtUtils.validateAPIIntegrationKey(jwt, "zanflowapi");
			req.setAttribute("bpmnid", claim.getSubject());
			req.setAttribute("companycode", claim.getAudience());
			chain.doFilter(request, response);
		}
		else if(req.getRequestURI().contains("/externaltask/authenticate")){
			System.out.println("Inside external task decode ... ");
			Claims claim = jwtUtils.validateAPIIntegrationKey(jwt, "zanflowexternaltask");
			req.setAttribute("taskid", claim.getSubject());
			req.setAttribute("companycode", claim.getAudience());
			chain.doFilter(request, response);
	    }
		 else if (req.getRequestURI().contains("/login") || req.getRequestURI().contains("swagger")
				|| req.getRequestURI().contains("/api-docs") || req.getRequestURI().contains("/signin")
				|| req.getRequestURI().contains("/refresh-token")) {
			System.out.println("doFilterInternal##");
			chain.doFilter(request, response);
		}
		else if (jwt != null && jwtUtils.validateJwtToken(jwt)) {

				String username = jwtUtils.getUserNameFromJwtToken(jwt);
				System.out.println("doFilterInternal#validateJwtToken#" + username);
				chain.doFilter(request, response);
				 
			}
		 else {
			System.out.println("doFilterInternal##else" + HttpStatus.UNAUTHORIZED.value());
			Map<String, Object> errorDetails = new HashMap<>();
			errorDetails.put("message", "Expired token");
			res.setStatus(500);
			res.setContentType(MediaType.APPLICATION_JSON_VALUE);
			res.setHeader("Access-Control-Allow-Origin", "*");
			res.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");

			ObjectMapper mapper = new ObjectMapper();
			mapper.writeValue(res.getWriter(), errorDetails);
		}
	}

	private String parseJwt(HttpServletRequest request) {
		System.out.println("parseJwt#" + request.getHeaderNames().toString());
		String headerAuth = request.getHeader("Authorization");
		System.out.println("headerAuth#" + headerAuth);
		if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
			System.out.println("headerAuth##" + headerAuth.substring(7, headerAuth.length()));
			return headerAuth.substring(7, headerAuth.length());
		}

		return null;
	}
}
