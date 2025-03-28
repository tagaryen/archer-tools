package com.archer.tools.arpc;

import com.archer.net.ChannelContext;

public class ARPCClientHandler extends ARPCHandler {
	
	private ARPCClient cli;
	
	public ARPCClientHandler(ARPCClient cli) {
		this.cli = cli;
	}
	
	@Override
	public void onConnect(ChannelContext ctx) {
		this.cli.active(ctx);
	}

	@Override
	public void onDisconnect(ChannelContext ctx) {
		this.cli.unActive();
	}
}
