package com.archer.tools.test.bytecode;

import java.io.IOException;

public class Child extends Parent {

	public Child(short a0) {
		super(a0);
	}
	public Child(short a0, int a1) throws IOException {
		super(a0, a1);
	}
	public Child(short a0, int a1, float a2) throws IOException {
		super(a0, a1, a2);
	}
	public Child(short a0, int a1, float a2, double a3) {
		super(a0, a1, a2, a3);
	}
	public Child(short a0, int a1, float a2, double a3, long a4) {
		super(a0, a1, a2, a3, a4);
	}
	public Child(short a0, int a1, float a2, double a3, long a4, String a5) {
		super(a0, a1, a2, a3, a4, a5);
	}
	public Child(short a0, int a1, float a2, double a3, long a4, String a5, String[] a6) {
		super(a0, a1, a2, a3, a4, a5, a6);
	}
	public Child(short a0, int a1, float a2, double a3, long a4, String a5, String[] a6, Object a7) {
		super(a0, a1, a2, a3, a4, a5, a6, a7);
	}
	
}
