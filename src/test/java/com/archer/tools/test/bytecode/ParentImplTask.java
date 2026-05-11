package com.archer.tools.test.bytecode;

public class ParentImplTask implements AsyncTask {

	public ParentImpl p0;

	public String p1;
	
	public ParentImplTask() {}
	
	public ParentImplTask(ParentImpl p0, String p1) {
		super();
		this.p0 = p0;
		this.p1 = p1;
	}

	@Override
	public void run() {
		p0.superfunc(p1);
	}
}
