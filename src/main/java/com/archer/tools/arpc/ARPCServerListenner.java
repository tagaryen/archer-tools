package com.archer.tools.arpc;

import java.nio.charset.StandardCharsets;

import com.archer.net.Bytes;
import com.archer.net.ChannelContext;

public abstract class ARPCServerListenner<P, R> extends ARPCListenner<P, R> {

	protected ARPCServerListenner(Class<P> firstClass, Class<R> secondClass) {
		super(firstClass, secondClass);
	}

	protected void send(ChannelContext ctx, R r) {
		String returnName = r.getClass().getSimpleName().toLowerCase();
		String data = json.stringify(r);
		Bytes bytes = new Bytes();
		bytes.writeInt16(returnName.length());
		bytes.write(returnName.getBytes());
		bytes.writeInt32(data.length());
		bytes.write(data.getBytes(StandardCharsets.UTF_8));
		ctx.toLastOnWrite(bytes);
	}
	
	protected void onMessage(ChannelContext ctx, String paramName, Bytes bytes) {
		Class<P> cls = getFirstClass();
		String clsName = cls.getSimpleName().toLowerCase();
		if(!clsName.equals(paramName)) {
			throw new ARPCException("can not deserialize input type " + paramName + " to " + clsName);
		}
		int jsonLen = bytes.readInt32();
		String jsonStr = new String(bytes.read(jsonLen), StandardCharsets.UTF_8);
		
		send(ctx, onMessage(json.parse(jsonStr, cls)));
	}
	
	public abstract R onMessage(P p);
	
}
