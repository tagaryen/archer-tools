package com.archer.tools.arpc;

import com.archer.net.Bytes;
import com.archer.net.ChannelContext;
import com.archer.net.handler.Handler;

public abstract class ARPCHandler implements Handler {

	private ARPCConnectListenner<?> connectListenner;
	
	private ARPCExceptionListenner exListenner;
	
	public void setConnectListenner(ARPCConnectListenner<?> connectListenner) {
		this.connectListenner = connectListenner;
	}
	public void setExceptionListenner(ARPCExceptionListenner exListenner) {
		this.exListenner = exListenner;
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
	public void onDisconnect(ChannelContext ctx) {}

	@Override
	public void onWrite(ChannelContext ctx, Bytes output) {}

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
