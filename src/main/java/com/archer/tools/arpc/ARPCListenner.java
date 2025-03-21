package com.archer.tools.arpc;

import com.archer.xjson.XJSON;

public abstract class ARPCListenner<P, R> {
	
	protected XJSON json = new XJSON();
	
	private Class<P> firstClass;
	
	private Class<R> secondClass;
	
	protected ARPCListenner(Class<P> firstClass, Class<R> secondClass) {
		this.firstClass = firstClass;
		this.secondClass = secondClass;
	}

	protected String stringifyObject(Object o) {
		return json.stringify(o);
	}
	
	protected Class<P> getFirstClass() {
		return firstClass;
	}
	
	protected Class<R> getSecondClass() {
		return secondClass;
	}
}
