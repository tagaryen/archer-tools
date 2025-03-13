package com.archer.tools.bytecode;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import com.archer.net.Bytes;
import com.archer.tools.bytecode.MemberInfo.AttributeInfo;
import com.archer.tools.bytecode.MemberInfo.CodeAttribute;
import com.archer.tools.bytecode.constantpool.ConstantClass;
import com.archer.tools.bytecode.constantpool.ConstantInfo;
import com.archer.tools.bytecode.constantpool.ConstantMemberRef;
import com.archer.tools.bytecode.constantpool.ConstantNameAndType;
import com.archer.tools.bytecode.constantpool.ConstantPool;
import com.archer.tools.bytecode.constantpool.ConstantUtf8;
import com.archer.tools.bytecode.util.DescriptorUtil;


public class ClassBytecode {
	
	private static BytecodeClassLoader loader = new BytecodeClassLoader();

	private Class<?> selfCls;
	
	private int magic;
    private int minorVersion;
    private int majorVersion;
    private ConstantPool constantPool;
    private int accessFlag;
    private int classIndex;
    private int superIndex;
    private int interfaceCount;
    private int[] interfaceIndexArr;
    
    private int fieldCount;
    private MemberInfo[] fields;
    private int methodCount;
    private MemberInfo[] methods;
    
    private ClassEnd classEnd;
    
    
    
    private String rawClassName;
    private String simpleName;
    private String className;
    private String superClass;
    private String[] interfaces;
    
    private boolean isInterface;
    
	public ClassBytecode() {
		this.constantPool = new ConstantPool();
		this.fields = new MemberInfo[0];
		this.methods = new MemberInfo[0];
		this.interfaceIndexArr  = new int[0];
		this.interfaces = new String[0];
		this.classEnd = new ClassEnd();
	}
	
	public int getMagic() {
		return magic;
	}
	public int getMinorVersion() {
		return minorVersion;
	}
	public int getMajorVersion() {
		return majorVersion;
	}
	public ConstantPool getConstantPool() {
		return constantPool;
	}
	public int getAccessFlag() {
		return accessFlag;
	}
	public String getClassName() {
		return className;
	}
	public String getRawClassName() {
		return rawClassName;
	}
	public String getSimpleName() {
		return simpleName;
	}
	public String getSuperClass() {
		return superClass;
	}
	public int getInterfaceCount() {
		return interfaceCount;
	}
	public String[] getInterfaces() {
		return interfaces;
	}
	public int getFieldCount() {
		return fieldCount;
	}
	public MemberInfo[] getFields() {
		return fields;
	}
	public int getMethodCount() {
		return methodCount;
	}
	public MemberInfo[] getMethods() {
		return methods;
	}
	public int getClassIndex() {
		return classIndex;
	}

	public int getSuperIndex() {
		return superIndex;
	}

	public int[] getInterfaceIndexArr() {
		return interfaceIndexArr;
	}

	public ClassEnd getClassEnd() {
		return classEnd;
	}

	public void setMagic(int magic) {
		this.magic = magic;
	}

	public void setMinorVersion(int minorVersion) {
		this.minorVersion = minorVersion;
	}

	public void setMajorVersion(int majorVersion) {
		this.majorVersion = majorVersion;
	}

	public void setConstantPool(ConstantPool constantPool) {
		this.constantPool = constantPool;
	}

	public void setAccessFlag(int accessFlag) {
		this.accessFlag = accessFlag;
	}

	public void setClassIndex(int classIndex) {
		this.classIndex = classIndex;
	}

	public void setSuperIndex(int superIndex) {
		this.superIndex = superIndex;
	}

	public void setInterfaceCount(int interfaceCount) {
		this.interfaceCount = interfaceCount;
	}

	public void setInterfaceIndexArr(int[] interfaceIndexArr) {
		this.interfaceIndexArr = interfaceIndexArr;
	}

	public void setFields(MemberInfo[] fields) {
		this.fields = fields;
		this.fieldCount = fields.length;
	}

	public void setMethods(MemberInfo[] methods) {
		this.methods = methods;
		this.methodCount = methods.length;
	}

