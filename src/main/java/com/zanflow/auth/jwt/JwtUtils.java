package com.zanflow.auth.jwt;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.zanflow.sec.common.exception.ApplicationException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
@Component("jwtUtils")
public class JwtUtils {
	private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

	@Value("${zanflow.app.jwtSecret}")
	private String jwtSecret;

	@Value("${zanflow.app.jwtExpirationMs}")
	private int jwtExpirationMs;

	/**
	 * 
	 * @param name
	 * @param subject
	 * @param expiration in Minutes
	 * @return
	 */
	public String generateJwtToken(String name,String subject, int expiration) {
		return Jwts.builder()
					.setSubject(subject)
					.setAudience(name)
					.setIssuedAt(new Date())
					.setExpiration(new Date((new Date()).getTime() + (expiration*60000)))
					.signWith(SignatureAlgorithm.HS512, "zanflowsecretket")
					.compact();
	}

	/**
	 * 
	 * @param name
	 * @param subject
	 * @param expiration in Minutes
	 * @return
	 */
	public String generateJwtRefreshToken(String name,String subject, int expiration) {
		return Jwts.builder()
					.setSubject((subject))
					.setAudience(name)
					.setIssuedAt(new Date())
					.setExpiration(new Date((new Date()).getTime() + (expiration*60000)))
					.signWith(SignatureAlgorithm.HS512, "zanflowrefreshkey")
					.compact();
	}

	/**
	 * 
	 * @param companycode
	 * @param processid
	 * @param expiration
	 * @return API Integration Key
	 */
	public String generateAPIIntegrationKey(String companycode,String processid, int expiration) {
		return Jwts.builder()
					.setSubject((processid))
					.setAudience(companycode)
					.setIssuedAt(new Date())
					.setExpiration(new Date((new Date()).getTime() + expiration))
					.signWith(SignatureAlgorithm.HS512, "zanflowapikey")
					.compact();
	}

	/** 
	 * @param companycode
	 * @param processid
	 * @param expiration
	 * @param algorithm
	 * @return
	 */
	public String generateAPIIntegrationKey(String companycode,String processid, int expiration,SignatureAlgorithm algorithm) {
		return Jwts.builder()
					.setSubject((processid))
					.setAudience(companycode)
					.setIssuedAt(new Date())
					.setExpiration(new Date((new Date()).getTime() + expiration))
					.signWith(algorithm, "zanflowapikey")
					.compact();
	}

	/**
	 * 
	 * @param token
	 * @param isRefresh
	 * @return
	 */
	public String getUserNameFromJwtToken(String token, boolean isRefresh) {
		if(isRefresh)
			return Jwts.parser().setSigningKey("zanflowrefreshkey").parseClaimsJws(token).getBody().getAudience();
		else
			return Jwts.parser().setSigningKey("zanflowsecretket").parseClaimsJws(token).getBody().getAudience();
	}

	/**
	 * 
	 * @param authToken
	 * @return
	 */
	public boolean validateJwtToken(String authToken) {
		try {
			//System.out.println("validateJwtToken#zanflowsecretket#"+authToken);
			Date date = Jwts.parser().setSigningKey("zanflowsecretket").parseClaimsJws(authToken).getBody().getExpiration();
			//System.out.println("validateJwtToken#"+date);
			
			return true;
		} catch (SignatureException e) {
			//System.out.println("Invalid JWT signature: {}"+e.getMessage());
		} catch (MalformedJwtException e) {
			//System.out.println("Invalid JWT token: {}"+ e.getMessage());
		} catch (ExpiredJwtException e) {
			//System.out.println("JWT token is expired: {}"+ e.getMessage());
		} catch (UnsupportedJwtException e) {
			//System.out.println("JWT token is unsupported: {}"+ e.getMessage());
		} catch (IllegalArgumentException e) {
			//System.out.println("JWT claims string is empty: {}"+ e.getMessage());
		}

		return false;
	}
	
	/**
	 * 
	 * @param authToken
	 * @return
	 * @throws ApplicationException
	 */
	public boolean validateJwtRefreshToken(String authToken) throws ApplicationException {
		try {
			//System.out.println("validateJwtRefreshToken#zanflowrefreshkey#"+authToken);
			Date date = Jwts.parser().setSigningKey("zanflowrefreshkey").parseClaimsJws(authToken).getBody().getExpiration();
			//System.out.println("validateJwtRefreshToken#"+date);
			
			return true;
		} catch (SignatureException e) {
			//System.out.println("Invalid JWT signature: {}"+e.getMessage());
			throw new ApplicationException("Invalid JWT signature:"+e.getMessage());
		} catch (MalformedJwtException e) {
			//System.out.println("Invalid JWT token: {}"+ e.getMessage());
			throw new ApplicationException("Invalid JWT signature:"+e.getMessage());
		} catch (ExpiredJwtException e) {
			//System.out.println("JWT Refresh token is expired:"+ e.getMessage());
			throw new ApplicationException("Refresh token is expired:"+e.getMessage());
		} catch (UnsupportedJwtException e) {
			//System.out.println("JWT token is unsupported:"+ e.getMessage());
			throw new ApplicationException("JWT token is unsupported:"+e.getMessage());
		} catch (IllegalArgumentException e) {
			//System.out.println("JWT claims string is empty:"+ e.getMessage());
			throw new ApplicationException("JWT claims string is empty:"+e.getMessage());
		}
	}
	
	/**
	 * 
	 * @param authToken
	 * @param secret
	 * @return String Requested Process id
	 */
	public Claims validateAPIIntegrationKey(String authToken,String secret) {
		try {
			//System.out.println(authToken+"#"+secret+"#"+jwtExpirationMs);
		//	Date date = Jwts.parser().setSigningKey(secret).parseClaimsJws(authToken).getBody().getExpiration();
			Claims claims =  Jwts.parser().setSigningKey(secret).parseClaimsJws(authToken).getBody();
			//System.out.println("validateAPIIntegrationKey#CompanyCode#"+claims.getAudience()+"#BPMNID#"+claims.getSubject()+"#Expiration#");
			return claims;
		} catch (SignatureException e) {
			//System.out.println("Invalid JWT signature: {}"+e.getMessage());
		} catch (MalformedJwtException e) {
			//System.out.println("Invalid JWT token: {}"+ e.getMessage());
		} catch (ExpiredJwtException e) {
			//System.out.println("JWT token is expired: {}"+ e.getMessage());
		} catch (UnsupportedJwtException e) {
			//System.out.println("JWT token is unsupported: {}"+ e.getMessage());
		} catch (IllegalArgumentException e) {
			//System.out.println("JWT claims string is empty: {}"+ e.getMessage());
		}

		return null;
	}
	
	public String getUserNameFromJwtToken(String token) {
		return Jwts.parser().setSigningKey("zanflowsecretket").parseClaimsJws(token).getBody().getAudience();
	}
	
	public static void main(String args[]) {
		JwtUtils utils =  new JwtUtils();
		//System.out.println(utils.generateAPIIntegrationKey("SCB", "APPRAISAL", 900000000));
		//System.out.println(utils.generateAPIIntegrationKey("SCB", "APPRAISAL", 900000000,SignatureAlgorithm.HS256));
		
	}

}
