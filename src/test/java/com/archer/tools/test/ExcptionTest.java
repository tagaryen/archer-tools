package com.archer.tools.test;

import java.util.Map;

import com.archer.tools.java.ExceptionUtil;
import com.archer.xjson.XJSONStatic;

public class ExcptionTest {

	public static void main(String args[]) {
		try {
			String a = "{\"ni\":}";
			Map<String, Object> obj = XJSONStatic.parse(a);
		} catch(Exception e) {
			System.out.println(ExceptionUtil.formatException(e));
			System.out.println("\n\n");
			e.printStackTrace();
			
		}
	}
}