	public void setClassEnd(ClassEnd classEnd) {
		this.classEnd = classEnd;
	}

	public void setClassName(String className) {
		if(className.indexOf('.') == -1 && className.indexOf('/') == -1) {
			if(className.equals(this.simpleName)) {
				throw new BytecodeException("duplicated calss name " + className);
			}
			this.simpleName = className;
			this.className = this.className.substring(0, this.className.lastIndexOf('.') + 1) + className;
			this.rawClassName = this.rawClassName.substring(0, this.rawClassName.lastIndexOf('/') + 1) + className;
		} else if(className.indexOf('.') == -1) {
			if(className.equals(this.rawClassName)) {
				throw new BytecodeException("duplicated class name " + className);
			}
			if(this.rawClassName != null && !this.rawClassName.substring(0, this.rawClassName.lastIndexOf('/')).equals(className.substring(0, className.lastIndexOf('/')))) {
				throw new BytecodeException("new class must be in the save package with old class");
			}
			this.rawClassName = className;
			this.className = DescriptorUtil.replaceSlash2Dot(className);
	        this.simpleName = className.substring(className.lastIndexOf('/') + 1);
		} else if(className.indexOf('/') == -1) {
			if(className.equals(this.className)) {
				throw new BytecodeException("duplicated class name " + className);
			}
			if(this.className != null && !this.className.substring(0, this.className.lastIndexOf('/')).equals(className.substring(0, className.lastIndexOf('/')))) {
				throw new BytecodeException("new class must be in the save package with old class");
			}
			this.className = className;
			this.rawClassName = DescriptorUtil.replaceDot2Slash(className);
	        this.simpleName = className.substring(className.lastIndexOf('.') + 1);
		}
	}

	public void setInterfaces(String[] interfaces) {
		this.interfaces = interfaces;
	}

	public boolean isInterface() {
		return isInterface;
	}

	public void setInterface(boolean isInterface) {
		this.isInterface = isInterface;
	}

