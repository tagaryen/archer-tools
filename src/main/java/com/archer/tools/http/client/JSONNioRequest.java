package com.archer.tools.http.client;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import com.archer.net.Bytes;
import com.archer.net.http.HttpException;
import com.archer.net.http.HttpStatus;
import com.archer.net.http.client.NioRequest;
import com.archer.net.http.client.NioRequest.*;
import com.archer.net.http.multipart.FormData;
import com.archer.net.http.client.NioResponse;
import com.archer.xjson.JavaTypeRef;
import com.archer.xjson.XJSONException;
import com.archer.xjson.XJSONStatic;

public class JSONNioRequest {
	
	public static String get(String httpUrl) {
        return get(httpUrl, (Options)null);
    }

    public static String post(String httpUrl, Object body) {
        return post(httpUrl, body, (Options)null);
    }

    public static String put(String httpUrl, Object body) {
        return put(httpUrl, body, (Options)null);
    }

    public static String delete(String httpUrl, Object body) {
        return delete(httpUrl, body, (Options)null);
    }

    public static String get(String httpUrl, Options options) {
        return request("GET", httpUrl, null, options);
    }

    public static String post(String httpUrl, Object body, Options options) {
        return request("POST", httpUrl, body, options);
    }

    public static String put(String httpUrl, Object body, Options options) {
        return request("PUT", httpUrl, body, options);
    }

    public static String delete(String httpUrl, Object body, Options options) {
        return request("DELETE", httpUrl, body, options);
    }
    

	public static <T> T get(String httpUrl, Class<T> cls) {
        return get(httpUrl, null, cls);
    }

    public static <T> T post(String httpUrl, Object body, Class<T> cls) {
        return post(httpUrl, body, null, cls);
    }

    public static <T> T put(String httpUrl, Object body, Class<T> cls) {
        return put(httpUrl, body, null, cls);
    }

    public static <T> T delete(String httpUrl, Object body, Class<T> cls) {
        return delete(httpUrl, body, null, cls);
    }

    public static <T> T get(String httpUrl, Options options, Class<T> cls) {
        return request("GET", httpUrl, null, options, cls);
    }

    public static <T> T post(String httpUrl, Object body, Options options, Class<T> cls) {
        return request("POST", httpUrl, body, options, cls);
    }

    public static <T> T put(String httpUrl, Object body, Options options, Class<T> cls) {
        return request("PUT", httpUrl, body, options, cls);
    }

    public static <T> T delete(String httpUrl, Object body, Options options, Class<T> cls) {
        return request("DELETE", httpUrl, body, options, cls);
    }
    

	public static <T> T get(String httpUrl, JavaTypeRef<T> ref) {
        return get(httpUrl, null, ref);
    }

    public static <T> T post(String httpUrl, Object body, JavaTypeRef<T> ref) {
        return post(httpUrl, body, null, ref);
    }

    public static <T> T put(String httpUrl, Object body, JavaTypeRef<T> ref) {
        return put(httpUrl, body, null, ref);
    }

    public static <T> T delete(String httpUrl, Object body, JavaTypeRef<T> ref) {
        return delete(httpUrl, body, null, ref);
    }

    public static <T> T get(String httpUrl, Options options, JavaTypeRef<T> ref) {
        return request("GET", httpUrl, null, options, ref);
    }

    public static <T> T post(String httpUrl, Object body, Options options, JavaTypeRef<T> ref) {
        return request("POST", httpUrl, body, options, ref);
    }

    public static <T> T put(String httpUrl, Object body, Options options, JavaTypeRef<T> ref) {
        return request("PUT", httpUrl, body, options, ref);
    }

    public static <T> T delete(String httpUrl, Object body, Options options, JavaTypeRef<T> ref) {
        return request("DELETE", httpUrl, body, options, ref);
    }
	
    public static <T> T request(String method, String httpUrl, Object body, Options option, JavaTypeRef<T> ref) {
    	String json = request(method, httpUrl, body, option);
    	try {
        	return XJSONStatic.parse(json, ref);
    	} catch(Exception e) {
    		throw new HttpException(HttpStatus.OK.getCode(), "can not parse '" + json+"' to " + ref.getJavaType().getTypeName());
    	}
	}
    
    public static <T> T request(String method, String httpUrl, Object body, Options option, Class<T> cls) {
    	String json = request(method, httpUrl, body, option);
    	try {
        	return XJSONStatic.parse(json, cls);
    	} catch(Exception e) {
    		throw new HttpException(HttpStatus.OK.getCode(), "can not parse '" + json+"' to " + cls.getName());
    	}
	}
    
	public static String request(String method, String httpUrl, Object body, Options option) {
		if(option == null) {
			option = new Options();
		}
		Bytes responseBody = rawRequest(method, httpUrl, body, option);
		try {
			return new String(responseBody.array(), option.getEncoding());
		} catch(UnsupportedEncodingException e) {
			throw new HttpException(HttpStatus.BAD_REQUEST.getCode(), e);
		}
	}
	
	public static Bytes rawRequest(String method, String httpUrl, Object body, Options option) {
		if(option == null) {
			option = new Options();
		}
		/*
		 * fix bugs with Map.of("k1", "v1")
		 */
		Map<String, String> headers = new HashMap<>();
		if(option.getHeaders() != null) {
			headers.putAll(option.getHeaders());
		}
		option.headers(headers);
		byte[] data = null;
		try {
			NioResponse res;
			if(body != null && body instanceof FormData) {
				res = NioRequest.request(method, httpUrl, data, option);
			} else {
				if(body != null) {
					if(!checkContentTypeExists(option.getHeaders())) {
						option.getHeaders().put("Content-Type", "application/json");
					}
					if(body instanceof String) {
						data = ((String) body).getBytes(option.getEncoding());
					} else {
						data = XJSONStatic.stringify(body).getBytes(option.getEncoding());
					}
				}
				res = NioRequest.request(method, httpUrl, data, option);
			}
			return new Bytes(res.getBody());
		} catch(Exception e) {
			e.printStackTrace();
			if(e instanceof HttpException) {
				throw (HttpException) e;
			}
			if(e instanceof XJSONException) {
				throw (XJSONException) e;
			}
			throw new HttpException(HttpStatus.BAD_REQUEST.getCode(), e);
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
}

