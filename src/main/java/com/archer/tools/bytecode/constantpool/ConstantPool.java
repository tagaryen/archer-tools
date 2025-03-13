package com.archer.tools.bytecode.constantpool;

import com.archer.net.Bytes;
import com.archer.tools.bytecode.BytecodeException;
import com.archer.tools.bytecode.util.DescriptorUtil;

public class ConstantPool {
	
	private static final String SOURCE_FILE = "SourceFile";
	
	private int constantPoolCount;
	private ConstantInfo[] cpInfo;

    public ConstantPool() {
    	this(0, new ConstantInfo[0]);
    }

    public ConstantPool(int constantPoolCount, ConstantInfo[] cpInfo) {
		this.constantPoolCount = constantPoolCount;
		this.cpInfo = cpInfo;
	}

	public int getConstantPoolCount() {
		return constantPoolCount;
	}

	public ConstantInfo[] getCpInfo() {
		return cpInfo;
	}
	public void setConstantPoolCount(int constantPoolCount) {
		this.constantPoolCount = constantPoolCount;
	}

	public void setCpInfo(ConstantInfo[] cpInfo) {
		this.cpInfo = cpInfo;
	}



	public void read(Bytes bytes) {
        constantPoolCount = bytes.readInt16();
        cpInfo = new ConstantInfo[constantPoolCount];
        for (int i = 1; i < constantPoolCount; i++) {
            int tag = (int) bytes.readInt8();
            ConstantInfo constantInfo = ConstantInfo.getConstantInfo(tag);
            constantInfo.read(bytes);
            cpInfo[i] = constantInfo;
            if (tag == ConstantInfo.CONSTANT_Double || tag == ConstantInfo.CONSTANT_Long) {
                i++;
            }
        }
    }
    
    public void write(Bytes bytes) {
    	bytes.writeInt16(constantPoolCount);
        for (int i = 1; i < constantPoolCount; i++) {
            ConstantInfo constantInfo = cpInfo[i];
            int tag = constantInfo.tag;
        	bytes.writeInt8(constantInfo.tag);
            constantInfo.write(bytes);
            if (tag == ConstantInfo.CONSTANT_Double || tag == ConstantInfo.CONSTANT_Long) {
                i++;
            }
        }
    }
    
    public int addName(String name) {
    	int idx = findName(name);
    	if(idx != 0) {
    		return idx;
    	}

    	ConstantInfo[] newCps = new ConstantInfo[cpInfo.length + 1];
    	
    	int sourceIndex = findSourceIndex();
    	
    	System.arraycopy(cpInfo, 0, newCps, 0, sourceIndex);
    	System.arraycopy(cpInfo, sourceIndex, newCps, sourceIndex + 1, cpInfo.length - sourceIndex);

    	ConstantUtf8 nameUtf8 = new ConstantUtf8();
    	nameUtf8.setValue(name);
    	newCps[sourceIndex] = nameUtf8;
    	
    	cpInfo = newCps;
    	
    	return sourceIndex;
    }
    
    public int addField(String name, Class<?> type) {
    	return addField(name, DescriptorUtil.getClassDescription(type));
    }
    

    public int addField(String name, String typeDesc) {
    	int idx = findField(name, typeDesc);
    	if(idx != 0) {
    		throw new BytecodeException("duplicated field " + name);
    	}
    	int off = 0, fieldIndex = 0;
    	ConstantInfo[] appends = new ConstantInfo[4];
    	
    	int sourceIndex = findSourceIndex();
    	
    	int nameIndex = findName(name);
    	if(nameIndex == 0) {
    		nameIndex = sourceIndex + off;
        	ConstantUtf8 nameUtf8 = new ConstantUtf8();
        	nameUtf8.setValue(name);
        	appends[off++] = nameUtf8;
    	}
    	
    	int descIndex = findName(typeDesc);
    	if(descIndex == 0) {
    		descIndex = sourceIndex + off;
        	ConstantUtf8 typeUtf8 = new ConstantUtf8();
        	typeUtf8.setValue(typeDesc);
        	appends[off++] = typeUtf8;
    	}

    	fieldIndex = sourceIndex + off;
    	ConstantMemberRef member = new ConstantMemberRef(ConstantInfo.CONSTANT_Fieldref);
    	member.setClassIndex(1);
    	member.setNameAndTypeIndex(fieldIndex + 1);
    	appends[off++] = member;
    	
    	ConstantNameAndType nameType = new ConstantNameAndType();
    	nameType.setNameIndex(nameIndex);
    	nameType.setDescIndex(descIndex);
    	appends[off++] = nameType;
    	
    	ConstantInfo[] newCpInfos = new ConstantInfo[cpInfo.length + off];
    	System.arraycopy(cpInfo, 0, newCpInfos, 0, sourceIndex);
    	System.arraycopy(appends, 0, newCpInfos, sourceIndex, off);
    	System.arraycopy(cpInfo, sourceIndex, newCpInfos, sourceIndex + off, cpInfo.length - sourceIndex);
    	
    	cpInfo = newCpInfos;
    	constantPoolCount = cpInfo.length;
    	
    	return fieldIndex;
    }
    
    
    public int addMethod(String name, Class<?>[] params, Class<?> returnType, Class<?> refClass) {
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
    	String refClassDesc = DescriptorUtil.replaceDot2Slash(refClass.getName());
    	return addMethod(name, paramDesces, returnTypeDesc, refClassDesc);
    }
    


