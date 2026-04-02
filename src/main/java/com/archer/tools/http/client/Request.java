package com.archer.tools.http.client;

import javax.net.ssl.*;

import com.archer.net.Bytes;
import com.archer.net.http.HttpException;
import com.archer.net.http.HttpStatus;
import com.archer.net.http.multipart.FormData;
import com.archer.net.http.multipart.MultipartParser;
import com.archer.net.util.HexUtil;

import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author xuyi
 */
public class Request {

    private static final int CONNECT_TIMEOUT = 5000;
    private static final int READ_TIMEOUT = 10000;

    private static final String HTTP = "http://";
    private static final String HTTPS = "https://";
    private static final String DEFAULT_CONTENT_TYPE = "application/json; charset=utf-8";
    private static final String HEADER_CONTENT_TYPE = "content-type";
    private static final String HEADER_CONTENT_LENGTH = "content-length";

    private static void setHeaders(HttpURLConnection conn, Map<String, String> headers) {
    	if(headers == null) {
    		return ;
    	}
        for(Map.Entry<String, String> en: headers.entrySet()) {
            conn.setRequestProperty(en.getKey(), en.getValue());
        }
    }

    public static Response get(String httpUrl) {
        return get(httpUrl, null);
    }

    public static Response post(String httpUrl, byte[] body) {
        return post(httpUrl, body, null);
    }

    public static Response put(String httpUrl, byte[] body) {
        return put(httpUrl, body, null);
    }

    public static Response delete(String httpUrl, byte[] body) {
        return delete(httpUrl, body, null);
    }

    public static Response get(String httpUrl, Options options) {
        return request("GET", httpUrl, (byte[])null, options);
    }

    public static Response post(String httpUrl, byte[] body, Options options) {
        return request("POST", httpUrl, body, options);
    }

    public static Response put(String httpUrl, byte[] body, Options options) {
        return request("PUT", httpUrl, body, options);
    }

    public static Response delete(String httpUrl, byte[] body, Options options) {
        return request("DELETE", httpUrl, body, options);
    }

    public static Response request(String httpMethod, String httpUrl, FormData body, Options options) {
    	if(options == null) {
    		options = new Options();
    	}
		if(body != null) {
    		removeHeaders(options.headers, "Content-Type", "Transfer-Encoding");
    		options.headers.put("Content-Type", MultipartParser.MULTIPART_HEADER + body.getBoundary());
    		options.headers.put("Transfer-Encoding", "chunked");
		}
        HttpURLConnection conn = getHttpConnection(httpMethod, httpUrl, options);
        try {
            if(body != null) {
				Bytes out = null;
				Bytes chunk = new Bytes(FormData.CACHE_SIZE + 16);
				while((out = body.read()) != null) {
					chunk.clear();
					chunk.write((HexUtil.intToHex(out.available()) + "\r\n").getBytes());
					chunk.readFromBytes(out);
					chunk.write("\r\n".getBytes());
					conn.getOutputStream().write(chunk.readAll());
				}
				conn.getOutputStream().write("0\r\n\r\n".getBytes());
            }
            
            return Response.parseHttpConnection(conn);
        } catch(Exception e) {
        	throw new HttpException(HttpStatus.BAD_REQUEST, e);
        }
    }
    
    
    public static Response request(String httpMethod, String httpUrl, byte[] body, Options options) {
    	if(options == null) {
    		options = new Options();
    	}
		if(body != null && checkContentTypeExists(options.headers)) {
			options.getHeaders().put(HEADER_CONTENT_TYPE, DEFAULT_CONTENT_TYPE);
			options.getHeaders().put(HEADER_CONTENT_LENGTH, String.valueOf(body.length));
		}
        HttpURLConnection conn = getHttpConnection(httpMethod, httpUrl, options);
        try {
            if(body != null) {
                conn.getOutputStream().write(body);
            }
            return Response.parseHttpConnection(conn);
        } catch(Exception e) {
        	throw new HttpException(HttpStatus.BAD_REQUEST, e);
        }
    }

