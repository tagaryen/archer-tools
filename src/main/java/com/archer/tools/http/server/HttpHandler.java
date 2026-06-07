package com.archer.tools.http.server;

import java.time.LocalDateTime;

import com.archer.net.http.ContentType;
import com.archer.net.http.HttpAbstractHandler;
import com.archer.net.http.HttpRequest;
import com.archer.net.http.HttpResponse;
import com.archer.net.http.HttpStatus;

final class HttpHandler extends HttpAbstractHandler {
	private HttpListener listener;
	
	public HttpHandler(HttpListener listener) {
		this.listener = listener;
	}

	@Override
	public void handle(HttpRequest req, HttpResponse res) {
		res.setContentType(ContentType.APPLICATION_JSON);
		try {
			listener.inComingMessage(req, res);
		} catch(Throwable t) {			
			if(res.getStatus() == null) {
				String body = "{" +
						"\"server\": \"Archer Http Server Support\"," +
						"\"time\": \"" + LocalDateTime.now().toString() + "\"," +
						"\"status\": \"" + HttpStatus.SERVICE_UNAVAILABLE.getStatus() + "\"" +
					"}";
				
				res.setStatus(HttpStatus.SERVICE_UNAVAILABLE);
				res.setContentType(ContentType.APPLICATION_JSON);
				res.sendContent(body.getBytes());
			}
			handleException(t);
		}
	}

	@Override
	public void handleException(Throwable t) {
		try {
			listener.onServerException(t);
		} catch(Exception ignore) {}
	}
}
