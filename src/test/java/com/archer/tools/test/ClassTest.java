package com.archer.tools.test;

import java.util.List;

import com.archer.tools.java.ClassUtil;

public class ClassTest {

	public static void main(String args[]) {
		List<Class<?>> classes = ClassUtil.listAllClasses();
		System.out.println(classes.size());
	}
}
