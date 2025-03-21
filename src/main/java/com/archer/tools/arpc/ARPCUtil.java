package com.archer.tools.arpc;

import java.util.Random;

public class ARPCUtil {
	
	public static final int BYTE_SIZE = 16;
	
	private static Random r = new Random();
	
	public static byte[] generateRandomBytes() {
		byte[] rs = new byte[BYTE_SIZE];
		r.nextBytes(rs);
		return rs;
	}
}
