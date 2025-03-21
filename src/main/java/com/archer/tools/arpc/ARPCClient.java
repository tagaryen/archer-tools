package com.archer.tools.arpc;

import com.archer.net.Channel;
import com.archer.net.ChannelContext;
import com.archer.net.HandlerList;
import com.archer.net.handler.BaseFrameHandler;
import com.archer.net.ssl.SslContext;

public class ARPCClient {

	private Channel channel;
	private String host;
	private int port;
	
	private ARPCClientHandler handler;
	private ChannelContext ctx;
	private Object activeLock = new Object();
	private volatile boolean active = false;
	
	public ARPCClient(String host, int port) {
		this(host, port, null);
	}
	
	public ARPCClient(String host, int port, SslContext ctx) {
		this.host = host;
		this.port = port;
		this.channel = new Channel(ctx);
		this.handler = new ARPCClientHandler(this);

		HandlerList handlers = new HandlerList();
		handlers.add(new BaseFrameHandler(), handler);
		this.channel.handlerList(handlers);
	}
	
	public void registerExceptionListener(ARPCExceptionListenner listenner) {
		this.handler.setExceptionListenner(listenner);
	}
	
	public void registerListener(ARPCClientListenner<?,?> ...listenners) {
		for(ARPCClientListenner<?,?> listenner: listenners) {
			this.handler.addListenner(listenner);
		}
	}
	
	protected void setChannelContext(ChannelContext ctx) {
		this.ctx = ctx;
		this.active = true;
		synchronized(activeLock) {
			activeLock.notifyAll();
		}
	}
	
	protected void unActive() {
		this.active = false;
		this.ctx = null;
	}
	
	protected void doConnect() {
		channel.connect(host, port);
		synchronized(activeLock) {
			if(active) {
				return ;
			}
			try {
				activeLock.wait(ARPCClientListenner.TIMEOUT);
			} catch (InterruptedException ignore) {}
		}
	}
	
	public void close() {
		this.channel.close();
	}
	
	@SuppressWarnings("unchecked")
	public <S, R> R callRemote(S s, Class<R> rcls) {
		if(!active) {
			doConnect();
		}
		ARPCClientListenner<S, R> listenner = (ARPCClientListenner<S, R>) this.handler.getListenner(rcls);
		listenner.sendParam(ctx, s);
		listenner.await();
		R r = listenner.getResponse();
		if(r == null) {
			ARPCException ex = listenner.getException();
			if(listenner.getException() != null) {
				throw ex;
			}
			throw new ARPCException("can not get return with param " + listenner.stringifyObject(s));
		}
		return r;
	}
	
	@SuppressWarnings("unchecked")
	public <S, R> void callRemoteAsync(S s, ARPCClientCallback<R> callback) {
		if(!active) {
			doConnect();
		}
		ARPCClientListenner<S, R> listenner = (ARPCClientListenner<S, R>) this.handler.getListenner(callback.getReturnClass());
		listenner.setCallback(callback);
		listenner.sendParam(ctx, s);
	}
}
