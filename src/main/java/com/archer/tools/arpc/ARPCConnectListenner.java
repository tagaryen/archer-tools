package com.archer.tools.arpc;

import java.nio.charset.StandardCharsets;
import java.util.Random;

import com.archer.net.Bytes;
import com.archer.net.ChannelContext;
import com.archer.xjson.XJSON;

public abstract class ARPCConnectListenner<R> {

	private static Random rand = new Random();
	
	private XJSON json = new XJSON();
	
	public abstract R genConnectMessage();
	
	protected void onConnectSend(ChannelContext ctx) {
		R r = genConnectMessage();
		byte[] seq = new byte[16];
		rand.nextBytes(seq);
		byte[] name = r.getClass().getSimpleName().toLowerCase().getBytes(StandardCharsets.UTF_8);
		byte[] data = json.stringify(r).getBytes(StandardCharsets.UTF_8);
		Bytes bytes = new Bytes();
		bytes.write(seq);
		bytes.writeInt16(name.length);
		bytes.write(name);
		bytes.write(data);
		ctx.toLastOnWrite(bytes);
	}
	
}
