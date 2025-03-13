package com.archer.tools.test.bytecode;

public class ClassA {
	private String name;
	private int age;
	private ClassB b;
	
	
	public String getName() {
		return name;
	}
	public void setName(String name, int age, ClassB b) {
		this.name = name;
		this.age = age;
		this.b = b;
	}
	public int getAge() {
		return age;
	}
	public ClassB getB() {
		return b;
	}
}
