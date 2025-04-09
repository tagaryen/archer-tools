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
	
	public void registerListener(ARPCClientMessageListenner<?,?> ...listenners) {
		for(ARPCClientMessageListenner<?,?> listenner: listenners) {
			this.handler.addListenner(listenner);
		}
	}
	
	protected void active(ChannelContext ctx) {
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
				activeLock.wait(ARPCClientMessageListenner.TIMEOUT);
			} catch (InterruptedException ignore) {}
		}
	}
	
	public void close() {
		this.channel.close();
	}
	
	@SuppressWarnings("unchecked")
	public <Send, Recv> Recv callRemote(Send s, Class<Recv> rcls) {
		if(!active) {
			doConnect();
		}
		ARPCClientMessageListenner<Send, Recv> listenner = 
				(ARPCClientMessageListenner<Send, Recv>) handler.getListenner(s.getClass(), rcls);
		ARPCClientCallback<Recv> cb = new ARPCClientCallback<Recv>() {
			@Override
			public void onReceive(Recv r) {
				response = r;
				synchronized(lock) {
					lock.notifyAll();
				}
			}};
		listenner.sendAsync(ctx, s, cb);
		RuntimeException ex = cb.await();
		if(ex != null) {
			throw ex;
		}
		if(cb.response == null) {
			throw new ARPCException("can not get response from remote");
		}
		return cb.response;
	}
	
	@SuppressWarnings("unchecked")
	public <Send, Recv> void callRemoteAsync(Send s, ARPCClientCallback<Recv> callback) {
		if(!active) {
			doConnect();
		}
		ARPCClientMessageListenner<Send, Recv> listenner = 
				(ARPCClientMessageListenner<Send, Recv>) this.handler.getListenner(s.getClass(), callback.getRecvClass());
		listenner.sendAsync(ctx, s, callback);
	}
}
