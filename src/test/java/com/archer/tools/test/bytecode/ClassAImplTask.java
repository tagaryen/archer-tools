package com.archer.tools.test.bytecode;

public class ClassAImplTask implements AsyncTask {
	
	ClassAImpl impl;
	String name;
	int b;
	
	public ClassAImplTask(ClassAImpl impl, String name, int b) {
		this.impl = impl;
		this.name = name;
		this.b = b;
	}
	
	public void run() {
		impl.setName(name, b);
	}

}
