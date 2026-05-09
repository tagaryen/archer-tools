package com.archer.tools.test.bytecode;

public class ChildTask implements AsyncTask {

	Child ins;
	
	@Override
	public void run() {
		ins.superfunc();
	}

}
