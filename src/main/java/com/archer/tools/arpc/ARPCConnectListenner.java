package com.archer.tools.arpc;

import java.nio.charset.StandardCharsets;

import com.archer.net.Bytes;
import com.archer.net.ChannelContext;
import com.archer.xjson.XJSON;

public abstract class ARPCConnectListenner<R> {
	
	private XJSON json = new XJSON();
	
	public abstract R onConnect();
	
	protected void onConnectSend(ChannelContext ctx) {
		R r = onConnect();
		String name = r.getClass().getSimpleName().toLowerCase();
		String data = json.stringify(r);
		Bytes bytes = new Bytes();
		bytes.writeInt16(name.length());
		bytes.write(name.getBytes());
		bytes.writeInt32(data.length());
		bytes.write(data.getBytes(StandardCharsets.UTF_8));
		ctx.toLastOnWrite(bytes);
	}
	
}
