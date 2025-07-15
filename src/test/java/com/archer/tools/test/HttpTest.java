package com.archer.tools.test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import com.archer.net.http.ContentType;
import com.archer.net.http.HttpRequest;
import com.archer.net.http.HttpResponse;
import com.archer.net.http.client.NativeRequest;
import com.archer.net.http.multipart.MultipartParser;
import com.archer.tools.http.client.FormData;
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
	
	public static void testUpload() {
		FormData data = new FormData();
		data.put("Node-Id", "alice");
		try {
			data.putFile("file", "smalld.csv", Files.readAllBytes(Paths.get("D:/da.csv")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		NativeRequest.Options opts = new NativeRequest.Options();
		HashMap<String, String> headers = new HashMap<>();
		headers.put("User-Token","1b32a1cdad2249a8a7e748499845abe6");
		opts.headers(headers);
		JSONRequest.postAsync("http://10.32.122.172:8080/api/v1alpha1/data/upload", data, opts, BaseResponse.class, (res) -> {
			System.out.println(res.getStatus().msg);
			System.out.println(XJSONStatic.stringify(res.getData()));
		});
	}
	
	public static void main(String args[]) {
//		startHttpServer();
		testUpload();
	}
	
	public static class BaseResponse {

	    private StatusCode status;

	    private Object data;

		public StatusCode getStatus() {
			return status;
		}

		public void setStatus(StatusCode status) {
			this.status = status;
		}

		public Object getData() {
			return data;
		}

		public void setData(Object data) {
			this.data = data;
		}
	}

    public static class StatusCode {
        private int code;
        private String msg;
		public int getCode() {
			return code;
		}
		public void setCode(int code) {
			this.code = code;
		}
		public String getMsg() {
			return msg;
		}
		public void setMsg(String msg) {
			this.msg = msg;
		}
    }
}
