package com.archer.tools.arpc;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class ARPCClientCallback<Recv> {

	private static final long TIMEOUT = 2000;
	
	protected Object lock = new Object();
	protected Recv response;
	
	public abstract void onReceive(Recv r);
	
	protected RuntimeException await() {
		long start = System.currentTimeMillis();
		synchronized(lock) {
			try {
				lock.wait(TIMEOUT);
			} catch (InterruptedException ignore) {}
		}
		if(start + TIMEOUT <= System.currentTimeMillis()) {
			return new ARPCException("wait for response timeout");
		}
		return null;
	}
	
	@SuppressWarnings({"unchecked" })
	protected Class<Recv> getRecvClass() {
		Type[] types = ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments();
		return (Class<Recv>) types[0];
	}
	
	
}
