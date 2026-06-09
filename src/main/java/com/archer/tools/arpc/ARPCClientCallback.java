package com.archer.tools.arpc;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.archer.xjson.XJSONStatic;

public abstract class ARPCClientCallback<Recv> {

	private static final long TIMEOUT = 2000;
	
	private Object lock = new Object();
	private Recv r = null;
	private RuntimeException ex = null;
	
	public abstract void onReceive(Recv r);
	
	protected void handle(String text, RuntimeException e) {
		if(e != null) {
			this.ex = e;
		} else {
			onReceive(XJSONStatic.parse(text, getJavaType()));
		}
	}
	
	protected void await() {
		long start = System.currentTimeMillis();
		synchronized(lock) {
			try {
				lock.wait(TIMEOUT);
			} catch (InterruptedException ignore) {}
		}
		if(start + TIMEOUT <= System.currentTimeMillis()) {
			throw new ARPCException("wait for response timeout");
		}
	}
	
	protected void release() {
		synchronized(lock) {
			lock.notify();
		}
	}
	
	protected void setResponse(Recv r) {
		this.r = r;
	}
	
	protected Recv getResponse() {
		return this.r;
	}

	protected void setException(RuntimeException e) {
		this.ex = e;
	}
	
	protected RuntimeException getException() {
		return ex;
	}
	
	protected Type getJavaType() {
		ParameterizedType t = (ParameterizedType) getClass().getGenericSuperclass();
		Type[] ts = t.getActualTypeArguments();
		return ts[0];
	}
}
