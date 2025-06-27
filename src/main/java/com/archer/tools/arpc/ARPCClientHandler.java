package com.archer.tools.arpc;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;

import com.archer.net.Bytes;
import com.archer.net.ChannelContext;

class ARPCClientHandler extends ARPCHandler {
	
	private ConcurrentHashMap<String, ARPCClientCallback<?>> cbCache = new ConcurrentHashMap<>();
	
	private ARPCClient cli;
	
	public ARPCClientHandler(ARPCClient cli) {
		this.cli = cli;
	}
	
	protected void addCallback(String url, ARPCClientCallback<?> cb) {
		cbCache.put(url, cb);
	}
	
	@Override
	public void onConnect(ChannelContext ctx) {
		this.cli.active(ctx);
	}

	@Override
	public void onRead(ChannelContext ctx, Bytes input) {
		byte[] uriBs = input.read(input.readInt16());
		if(!check(uriBs)) {
			this.onError(ctx, new ARPCException("Remote send Not found"));
			return;
		}
		String url = new String(uriBs, StandardCharsets.UTF_8);
		ARPCClientCallback<?> cb = cbCache.getOrDefault(url, null);
		if(cb == null) {
			this.onError(ctx, new ARPCException("Can not found url " + url));
			return ;
		}
		cb.handle(new String(input.readAll(), StandardCharsets.UTF_8));
	}
	
	@Override
	public void onDisconnect(ChannelContext ctx) {
		this.cli.unActive();
	}
}
