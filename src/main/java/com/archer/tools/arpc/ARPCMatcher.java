package com.archer.tools.arpc;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

import com.archer.net.Bytes;
import com.archer.xjson.XJSONStatic;

class ARPCMatcher {
	
	private String uri;
	
	private ARPCMessageListenner<?> listenner;
	
	private Type paramType;

	public ARPCMatcher(String uri, ARPCMessageListenner<?> listenner) {
		this.uri = uri;
		this.listenner = listenner;
		this.paramType = listenner.getJavaType();
	}

	public String getUri() {
		return uri;
	}

	public ARPCMessageListenner<?> getListenner() {
		return listenner;
	}

	public Type getParamType() {
		return paramType;
	}
	
	protected Bytes handle(String text) {
		Object ret = this.listenner.onMessage(XJSONStatic.parse(text, this.paramType));
		if(ret == null) {
			ret = new Object();
		}
		Bytes out = new Bytes();
		byte[] uriBs = uri.getBytes(StandardCharsets.UTF_8);
		out.writeInt16(uriBs.length);
		out.write(uriBs);
		out.write(XJSONStatic.stringify(ret).getBytes(StandardCharsets.UTF_8));
		return out;
		
	}
}