    public int addMethod(String name, String[] paramDesces, String returnTypeDesc, String refClassName) {
    	String desc = DescriptorUtil.getMethodDescription(paramDesces, returnTypeDesc);
    	int methodIndex = findMethod(name, desc, refClassName);
    	if(methodIndex != 0) {
    		throw new BytecodeException("duplicated method " + name);
    	}
    	int off = 0, theMethodIndex = 0;
    	ConstantInfo[] appends = new ConstantInfo[16];

    	int firstIndex = findSourceIndex();
    	
    	int nameIndex = findName(name);
    	if(nameIndex == 0) {
    		nameIndex = firstIndex + off;

        	ConstantUtf8 typeUtf8 = new ConstantUtf8();
        	typeUtf8.setValue(name);
        	appends[off++] = typeUtf8;
    	}
    	
    	int descIndex = findName(desc);
    	if(descIndex == 0) {
    		descIndex = firstIndex + off;
        	ConstantUtf8 typeUtf8 = new ConstantUtf8();
        	typeUtf8.setValue(desc);
        	appends[off++] = typeUtf8;
    	}
//    	if(paramDesces != null) {
//        	for(String s: paramDesces) {
//            	if(findName(s) == 0) {
//                	ConstantUtf8 typeUtf8 = new ConstantUtf8();
//                	typeUtf8.setValue(desc);
//                	appends[off++] = typeUtf8;
//            	}
//        	}
//    	}

    	theMethodIndex = firstIndex + off;
    	ConstantMemberRef member = new ConstantMemberRef(ConstantInfo.CONSTANT_Methodref);
    	member.setNameAndTypeIndex(theMethodIndex + 1);
    	appends[off++] = member;
    	
    	ConstantNameAndType nameType = new ConstantNameAndType();
    	nameType.setNameIndex(nameIndex);
    	nameType.setDescIndex(descIndex);
    	appends[off++] = nameType;

    	int refClassIndex = findClass(refClassName);
    	if(refClassIndex == 0) {
        	member.setClassIndex(firstIndex + off);
        	ConstantClass refClassCon = new ConstantClass();
        	appends[off++] = refClassCon;
        	int refClassNameIndex = findName(refClassName);
        	if(refClassNameIndex == 0) {
        		refClassCon.setNameIndex(firstIndex + off);
            	ConstantUtf8 refClassNameUtf8 = new ConstantUtf8();
            	refClassNameUtf8.setValue(refClassName);
            	appends[off++] = refClassNameUtf8;
        	} else {
        		refClassCon.setNameIndex(refClassNameIndex);
        	}
        	
    	} else {
        	member.setClassIndex(refClassIndex);
    	}
    	
    	ConstantInfo[] newCpInfos = new ConstantInfo[cpInfo.length + off];
    	System.arraycopy(cpInfo, 0, newCpInfos, 0, firstIndex);
    	System.arraycopy(appends, 0, newCpInfos, firstIndex, off);
    	System.arraycopy(cpInfo, firstIndex, newCpInfos, firstIndex + off, cpInfo.length - firstIndex);
    	
    	cpInfo = newCpInfos;
    	constantPoolCount = cpInfo.length;
    	
    	return theMethodIndex;
    }
    
    
    public int addClass(Class<?> cls) {
    	return addClass(DescriptorUtil.replaceDot2Slash(cls.getName()));
    }
    

    public int addClass(String className) {
    	int idx = findClass(className);
    	if(idx != 0) {
    		throw new BytecodeException("duplicated class " + className);
    	}

    	int sourceIndex = findSourceIndex();
    	ConstantInfo[] appends = new ConstantInfo[2];
    	
    	
    	int off = 0, classIndex = 0;
    	ConstantClass clsCon = new ConstantClass();
    	classIndex = sourceIndex + off;
    	appends[off++] = clsCon;
    	

    	int nameIndex = findName(className);
    	if(nameIndex == 0) {
    		clsCon.setNameIndex(sourceIndex + off);
        	ConstantUtf8 nameUtf8 = new ConstantUtf8();
        	nameUtf8.setValue(className);
        	appends[off++] = nameUtf8;
    	} else {
    		clsCon.setNameIndex(nameIndex);
    	}
    	
    	ConstantInfo[] newCpInfos = new ConstantInfo[cpInfo.length + off];
    	System.arraycopy(cpInfo, 0, newCpInfos, 0, sourceIndex);
    	System.arraycopy(appends, 0, newCpInfos, sourceIndex, off);
    	System.arraycopy(cpInfo, sourceIndex, newCpInfos, sourceIndex + off, cpInfo.length - sourceIndex);
    	
    	cpInfo = newCpInfos;
    	constantPoolCount = cpInfo.length;
    	
    	return classIndex;
    }
    
