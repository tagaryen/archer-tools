package com.archer.tools.arpc;

import java.nio.charset.StandardCharsets;

import com.archer.net.Bytes;
import com.archer.net.ChannelContext;

class ARPCClientHandler extends ARPCHandler {
	
	private ARPCClient cli;
	private ARPCMap cbCache = new ARPCMap();
	
	public ARPCClientHandler(ARPCClient cli) {
		this.cli = cli;
	}
	
	protected void addCallback(byte[] nonce, ARPCClientCallback<?> cb) {
		cbCache.saveCallback(nonce, cb);
	}
	
	@Override
	public void onConnect(ChannelContext ctx) {
		this.cli.active(ctx);
	}

	@Override
	public void onRead(ChannelContext ctx) {
		while(true) {
			int totalLen = ctx.readInt32();
			if(totalLen <= 0) {
				return ;
			}
			byte[] dataBs = ctx.read(totalLen);
			if(dataBs.length != totalLen) {
				this.onError(ctx, new ARPCException("Remote send Data that can not be parsed"));
			}
			Bytes data = new Bytes(dataBs);
			byte[] nonce = data.read(16);
			byte[] uriBs = data.read(data.readInt16());
			ARPCClientCallback<?> cb = cbCache.findCallback(nonce);
			if(cb == null) {
				this.onError(ctx, new ARPCException("Invalid nonce"));
			} else {
				if(isNotFound(uriBs)) {
					cb.handle(null, new ARPCException("Server url Not found"));
				} else {
					cb.handle(new String(data.readAll(), StandardCharsets.UTF_8), null);
				}
			}
		}
	}
	
	@Override
	public void onDisconnect(ChannelContext ctx) {
		this.cli.unActive();
	}
}
