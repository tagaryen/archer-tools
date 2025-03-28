package com.archer.tools.arpc;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class ARPCClientCallback<Recv> {
	
	public abstract void onReceive(Recv r);
	
	@SuppressWarnings({"unchecked" })
	protected Class<Recv> getRecvClass() {
		Type[] types = ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments();
		return (Class<Recv>) types[0];
	}
}
