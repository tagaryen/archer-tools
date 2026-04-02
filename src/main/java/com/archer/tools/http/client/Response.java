package com.archer.tools.http.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.archer.net.Bytes;
import com.archer.xjson.JavaTypeRef;
import com.archer.xjson.XJSONStatic;


public class Response {
	public static final int HTTP_OK = 200;
	
	private static final int BUF_SIZE = 10 * 1024;
	private static final String JOIN_SEP = "; ";
	private static final int DEFAULT_HEADER_SIZE = 64;
	
	int httpStatus;
	
	String httpMessage;
	
	Map<String, String> headers;
	
	Bytes body;

	private Response() {}
	
	public int getHttpStatus() {
		return httpStatus;
	}

	public String getHttpMessage() {
		return httpMessage;
	}
	
	public String getHeader(String key) {
		for(Map.Entry<String, String> entry: headers.entrySet()) {
			if(entry.getKey().toLowerCase().equals(key.toLowerCase())) {
				return entry.getValue();
			}
		}
		return null;
	}
	
	public Map<String, String> getHeaders() {
		return headers;
	}

	public Bytes getBody() {
		return body;
	}
	
	public String getBodyAsString() {
		return new String(body.readAll(), StandardCharsets.UTF_8);
	}
	
	public <T> T getBodyAsObject(Class<T> cls) {
		return XJSONStatic.parse(new String(body.readAll(), StandardCharsets.UTF_8), cls);
	}
	
	public <T> T getBodyAsObject(JavaTypeRef<T> ref) {
		return XJSONStatic.parse(new String(body.readAll(), StandardCharsets.UTF_8), ref);
	}

	public static Response parseHttpConnection(HttpURLConnection conn) throws IOException {
		Response res = new Response();
		res.httpStatus = conn.getResponseCode();
		res.httpMessage = conn.getResponseMessage();
		res.headers = new HashMap<>();
		for(Map.Entry<String, List<String>> headerEntry: conn.getHeaderFields().entrySet()) {
			res.headers.put(headerEntry.getKey(), joinList(headerEntry.getValue()));
		}
		InputStream in;
		if(res.httpStatus == HTTP_OK) {
			in = conn.getInputStream();
		} else {
			in = conn.getErrorStream();
		}
		if(in != null) {
			res.body = new Bytes();
			byte[] out = new byte[BUF_SIZE];
			int bytes = 0;
			while((bytes = in.read(out)) >= 0) {
				res.body.write(Arrays.copyOfRange(out, 0, bytes));
			}
		}
		in.close();
		conn.disconnect();
        return res;
	}
	
	private static String joinList(List<String> list) {
		int i = list.size();
		StringBuilder sb = new StringBuilder(list.size() * DEFAULT_HEADER_SIZE);
		for(String l: list) {
			sb.append(l);
			if(i > 0) {
				sb.append(JOIN_SEP);
			}
		}
		return sb.toString();
	}
}