	public ClassBytecode fillSelfAsEmptyClass(String className, String pkg) {
		
		this.simpleName = className;
		if(pkg.indexOf('.') == -1 && pkg.indexOf('/') == -1) {
			this.className = pkg + '.' + className;
			this.rawClassName = pkg + '/' + className;
		} else if(pkg.indexOf('.') == -1) {
			this.className = DescriptorUtil.replaceSlash2Dot(pkg) + '.' + className;
			this.rawClassName = pkg + '/' + className;
		} else if(pkg.indexOf('/') == -1) {
			this.className = pkg + '.' + className;
			this.rawClassName = DescriptorUtil.replaceDot2Slash(pkg) + '/' + className;
		}
		
		setMagic(-889275714);
		setMinorVersion(0);
		setMajorVersion(52);
		setAccessFlag(1);
		setClassIndex(1);
		setSuperIndex(3);
		setInterfaceCount(0);
		setInterfaceIndexArr(new int[0]);

		
		int index = 1;
		ConstantInfo[] constants = new ConstantInfo[256];
		
		ConstantClass clazzInfo = new ConstantClass();
		clazzInfo.setNameIndex(2);
		constants[index++] = clazzInfo;
		
		ConstantUtf8 clazzUtf8Info = new ConstantUtf8();
		clazzUtf8Info.setValue(rawClassName);
		constants[index++] = clazzUtf8Info;
		
		ConstantClass superInfo = new ConstantClass();
		superInfo.setNameIndex(4);
		constants[index++] = superInfo;
		
		ConstantUtf8 superUtf8Info = new ConstantUtf8();
		superUtf8Info.setValue("java/lang/Object");
		constants[index++] = superUtf8Info;

		ConstantUtf8 mcode = new ConstantUtf8();
		mcode.setValue("Code");
		constants[index++] = mcode;

		ConstantUtf8 lnt = new ConstantUtf8();
		lnt.setValue("LineNumberTable");
		constants[index++] = lnt;

		ConstantUtf8 lvt = new ConstantUtf8();
		lvt.setValue("LocalVariableTable");
		constants[index++] = lvt;
		

		ConstantUtf8 thi = new ConstantUtf8();
		thi.setValue("this");
		constants[index++] = thi;
		
		String classTypeName = "L"+rawClassName + ";";
		ConstantUtf8 thiType = new ConstantUtf8();
		thiType.setValue(classTypeName);
		constants[index++] = thiType;
		
		int consNameIndex = index;
		ConstantUtf8 consName = new ConstantUtf8();
		consName.setValue("<init>");
		constants[index++] = consName;
		

		int consDescIndex = index;
		ConstantUtf8 consDesc = new ConstantUtf8();
		consDesc.setValue("()V");
		constants[index++] = consDesc;
		
		ConstantMemberRef consMethod = new ConstantMemberRef(ConstantInfo.CONSTANT_Methodref);
		consMethod.setClassIndex(3);
		consMethod.setNameAndTypeIndex(index + 1);
		constants[index++] = consMethod;
		
		ConstantNameAndType consNameType = new ConstantNameAndType();
		consNameType.setNameIndex(consNameIndex);
		consNameType.setDescIndex(consDescIndex);
		constants[index++] = consNameType;
		

		int sourceIndex = index;
		ConstantUtf8 source = new ConstantUtf8();
		source.setValue("SourceFile");
		constants[index++] = source;

		int sourceNameIndex = index;
		ConstantUtf8 sourceName = new ConstantUtf8();
		sourceName.setValue(simpleName+".java");
		constants[index++] = sourceName;
		
		ConstantPool cp = new ConstantPool();
		cp.setCpInfo(Arrays.copyOfRange(constants, 0, index));
		cp.setConstantPoolCount(cp.getCpInfo().length);
		setConstantPool(cp);
	
		MemberInfo cons = new MemberInfo();
		cons.setAccessFlags(1);
		cons.setName("<init>");
		cons.setDesc("()V");
		cons.setNameIndex(consNameIndex);
		cons.setDescriptorIndex(consDescIndex);
		
		CodeAttribute codeAttr = new CodeAttribute();
		codeAttr.setName("Code");
		codeAttr.setNameIndex(cp.findName("Code"));
		codeAttr.setLength(17);
		codeAttr.setMaxStack(1);
		codeAttr.setMaxLocals(1);
		codeAttr.setCode(new byte[] {42, -73, 0, (byte) constantPool.findMethod("<init>", "()V", "java/lang/Object"), -79});
		
		codeAttr.setException(new byte[0]);
		codeAttr.setAttributes(new AttributeInfo[0]);
		
		cons.setAttributes(new AttributeInfo[] {codeAttr});
		setMethods(new MemberInfo[] {cons});
		
		ClassEnd end = new ClassEnd();
		end.setSourceLen(1);
		end.setSourceIndex(sourceIndex);
		end.setSourceNop(2);
		end.setSourceNameIndex(sourceNameIndex);
		
		setClassEnd(end);
		
		return this;
	}

	
    public ClassBytecode readAndDecodeClass(Class<?> cls) throws IOException {
    	String className = DescriptorUtil.replaceDot2Slash(cls.getName());
    	this.selfCls = cls;
    	this.isInterface = this.selfCls.isInterface();
		try(InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(className + ".class")) {
			Bytes rawClass = new Bytes();
			byte[] buf = new byte[1024];
			int off = 0;
			while((off = in.read(buf)) >= 0) {
				rawClass.write(buf, 0, off);
			}
			decodeClassBytes(rawClass);
		}
		return this;
	}
	
