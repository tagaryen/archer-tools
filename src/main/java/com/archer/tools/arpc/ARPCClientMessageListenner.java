package com.archer.tools.arpc;

import java.nio.charset.StandardCharsets;

import com.archer.net.Bytes;
import com.archer.net.ChannelContext;

public class ARPCClientMessageListenner<Send, Recv> extends ARPCMessageListenner<Send, Recv> {
	
	public static final long TIMEOUT = 2000;

	private volatile boolean sync = true;
	private Recv response;
	private Object lock = new Object();
	private ARPCClientCallback<Recv> consumer;
	private RuntimeException e;
	
	public ARPCClientMessageListenner(Class<Send> sendCls, Class<Recv> recvCls) {
		super(sendCls, recvCls);
	}

	protected RuntimeException getException() {
		return this.e;
	}
	
	protected Recv getResponse() {
		return this.response;
	}
	
	protected void send(ChannelContext ctx, Send send) {
		sendAsync(ctx, send, null);
		sync = true;
		long start = System.currentTimeMillis();
		synchronized(lock) {
			try {
				lock.wait(TIMEOUT);
			} catch (InterruptedException ignore) {}
		}
		if(start + TIMEOUT <= System.currentTimeMillis()) {
			e = new ARPCException("can not get response from remote");
		}
	}
	
	protected void sendAsync(ChannelContext ctx, Send send, ARPCClientCallback<Recv> callback) {
		sync = false;
		consumer = callback;

		byte[] name = send.getClass().getSimpleName().toLowerCase().getBytes(StandardCharsets.UTF_8);
		Bytes output = new Bytes();
		output.writeInt16(name.length);
		output.write(name);
		output.write(xjson.stringify(send).getBytes(StandardCharsets.UTF_8));
		ctx.toLastOnWrite(output);
	}
	
	@Override
	protected void handleMessage(ChannelContext ctx, Bytes data) {
		Recv recv = xjson.parse(new String(data.readAll(), StandardCharsets.UTF_8), getRecvClass());
		if(sync) {
			this.response = recv;
			synchronized(lock) {
				lock.notifyAll();
			}
		} else {
			if(consumer != null) {
				consumer.onReceive(recv);
				consumer = null;
			}
			sync = true;
		}
	}

	@Override
	public Send onReceiveAndGenerateSend(ChannelContext ctx, Recv recv) {return null;}
}
