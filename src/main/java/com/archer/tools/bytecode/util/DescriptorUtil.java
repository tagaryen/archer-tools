package com.archer.tools.bytecode.util;

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
	
	public static String getMethodDescription(Class<?>[] params, Class<?> cls) {
		String out = "(";
		if(params != null) {
			for(int i = 0; i < params.length; i++) {
				out += getClassDescription(params[i]);
			}
		}
		out += ")"; 
		if(cls != null) {
			out += getClassDescription(cls);
		} else {
			out += "V";
		}
		return out;
	}
	
	public static String getMethodDescription(String[] paramDesces, String returnDesc) {
		String out = "(";
		if(paramDesces != null) {
			for(int i = 0; i < paramDesces.length; i++) {
				out += paramDesces[i];
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
}
