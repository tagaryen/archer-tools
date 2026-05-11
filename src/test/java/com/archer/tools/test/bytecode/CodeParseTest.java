package com.archer.tools.test.bytecode;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import com.archer.tools.bytecode.ClassBytecode;
import com.archer.tools.java.ClassUtil;

public class CodeParseTest {
	public static void main(String args[]) {
		
		ClassBytecode newClass = new ClassBytecode(Parent.class);
		ClassBytecode childCode = newClass.generateImplClass(Parent.class.getSimpleName() + "_Chi", Parent.class.getPackage().getName());
		Class<?> childCls = childCode.loadSelfClass();
		try {

			Constructor<?>[] cons = childCls.getConstructors();
			System.out.println("Constructor length = " + cons.length);
			for(int i = 0; i < cons.length; i++) {
				System.out.println(Arrays.toString(cons[i].getParameterTypes()));
			}
			
//			Constructor<?> cons = childCls.getConstructor(short.class, int.class);
//			Object obj = cons.newInstance(0, 0);
//			System.out.println(obj.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
//		try {
//			Files.write(Paths.get("D:/chil.class"), childCode.encodeClassBytes().readAll());
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		

//		ClassBytecode newClass = new ClassBytecode(Child.class);
//		ClassBytecode read = new ClassBytecode();
//		try {
//			read.readAndDecodeClass("D:/chil.class");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}
}
//42, 27, -73, 0, 8, -79
//42, 27, 28, -73, 0, 20, -79