	public void decodeClassBytes(Bytes bytes) {
    	this.magic = bytes.readInt32();
    	this.minorVersion = bytes.readInt16();
    	this.majorVersion = bytes.readInt16();
    	this.constantPool.read(bytes);
        
        ConstantInfo[] cpInfo = constantPool.getCpInfo();
        
        //获取类信息
        this.accessFlag = bytes.readInt16();
        this.classIndex = bytes.readInt16();
        ConstantClass clazz = (ConstantClass) cpInfo[classIndex];
        ConstantUtf8 className = (ConstantUtf8) cpInfo[clazz.getNameIndex()];
        this.rawClassName = className.getValue();
        this.className = DescriptorUtil.replaceSlash2Dot(className.getValue());
        this.simpleName = this.className.substring(this.className.lastIndexOf('.') + 1);
        
        //获取父类信息
        this.superIndex = bytes.readInt16();
        ConstantClass superClazz = (ConstantClass)cpInfo[superIndex];
        ConstantUtf8 superclassName = (ConstantUtf8)cpInfo[superClazz.getNameIndex()];
        this.superClass = superclassName.getValue();
    	
        //获取接口信息
        this.interfaceCount = bytes.readInt16();
        this.interfaceIndexArr = new int[interfaceCount];
        String[] interfaceArr = new String[interfaceCount];
        for (int i = 0; i < interfaceCount; i++) {
            int interfaceIndex = bytes.readInt16();
            interfaceIndexArr[i] = interfaceIndex;
            ConstantClass interfaceClazz = (ConstantClass)cpInfo[interfaceIndex];
            ConstantUtf8 interfaceName = (ConstantUtf8)cpInfo[interfaceClazz.getNameIndex()];
            interfaceArr[i] = interfaceName.getValue();
        }
        this.interfaces = interfaceArr;

        //获取字段信息
        this.fieldCount = bytes.readInt16();
        MemberInfo[] fields = new MemberInfo[fieldCount];
        for (int i = 0; i < fieldCount; i++) {
        	fields[i] = new MemberInfo();
        	fields[i].read(bytes, cpInfo);
        }
        this.fields = fields;

        //获取方法信息
        this.methodCount = bytes.readInt16();
        MemberInfo[] methods = new MemberInfo[methodCount];
        for (int i = 0; i < methodCount; i++) {
        	methods[i] = new MemberInfo();
        	methods[i].read(bytes, cpInfo);
        }
        this.methods = methods;
        
        this.classEnd = new ClassEnd();
        this.classEnd.read(bytes);
    }
	
	
	public Bytes encodeClassBytes() {
		Bytes opcode = new Bytes();
		//写入头部
		opcode.writeInt32(magic);
		opcode.writeInt16(minorVersion);
		opcode.writeInt16(majorVersion);
		
		//写入常量池
		this.constantPool.write(opcode);
		
		
		//写入类信息
		opcode.writeInt16(accessFlag);
		opcode.writeInt16(classIndex);

		//写入父类信息
		opcode.writeInt16(superIndex);
		
		//写入接口信息
		opcode.writeInt16(interfaceCount);
		for(int i = 0; i < interfaceCount; i++) {
			opcode.writeInt16(interfaceIndexArr[i]);
		}

		//写入获取字段信息
		opcode.writeInt16(fieldCount);
		for (int i = 0; i < fieldCount; i++) {
			fields[i].write(opcode);
	    }

		//写入获取方法信息
		opcode.writeInt16(methodCount);
		for (int i = 0; i < methodCount; i++) {
			methods[i].write(opcode);
	    }
		
		classEnd.write(opcode);
		
		return opcode;
	}
	
