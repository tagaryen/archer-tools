package com.archer.tools.test.bytecode;

public class Child extends Parent {

	AsyncPool pool;
	
	public void func() {
		super.func();
	}
	
	public void superfunc() {
		func();
	}
	
}
