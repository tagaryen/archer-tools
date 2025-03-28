package com.archer.tools.arpc;

import java.nio.charset.StandardCharsets;

import com.archer.net.Bytes;
import com.archer.net.ChannelContext;
import com.archer.xjson.XJSON;

public abstract class ARPCConnectListenner<R> {
	
	private XJSON json = new XJSON();
	
	public abstract R genConnectMessage();
	
	protected void onConnectSend(ChannelContext ctx) {
		R r = genConnectMessage();
		byte[] name = r.getClass().getSimpleName().toLowerCase().getBytes(StandardCharsets.UTF_8);
		byte[] data = json.stringify(r).getBytes(StandardCharsets.UTF_8);
		Bytes bytes = new Bytes();
		bytes.writeInt16(name.length);
		bytes.write(name);
		bytes.write(data);
		ctx.toLastOnWrite(bytes);
	}
	
}
