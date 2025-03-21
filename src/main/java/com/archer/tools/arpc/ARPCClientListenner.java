package com.archer.tools.arpc;

import java.nio.charset.StandardCharsets;

import com.archer.net.Bytes;
import com.archer.net.ChannelContext;

public class ARPCClientListenner<S,R> extends ARPCListenner<S, R> {

	public ARPCClientListenner(Class<S> firstClass, Class<R> secondClass) {
		super(firstClass, secondClass);
	}

	public static final long TIMEOUT = 3000;
	
	private Object lock = new Object();
	
	private R r;
	
	private ARPCException ex;
	
	private ARPCClientCallback<R> callback;
	
	protected void onCallbackSetResponse(R r) {
		this.r = r;
	}

	protected void onReturn(ChannelContext ctx, String paramName, Bytes bytes) {
		Class<R> cls = getSecondClass();
		String clsName = cls.getSimpleName().toLowerCase();
		if(!clsName.equals(paramName)) {
			this.ex = new ARPCException("can not deserialize input type " + paramName + " to " + clsName);
			synchronized(lock) {
				lock.notifyAll();
			}
		}
		int jsonLen = bytes.readInt32();
		String jsonStr = new String(bytes.read(jsonLen), StandardCharsets.UTF_8);
		
		this.r = json.parse(jsonStr, cls);
		synchronized(lock) {
			lock.notifyAll();
		}
		if(this.callback != null) {
			ARPCClientCallback<R> cb = this.callback;
			this.callback = null;
			cb.onReturn(r);
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
			throw new ARPCException("timeout");
		}
	}
	
	protected void sendParam(ChannelContext ctx, S s) {
		String returnName = s.getClass().getSimpleName().toLowerCase();
		String data = json.stringify(s);
		Bytes bytes = new Bytes();
		bytes.writeInt16(returnName.length());
		bytes.write(returnName.getBytes());
		bytes.writeInt32(data.length());
		bytes.write(data.getBytes(StandardCharsets.UTF_8));
		ctx.toLastOnWrite(bytes);
	}
	
	protected R getResponse() {
		return r;
	}
	
	protected ARPCException getException() {
		return ex;
	}
	
	protected void setCallback(ARPCClientCallback<R> callback) {
		this.callback = callback;
	}
}
