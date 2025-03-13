package com.archer.tools.bytecode;

class BytecodeClassLoader extends ClassLoader {
	
	public BytecodeClassLoader() {
		super();
	}
	
	public final Class<?> defineBytecodeClass(String name, byte[] b, int off, int len) {
		return super.defineClass(name, b, off, len);
	}
}
