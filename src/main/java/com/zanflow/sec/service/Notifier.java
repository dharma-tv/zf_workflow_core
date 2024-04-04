package com.zanflow.sec.service;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Notifier 
{
	 final String username = "notification-no-reply@zanflow.com";
     final String password = "Nocode2020#";
     Properties prop = new Properties();
     private static Notifier objNotifier=null;
     
     private Notifier()
     {
         prop.put("mail.smtp.host", "smtp.zoho.in");
         prop.put("mail.smtp.auth", "true");
         prop.put("mail.smtp.port", "465");
         //prop.setProperty("mail.pop3.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); 
         
         prop.setProperty("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
         prop.setProperty("mail.smtp.socketFactory.fallback","false");
         prop.setProperty("mail.smtp.socketFactory.port","465");
         prop.put("mail.smtp.startssl.enable", "true");
         prop.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");
         
         //prop.put("mail.smtp.port", "587");
         //prop.setProperty("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
        // prop.setProperty("mail.smtp.socketFactory.fallback","false");
         //prop.setProperty("mail.smtp.socketFactory.port","465");
         //prop.put("mail.smtp.starttls.enable", "true");
         
         
     }

     
     public static Notifier getNotifier()
     {
    	 if(objNotifier==null)
    	 {
    		 objNotifier=new Notifier();
    	 }
    	 return objNotifier;
     }
     
     public void sendEmail(String toEmail,String ccEmail,String subject,String mailContent)
     {
   	 
    	 Session session = Session.getInstance(prop,
                 new javax.mail.Authenticator() {
                     protected PasswordAuthentication getPasswordAuthentication() {
                         return new PasswordAuthentication(username, password);
                     }
                 });

         try 
         {
             Message message = new MimeMessage(session);
             message.setFrom(new InternetAddress(username));
             message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(toEmail));
             if(ccEmail!=null && ccEmail.length()>0)
             {
            	 message.setRecipients(Message.RecipientType.CC,InternetAddress.parse(ccEmail));
             }
             message.setSubject(subject);
             message.setText(mailContent);
             message.setContent(mailContent, "text/html");

             Transport.send(message);

             //System.out.println("Done");
 

         } catch (MessagingException e) {
             e.printStackTrace();
         }
     }
     
     public void sendActEmail(String toEmail,String ccEmail,String subject,String mailContent)
     {
   	 
    	 Session session = Session.getInstance(prop,
                 new javax.mail.Authenticator() {
                     protected PasswordAuthentication getPasswordAuthentication() {
                         return new PasswordAuthentication(username, password);
                     }
                 });

         try 
         {
             Message message = new MimeMessage(session);
             message.setFrom(new InternetAddress(username));
             message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(toEmail));
             message.setRecipients(Message.RecipientType.BCC,InternetAddress.parse("nagasri.sekar@zanflow.com"));
             if(ccEmail!=null && ccEmail.length()>0)
             {
            	 message.setRecipients(Message.RecipientType.CC,InternetAddress.parse(ccEmail));
             }
             message.setSubject(subject);
             message.setText(mailContent);
             message.setContent(mailContent, "text/html");

             Transport.send(message);

             //System.out.println("Done");
 

         } catch (MessagingException e) {
             e.printStackTrace();
         }
     }
     public static void main(String[] args) {
    	 Notifier notify = Notifier.getNotifier();
    	 String bodyMsg = "<a href='http://zanflow.in/fp-security/activate-profile?activationkey="+"'>Click to Activate</a><br>";
			bodyMsg="<h1>You have successfully registed with Zanflow</h2><br> <h1>Your account activation key: "+"</h2><br>";
			bodyMsg=bodyMsg+"<br> <h2>Your account Login Credentials: login id : "+" and password : "+"</h2>";
			
    	 notify.sendEmail("dharmendran.v@gmail.com", "snagasri@gmail.com", "zoho test", bodyMsg);
	}
}
