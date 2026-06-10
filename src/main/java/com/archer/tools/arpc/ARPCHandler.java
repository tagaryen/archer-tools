package com.archer.tools.arpc;

import java.util.function.Consumer;

import com.archer.net.Bytes;
import com.archer.net.ChannelContext;
import com.archer.net.handler.Handler;

abstract class ARPCHandler implements Handler {
	protected static final byte[] NOT_FOUND_URI = {0, 0, 0, 0, 'N', 'O', 'T', 'F', 'O', 'U', 'N', 'D'};
	protected static final byte[] PARAM_ERR_URI = {0, 0, 0, 0, 'P', 'A', 'R', 'A', 'M', 'E', 'R', 'R'};
	
	private Consumer<Throwable> exHandler = null;
	
	public void addExceptionHandler(Consumer<Throwable> exHandler) {
		this.exHandler = exHandler;
	}
	
	@Override
	public void onAccept(ChannelContext ctx) {}

	@Override
	public void onConnect(ChannelContext ctx) {}

	@Override
	public void onDisconnect(ChannelContext ctx) {}

	@Override
	public void onWrite(ChannelContext ctx, byte[] output) {}

	@Override
	public void onError(ChannelContext ctx, Throwable t) {
		if (this.exHandler == null) {
			t.printStackTrace();
		} else {
			this.exHandler.accept(t);
		}
	}

	@Override
	public void onSslCertificate(ChannelContext ctx, byte[] cert) {}
	
	protected boolean isNotFound(byte[] uriBs) {
		if(uriBs.length == NOT_FOUND_URI.length) {
			for(int i = 0; i < NOT_FOUND_URI.length; i++) {
				if(uriBs[i] != NOT_FOUND_URI[i]) {
					return false;
				}
			}
		}
		return true;
	}
	
	protected boolean isParamErr(byte[] uriBs) {
		if(uriBs.length == PARAM_ERR_URI.length) {
			for(int i = 0; i < PARAM_ERR_URI.length; i++) {
				if(uriBs[i] != PARAM_ERR_URI[i]) {
					return false;
				}
			}
		}
		return true;
	}
	
	protected void sendNotFound(ChannelContext ctx, byte[] nonce) {
		int length = 16 + 2 + NOT_FOUND_URI.length;
		Bytes out = new Bytes(4 + length);
		out.writeInt32(length);
		out.write(nonce);
		out.writeInt16(NOT_FOUND_URI.length);
		out.write(NOT_FOUND_URI);
		ctx.toLastOnWrite(out.array());
	}
}
