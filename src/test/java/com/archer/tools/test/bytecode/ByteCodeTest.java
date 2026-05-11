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
			ClassBytecode impl = (new ClassBytecode()).readAndDecodeClass(ClassA.class).generateImplClass("ClassA$Impl", "com.archer.tools.test.bytecode");
			
			/**
			 * add field com.archer.tools.test.bytecode.ClassA$Impl.nameCp Ljava/lang/String
			 * 
			 * add invoke method com/archer/tools/test/bytecode/ClassA.setName()
			 * 
			 * */
			
			impl.addField("nameCp", String.class, 1);
			int fieldIndex = impl.getConstantPool().findField("nameCp", String.class);
			int methodIndex = impl.getConstantPool().addMethod("setName", new String[] {"Ljava/lang/String;","I"}, "V", "com/archer/tools/test/bytecode/ClassA");
			
			
			/**
			 * add override method to com.archer.tools.test.bytecode.ClassA$Impl:
			 * public void setName(String a0, int a1) {
			 *     this.nameCp = a0;
			 *     super.setName(null, a1);
			 * }
			 * 
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
			
			
			ClassA classAins = (ClassA)ClassUtil.newInstance(impl.loadSelfClass());
			classAins.setName("xuyihaoshuai", 18);

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
		AsyncProxy proxy = new AsyncProxy(new ClassBytecode(Parent.class));
		Class<?> parentImpl = proxy.newAsyncClass();
		Object implObj = ClassUtil.newInstance(parentImpl);
		Field pool = parentImpl.getDeclaredField("pool");
		pool.set(implObj, new AsyncPool(1));
		
		Method setName = parentImpl.getDeclaredMethod("setName", String.class);
		setName.invoke(implObj, "xuyi");
		System.out.println("main print");
		
	}
	
	public static void main(String args[]) {
//		test();
		
		try {
			asyncTest();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
