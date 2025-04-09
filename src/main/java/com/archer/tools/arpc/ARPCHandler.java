package com.archer.tools.arpc;

import java.util.HashMap;

import com.archer.net.Bytes;
import com.archer.net.ChannelContext;
import com.archer.net.handler.Handler;

class ARPCHandler implements Handler {

	private ARPCConnectListenner<?> connectListenner;

	private HashMap<String, ARPCMessageListenner<?,?>> sendMsg = new HashMap<>();
	
	private HashMap<String, ARPCMessageListenner<?,?>> recvMsg = new HashMap<>();
	
	private ARPCExceptionListenner exListenner;
	
	public void setConnectListenner(ARPCConnectListenner<?> connectListenner) {
		this.connectListenner = connectListenner;
	}
	public void setExceptionListenner(ARPCExceptionListenner exListenner) {
		this.exListenner = exListenner;
	}
	public void addListenner(ARPCMessageListenner<?, ?> listenner) {
		sendMsg.put(listenner.getSendClass().getSimpleName().toLowerCase(), listenner);
		recvMsg.put(listenner.getRecvClass().getSimpleName().toLowerCase(), listenner);
	}
	
	@SuppressWarnings("unchecked")
	public <Send, Recv>  ARPCMessageListenner<Send, Recv> getListenner(Class<Send> sendCls, Class<Recv> recvCls) {
		ARPCMessageListenner<?, ?> listenner = sendMsg.getOrDefault(sendCls.getSimpleName().toLowerCase(), null);
		if(listenner == null) {
			throw new ARPCException("can not found message listenner for " + sendCls);
		}
		return (ARPCMessageListenner<Send, Recv>) listenner;
	}
	
	public ARPCMessageListenner<?, ?> getListennerBySendMsgName(String sendName) {
		ARPCMessageListenner<?, ?> listenner = sendMsg.getOrDefault(sendName, null);
		if(listenner == null) {
			throw new ARPCException("can not found message listenner for " + sendName);
		}
		return listenner;
	}
	

	public ARPCMessageListenner<?, ?> getListennerByRecvMsgName(String recvName) {
		ARPCMessageListenner<?, ?> listenner = recvMsg.getOrDefault(recvName, null);
		if(listenner == null) {
			throw new ARPCException("can not found message listenner for " + recvName);
		}
		return listenner;
	}
	
	@Override
	public void onAccept(ChannelContext ctx) {}

	@Override
	public void onConnect(ChannelContext ctx) {
		if(this.connectListenner != null) {
			this.connectListenner.onConnectSend(ctx);
		}
	}

	@Override
	public void onRead(ChannelContext ctx, Bytes input) {
		byte[] seq = input.read(16);
		int nameLen = input.readInt16();
		String name = new String(input.read(nameLen));
		ARPCMessageListenner<?, ?> listenner = getListennerByRecvMsgName(name);
		if(listenner == null) {
			throw new ARPCException("can not found listenner for " + name);
		}
		listenner.handleMessage(ctx, seq, input);
	}
	
	@Override
	public void onDisconnect(ChannelContext ctx) {}

	@Override
	public void onWrite(ChannelContext ctx, Bytes output) {
		ctx.toLastOnWrite(output);
	}

	@Override
	public void onError(ChannelContext ctx, Throwable t) {
		if(this.exListenner != null) {
			this.exListenner.onException(t);
		} else {
			t.printStackTrace();
		}
	}

	@Override
	public void onSslCertificate(ChannelContext ctx, byte[] cert) {}
}
