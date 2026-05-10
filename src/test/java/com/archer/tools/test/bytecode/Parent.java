package com.archer.tools.test.bytecode;

import java.io.IOException;

public class Parent {

	private short a0;
	private int a1;
	private float a2;
	private double a3;
	private long a4;
	private String a5;
	private String[] a6;
	private Object a7;

	public Parent(short a0) {
		this.a0 = a0;
	}
	

	public Parent(short a0, int a1) throws IOException {
		this.a0 = a0;
		this.a1 = a1;
		if(a0 == 0) {
			throw new IOException("invalid");
		}
	}
	
	public Parent(short a0, int a1, float a2) throws IOException {
		this.a0 = a0;
		this.a1 = a1;
		this.a2 = a2;
		if(a0 == 0) {
			throw new IOException("invalid");
		}
	}
	
	public Parent(short a0, int a1, float a2, double a3) {
		this.a0 = a0;
		this.a1 = a1;
		this.a2 = a2;
		this.a3 = a3;
	}
	
	
	public Parent(short a0, int a1, float a2, double a3, long a4) {
		this.a0 = a0;
		this.a1 = a1;
		this.a2 = a2;
		this.a3 = a3;
		this.a4 = a4;
	}
	
	public Parent(short a0, int a1, float a2, double a3, long a4, String a5) {
		this.a0 = a0;
		this.a1 = a1;
		this.a2 = a2;
		this.a3 = a3;
		this.a4 = a4;
	}
	
	public Parent(short a0, int a1, float a2, double a3, long a4, String a5, String[] a6) {
		this.a0 = a0;
		this.a1 = a1;
		this.a2 = a2;
		this.a3 = a3;
		this.a4 = a4;
		this.a5 = a5;
		this.a6 = a6;
	}
	
	
	
	public Parent(short a0, int a1, float a2, double a3, long a4, String a5, String[] a6, Object a7) {
		this.a0 = a0;
		this.a1 = a1;
		this.a2 = a2;
		this.a3 = a3;
		this.a4 = a4;
		this.a5 = a5;
		this.a6 = a6;
		this.a7 = a7;
	}




	@Async
	public void func() {
		System.out.println("print here");
	}
}
