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
	
	protected byte[] handle(byte[] nonce, String text) {
		Object ret = this.listenner.onMessage(XJSONStatic.parse(text, this.paramType));
		byte[] data = null;
		if(ret == null) {
			data = new byte[]{'{', '}'};
		} else {
			data = XJSONStatic.stringify(ret).getBytes(StandardCharsets.UTF_8);
		}
		byte[] uriBs = uri.getBytes(StandardCharsets.UTF_8);
		int length = 16 + 2 + uriBs.length + data.length;
		Bytes out = new Bytes(4 + length);
		out.writeInt32(length);
		out.write(nonce);
		out.writeInt16(uriBs.length);
		out.write(uriBs);
		out.write(data);
		return out.array();
		
	}
}
