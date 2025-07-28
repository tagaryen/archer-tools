package com.archer.tools.http.client;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.archer.net.http.HttpException;
import com.archer.net.http.HttpStatus;
import com.archer.net.http.client.NativeRequest;
import com.archer.net.http.client.NativeRequest.Options;
import com.archer.net.http.client.NativeResponse;
import com.archer.net.http.multipart.MultipartParser;
import com.archer.xjson.JavaTypeRef;
import com.archer.xjson.XJSONException;
import com.archer.xjson.XJSONStatic;

public class JSONRequest {
	
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
    
	public static <T> void getAsync(String httpUrl, JavaTypeRef<T> ref, Consumer<T> callback) {
        getAsync(httpUrl, null, ref, callback, null);
    }

    public static <T> void postAsync(String httpUrl, Object body, JavaTypeRef<T> ref, Consumer<T> callback) {
        postAsync(httpUrl, body, null, ref, callback, null);
    }

    public static <T> void putAsync(String httpUrl, Object body, JavaTypeRef<T> ref, Consumer<T> callback) {
        putAsync(httpUrl, body, null, ref, callback, null);
    }

    public static <T> void deleteAsync(String httpUrl, Object body, JavaTypeRef<T> ref, Consumer<T> callback) {
        deleteAsync(httpUrl, body, null, ref, callback, null);
    }
    
	public static <T> void getAsync(String httpUrl, Options options, JavaTypeRef<T> ref, Consumer<T> callback) {
        getAsync(httpUrl, options, ref, callback, null);
    }

    public static <T> void postAsync(String httpUrl, Object body, Options options, JavaTypeRef<T> ref, Consumer<T> callback) {
        postAsync(httpUrl, body, options, ref, callback, null);
    }

    public static <T> void putAsync(String httpUrl, Object body, Options options, JavaTypeRef<T> ref, Consumer<T> callback) {
        putAsync(httpUrl, body, options, ref, callback, null);
    }

    public static <T> void deleteAsync(String httpUrl, Object body, Options options, JavaTypeRef<T> ref, Consumer<T> callback) {
        deleteAsync(httpUrl, body, options, ref, callback, null);
    }
    
	public static <T> void getAsync(String httpUrl, Class<T> cls, Consumer<T> callback) {
        getAsync(httpUrl, null, cls, callback, null);
    }

    public static <T> void postAsync(String httpUrl, Object body, Class<T> cls, Consumer<T> callback) {
        postAsync(httpUrl, body, null, cls, callback, null);
    }

    public static <T> void putAsync(String httpUrl, Object body, Class<T> cls, Consumer<T> callback) {
        putAsync(httpUrl, body, null, cls, callback, null);
    }

    public static <T> void deleteAsync(String httpUrl, Object body, Class<T> cls, Consumer<T> callback) {
        deleteAsync(httpUrl, body, null, cls, callback, null);
    }
    
	public static <T> void getAsync(String httpUrl, Options options, Class<T> cls, Consumer<T> callback) {
        getAsync(httpUrl, options, cls, callback, null);
    }

    public static <T> void postAsync(String httpUrl, Object body, Options options, Class<T> cls, Consumer<T> callback) {
        postAsync(httpUrl, body, options, cls, callback, null);
    }

    public static <T> void putAsync(String httpUrl, Object body, Options options, Class<T> cls, Consumer<T> callback) {
        putAsync(httpUrl, body, options, cls, callback, null);
    }

    public static <T> void deleteAsync(String httpUrl, Object body, Options options, Class<T> cls, Consumer<T> callback) {
        deleteAsync(httpUrl, body, options, cls, callback, null);
    }
    
	public static <T> void getAsync(String httpUrl, JavaTypeRef<T> ref, Consumer<T> callback, Consumer<Throwable> exceptionCallback) {
        getAsync(httpUrl, null, ref, callback, exceptionCallback);
    }

