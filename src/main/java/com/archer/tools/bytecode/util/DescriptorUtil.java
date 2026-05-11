package com.archer.tools.bytecode.util;

import java.util.Arrays;

public class DescriptorUtil {


	static final String BOOL_TYPE = "boolean";
	static final String BYTE_TYPE = "byte";
	static final String CHAR_TYPE = "char";
	static final String SHORT_TYPE = "short";
	static final String INT_TYPE = "int";
	static final String LONG_TYPE = "long";
	static final String FLOAT_TYPE = "float";
	static final String DOUBLE_TYPE = "double";
	static final String VOID_TYPE = "void";
	

	static final String BOOL_ARR = "[Z";
	static final String BYTE_ARR = "[B";
	static final String CHAR_ARR = "[C";
	static final String SHORT_ARR = "[S";
	static final String INT_ARR = "[I";
	static final String LONG_ARR = "[J";
	static final String FLOAT_ARR = "[F";
	static final String DOUBLE_ARR = "[D";
	
	public static String getClassDescription(Class<?> cls) {
		if(BOOL_TYPE.equals(cls.getName())) {
			return "Z";
		} else if(BYTE_TYPE.equals(cls.getName())) {
			return "B";
		} else if(CHAR_TYPE.equals(cls.getName())) {
			return "C";
		} else if(SHORT_TYPE.equals(cls.getName())) {
			return "S";
		} else if(INT_TYPE.equals(cls.getName())) {
			return "I";
		} else if(LONG_TYPE.equals(cls.getName())) {
			return "J";
		} else if(FLOAT_TYPE.equals(cls.getName())) {
			return "F";
		} else if(DOUBLE_TYPE.equals(cls.getName())) {
			return "D";
		} else if(VOID_TYPE.equals(cls.getName())) {
			return "V";
		} else if(cls.isArray()) {
			return cls.getName();
		} else {
			return "L"+replaceDot2Slash(cls.getName())+";";
		}
	}
	
	public static String getMethodDescription(Class<?>[] params, Class<?> returnType) {
		String[] paramDescs = new String[params==null?0:params.length];
		if(paramDescs != null) {
			for(int i = 0; i < paramDescs.length; i++) {
				paramDescs[i] = getClassDescription(params[i]);
			}
		}
		return getMethodDescription(paramDescs, getClassDescription(returnType));
	}
	
	public static String getMethodDescription(String[] paramDescs, String returnDesc) {
		String out = "(";
		if(paramDescs != null) {
			for(int i = 0; i < paramDescs.length; i++) {
				out += paramDescs[i];
			}
		}
		out += ")"; 
		if(returnDesc != null) {
			out += returnDesc;
		} else {
			out += "V";
		}
		return out;
	}
	
    public static String getLastName(String name) {
    	int idx = name.lastIndexOf('/');
    	if(idx < 0) {
    		throw new IllegalArgumentException("Invalid name: " + name);
    	}
    	if(idx >= name.length()) {
    		throw new IllegalArgumentException("Invalid name: " + name);
    	}
    	return name.substring(idx + 1);
    }
    
    public static String getPackageName(String name) {
    	int idx = name.lastIndexOf('/');
    	if(idx < 0) {
    		throw new IllegalArgumentException("Invalid name: " + name);
    	}
    	if(idx >= name.length()) {
    		throw new IllegalArgumentException("Invalid name: " + name);
    	}
    	return name.substring(0, idx);
    }

    public static String replaceDot2Slash(String name) {
    	byte[] bs = name.getBytes();
    	for(int i = 0; i < bs.length; i++) {
    		if('.' == bs[i]) {
    			bs[i] = '/';
    		}
    	}
    	return new String(bs);
    }
    
    public static String replaceSlash2Dot(String name) {
    	byte[] bs = name.getBytes();
    	for(int i = 0; i < bs.length; i++) {
    		if('/' == bs[i]) {
    			bs[i] = '.';
    		}
    	}
    	return new String(bs);
    }
    
	public static boolean checkPrimitive(byte _1, byte _2) {
		if(_1 == 'Z' || _1 == 'B' || _1 == 'C' || _1 == 'S' || _1 == 'I' || _1 == 'J' || _1 == 'F' || _1 == 'D') {
			return _2 == 'L' || _2 == '[' || _2 == ')' || _2 == 'Z' || _2 == 'B' || _2 == 'C' || _2 == 'S' || _2 == 'I' || _2 == 'J' || _2 == 'F' || _2 == 'D';
		}
		return false;
	}
    
	public static String[] methodDescToArgDescs(String desc) {
		byte[] bs = desc.getBytes();
		String[] types = new String[128];
		int off = 0, typeS = 0;
		for(int i = 1; i < bs.length - 2; i++) {
			if(bs[i] == ')') {
				break ;
			}
			if(checkPrimitive(bs[i], bs[i+1])) {
				types[off++] = String.valueOf((char)bs[i]);
				continue ;
			}
			if(bs[i] == '[') {
				boolean ok = false;
				typeS = i;
				i++;
				for(;i < bs.length - 2; i++) {
					if(bs[i] == ';' || checkPrimitive(bs[i], bs[i+1])) {
						types[off++] = new String(Arrays.copyOfRange(bs, typeS, i+1));
						ok = true;
						break;
					}
				}
				if(!ok) {
					throw new IllegalArgumentException("invalid method descriptor " + desc);
				}
				continue ;
			}
			if(bs[i] == 'L') {
				boolean ok = false;
				typeS = i;
				i += 2;
				for(; i < bs.length - 2; i++) {
					if(bs[i] == ';') {
						types[off++] = new String(Arrays.copyOfRange(bs, typeS, i+1));
						ok = true;
						break;
					}
				}
				if(!ok) {
					throw new IllegalArgumentException("invalid method descriptor " + desc);
				}
			}
		}
		return Arrays.copyOfRange(types, 0, off);
	}
	
}
