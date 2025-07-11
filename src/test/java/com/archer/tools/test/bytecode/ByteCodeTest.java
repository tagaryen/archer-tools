package com.archer.tools.test.bytecode;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import com.archer.tools.bytecode.ClassBytecode;
import com.archer.tools.bytecode.ClassBytecodePrinter;
import com.archer.tools.bytecode.InstructionTable;
import com.archer.tools.bytecode.MemberInfo.AttributeInfo;
import com.archer.tools.bytecode.MemberInfo.CodeAttribute;
import com.archer.tools.bytecode.constantpool.ConstantNameAndType;
import com.archer.tools.bytecode.util.DescriptorUtil;
import com.archer.tools.java.ClassUtil;

public class ByteCodeTest {
	
	public static void generateClassAImpl(Class<?> cls) {
		ClassBytecode clazz = new ClassBytecode();
		ClassBytecode taskClass = new ClassBytecode();
		String overrideMethodName = "setName";
		Class<?>[] methodParams = new Class<?>[] {String.class};
		
		
		
		String implClassName = cls.getSimpleName() + "$ArcherProxy";
		String taskClassName = cls.getSimpleName() + "$ArcherTask";
		String asyncPoolDesc = DescriptorUtil.replaceDot2Slash(AsyncPool.class.getName());
		String taskClassDesc = DescriptorUtil.getClassDescription(AsyncTask.class);
		try {
			clazz.readAndDecodeClass(cls);
			
			ClassBytecode classImpl = clazz.generateImplClass(implClassName);
			String classImplDesc = "L"+classImpl.getRawClassName()+";";
			String[] paramDesc = new String[methodParams.length],  taskParamDesc = new String[methodParams.length + 1];
			taskParamDesc[0] = classImplDesc;
			for(int i = 0; i < methodParams.length; i++) {
				String pdesc = DescriptorUtil.getClassDescription(methodParams[i]);
				taskParamDesc[i+1] = pdesc;
				paramDesc[i] = pdesc;
			}
			
			taskClass.readAndDecodeClass(AsyncTask.class);
			ClassBytecode taskImpl = taskClass.generateImplClass(taskClassName);
			
			
			// classImpl  generate
			classImpl.addField("pool", AsyncPool.class, 1);
			
			//add method setName(String)V
			CodeAttribute code = new CodeAttribute();
			code.setMaxStack(paramDesc.length + 4);
			code.setMaxLocals(paramDesc.length + 1);
			code.setException(new byte[0]);
			code.setAttributes(new AttributeInfo[0]);
			
			int off = 0;
			byte[] codeBytes = new byte[256];
			codeBytes[off++] = InstructionTable.getInstructionCode("aload_0");
			codeBytes[off++] = InstructionTable.getInstructionCode("getfield");
			int poolIndex = classImpl.getConstantPool().findField("pool", AsyncPool.class);
			codeBytes[off++] = (byte) ((poolIndex >> 8) & 0xff);
			codeBytes[off++] = (byte) (poolIndex & 0xff);
			codeBytes[off++] = InstructionTable.getInstructionCode("new");
			int taskClassIndex = classImpl.getConstantPool().addClass(classImpl.getRawClassName());
			codeBytes[off++] = (byte) ((taskClassIndex >> 8) & 0xff);
			codeBytes[off++] = (byte) (taskClassIndex & 0xff);
			codeBytes[off++] = InstructionTable.getInstructionCode("dup");
			codeBytes[off++] = InstructionTable.getInstructionCode("aload_0");
			int opIndex = 9;
			for(int j = 0; j < paramDesc.length; j++) {
				byte[] codes = InstructionTable.decodeLoadIns(paramDesc[j],  j);
				if(codes.length == 1) {
					codeBytes[opIndex++] = codes[0];
				}
				if(codes.length == 2) {
					codeBytes[opIndex++] = codes[0];
					codeBytes[opIndex++] = codes[1];
				}
			}
			
			codeBytes[opIndex++] = InstructionTable.getInstructionCode("invokespecial");
			int taskInitIndex = classImpl.getConstantPool().addMethod("<init>", taskParamDesc, "V", taskClass.getRawClassName());
			codeBytes[opIndex++] = (byte) ((taskInitIndex >> 8) & 0xff);
			codeBytes[opIndex++] = (byte) (taskInitIndex & 0xff);
			codeBytes[opIndex++] = InstructionTable.getInstructionCode("invokevirtual");
			int submitIndex = classImpl.getConstantPool().addMethod("submit", new String[] {taskClassDesc}, "V", asyncPoolDesc);
			codeBytes[opIndex++] = (byte) ((submitIndex >> 8) & 0xff);
			codeBytes[opIndex++] = (byte) (submitIndex & 0xff);
			codeBytes[opIndex++] = InstructionTable.getInstructionCode("return");
			
			code.setCode(Arrays.copyOfRange(codeBytes, 0, off));
			
			classImpl.addMethod(overrideMethodName, paramDesc, "V", code);
			
			
			//add method setName$ArcherProxy(String)V
			String superCallMethodName = overrideMethodName + "$ArcherProxy";
			CodeAttribute superCode = new CodeAttribute();
			
			superCode.setMaxStack(paramDesc.length + 1);
			superCode.setMaxLocals(paramDesc.length + 1);
			superCode.setException(new byte[0]);
			superCode.setAttributes(new AttributeInfo[0]);

			byte[] superCodeBytes = new byte[256];
			off = 0;
			superCodeBytes[off++] = InstructionTable.getInstructionCode("aload_0");
			for(int j = 0; j < paramDesc.length; j++) {
				byte[] codes = InstructionTable.decodeLoadIns(paramDesc[j],  j);
				if(codes.length == 1) {
					superCodeBytes[off++] = codes[0];
				}
				if(codes.length == 2) {
					superCodeBytes[off++] = codes[0];
					superCodeBytes[off++] = codes[1];
				}
			}
			superCodeBytes[off++] = InstructionTable.getInstructionCode("invokespecial");
			int superCallIndex = classImpl.getConstantPool().addMethod(overrideMethodName, paramDesc, "V", clazz.getRawClassName());
			superCodeBytes[off++] = (byte) ((superCallIndex >> 8) & 0xff);
			superCodeBytes[off++] = (byte) (superCallIndex & 0xff);
			superCodeBytes[off++] = InstructionTable.getInstructionCode("return");

			superCode.setCode(Arrays.copyOfRange(superCodeBytes, 0, off));

			classImpl.addMethod(superCallMethodName, paramDesc, "V", superCode);
			
			
			
			//task class generate
			taskImpl.addField("impl", classImplDesc, 1);
			for(int i = 0; i < methodParams.length; i++) {
				taskImpl.addField("p" + i, methodParams[i], 1);
			}
			
			String taskInitMethodName = "<init>";
			CodeAttribute taskInitCode = new CodeAttribute();
			taskInitCode.setMaxStack(2);
			taskInitCode.setMaxLocals(2);
			taskInitCode.setException(new byte[0]);
			taskInitCode.setAttributes(new AttributeInfo[0]);
			
			byte[] taskCode = new byte[256];

			taskCode[0] = InstructionTable.getInstructionCode("aload_0");
			taskCode[1] = InstructionTable.getInstructionCode("getfield");
			taskCode[2] = (byte) ((poolIndex >> 8) & 0xff);
			taskCode[3] = (byte) (poolIndex & 0xff);
			taskCode[4] = InstructionTable.getInstructionCode("new");
			taskCode[5] = (byte) ((taskClassIndex >> 8) & 0xff);
			taskCode[6] = (byte) (taskClassIndex & 0xff);
			taskCode[7] = InstructionTable.getInstructionCode("dup");
			taskCode[8] = InstructionTable.getInstructionCode("aload_0");
			off = 9;
			for(int j = 0; j < paramDesc.length; j++) {
				byte[] codes = InstructionTable.decodeLoadIns(paramDesc[j],  j);
				if(codes.length == 1) {
					taskCode[off++] = codes[0];
				}
				if(codes.length == 2) {
					taskCode[off++] = codes[0];
					taskCode[off++] = codes[1];
				}
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void test() {

		try {
			ClassBytecode impl = (new ClassBytecode()).readAndDecodeClass(ClassA.class).generateImplClass("ClassA$Impl");
			impl.addField("nameCp", String.class, 1);
			int fieldIndex = impl.getConstantPool().findField("nameCp", String.class);
			int methodIndex = impl.getConstantPool().addMethod("setName", new String[] {"Ljava/lang/String;","I","Lcom/archer/tools/test/bytecode/ClassB;"}, "V", "com/archer/tools/test/bytecode/ClassA");
			
			
			CodeAttribute code = new CodeAttribute();
			code.setMaxLocals(4);
			code.setMaxStack(4);
			code.setAttributes(new AttributeInfo[0]);
			code.setException(new byte[0]);
			code.setLength(12 + 13);
			code.setNameIndex(impl.getConstantPool().findName("Code"));
			code.setName("Code");
			byte[] data = new byte[13];
			data[0] = InstructionTable.getInstructionCode("aload_0");
			data[1] = InstructionTable.getInstructionCode("aload_1");
			data[2] = InstructionTable.getInstructionCode("putfield");
			data[3] = (byte) ((fieldIndex >> 8) & 0xff);
			data[4] = (byte) (fieldIndex & 0xff);
			data[5] = InstructionTable.getInstructionCode("aload_0");
			data[6] = InstructionTable.getInstructionCode("aload_1");
			data[7] = InstructionTable.getInstructionCode("iload_2");
			data[8] = InstructionTable.getInstructionCode("aload_3");
			data[9] = InstructionTable.getInstructionCode("invokespecial");
			data[10] = (byte) ((methodIndex >> 8) & 0xff);
			data[11] = (byte) (methodIndex & 0xff);
			data[12] = InstructionTable.getInstructionCode("return");
			code.setCode(data);
			
			impl.addMethod("setName", new String[] {"Ljava/lang/String;","I","Lcom/archer/tools/test/bytecode/ClassB;"}, "V", code);
			impl.refreshClassEnd();
			
			Files.write(Paths.get("d:/test.class"), impl.encodeClassBytes().readAll());
			ClassBytecodePrinter.print(impl);
			
			Class<?> classAImpl = impl.loadSelfClass();
			Object classAins = ClassUtil.newInstance(classAImpl);
			Method setName = classAImpl.getDeclaredMethod("setName", String.class, int.class, ClassB.class);
			setName.invoke(classAins, "xuyihaoshuai", 18, null);
			
			Field f = classAImpl.getDeclaredField("nameCp");
			String nameCp = (String) f.get(classAins);
			System.out.println(nameCp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String args[]) {
		test();
		
	}
}
