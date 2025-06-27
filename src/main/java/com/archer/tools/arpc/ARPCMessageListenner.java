package com.archer.tools.arpc;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class ARPCMessageListenner<T> {
	
	public abstract Object onMessage(T in);
	
	public Type getJavaType() {
		ParameterizedType t = (ParameterizedType) getClass().getGenericSuperclass();
		Type[] ts = t.getActualTypeArguments();
		return ts[0];
	}
}
