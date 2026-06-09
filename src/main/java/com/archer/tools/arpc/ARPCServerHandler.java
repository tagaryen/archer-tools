package com.archer.tools.arpc;

import java.nio.charset.StandardCharsets;
import com.archer.net.Bytes;
import com.archer.net.ChannelContext;
import com.archer.tools.java.ArcherMap;

class ARPCServerHandler extends ARPCHandler {
	
	private ArcherMap<String, ARPCMatcher> urlMatcher = new ArcherMap<>(128);
	
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
	public void onConnect(ChannelContext ctx) {
		ctx.addChannelAttachment(new PkgSize());
	}

	@Override
	public void onRead(ChannelContext ctx) {
		PkgSize pkgSize = (PkgSize) ctx.getChannelAttachment();
		if(pkgSize == null) {
			return ;
		}
		synchronized(pkgSize) {
			while(pkgSize.size == 0) {
				pkgSize.size = ctx.readInt32();
				if(pkgSize.size < 0) {
					pkgSize.size = 0;
					return ;
				}
				if(ctx.readableSize() < pkgSize.size) {
					return ;
				}
				byte[] inputBs = ctx.read(pkgSize.size);
				if(inputBs.length != pkgSize.size) {
					pkgSize.size = 0;
					this.onError(ctx, new ARPCException("Remote send Data can not be parsed"));
					ctx.close();
					return;
				}
				Bytes input = new Bytes(inputBs);
				byte[] nonce = input.read(16);
				int uriLen = input.readInt16();
				byte[] uriBs = input.read(uriLen);
				String url = new String(uriBs, StandardCharsets.UTF_8);
				ARPCMatcher matcher = urlMatcher.getOrDefault(url, null);
				if(matcher == null) {
					pkgSize.size = 0;
					this.onError(ctx, new ARPCException("Can not found matcher for url " + url));
					this.sendNotFound(ctx, nonce);
					return ;
				}
				pkgSize.size = 0;
				try {
					byte[] ret = matcher.handle(nonce, new String(input.readAll(), StandardCharsets.UTF_8));
					ctx.toLastOnWrite(ret);
				} catch(Exception e) {
					sendParamErr(ctx, nonce);
				}
			}
		}
	}
	
	@Override
	public void onDisconnect(ChannelContext ctx) {}

	@Override
	public void onWrite(ChannelContext ctx, byte[] output) {}
	
	private void sendParamErr(ChannelContext ctx, byte[] nonce) {
		int length = 16 + 2 + PARAM_ERR_URI.length;
		Bytes out = new Bytes(4 + length);
		out.writeInt32(length);
		out.write(nonce);
		out.writeInt16(PARAM_ERR_URI.length);
		out.write(PARAM_ERR_URI);
		ctx.toLastOnWrite(out.array());
	}
	
	class PkgSize {
		volatile int size = 0;
	}
}
