package com.zanflow.sec.service;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class PasswordGenerator {
	
	public static Stream<Character> getRandomSpecialChars(int count) {
	    Random random = new SecureRandom();
	    IntStream specialChars = random.ints(count, 33, 45);
	    return specialChars.mapToObj(data -> (char) data);
	}
	
	public static Stream<Character> getRandomAlphabets(int count, boolean upperCase) {
	        IntStream characters = null;
	        Random random = new SecureRandom();
	        if (upperCase) {
	            characters = random.ints(count, 65, 90);
	        } else {
	            characters = random.ints(count, 97, 122);
	        }
	        return characters.mapToObj(data -> (char) data);
	}

	public static Stream<Character> getRandomNumbers(int count) {
	    	Random random = new SecureRandom();
	        IntStream numbers = random.ints(count, 48, 57);
	        return numbers.mapToObj(data -> (char) data);
	 }
	    
	public static String generateSecureRandomPassword() {
	   // Stream<Character> pwdStream = Stream.concat(getRandomNumbers(2), 
	   //   Stream.concat(getRandomSpecialChars(2), 
	   //   Stream.concat(getRandomAlphabets(2, true), getRandomAlphabets(4, false))));
	    Stream<Character> pwdStream = Stream.concat(getRandomNumbers(2),
	  	      Stream.concat(getRandomAlphabets(2, true), getRandomAlphabets(4, false)));
	    List<Character> charList = pwdStream.collect(Collectors.toList());
	    Collections.shuffle(charList);
	    String password = charList.stream()
	        .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
	        .toString();
	    return password;
	}
	
	static public void main(String args[])  {
		String s = "P0001";
		s = s.substring(1);
		//System.out.println(String.format("%04d", Integer.valueOf(s)+20)); 
	}

}
