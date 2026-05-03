package com.archer.tools.algorithm.hash;

import java.nio.charset.StandardCharsets;

import com.archer.tools.java.NumberUtil;

public class Hmac {
	
	public enum HmacType {
		SHA1,
		SHA256,
		SHA384,
		SHA512,
		Keccak256,
		SM3;
	}
	
    private static final int[] _5C = new int[256];

    private static final int[] _36 = new int[256];

    static {
        for(int i = 0; i < 256; ++i) {
            _5C[i] = i^0x5c;
            _36[i] = i^0x36;
        }
    }

    static byte[] translate(byte[] bs, int[] trans) {
        byte[] out = new byte[bs.length];
        for(int i = 0; i < bs.length; ++i) {
            int k = bs[i];
            if(k < 0) {
                k = k+256;
            }
            out[i] = (byte)trans[k];
        }
        return out;
    }

    /**
     * @param privKey private key .
     * @param message .
     * @param type hash type.
     * @return bytes get mystic hash bytes from private key and hash content.
     * */
    public static String hmac(String privKey, String message, HmacType type) {
    	return NumberUtil.bytesToHexStr(hmac(privKey.getBytes(StandardCharsets.UTF_8), message.getBytes(StandardCharsets.UTF_8), type));
    }
    
    /**
     * @param privKey private key content in bytes.
     * @param message hash content in bytes.
     * @param type hash type.
     * @return bytes get mystic hash bytes from private key and hash content.
     * */
    public static byte[] hmac(byte[] privKey, byte[] message, HmacType type) {
        byte[] priv = new byte[64];

        System.arraycopy(privKey, 0, priv, 0, 32);

        byte[] innerBytes;
        switch(type) {
        case SHA1: {
        	innerBytes = SHA1.hash(concatBytes(translate(priv,_36), message)); 
            return SHA1.hash(concatBytes(translate(priv,_5C), innerBytes));
        }
        case SHA256: {
        	innerBytes = SHA256.hash(concatBytes(translate(priv,_36), message)); 
            return SHA256.hash(concatBytes(translate(priv,_5C), innerBytes));
        }
        case SHA384: {
        	innerBytes = SHA384.hash(concatBytes(translate(priv,_36), message)); 
            return SHA384.hash(concatBytes(translate(priv,_5C), innerBytes));
        }
        case SHA512: {
        	innerBytes = SHA512.hash(concatBytes(translate(priv,_36), message));
            return SHA512.hash(concatBytes(translate(priv,_5C), innerBytes));
        }
        case Keccak256: {
        	innerBytes = Keccak256.hash(concatBytes(translate(priv,_36), message)); 
            return Keccak256.hash(concatBytes(translate(priv,_5C), innerBytes));
        }
        case SM3: {
        	innerBytes = SM3.hash(concatBytes(translate(priv,_36), message));
            return SM3.hash(concatBytes(translate(priv,_5C), innerBytes));
        }
        default: {
        	innerBytes = SHA256.hash(concatBytes(translate(priv,_36), message)); 
            return SHA256.hash(concatBytes(translate(priv,_5C), innerBytes));
        }
        }
    }
    

    public static byte[] concatBytes(byte[] ...bytes) {
        int l = 0;
        for(byte[] bs: bytes) {
            l += bs.length;
        }
        byte[] out = new byte[l];
        int s = 0;
        for(byte[] bs: bytes) {
            System.arraycopy(bs, 0, out, s, bs.length);
            s += bs.length;
        }
        return out;
    }
}


