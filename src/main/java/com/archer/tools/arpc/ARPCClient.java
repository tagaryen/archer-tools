package com.archer.tools.arpc;

import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import com.archer.net.Bytes;
import com.archer.net.Channel;
import com.archer.net.ChannelContext;
import com.archer.net.HandlerList;
import com.archer.net.handler.BaseFrameHandler;
import com.archer.net.ssl.SslContext;
import com.archer.xjson.JavaTypeRef;
import com.archer.xjson.XJSONStatic;

public class ARPCClient {
	public static final long TIMEOUT = 2000;

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
	
	public void addExceptionHandler(Consumer<Throwable> exHandler) {
		this.handler.addExceptionHandler(exHandler);
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
		long s = System.currentTimeMillis();
		synchronized(activeLock) {
			if(active) {
				return ;
			}
			try {
				activeLock.wait(TIMEOUT);
			} catch (InterruptedException ignore) {}
		}
		if(System.currentTimeMillis() - s >= TIMEOUT) {
			throw new ARPCException("Connect time out");
		}
	}
	
	public void close() {
		this.channel.close();
	}
	
	public <T> T call(String url, Object data, Class<T> clazz) {
		ARPCClientCallback<T> cb = new ARPCClientCallback<T>() {
			@Override
			public void onReceive(T r) {}
			@Override
			protected void handle(String text) {
				super.setResponse(XJSONStatic.parse(text, clazz));
				super.release();
			}
		};
		return call(url, data, cb);
	}
	
	public <T> T call(String url, Object data, JavaTypeRef<T> type) {
		ARPCClientCallback<T> cb = new ARPCClientCallback<T>() {
			@Override
			public void onReceive(T r) {}
			@Override
			protected void handle(String text) {
				super.setResponse(XJSONStatic.parse(text, type));
				super.release();
			}
		};
		return call(url, data, cb);
	}
	
	public <T> void callAsync(String url, Object data, ARPCClientCallback<T> callback) {
		handler.addCallback(url, callback);
		doSendAsync(url, data);
	}
	
	private <T> T call(String url, Object data, ARPCClientCallback<T> cb) {
		this.handler.addCallback(url, cb);
		doSendAsync(url, data);
		cb.await();
		if(cb.getResponse() == null) {
			throw new ARPCException("Can not get response");
		}
		return cb.getResponse();
	}
	
	public void doSendAsync(String url, Object data) {
		doConnect();
		Bytes out = new Bytes();
		byte[] uriBs = url.getBytes(StandardCharsets.UTF_8);
		out.writeInt16(uriBs.length);
		out.write(uriBs);
		out.write(XJSONStatic.stringify(data).getBytes(StandardCharsets.UTF_8));
		ctx.toLastOnWrite(out);
	}
}
