package com.archer.tools.arpc;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class ARPCClientCallback<R> {
	
	private Class<R> cls;
	
	public abstract void onReturn(R r);
	
	@SuppressWarnings({"unchecked" })
	protected Class<R> getReturnClass() {
		if(this.cls == null) {
			Type[] types = ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments();
			cls = (Class<R>) types[0];
		}
		return cls;
	}
	
	protected boolean doCallback() {
		return true;
	}
}
