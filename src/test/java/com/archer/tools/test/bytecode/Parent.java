package com.archer.tools.test.bytecode;

public class Parent {

	@Async
	public void func(String name) {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("print here " + name);
	}
}
