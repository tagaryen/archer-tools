package com.archer.tools.arpc;

import java.util.concurrent.ConcurrentHashMap;

import com.archer.net.Bytes;
import com.archer.net.ChannelContext;

final class ARPCServerHandler extends ARPCHandler {
	
	private ConcurrentHashMap<String, ARPCServerListenner<?,?>> listenners = new  ConcurrentHashMap<>();

	public void addListenner(ARPCServerListenner<?,?> listenner) {
		String name = listenner.getFirstClass().getSimpleName().toLowerCase();
		if(listenners.containsKey(name)) {
			throw new ARPCException("duplicated listenner for class " + listenner.getFirstClass().getName());
		}
		listenners.put(name, listenner);
	}

	@Override
	public void onRead(ChannelContext ctx, Bytes input) {
		int nameLen = input.readInt16();
		String name = new String(input.read(nameLen));
		ARPCServerListenner<?,?> listenner = listenners.getOrDefault(name, null);
		if(listenner == null) {
			throw new ARPCException("can not found listenner for " + name);
		}
		listenner.onMessage(ctx, name, input);
	}
}
