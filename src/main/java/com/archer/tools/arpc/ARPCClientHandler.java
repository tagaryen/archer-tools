package com.archer.tools.arpc;

import java.util.concurrent.ConcurrentHashMap;

import com.archer.net.Bytes;
import com.archer.net.ChannelContext;

public class ARPCClientHandler extends ARPCHandler {

	private ConcurrentHashMap<String, ARPCClientListenner<?,?>> listenners = new ConcurrentHashMap<>();

	private ARPCClient cli;
	
	public ARPCClientHandler(ARPCClient cli) {
		this.cli = cli;
	}
	
	protected void addListenner(ARPCClientListenner<?, ?> listenner) {
		String name = listenner.getSecondClass().getSimpleName().toLowerCase();
		if(listenners.containsKey(name)) {
			throw new ARPCException("duplicated listenner for class " + listenner.getFirstClass().getName());
		}
		listenners.put(name, listenner);
	}
	
	@SuppressWarnings("unchecked")
	protected <R> ARPCClientListenner<?, R> getListenner(Class<R> rCls) {
		String name = rCls.getSimpleName().toLowerCase();
		ARPCClientListenner<?, ?> listenner = listenners.getOrDefault(name, null);
		if(listenner == null) {
			throw new ARPCException("can not found listenner for " + name);
		}
		return (ARPCClientListenner<?, R>) listenner;
	}
	
	@Override
	public void onConnect(ChannelContext ctx) {
		this.cli.setChannelContext(ctx);
	}
	
	@Override
	public void onRead(ChannelContext ctx, Bytes input) {
		int nameLen = input.readInt16();
		String name = new String(input.read(nameLen));
		ARPCClientListenner<?, ?> listenner = listenners.getOrDefault(name, null);
		if(listenner == null) {
			throw new ARPCException("can not found listenner for " + name);
		}
		listenner.onReturn(ctx, name, input);
	}

	@Override
	public void onDisconnect(ChannelContext ctx) {
		this.cli.unActive();
	}
}