    public static <T> void postAsync(String httpUrl, Object body, JavaTypeRef<T> ref, Consumer<T> callback, Consumer<Throwable> exceptionCallback) {
        postAsync(httpUrl, body, null, ref, callback, exceptionCallback);
    }

    public static <T> void putAsync(String httpUrl, Object body, JavaTypeRef<T> ref, Consumer<T> callback, Consumer<Throwable> exceptionCallback) {
        putAsync(httpUrl, body, null, ref, callback, exceptionCallback);
    }

    public static <T> void deleteAsync(String httpUrl, Object body, JavaTypeRef<T> ref, Consumer<T> callback, Consumer<Throwable> exceptionCallback) {
        deleteAsync(httpUrl, body, null, ref, callback, exceptionCallback);
    }

    public static <T> void getAsync(String httpUrl, Options options, JavaTypeRef<T> ref, Consumer<T> callback, Consumer<Throwable> exceptionCallback) {
        requestAsync("GET", httpUrl, null, options, ref, callback, exceptionCallback);
    }

    public static <T> void postAsync(String httpUrl, Object body, Options options, JavaTypeRef<T> ref, Consumer<T> callback, Consumer<Throwable> exceptionCallback) {
        requestAsync("POST", httpUrl, body, options, ref, callback, exceptionCallback);
    }

    public static <T> void putAsync(String httpUrl, Object body, Options options, JavaTypeRef<T> ref, Consumer<T> callback, Consumer<Throwable> exceptionCallback) {
        requestAsync("PUT", httpUrl, body, options, ref, callback, exceptionCallback);
    }

    public static <T> void deleteAsync(String httpUrl, Object body, Options options, JavaTypeRef<T> ref, Consumer<T> callback, Consumer<Throwable> exceptionCallback) {
        requestAsync("DELETE", httpUrl, body, options, ref, callback, exceptionCallback);
    }
    

	public static <T> void getAsync(String httpUrl, Class<T> cls, Consumer<T> callback, Consumer<Throwable> exceptionCallback) {
        getAsync(httpUrl, null, cls, callback, exceptionCallback);
    }

    public static <T> void postAsync(String httpUrl, Object body, Class<T> cls, Consumer<T> callback, Consumer<Throwable> exceptionCallback) {
        postAsync(httpUrl, body, null, cls, callback, exceptionCallback);
    }

    public static <T> void putAsync(String httpUrl, Object body, Class<T> cls, Consumer<T> callback, Consumer<Throwable> exceptionCallback) {
        putAsync(httpUrl, body, null, cls, callback, exceptionCallback);
    }

    public static <T> void deleteAsync(String httpUrl, Object body, Class<T> cls, Consumer<T> callback, Consumer<Throwable> exceptionCallback) {
        deleteAsync(httpUrl, body, null, cls, callback, exceptionCallback);
    }

    public static <T> void getAsync(String httpUrl, Options options, Class<T> cls, Consumer<T> callback, Consumer<Throwable> exceptionCallback) {
        requestAsync("GET", httpUrl, null, options, cls, callback, exceptionCallback);
    }

    public static <T> void postAsync(String httpUrl, Object body, Options options, Class<T> cls, Consumer<T> callback, Consumer<Throwable> exceptionCallback) {
        requestAsync("POST", httpUrl, body, options, cls, callback, exceptionCallback);
    }

    public static <T> void putAsync(String httpUrl, Object body, Options options, Class<T> cls, Consumer<T> callback, Consumer<Throwable> exceptionCallback) {
        requestAsync("PUT", httpUrl, body, options, cls, callback, exceptionCallback);
    }

