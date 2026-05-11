package com.archer.tools.test.bytecode;

public class ParentImpl extends Parent {

	public AsyncPool pool;
	
	public void func(String name) {
		pool.submit(new ParentImplTask(this, name));
	}
	
	public void superfunc(String name) {
		try {
			super.func(name);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
