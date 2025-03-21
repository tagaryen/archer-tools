package com.archer.tools.arpc;

import java.nio.charset.StandardCharsets;

import com.archer.net.Bytes;
import com.archer.net.ChannelContext;

public abstract class ARPCServerVoidListenner<P> extends ARPCServerListenner<P, Void> {

	protected ARPCServerVoidListenner(Class<P> firstClass) {
		super(firstClass, Void.class);
	}

	@Override
	protected void onMessage(ChannelContext ctx, String paramName, Bytes bytes) {
		Class<P> cls = getFirstClass();
		String clsName = cls.getSimpleName().toLowerCase();
		if(!clsName.equals(paramName)) {
			throw new ARPCException("can not deserialize input type " + paramName + " to " + clsName);
		}
		int jsonLen = bytes.readInt32();
		String jsonStr = new String(bytes.read(jsonLen), StandardCharsets.UTF_8);
		
		onMessageComing(json.parse(jsonStr, cls));
	}
	
	@Override
	public Void onMessage(P p) {
		return null;
	}

	public abstract void onMessageComing(P p);
}
