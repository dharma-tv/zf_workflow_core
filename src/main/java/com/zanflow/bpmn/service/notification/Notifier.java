package com.zanflow.bpmn.service.notification;

import java.io.UnsupportedEncodingException;
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
	 //final String username = "support@shopperslike.com";
     //final String password = "shopperslike@123";
     
     final String username = "support@zanflow.com";
     final String password = "Nocode2020#";
     Properties prop = new Properties();
     private static Notifier objNotifier=null;
     
     private Notifier()
     {
    	 //prop.put("mail.smtp.host", "mail.shopperslike.com");
         //prop.put("mail.smtp.port", "587");
         //prop.put("mail.smtp.auth", "true");
         //prop.put("mail.smtp.starttls.enable", "false");
    	 
    	// Properties properties = new Properties();
    	 prop.setProperty("mail.smtp.host", "smtp.zoho.in");
         prop.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
         prop.setProperty("mail.smtp.socketFactory.fallback", "false");
    	 prop.setProperty("mail.smtp.port", "465");
         prop.setProperty("mail.smtp.socketFactory.port", "465");
    	 prop.put("mail.smtp.auth", "true");
    	 prop.put("mail.smtp.starttls.enable", "true");
         
          prop.put("mail.debug", "true");
          prop.put("mail.store.protocol", "pop3");
          prop.put("mail.transport.protocol", "smtp");
          prop.put("mail.smtp.ssl.protocols", "TLSv1.2");
          prop.put("mail.debug.auth", "true");
          prop.setProperty( "mail.pop3.socketFactory.fallback", "false");
     }

     
     public static Notifier getNotifier()
     {
    	 if(objNotifier==null)
    	 {
    		 objNotifier=new Notifier();
    	 }
    	 return objNotifier;
     }
     
     public void sendEmail(String toEmail,String ccEmail,String subject,String mailContent) throws UnsupportedEncodingException
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
             message.setFrom(new InternetAddress("support@zanflow.com", "zanflow Notifications"));
             message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(toEmail));
             if(ccEmail!=null && ccEmail.length()>0)
             {
            	 message.setRecipients(Message.RecipientType.CC,InternetAddress.parse(ccEmail));
             }
             message.setSubject(subject);
            // message.setText(mailContent);
             message.setContent(mailContent,"text/html");

             Transport.send(message);

             System.out.println(" Mail sent Done");
 

         } catch (MessagingException e) {
             e.printStackTrace();
         }
     }
}
