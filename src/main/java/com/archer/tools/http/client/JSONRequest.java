package com.archer.tools.http.client;

import java.io.UnsupportedEncodingException;
import java.util.function.Consumer;

import com.archer.net.http.HttpException;
import com.archer.net.http.HttpStatus;
import com.archer.net.http.client.NativeRequest;
import com.archer.net.http.client.NativeRequest.*;
import com.archer.net.http.client.NativeResponse;
import com.archer.xjson.JavaTypeRef;
import com.archer.xjson.XJSONException;
import com.archer.xjson.XJSONStatic;

public class JSONRequest {
	
	public static String get(String httpUrl) throws Exception {
        return get(httpUrl, (Options)null);
    }

    public static String post(String httpUrl, Object body) throws Exception {
        return post(httpUrl, body, (Options)null);
    }

    public static String put(String httpUrl, Object body) throws Exception {
        return put(httpUrl, body, (Options)null);
    }

    public static String delete(String httpUrl, Object body) throws Exception {
        return delete(httpUrl, body, (Options)null);
    }

    public static String get(String httpUrl, Options options) throws Exception {
        return request("GET", httpUrl, null, options);
    }

    public static String post(String httpUrl, Object body, Options options) throws Exception {
        return request("POST", httpUrl, body, options);
    }

    public static String put(String httpUrl, Object body, Options options) throws Exception {
        return request("PUT", httpUrl, body, options);
    }

    public static String delete(String httpUrl, Object body, Options options) throws Exception {
        return request("DELETE", httpUrl, body, options);
    }
    

	public static <T> T get(String httpUrl, Class<T> cls) throws Exception {
        return get(httpUrl, null, cls);
    }

    public static <T> T post(String httpUrl, Object body, Class<T> cls) throws Exception {
        return post(httpUrl, body, null, cls);
    }

    public static <T> T put(String httpUrl, Object body, Class<T> cls) throws Exception {
        return put(httpUrl, body, null, cls);
    }

    public static <T> T delete(String httpUrl, Object body, Class<T> cls) throws Exception {
        return delete(httpUrl, body, null, cls);
    }

    public static <T> T get(String httpUrl, Options options, Class<T> cls) throws Exception {
        return request("GET", httpUrl, null, options, cls);
    }

    public static <T> T post(String httpUrl, Object body, Options options, Class<T> cls) throws Exception {
        return request("POST", httpUrl, body, options, cls);
    }

    public static <T> T put(String httpUrl, Object body, Options options, Class<T> cls) throws Exception {
        return request("PUT", httpUrl, body, options, cls);
    }

    public static <T> T delete(String httpUrl, Object body, Options options, Class<T> cls) throws Exception {
        return request("DELETE", httpUrl, body, options, cls);
    }

	public static <T> T get(String httpUrl, JavaTypeRef<T> ref) throws Exception {
        return get(httpUrl, null, ref);
    }

    public static <T> T post(String httpUrl, Object body, JavaTypeRef<T> ref) throws Exception {
        return post(httpUrl, body, null, ref);
    }

    public static <T> T put(String httpUrl, Object body, JavaTypeRef<T> ref) throws Exception {
        return put(httpUrl, body, null, ref);
    }

    public static <T> T delete(String httpUrl, Object body, JavaTypeRef<T> ref) throws Exception {
        return delete(httpUrl, body, null, ref);
    }

    public static <T> T get(String httpUrl, Options options, JavaTypeRef<T> ref) throws Exception {
        return request("GET", httpUrl, null, options, ref);
    }

    public static <T> T post(String httpUrl, Object body, Options options, JavaTypeRef<T> ref) throws Exception {
        return request("POST", httpUrl, body, options, ref);
    }

    public static <T> T put(String httpUrl, Object body, Options options, JavaTypeRef<T> ref) throws Exception {
        return request("PUT", httpUrl, body, options, ref);
    }

    public static <T> T delete(String httpUrl, Object body, Options options, JavaTypeRef<T> ref) throws Exception {
        return request("DELETE", httpUrl, body, options, ref);
    }
    
    
    

	public static <T> void getAsync(String httpUrl, JavaTypeRef<T> ref, Consumer<T> callback) throws Exception {
        getAsync(httpUrl, null, ref, callback);
    }

    public static <T> void postAsync(String httpUrl, Object body, JavaTypeRef<T> ref, Consumer<T> callback) throws Exception {
        postAsync(httpUrl, body, null, ref, callback);
    }

    public static <T> void putAsync(String httpUrl, Object body, JavaTypeRef<T> ref, Consumer<T> callback) throws Exception {
        putAsync(httpUrl, body, null, ref, callback);
    }

    public static <T> void deleteAsync(String httpUrl, Object body, JavaTypeRef<T> ref, Consumer<T> callback) throws Exception {
        deleteAsync(httpUrl, body, null, ref, callback);
    }

    public static <T> void getAsync(String httpUrl, Options options, JavaTypeRef<T> ref, Consumer<T> callback) throws Exception {
        requestAsync("GET", httpUrl, null, options, ref, callback);
    }

    public static <T> void postAsync(String httpUrl, Object body, Options options, JavaTypeRef<T> ref, Consumer<T> callback) throws Exception {
        requestAsync("POST", httpUrl, body, options, ref, callback);
    }

