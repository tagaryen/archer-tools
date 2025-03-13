package com.archer.tools.test.bytecode;

public class ClassB {
	private String name;
	private int age;
	private float income;
	
	public ClassB(String name, int age, float income) {
		this.name = name;
		this.age = age;
		this.income = income;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public float getIncome() {
		return income;
	}
	public void setIncome(float income) {
		this.income = income;
	}

	public void tmp(String a, ClassA ac) {
		name = a;
		age = ac.getAge();
	}
}