	public Class<?> loadSelfClass() {
		refreshClassEnd();
		Bytes codeBs = encodeClassBytes();
		try {
			return loader.defineBytecodeClass(className, codeBs.array(), 0, codeBs.available()); 
			//return (Class<?>) defineClass.invoke(loader, className, codeBs.array(), 0, codeBs.available());
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public ClassBytecode generateImplClass(String name) {
		ClassBytecode newcls = new ClassBytecode();
		newcls.setClassName(name);
		newcls.setMagic(getMagic());
		newcls.setMinorVersion(getMinorVersion());
		newcls.setMajorVersion(getMajorVersion());
		newcls.setAccessFlag(getAccessFlag());
		newcls.setClassIndex(1);
		newcls.setSuperIndex(3);
		newcls.setInterfaceCount(0);
		newcls.setInterfaceIndexArr(new int[0]);

		
		int index = 1;
		ConstantInfo[] constants = new ConstantInfo[256];
		
		ConstantClass clazzInfo = new ConstantClass();
		clazzInfo.setNameIndex(2);
		constants[index++] = clazzInfo;
		
		ConstantUtf8 clazzUtf8Info = new ConstantUtf8();
		clazzUtf8Info.setValue(newcls.rawClassName);
		constants[index++] = clazzUtf8Info;
		
		if(this.isInterface) {
			ConstantClass interfaceInfo = new ConstantClass();
			interfaceInfo.setNameIndex(index+1);
			constants[index++] = interfaceInfo;
			
			ConstantUtf8 interfaceUtf8Info = new ConstantUtf8();
			interfaceUtf8Info.setValue("java/lang/Object");
			constants[index++] = interfaceUtf8Info;

			newcls.setInterfaceCount(1);
			newcls.setInterfaceIndexArr(new int[] {index});
		}
		
		ConstantClass superInfo = new ConstantClass();
		superInfo.setNameIndex(index+1);
		constants[index++] = superInfo;
		
		ConstantUtf8 superUtf8Info = new ConstantUtf8();
		superUtf8Info.setValue(this.rawClassName);
		constants[index++] = superUtf8Info;
		
		ConstantUtf8 mcode = new ConstantUtf8();
		mcode.setValue("Code");
		constants[index++] = mcode;

		ConstantUtf8 lnt = new ConstantUtf8();
		lnt.setValue("LineNumberTable");
		constants[index++] = lnt;

		ConstantUtf8 lvt = new ConstantUtf8();
		lvt.setValue("LocalVariableTable");
		constants[index++] = lvt;
		

		ConstantUtf8 thi = new ConstantUtf8();
		thi.setValue("this");
		constants[index++] = thi;
		
		String classTypeName = "L"+newcls.rawClassName + ";";
		ConstantUtf8 thiType = new ConstantUtf8();
		thiType.setValue(classTypeName);
		constants[index++] = thiType;
		
		int consNameIndex = index;
		ConstantUtf8 consName = new ConstantUtf8();
		consName.setValue("<init>");
		constants[index++] = consName;
		

		int consDescIndex = index;
		ConstantUtf8 consDesc = new ConstantUtf8();
		consDesc.setValue("()V");
		constants[index++] = consDesc;

		int superInitIndex = index;
		ConstantMemberRef consMethod = new ConstantMemberRef(ConstantInfo.CONSTANT_Methodref);
		consMethod.setClassIndex(3);
		consMethod.setNameAndTypeIndex(index + 1);
		constants[index++] = consMethod;
		
		ConstantNameAndType consNameType = new ConstantNameAndType();
		consNameType.setNameIndex(consNameIndex);
		consNameType.setDescIndex(consDescIndex);
		constants[index++] = consNameType;
		

		int sourceIndex = index;
		ConstantUtf8 source = new ConstantUtf8();
		source.setValue("SourceFile");
		constants[index++] = source;

		int sourceNameIndex = index;
		ConstantUtf8 sourceName = new ConstantUtf8();
		sourceName.setValue(newcls.simpleName+".java");
		constants[index++] = sourceName;
		
		ConstantPool cp = new ConstantPool();
		cp.setCpInfo(Arrays.copyOfRange(constants, 0, index));
		cp.setConstantPoolCount(cp.getCpInfo().length);
		newcls.setConstantPool(cp);
	
		MemberInfo cons = new MemberInfo();
		cons.setAccessFlags(1);
		cons.setName("<init>");
		cons.setDesc("()V");
		cons.setNameIndex(consNameIndex);
		cons.setDescriptorIndex(consDescIndex);
		
		CodeAttribute codeAttr = new CodeAttribute();
		codeAttr.setName("Code");
		codeAttr.setNameIndex(cp.findName("Code"));
		codeAttr.setLength(17);
		codeAttr.setMaxStack(1);
		codeAttr.setMaxLocals(1);
		codeAttr.setCode(new byte[] {42, -73, (byte) ((superInitIndex >> 8) & 0xff), (byte) (superInitIndex & 0xff), -79});
		
		codeAttr.setException(new byte[0]);
		codeAttr.setAttributes(new AttributeInfo[0]);
		
		cons.setAttributes(new AttributeInfo[] {codeAttr});
		newcls.setMethods(new MemberInfo[] {cons});
		
		ClassEnd end = new ClassEnd();
		end.setSourceLen(1);
		end.setSourceIndex(sourceIndex);
		end.setSourceNop(2);
		end.setSourceNameIndex(sourceNameIndex);
		
		newcls.setClassEnd(end);
		
		return newcls;
	}
	
	public void addField(String name, Class<?> type, int accessFlag) {
		addField(name, DescriptorUtil.getClassDescription(type), accessFlag);
	}
	
	public void addField(String name, String typeDesc, int accessFlag) {
		int fieldIndex = constantPool.addField(name, typeDesc);
		ConstantMemberRef field = (ConstantMemberRef) constantPool.getCpInfo()[fieldIndex];
		ConstantNameAndType nameType = (ConstantNameAndType) constantPool.getCpInfo()[field.getNameAndTypeIndex()];
		MemberInfo[] newFields = new MemberInfo[fields.length + 1];
		System.arraycopy(fields, 0, newFields, 0, fields.length);
		MemberInfo newField = new MemberInfo();
		newField.setAccessFlags(accessFlag);
		newField.setName(name);
		newField.setDesc(typeDesc);
		newField.setNameIndex(nameType.getNameIndex());
		newField.setDescriptorIndex(nameType.getDescIndex());
		newField.setAttributes(new AttributeInfo[0]);
		newFields[newFields.length - 1] = newField;
		setFields(newFields);
	}
	
	public void addMethod(String name, Class<?>[] params, Class<?> returnType, CodeAttribute codeAttr) {
    	String[] paramDesces = null;
    	if(params != null) {
    		paramDesces = new String[params.length];
    		for(int i = 0; i < params.length; i++) {
    			paramDesces[i] = DescriptorUtil.getClassDescription(params[i]);
    		}
    	}
    	String returnTypeDesc = null;
    	if(returnType != null) {
    		returnTypeDesc = DescriptorUtil.getClassDescription(returnType);
    	}
    	addMethod(name, paramDesces, returnTypeDesc, codeAttr);
	}
	
	public void addMethod(String name, String[] paramDesces, String returnTypeDesc, CodeAttribute codeAttr) {
    	String desc = DescriptorUtil.getMethodDescription(paramDesces, returnTypeDesc);
    	int nameIndex = constantPool.addName(name);
    	int descIndex = constantPool.addName(desc);
		MemberInfo[] newMethods = new MemberInfo[methods.length + 1];
		System.arraycopy(methods, 0, newMethods, 0, methods.length);
		MemberInfo method = new MemberInfo();
		method.setAccessFlags(1);
		method.setName(name);
		method.setDesc(desc);
		method.setNameIndex(nameIndex);
		method.setDescriptorIndex(descIndex);
		method.setAttributes(new AttributeInfo[] {codeAttr});
		
		newMethods[methods.length] = method;
		
		setMethods(newMethods);
	}
	
	public void renameMethod(String oldName, String[] paramDesces, String returnTypeDesc, String newName) {
    	String desc = DescriptorUtil.getMethodDescription(paramDesces, returnTypeDesc);
		for(int i = 0; i < methods.length; i++) {
			String theName = ((ConstantUtf8)constantPool.getCpInfo()[methods[i].getNameIndex()]).getValue();
			String theDesc = ((ConstantUtf8)constantPool.getCpInfo()[methods[i].getDescriptorIndex()]).getValue();
			if(oldName.equals(theName) && desc.equals(theDesc)) {
				int newIdx = constantPool.addName(newName);
				methods[i].setNameIndex(newIdx);
			}
		}
	}
	
	public void refreshClassEnd() {
		int sourceIndex = constantPool.findSourceIndex();
		classEnd.setSourceIndex(sourceIndex);
		classEnd.setSourceNameIndex(sourceIndex + 1);
		if(classEnd.getInnerClassRawNameIndex() > 0) {
			classEnd.setInnerClassRawNameIndex(sourceIndex + 2);
		}
	}
}
