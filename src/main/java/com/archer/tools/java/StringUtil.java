package com.archer.tools.java;

import java.util.List;
import java.util.ListIterator;
import java.util.Random;

public class StringUtil {

	private static final Random r = new Random();
	private static final char[] chars = {'0', '1', '2', '3', '4', '5', '6', '7', '8','9','a', 'b', 'c', 'd', 'e', 'f', 'g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z','~','!','@','#','$','%','^','&','*','(',')','_','+','=',';','<','>','/'};
	
	public static boolean isEmpty(String s) {
		return s == null ? true : s.isEmpty();
	}
	
	public static boolean isHex(String s) {
		try {
			NumberUtil.hexStrToBytes(s);
			return true;
		} catch(Exception ignore) {
			return false;
		}
	}
	
	public static boolean isNumber(String s) {
		try {
			Long.parseLong(s);
			return true;
		} catch(Exception ignore) {}
		try {
			Double.parseDouble(s);
			return true;
		} catch(Exception ignore) {}
		return false;
	}
	
	public static String join(List<String> strs, String sep) {
		if(strs == null || strs.isEmpty()) {
			return "";
		}
		StringBuilder sb = new StringBuilder(1024 * 16);
		ListIterator<String> it = strs.listIterator();
		sb.append(it.next());
		while(it.hasNext()) {
			sb.append(sep).append(it.next());
		}
		return sb.toString();
	}
	

	public static String join(String[] strs, String sep) {
		if(strs == null || strs.length == 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder(1024 * 16);
		sb.append(strs[0]);
		for(int i = 1; i < strs.length; i++) {
			sb.append(sep).append(strs[i]);
		}
		return sb.toString();
	}
	
	public static String random() {
		return random(32);
	}
	
	public static String random(int len) {
		char[] sc = new char[len];
		for(int i = 0; i < len; i++) {
			sc[i] = chars[r.nextInt(36)];
		}
		return new String(sc);
	}
	
	public static String randomHex() {
		return random(32);
	}
	
	public static String randomHex(int len) {
		char[] sc = new char[len];
		for(int i = 0; i < len; i++) {
			sc[i] = chars[r.nextInt(16)];
		}
		return new String(sc);
	}
	
	public static String randomCaseSensitive() {
		return randomCaseSensitive(32);
	}
	
	public static String randomCaseSensitive(int len) {
		char[] sc = new char[len];
		for(int i = 0; i < len; i++) {
			sc[i] = chars[r.nextInt(62)];
		}
		return new String(sc);
	}
	
	public static String randomSpecialChar() {
		return randomCaseSensitive(32);
	}
	
	public static String randomSpecialChar(int len) {
		char[] sc = new char[len];
		for(int i = 0; i < len; i++) {
			sc[i] = chars[r.nextInt(chars.length)];
		}
		return new String(sc);
	}
}