    public void resetUtf8Name(String oldName, String newName) {
    	int idx = findName(oldName);
    	if(idx == 0) {
    		throw new BytecodeException("can not found name " + oldName);
    	}
    	((ConstantUtf8) cpInfo[idx]).setValue(newName);
    }
    
    public int findSourceIndex() {
    	for(int i = 1; i < cpInfo.length; i++) {
    		if(cpInfo[i].tag == ConstantInfo.CONSTANT_Utf8) {
    			if(((ConstantUtf8)cpInfo[i]).getValue().equals(SOURCE_FILE)) {
    				return i;
    			}
    		}
    	}
    	return 0;
    }

    public int findClass(Class<?> cls) {
    	return findClass(DescriptorUtil.replaceDot2Slash(cls.getName()));
    }
    
    public int findClass(String classDesc) {
    	for(int i = 1; i < cpInfo.length; i++) {
    		if(cpInfo[i].tag == ConstantInfo.CONSTANT_Class) {
    			ConstantClass clsCon = (ConstantClass) cpInfo[i];
    			if(((ConstantUtf8)cpInfo[clsCon.getNameIndex()]).getValue().equals(classDesc)) {
    				return i;
    			}
    		}
    	}
    	return 0;
    }
    
    public int findName(String name) {
    	for(int i = 1; i < cpInfo.length; i++) {
    		if(cpInfo[i].tag == ConstantInfo.CONSTANT_Utf8) {
    			if(((ConstantUtf8)cpInfo[i]).getValue().equals(name)) {
    				return i;
    			}
    		}
    	}
    	return 0;
    }
    
    public int findField(String name, Class<?> type) {
    	return findField(name, DescriptorUtil.getClassDescription(type));
    }
    
    public int findField(String name, String fieldDesc) {
    	for(int i = 1; i < cpInfo.length; i++) {
    		if(cpInfo[i].tag == ConstantInfo.CONSTANT_Fieldref) {
    			ConstantMemberRef cons = (ConstantMemberRef) cpInfo[i];
    			ConstantNameAndType consNameType = (ConstantNameAndType) cpInfo[cons.getNameAndTypeIndex()];
    			if(((ConstantUtf8)cpInfo[consNameType.getNameIndex()]).getValue().equals(name)
        				&& ((ConstantUtf8)cpInfo[consNameType.getDescIndex()]).getValue().equals(fieldDesc)) {
    				return i;
    			}
    		}
    	}
    	return 0;
    }
    
    public int findMethod(String name, String[] paramDesces, String returnTypeDesc, String refClassName) {
    	String desc = DescriptorUtil.getMethodDescription(paramDesces, returnTypeDesc);
    	return findMethod(name, desc, refClassName);
    }
    
    public int findMethod(String name, String desc, String refClassName) {
    	for(int i = 1; i < cpInfo.length; i++) {
    		if(cpInfo[i].tag == ConstantInfo.CONSTANT_Methodref) {
    			ConstantMemberRef cons = (ConstantMemberRef) cpInfo[i];
    			ConstantClass refClass = (ConstantClass) cpInfo[cons.getClassIndex()];
    			if(!((ConstantUtf8)cpInfo[refClass.getNameIndex()]).getValue().equals(refClassName)) {
    				continue ;
    			}
    			ConstantNameAndType consNameType = (ConstantNameAndType) cpInfo[cons.getNameAndTypeIndex()];
    			if(((ConstantUtf8)cpInfo[consNameType.getNameIndex()]).getValue().equals(name)
    				&& ((ConstantUtf8)cpInfo[consNameType.getDescIndex()]).getValue().equals(desc)) {
    				return i;
    			}
    		}
    	}
    	return 0;
    }
    
    public int findNameAndType(String name, String desc) {
    	for(int i = 1; i < cpInfo.length; i++) {
    		if(cpInfo[i].tag == ConstantInfo.CONSTANT_NameAndType) {
    			ConstantNameAndType consNameType = (ConstantNameAndType) cpInfo[i];
    			if(((ConstantUtf8)cpInfo[consNameType.getNameIndex()]).getValue().equals(name)
    				&& ((ConstantUtf8)cpInfo[consNameType.getDescIndex()]).getValue().equals(desc)) {
    				return i;
    			}
    		}
    	}
    	return 0;
    }
}
