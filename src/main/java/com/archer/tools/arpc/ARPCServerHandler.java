package com.archer.tools.arpc;

import java.nio.charset.StandardCharsets;
import java.util.TreeMap;
import com.archer.net.Bytes;
import com.archer.net.ChannelContext;

class ARPCServerHandler extends ARPCHandler {
	
	private TreeMap<String, ARPCMatcher> urlMatcher = new TreeMap<>();
	
	public void addMessageListenner(String url, ARPCMessageListenner<?> listenner) {
		if(url.length() > Short.MAX_VALUE) {
			throw new ARPCException("url too long");
		}
		if(urlMatcher.containsKey(url)) {
			throw new ARPCException("Duplicated url " + url);
		}
		urlMatcher.put(url, new ARPCMatcher(url, listenner));
	}
	
	@Override
	public void onAccept(ChannelContext ctx) {}

	@Override
	public void onConnect(ChannelContext ctx) {}

	@Override
	public void onRead(ChannelContext ctx, Bytes input) {
		byte[] uriBs = input.read(input.readInt16());
		if(!check(uriBs)) {
			this.onError(ctx, new ARPCException("Remote send Not found"));
			return;
		}
		String url = new String(uriBs, StandardCharsets.UTF_8);
		ARPCMatcher matcher = urlMatcher.getOrDefault(url, null);
		if(matcher == null) {
			this.onError(ctx, new ARPCException("Can not found matcher for url " + url));
			this.sendNotFound(ctx);
			return ;
		}
		Bytes ret = matcher.handle(new String(input.readAll(), StandardCharsets.UTF_8));
		ctx.toLastOnWrite(ret);
	}
	
	@Override
	public void onDisconnect(ChannelContext ctx) {}

	@Override
	public void onWrite(ChannelContext ctx, Bytes output) {}


	@Override
	public void onSslCertificate(ChannelContext ctx, byte[] cert) {}
}
