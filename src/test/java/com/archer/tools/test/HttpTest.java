package com.archer.tools.test;

import java.nio.charset.StandardCharsets;

import com.archer.net.http.ContentType;
import com.archer.net.http.HttpRequest;
import com.archer.net.http.HttpResponse;
import com.archer.tools.http.client.JSONRequest;
import com.archer.tools.http.server.HttpListener;
import com.archer.tools.http.server.HttpServerException;
import com.archer.tools.http.server.SimpleHttpServer;
import com.archer.xjson.XJSONStatic;

public class HttpTest {
	
	public static void startHttpServer() {
		SimpleHttpServer http = new SimpleHttpServer();
		try {
			http.listen("127.0.0.1", 9677, new HttpListener() {

				@Override
				public void inComingMessage(HttpRequest req, HttpResponse res) {
					res.setContentType(ContentType.APPLICATION_JSON);
					res.setContent(XJSONStatic.stringify(new HttpVo(18, "帅哥")).getBytes(StandardCharsets.UTF_8));
				}

				@Override
				public void onServerException(HttpRequest req, HttpResponse res, Throwable t) {
					
				}});
		} catch (HttpServerException e) {
			e.printStackTrace();
			return ;
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		try {
			JSONRequest.getAsync("http://127.0.0.1:9677/", HttpVo.class, vo -> {
				System.out.println("vo.name = " + vo.getName());
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String args[]) {
		startHttpServer();
	}
}
