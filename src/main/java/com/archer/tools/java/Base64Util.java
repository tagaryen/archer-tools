package com.archer.tools.java;

import java.util.Base64;
import java.util.Base64.*;

public class Base64Util {
	
	private static Encoder encoder = Base64.getEncoder();
	private static Decoder decoder = Base64.getDecoder();
	
	private static Encoder mineEncoder = Base64.getMimeEncoder();
	private static Decoder mineDecoder = Base64.getMimeDecoder();

	public static String encodeToString(byte[] input) {
		return new String(encode(input));
	}
	
	public static byte[] encode(byte[] input) {
		return encoder.encode(input);
	}
	
	public static byte[] decodeFromString(String base64) {
		return decode(base64.getBytes());
	}
	
	public static byte[] decode(byte[] base64Input) {
		return decoder.decode(base64Input);
	}
	
	
	public static String mineEncodeToString(byte[] input) {
		return new String(mineEncode(input));
	}
	
	public static byte[] mineEncode(byte[] input) {
		return mineEncoder.encode(input);
	}
	
	public static byte[] mineDecodeFromString(String base64) {
		return mineDecode(base64.getBytes());
	}
	
	public static byte[] mineDecode(byte[] base64Input) {
		return mineDecoder.decode(base64Input);
	}
	
}
