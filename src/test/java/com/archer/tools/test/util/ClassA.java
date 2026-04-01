package com.archer.tools.test.util;

import java.util.Random;
import java.util.UUID;

public class ClassA {
	
	private String name;
	
	private int age;

	public ClassA() {
		this.name = UUID.randomUUID().toString();
		this.age = (new Random()).nextInt(100);
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}