    private static HttpURLConnection getHttpConnection(String httpMethod, String httpUrl, Options options) {
    	if(httpMethod == null || httpUrl == null) {
    		throw new NullPointerException();
    	}
        try {
        	URL url = new URL(httpUrl);
            
            HttpURLConnection conn;
            if(isHttp(httpUrl)) {
            	conn = (HttpURLConnection) url.openConnection();
            } else if(isHttps(httpUrl)) {
            	HttpsURLConnection httpsConn = (HttpsURLConnection) url.openConnection();
            	SSLContext ctx;
            	if(options.getSslProtocol() == null) {
                    ctx = SSLContext.getInstance("TLS");
            	} else {
                    ctx = SSLContext.getInstance(options.getSslProtocol());
            	}
            	if(!options.isVerifyCert()) {
                    ctx.init(options.getKeyManager(), TRUSTED_MGR, null);
                } else {
                	ctx.init(options.getKeyManager(), options.getTrustManager(), null);
                }
                httpsConn.setSSLSocketFactory(ctx.getSocketFactory());
            	conn = httpsConn;
            } else {
            	throw new IllegalArgumentException("invalid http url " + httpUrl);
            }
            
            setHeaders(conn, options.getHeaders());
            
            conn.setRequestMethod(httpMethod);
            conn.setConnectTimeout(options.getConnectTimeout());
            conn.setReadTimeout(options.getReadTimeout());
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.connect();
            return conn;
        } catch(Exception e) {
        	throw new HttpException(HttpStatus.BAD_REQUEST, e);
        }
    	
    }
	
	private static void removeHeaders(Map<String, String> headers, String...keys ) {
		List<String> removeList = new ArrayList<>();
		for(String key: keys) {
			for(String k: headers.keySet()) {
				if(k.toLowerCase().equals(key.toLowerCase())) {
					removeList.add(k);
				}
			}
		}
		for(String key: removeList) {
			headers.remove(key);
		}
	}
    
	private static boolean checkContentTypeExists(Map<String, String> headers) {
		boolean ok = false;
		for(String k: headers.keySet()) {
			if(k.toLowerCase().equals("content-type")) {
				ok = true;
			}
		}
		return ok;
	}
	
    private static boolean isHttp(String url) {
    	return url != null && url.startsWith(HTTP);
    }
    
    private static boolean isHttps(String url) {
    	return url != null && url.startsWith(HTTPS);
    }

    public static final TrustManager[] TRUSTED_MGR = new TrustManager[] { new X509ExtendedTrustManager() {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
        @Override
        public X509Certificate[] getAcceptedIssuers() {return new X509Certificate[0];}
		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType, Socket socket) throws CertificateException {}
		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType, Socket socket) throws CertificateException {}
		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType, SSLEngine engine) throws CertificateException {}
		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType, SSLEngine engine) throws CertificateException {}
    } };
    

	public static class Options {
    	
    	private boolean verifyCert = true;
    	
    	private Map<String, String> headers = null;
    	
    	private int connectTimeout = CONNECT_TIMEOUT;
    	
    	private int readTimeout = READ_TIMEOUT;
    	
    	private String sslProtocol;
    	
    	private KeyManager[] keyManager;
    	
    	private TrustManager[] trustManager;

    	public Options() {}
    	
		public boolean isVerifyCert() {
			return verifyCert;
		}

		public Options verifyCert(boolean verifyCert) {
			this.verifyCert = verifyCert;
			return this;
		}

		public Map<String, String> getHeaders() {
			return headers;
		}

		public Options headers(Map<String, String> headers) {
			this.headers = headers;
			return this;
		}

		public int getConnectTimeout() {
			return connectTimeout;
		}

		public Options connectTimeout(int connectTimeout) {
			this.connectTimeout = connectTimeout;
			return this;
		}

		public int getReadTimeout() {
			return readTimeout;
		}

		public Options readTimeout(int readTimeout) {
			this.readTimeout = readTimeout;
			return this;
		}

		public String getSslProtocol() {
			return sslProtocol;
		}
		
		public Options sslProtocol(String sslProtocol) {
			this.sslProtocol = sslProtocol;
			return this;
		}

		public KeyManager[] getKeyManager() {
			return keyManager;
		}

		public Options keyManager(KeyManager[] keyManager) {
			this.keyManager = keyManager;
			return this;
		}

		public TrustManager[] getTrustManager() {
			return trustManager;
		}

		public Options trustManager(TrustManager[] trustManager) {
			this.trustManager = trustManager;
			return this;
		}
	}

}