    public static <T> void deleteAsync(String httpUrl, Object body, Options options, Class<T> cls, Consumer<T> callback, Consumer<Throwable> exceptionCallback) {
        requestAsync("DELETE", httpUrl, body, options, cls, callback, exceptionCallback);
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
		/*
		 * fix bugs with Map.of("k1", "v1")
		 */
		Map<String, String> headers = new HashMap<>(option.getHeaders());
		if(body instanceof FormData) {
			FormData formData = (FormData) body;
			String boundary = MultipartParser.generateBoundary();
			body = MultipartParser.generateMultipartBody(formData.getMultiparts(), boundary);
			headers.put("Content-Type", MultipartParser.MULTIPART_HEADER + boundary);
		} else {
			headers.put("Content-Type", "application/json");
		}
		option.headers(headers);
		byte[] data = new byte[0];
		try {
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
		} catch(Exception e) {
			if(e instanceof HttpException) {
				throw (HttpException) e;
			}
			if(e instanceof XJSONException) {
				throw (XJSONException) e;
			}
			throw new HttpException(HttpStatus.BAD_REQUEST.getCode(), e.getLocalizedMessage());
		}
	}
	
    public static <T> void requestAsync(String method, String httpUrl, Object body, Options option, 
    		JavaTypeRef<T> ref, Consumer<T> callback, Consumer<Throwable> exceptionCallback) {
		String encoding = option == null ? "utf-8": option.getEncoding();
    	requestAsync(method, httpUrl, body, option, res -> {
    		try {
        		String bodyString = new String(res.getBody(), encoding);
            	T resBody = XJSONStatic.parse(bodyString, ref);
            	callback.accept(resBody);
    		} catch(Exception e) {
    			if(exceptionCallback != null) {
    				exceptionCallback.accept(e);
    			} else {
        			e.printStackTrace(); //do not throw Exception to interrupt the eventloop
    			}
    		}
    	}, exceptionCallback);
	}
    
    public static <T> void requestAsync(String method, String httpUrl, Object body, Options option, Class<T> cls, 
    		Consumer<T> callback, Consumer<Throwable> exceptionCallback) {
		String encoding = option == null ? "utf-8": option.getEncoding();
    	requestAsync(method, httpUrl, body, option, res -> {
    		try {
        		String bodyString = new String(res.getBody(), encoding);
    	       	T resBody = XJSONStatic.parse(bodyString, cls);
    	       	callback.accept(resBody);
    		} catch(Exception e) {
    			if(exceptionCallback != null) {
    				exceptionCallback.accept(e);
    			} else {
        			e.printStackTrace(); //do not throw Exception to interrupt the eventloop
    			}
    		}
	   	}, exceptionCallback);
	}
	
	public static void requestAsync(String method, String httpUrl, Object body, Options option, 
			Consumer<NativeResponse> callback, Consumer<Throwable> exceptionCallback) {
		if(option == null) {
			option = new Options();
		}

		/*
		 * fix bugs with Map.of("k1", "v1")
		 */
		Map<String, String> headers = new HashMap<>(option.getHeaders());
		if(body instanceof FormData) {
			FormData formData = (FormData) body;
			String boundary = MultipartParser.generateBoundary();
			body = MultipartParser.generateMultipartBody(formData.getMultiparts(), boundary);
			headers.put("Content-Type", MultipartParser.MULTIPART_HEADER + boundary);
		} else {
			headers.put("Content-Type", "application/json");
		}
		option.headers(headers);
		byte[] data = new byte[0];
		try {
			if(body != null) {
				if(body instanceof String) {
					data = ((String) body).getBytes(option.getEncoding());
				} else {
					data = XJSONStatic.stringify(body).getBytes(option.getEncoding());
				}
			}
			NativeRequest.requestAsync(method, httpUrl, data, option, callback, exceptionCallback);
		} catch(Exception e) {
			if(e instanceof HttpException) {
				throw (HttpException) e;
			}
			if(e instanceof XJSONException) {
				throw (XJSONException) e;
			}
			throw new HttpException(HttpStatus.BAD_REQUEST.getCode(), e.getLocalizedMessage());
		}
	}
}

