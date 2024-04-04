package com.zanflow.sec.service;
import java.util.Random;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.zanflow.sec.common.exception.ApplicationException;
public class Util {
	
	@Autowired
    private JavaMailSender javaMailSender;

	public void sendEmail(String[] toList,String subject,String bodyContent) throws ApplicationException {
		JavaMailSender javaMailSender = new JavaMailSenderImpl();
		MimeMessage msg = javaMailSender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(msg, true);
			helper.setTo(toList);
			helper.setSubject(subject);
			helper.setText(bodyContent, true);
		} catch (MessagingException e) {
			e.printStackTrace();
			throw new ApplicationException(e);
		}
        javaMailSender.send(msg);

    }
	
	@Value("${zanflow.app.jwtSecret}")
	private String activationlink;
	public String prepareActivationLink(String key,String requestor) {
		//System.out.println("activationlink#"+activationlink+key);
		return activationlink+key;
		
	}
	
	public static String getRandomAlphanumeric(int lenth) {

		Random rand = new Random();
		//String alphaNumericCharacters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJLMNOPQRSTUVWXYZ1234567890@$!";
		String alphaNumericCharacters = "1234567890";
		// Use StringBuilder in place of String to avoid unnecessary object formation
		StringBuilder result = new StringBuilder();

		for (int i = 0; i < lenth; i++) {
			result.append(alphaNumericCharacters.charAt(rand.nextInt(alphaNumericCharacters.length())));
		}
		//System.out.println(result.toString());
		return result.toString();

	}
}
