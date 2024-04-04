package com.zanflow.bpmn.util;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.persistence.Query;

import com.zanflow.bpmn.exception.JPAIllegalStateException;
import com.zanflow.common.db.JPersistenceProvider;


public class DateUtility {
	
	private static final String  GETCUNTRY_TIMEZONE = "select timezoneid from {h-schema}zf_cfg_country where countrycode=?1";
	private static char separator = '/';
	
	
	public static Timestamp getCountryTimeStamp(String argPUnit,String countryCode)
	{
		long t = System.currentTimeMillis();
		//System.out.println("#getCountryTimeStamp#"+countryCode+"#I#"+t);
	    Timestamp timestamp = null;
		try{
			
			if(countryCode==null ||  "".equals(countryCode.trim())){
				countryCode = "IN";
			}
		     // to country zone
			String  strCountryZone = getCurrentTimeZone(argPUnit,countryCode);
			//System.out.println("#getCountryTimeStamp#strCountryZone#"+strCountryZone);
			 
	    	Calendar scanDateTime = new GregorianCalendar(TimeZone.getTimeZone(strCountryZone.trim())); 
	    	
		   // timestamp =  new Timestamp(scanDateTime.get(Calendar.YEAR),scanDateTime.get(Calendar.MONTH),scanDateTime.get(Calendar.DATE),scanDateTime.get(Calendar.HOUR_OF_DAY),scanDateTime.get(Calendar.MINUTE),scanDateTime.get(Calendar.SECOND),scanDateTime.get(Calendar.MILLISECOND));
		    timestamp = new Timestamp(scanDateTime.getTimeInMillis());
	    	//System.out.println("#getCountryTimeStamp#Time#"+timestamp);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		//System.out.println("#getCountryTimeStamp#"+countryCode+"#O#"+(System.currentTimeMillis()-t));
		return timestamp;
	}
	
	public static String getCurrentTimeZone(String dbParam,String countryCode){
		String strCountryZone = "";
		JPersistenceProvider jPeristenceProvider = null;
		if(countryCode ==null||"".equals(countryCode.trim())){
			countryCode = "IN";
		}
		try{
			jPeristenceProvider   = new JPersistenceProvider(dbParam);
			Query countryQuery    = jPeristenceProvider.createNativeQuery(GETCUNTRY_TIMEZONE);
			countryQuery.setParameter(1,countryCode.toUpperCase().trim());
			strCountryZone        = (String)countryQuery.getSingleResult();
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			try {
				if(jPeristenceProvider!=null)
				{
					jPeristenceProvider.close(dbParam);
				}
			} catch (JPAIllegalStateException e) {
				e.printStackTrace();
			}
		}
		return strCountryZone;
	}
	
	public static String getCurrentTimeStamp(String argPUnit, String dateFormat){
		Timestamp timestamp = null;
		String formattedDate = null;
		try{
			
			timestamp = getCountryTimeStamp(argPUnit,null);
			formattedDate = format(timeStampToDate(timestamp), dateFormat);
			
		}catch(Exception excp){
			excp.printStackTrace();
		}
		return formattedDate;
	}
	public static String format(Date date, String format, char dateSeparator)throws Exception {
		if (date == null)
			return null;
		String allowed = " -./";
		String dateSeparatorString = String.valueOf(separator);
		if (allowed.indexOf(dateSeparator) >= 0) {
			dateSeparatorString = String.valueOf(dateSeparator);
		} else if (dateSeparator == '\u0000') {
			dateSeparatorString = "";
		}
		if (format.trim().equalsIgnoreCase("DDMMYYYY")) {
			return formatDate(date, "dd" + dateSeparatorString + "MM"
					+ dateSeparatorString + "yyyy");
		} else if (format.trim().equalsIgnoreCase("MMDDYYYY")) {
			return formatDate(date, "MM" + dateSeparatorString + "dd"
					+ dateSeparatorString + "yyyy");
		} else if (format.trim().equalsIgnoreCase("YYYYDDMM")) {
			return formatDate(date, "yyyy" + dateSeparatorString + "dd"
					+ dateSeparatorString + "MM");
		} else if (format.trim().equalsIgnoreCase("YYYYMMDD")) {
			return formatDate(date, "yyyy" + dateSeparatorString + "MM"
					+ dateSeparatorString + "dd");
		} else if (format.trim().equalsIgnoreCase("MMYYYY")) {
			return formatDate(date, "MM" + dateSeparatorString + "yyyy");
		} else if (format.trim().equalsIgnoreCase("YYYYMM")) {
			return formatDate(date, "yyyy" + dateSeparatorString + "MM");
		} else if (format.trim().equalsIgnoreCase("DD")) {
			return formatDate(date, "dd");
		} else if (format.trim().equalsIgnoreCase("MMMYYYY")) {
			return formatDate(date, "MMM" + dateSeparatorString + "yyyy");
		} else if (format.trim().equalsIgnoreCase("DDMMYY")) {
			return formatDate(date, "dd" + dateSeparatorString + "MM"
					+ dateSeparatorString + "yy");
		} else if (format.trim().equalsIgnoreCase("MMDDYY")) {
			return formatDate(date, "MM" + dateSeparatorString + "dd"
					+ dateSeparatorString + "yy");
		} else if (format.trim().equalsIgnoreCase("YYMMDD")) {
			return formatDate(date, "yy" + dateSeparatorString + "MM"
					+ dateSeparatorString + "dd");
		} else
			throw new Exception(
					"Format is not valid. Valid Formats are 'MMDDYYYY', 'DDMMYYYY', 'YYYYMMDD', 'YYYYDDMM', 'MMYYYY', 'YYYYMM', 'MMMYYYY', 'DD', 'DDMMYY', 'MMDDYY', 'YYMMDD'.");
	}
	
	public static String format(Date date, String format)
			throws Exception {
		return format(date, format, separator);
	}
	
	public static java.sql.Date timeStampToDate(java.sql.Timestamp timestamp){
		long milliseconds = timestamp.getTime() + (timestamp.getNanos() / 1000000);
	    return new java.sql.Date(milliseconds);
	}
	
	private static String formatDate(java.util.Date date, String format) {
		SimpleDateFormat df = new SimpleDateFormat(format);
		return df.format(date) + "";
	}
	public static void main(String []args) {
		Calendar scanDateTime = new GregorianCalendar(TimeZone.getTimeZone("Asia/Kolkata"));
		Timestamp stamp =new Timestamp(scanDateTime.getTimeInMillis());
		//System.out.println(stamp);
	}
	
}
