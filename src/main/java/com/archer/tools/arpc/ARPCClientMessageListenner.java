package com.archer.tools.arpc;

import java.nio.charset.StandardCharsets;
import java.util.Random;

import com.archer.net.Bytes;
import com.archer.net.ChannelContext;

public class ARPCClientMessageListenner<Send, Recv> extends ARPCMessageListenner<Send, Recv> {
	
	public static final long TIMEOUT = 2000;
	
	private static ARCPCallbackMap map = new ARCPCallbackMap();
	private static Random r = new Random();
	
	public ARPCClientMessageListenner(Class<Send> sendCls, Class<Recv> recvCls) {
		super(sendCls, recvCls);
	}
	
	protected void sendAsync(ChannelContext ctx, Send send, ARPCClientCallback<Recv> callback) {
		byte[] name = send.getClass().getSimpleName().toLowerCase().getBytes(StandardCharsets.UTF_8);
		byte[] seq = new byte[16];
		r.nextBytes(seq);
		map.add(seq, callback);
		Bytes output = new Bytes();
		output.write(seq);
		output.writeInt16(name.length);
		output.write(name);
		output.write(xjson.stringify(send).getBytes(StandardCharsets.UTF_8));
		ctx.toLastOnWrite(output);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void handleMessage(ChannelContext ctx, byte[] seq, Bytes data) {
		ARPCClientCallback<Recv> cb = (ARPCClientCallback<Recv>) map.get(seq);
		Recv recv = xjson.parse(new String(data.readAll(), StandardCharsets.UTF_8), getRecvClass());
		if(cb == null) {
			throw new ARPCException("can not handle message " + getRecvClass().getName());
		}
		cb.onReceive(recv);
	}

	@Override
	public Send onReceiveAndGenerateSend(ChannelContext ctx, Recv recv) {return null;}
}
