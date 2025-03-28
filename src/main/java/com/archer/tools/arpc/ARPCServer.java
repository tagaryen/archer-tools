package com.archer.tools.arpc;

import com.archer.net.HandlerList;
import com.archer.net.ServerChannel;
import com.archer.net.handler.BaseFrameHandler;
import com.archer.net.ssl.SslContext;

public class ARPCServer {
	
	private ServerChannel server;
	private String host;
	private int port;
	private int threadNums;
	
	private ARPCHandler handler;
	
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
		this.handler = new ARPCHandler();
	}
	
	public void registerConnectListener(ARPCConnectListenner<?> listenner) {
		this.handler.setConnectListenner(listenner);
	}
	
	public void registerExceptionListener(ARPCExceptionListenner listenner) {
		this.handler.setExceptionListenner(listenner);
	}
	
	public void registerListener(ARPCMessageListenner<?,?> ...listenners) {
		for(ARPCMessageListenner<?,?> listenner: listenners) {
			this.handler.addListenner(listenner);
		}
	}
	
	public void start() {
		HandlerList handlers = new HandlerList();
		handlers.add(new BaseFrameHandler(), handler);
		server.setReadThreads(threadNums);
		server.handlerList(handlers);
		server.listen(host, port);
	}
	
	public void close() {
		this.server.close();
	}
}
