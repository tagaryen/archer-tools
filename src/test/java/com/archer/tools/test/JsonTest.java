package com.archer.tools.test;

import com.archer.xjson.XJSONStatic;

public class JsonTest {
	public static void main(String args[]) {
		JsonClass j = new JsonClass();
		j.setAge(18);
		j.setName("nihao");
		
		String json = XJSONStatic.stringify(j);
		System.out.println(json);
		
		JsonClass jn = XJSONStatic.parse(json, JsonClass.class);
		System.out.println(jn.getAge());
		System.out.println(jn.getName());
	}
}