    public static <T> void putAsync(String httpUrl, Object body, Options options, JavaTypeRef<T> ref, Consumer<T> callback) throws Exception {
        requestAsync("PUT", httpUrl, body, options, ref, callback);
    }

    public static <T> void deleteAsync(String httpUrl, Object body, Options options, JavaTypeRef<T> ref, Consumer<T> callback) throws Exception {
        requestAsync("DELETE", httpUrl, body, options, ref, callback);
    }
    

	public static <T> void getAsync(String httpUrl, Class<T> cls, Consumer<T> callback) throws Exception {
        getAsync(httpUrl, null, cls, callback);
    }

    public static <T> void postAsync(String httpUrl, Object body, Class<T> cls, Consumer<T> callback) throws Exception {
        postAsync(httpUrl, body, null, cls, callback);
    }

    public static <T> void putAsync(String httpUrl, Object body, Class<T> cls, Consumer<T> callback) throws Exception {
        putAsync(httpUrl, body, null, cls, callback);
    }

    public static <T> void deleteAsync(String httpUrl, Object body, Class<T> cls, Consumer<T> callback) throws Exception {
        deleteAsync(httpUrl, body, null, cls, callback);
    }

    public static <T> void getAsync(String httpUrl, Options options, Class<T> cls, Consumer<T> callback) throws Exception {
        requestAsync("GET", httpUrl, null, options, cls, callback);
    }

    public static <T> void postAsync(String httpUrl, Object body, Options options, Class<T> cls, Consumer<T> callback) throws Exception {
        requestAsync("POST", httpUrl, body, options, cls, callback);
    }

    public static <T> void putAsync(String httpUrl, Object body, Options options, Class<T> cls, Consumer<T> callback) throws Exception {
        requestAsync("PUT", httpUrl, body, options, cls, callback);
    }

    public static <T> void deleteAsync(String httpUrl, Object body, Options options, Class<T> cls, Consumer<T> callback) throws Exception {
        requestAsync("DELETE", httpUrl, body, options, cls, callback);
    }

	
    public static <T> T request(String method, String httpUrl, Object body, Options option, JavaTypeRef<T> ref) 
			throws UnsupportedEncodingException, XJSONException {
    	String json = request(method, httpUrl, body, option);
    	try {
        	return XJSONStatic.parse(json, ref);
    	} catch(Exception e) {
    		throw new HttpException(HttpStatus.OK.getCode(), "can not parse '" + json+"' to " + ref.getJavaType().getTypeName());
    	}
	}
    
    public static <T> T request(String method, String httpUrl, Object body, Options option, Class<T> cls) 
			throws UnsupportedEncodingException, XJSONException {
    	String json = request(method, httpUrl, body, option);
    	try {
        	return XJSONStatic.parse(json, cls);
    	} catch(Exception e) {
    		throw new HttpException(HttpStatus.OK.getCode(), "can not parse '" + json+"' to " + cls.getName());
    	}
	}
    
	public static String request(String method, String httpUrl, Object body, Options option) 
			throws UnsupportedEncodingException, XJSONException {
		if(option == null) {
			option = new Options();
		}
		byte[] data = new byte[0];
		if(body != null) {
			if(body instanceof String) {
				data = ((String) body).getBytes(option.getEncoding());
			} else {
				data = XJSONStatic.stringify(body).getBytes(option.getEncoding());
			}
		}
		NativeResponse res = NativeRequest.request(method, httpUrl, data, option);
		String resBody = new String(res.getBody(), option.getEncoding());
		if(res.getStatusCode() != NativeResponse.HTTP_OK) {
			throw new HttpException(res.getStatusCode(), resBody);
		}
		return resBody;
	}
	

    public static <T> void requestAsync(String method, String httpUrl, Object body, Options option, JavaTypeRef<T> ref, Consumer<T> callback) 
			throws UnsupportedEncodingException, XJSONException {
    	requestAsync(method, httpUrl, body, option, bodyString -> {
        	 T resBody = XJSONStatic.parse(bodyString, ref);
        	 callback.accept(resBody);
    	});
	}
    
    public static <T> void requestAsync(String method, String httpUrl, Object body, Options option, Class<T> cls, Consumer<T> callback) 
			throws UnsupportedEncodingException, XJSONException {
    	requestAsync(method, httpUrl, body, option, bodyString -> {
	       	 T resBody = XJSONStatic.parse(bodyString, cls);
	       	 callback.accept(resBody);
	   	});
	}
	
	public static void requestAsync(String method, String httpUrl, Object body, Options option, Consumer<String> callback) 
			throws UnsupportedEncodingException, XJSONException {
		byte[] data = new byte[0];
		if(body != null) {
			if(body instanceof String) {
				data = ((String) body).getBytes(option.getEncoding());
			} else {
				data = XJSONStatic.stringify(body).getBytes(option.getEncoding());
			}
		}
		String encoding = option == null ? "utf-8": option.getEncoding();
		NativeRequest.requestAsync(method, httpUrl, data, option, res -> {
			try {
				String resBody = new String(res.getBody(), encoding);
				if(res.getStatusCode() != NativeResponse.HTTP_OK) {
					throw new HttpException(res.getStatusCode(), resBody);
				}
				callback.accept(resBody);
			} catch (UnsupportedEncodingException e) {
				throw new HttpException(res.getStatusCode(), e.getLocalizedMessage());
			}
		});
	}
}

