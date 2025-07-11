package com.archer.tools.java;

import java.util.List;
import java.util.ListIterator;

public class StringUtil {
	
	public static boolean isEmpty(String s) {
		return s == null ? true : s.isEmpty();
	}
	
	public static boolean isHex(String s) {
		try {
			NumberUtil.hexStrToBytes(s);
			return true;
		} catch(Exception e) {
			return false;
		}
	}
	
	public static boolean isNumber(String s) {
		try {
			Long.parseLong(s);
			return true;
		} catch(Exception e) {
			return false;
		}
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
}
