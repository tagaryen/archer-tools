package com.archer.tools.test.bytecode;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import com.archer.tools.bytecode.ClassBytecode;
import com.archer.tools.bytecode.InstructionTable;
import com.archer.tools.bytecode.AttributeInfo;
import com.archer.tools.bytecode.AttributeInfo.*;
import com.archer.tools.bytecode.constantpool.ConstantNameAndType;
import com.archer.tools.bytecode.constantpool.ConstantPool;
import com.archer.tools.bytecode.util.DescriptorUtil;
import com.archer.tools.java.ClassUtil;

public class ByteCodeTest {
	
	public static void test() {

		try {
		
		/***
		 * here we generate a new class
		 * public class ClassA$Impl extends ClassA {
		 *     public String nameCp;
		 *     public void setName(String name, int age) {
		 *         this.nameCp = name;
		 *         super.setName(null, age);
		 *     }
		 * }
		 * */
		ClassBytecode impl = (new ClassBytecode()).readAndDecodeClass(ClassA.class).generateImplClass("ClassA$Impl", "com.archer.tools.test.bytecode");
		
		/**
		 * Add constant pool
		 * 1. add field com.archer.tools.test.bytecode.ClassA$Impl.nameCp Ljava/lang/String
		 * 2. add invoke method com/archer/tools/test/bytecode/ClassA.setName()
		 * */
		impl.addField("nameCp", String.class, 1);
		int fieldIndex = impl.getConstantPool().findField("nameCp", String.class);
		int methodIndex = impl.getConstantPool().addMethod("setName", new String[] {"Ljava/lang/String;","I"}, "V", "com/archer/tools/test/bytecode/ClassA");
		
		
		/**
		 * Add override method to com.archer.tools.test.bytecode.ClassA$Impl:
		 * public void setName(String name, int age) {
		 *     this.nameCp = name;
		 *     super.setName(null, age);
		 * }
		 * */
		CodeAttributeWriter writer = CodeAttributeWriter.of(impl.getConstantPool().findName("Code"));
		writer.addInstruction("aload_0")
			.addInstruction("aload_1")
			.addInstruction16("putfield", fieldIndex)
			.addInstruction("aload_0")
			.addInstruction("aconst_null")
			.addInstruction("iload_2")
			.addInstruction16("invokespecial", methodIndex)
			.addInstruction("return");
		impl.addMethod("setName", new String[] {"Ljava/lang/String;","I"}, "V", writer.toCodeAttribute());
		
		
		/**
		 * New com.archer.tools.test.bytecode.ClassA$Impl()
		 * */
		ClassA classAins = (ClassA)ClassUtil.newInstance(impl.loadSelfClass());
		classAins.setName("xuyihaoshuai", 18);

		/**
		 * Invoke methods for tests
		 * */
		System.out.println(classAins.getName());
		System.out.println(classAins.getAge());
		
		Field f = classAins.getClass().getDeclaredField("nameCp");
		String nameCp = (String) f.get(classAins);
		System.out.println(nameCp);

			System.out.println(classAins.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static void asyncTest() throws Exception {
		
		System.setProperty("archer.bytecode.debug", "true");
		
		AsyncProxy proxy = new AsyncProxy(new ClassBytecode(Parent.class));
		Class<?> parentImpl = proxy.newAsyncClass();
		Object implObj = ClassUtil.newInstance(parentImpl);
		Field pool = parentImpl.getDeclaredField("pool");
		AsyncPool poolObj = new AsyncPool(1);
		poolObj.start();
		pool.set(implObj, poolObj);
		
		Method setName = parentImpl.getDeclaredMethod("func", String.class);
		setName.invoke(implObj, "xuyi");
		System.out.println("main print");
		
	}
	
	public static void printClassName(Object o) {
		System.out.println(o.getClass().getName());
	}
	
	public static void main(String args[]) {
//		test();
		
		try {
			asyncTest();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
//		int[] a = new int[] {1};
//		int b = 1;
//		printClassName(a);
//		System.out.println(int.class.getName());
//		printClassName(b);
	}
}
