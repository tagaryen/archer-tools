package com.archer.tools.arpc.x;

import com.archer.net.ChannelContext;

@Deprecated
public class ARPCClientHandler extends ARPCHandler {
	
	private ARPCClient cli;
	
	public ARPCClientHandler(ARPCClient cli) {
		this.cli = cli;
	}
	
	@Override
	public void onConnect(ChannelContext ctx) {
		this.cli.active(ctx);
		if(this.connectListenner != null) {
			this.connectListenner.onConnectSend(ctx);
		}
	}

	@Override
	public void onDisconnect(ChannelContext ctx) {
		this.cli.unActive();
	}
}
