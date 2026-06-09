package com.archer.tools.arpc;

import java.util.function.Consumer;

import com.archer.net.HandlerList;
import com.archer.net.ServerChannel;
import com.archer.net.ssl.SslContext;

public class ARPCServer {
	
	private ServerChannel server;
	private String host;
	private int port;
	private int threadNums;
	
	private ARPCServerHandler handler;
	
	public ARPCServer(String host, int port) {
		this(host, port, null);
	}
	
	public ARPCServer(String host, int port, SslContext ctx) {
		this(host, port, ctx, 0);
	}
	
	public ARPCServer(String host, int port, SslContext ctx, int threadNums) {
		this.host = host;
		this.port = port;
		this.server = new ServerChannel(ctx);
		this.threadNums = threadNums;
		this.handler = new ARPCServerHandler();
	}
	
	public void addMessageListenner(String url, ARPCMessageListenner<?> listenner) {
		this.handler.addMessageListenner(url, listenner);
	}
	
	public void addExceptionHandler(Consumer<Throwable> exHandler) {
		this.handler.addExceptionHandler(exHandler);
	}
	
	public void start() {
		HandlerList handlers = new HandlerList();
		handlers.add(handler);
		server.setLoopThreads(threadNums);
		server.handlerList(handlers);
		server.listen(host, port);
	}
	
	public void close() {
		this.server.close();
	}
}
