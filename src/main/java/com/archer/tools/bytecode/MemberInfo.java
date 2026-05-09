package com.archer.tools.bytecode;


import com.archer.net.Bytes;
import com.archer.tools.bytecode.constantpool.ConstantInfo;
import com.archer.tools.bytecode.constantpool.ConstantUtf8;


public class MemberInfo {
    private int accessFlags;
    private int nameIndex;
    private int descriptorIndex;
    private int attributesCount;
    
    private AttributeInfo[] attributes;
    

    private String name;
    private String desc;
    
    
    
    public int getAccessFlags() {
		return accessFlags;
	}

	public int getNameIndex() {
		return nameIndex;
	}

	public int getDescriptorIndex() {
		return descriptorIndex;
	}

	public int getAttributesCount() {
		return attributesCount;
	}

	public AttributeInfo[] getAttributes() {
		return attributes;
	}

	public String getName() {
		return name;
	}

	public String getDesc() {
		return desc;
	}

	public void setAccessFlags(int accessFlags) {
		this.accessFlags = accessFlags;
	}

	public void setNameIndex(int nameIndex) {
		this.nameIndex = nameIndex;
	}

	public void setDescriptorIndex(int descriptorIndex) {
		this.descriptorIndex = descriptorIndex;
	}

	public void setAttributes(AttributeInfo[] attributes) {
		this.attributesCount = attributes.length;
		this.attributes = attributes;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public static class ExceptionTableEntry {
        int startPc, endPc, handlerPc, catchType;

		public int getStartPc() {
			return startPc;
		}

		public void setStartPc(int startPc) {
			this.startPc = startPc;
		}

		public int getEndPc() {
			return endPc;
		}

		public void setEndPc(int endPc) {
			this.endPc = endPc;
		}

		public int getHandlerPc() {
			return handlerPc;
		}

		public void setHandlerPc(int handlerPc) {
			this.handlerPc = handlerPc;
		}

		public int getCatchType() {
			return catchType;
		}

		public void setCatchType(int catchType) {
			this.catchType = catchType;
		}
        
    }
	
	public static class AttributeInfo {
       
		private int nameIndex;
		private int length;
		private byte[] info;
		private String name;
		
		public AttributeInfo(int nameIdx, int len, byte[] data) { nameIndex = nameIdx; length = len; info = data; }
       

		public int getNameIndex() {
			return nameIndex;
		}

		public int getLength() {
			return length;
		}

		public byte[] getInfo() {
			return info;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
    }
    
	public static class CodeAttributeWriter {
		int nameIndex;
		Bytes data;
		
		public CodeAttributeWriter(int maxStack, int maxLocals) {
			data.writeInt16(maxStack);
			data.writeInt16(maxLocals);
		}
		
		public CodeAttributeWriter addInstruction(int instruction) {
			data.writeInt8(instruction);
			return this;
		}
		
		public CodeAttributeWriter addInstruction8(int instruction, int pos) {
			data.writeInt8(instruction);
			data.writeInt8(pos);
			return this;
		}
		
		public CodeAttributeWriter addInstruction16(int instruction, int pos) {
			data.writeInt8(instruction);
			data.writeInt16(pos);
			return this;
		}
	}
	
    public static class CodeAttribute extends AttributeInfo {

    	public CodeAttribute(int nameIdx, int len, byte[] data) {
			super(nameIdx, len, data);
		}
        
    	private int maxStack, maxLocals;
    	private int codeLength;
    	private byte[] code;
    	private int excepetionTableLength;
        private ExceptionTableEntry[] exceptionTable;
    	private int attributesCount;
    	private AttributeInfo[] attributes;
		public int getMaxStack() {
			return maxStack;
		}
		public int getMaxLocals() {
			return maxLocals;
		}
		public int getCodeLength() {
			return codeLength;
		}
		public byte[] getCode() {
			return code;
		}
		public int getExcepetionTableLength() {
			return excepetionTableLength;
		}
		public int getAttributesCount() {
			return attributesCount;
		}
		public AttributeInfo[] getAttributes() {
			return attributes;
		}
		public void setMaxStack(int maxStack) {
			this.maxStack = maxStack;
		}
		public void setMaxLocals(int maxLocals) {
			this.maxLocals = maxLocals;
		}
		public void setCode(byte[] code) {
			this.code = code;
			this.codeLength = code.length;
		}
		public void setAttributes(AttributeInfo[] attributes) {
			this.attributes = attributes;
			this.attributesCount = attributes.length;
		}
		public ExceptionTableEntry[] getExceptionTable() {
			return exceptionTable;
		}
		public void setExceptionTable(ExceptionTableEntry[] exceptionTable) {
			this.exceptionTable = exceptionTable;
			this.excepetionTableLength = exceptionTable.length;
		}
    	
    }
    
    public static class LineNumAttribute extends AttributeInfo {
    	
    	public LineNumAttribute(int nameIdx, int len, byte[] data) {
			super(nameIdx, len, data);
		}
		
		private int lineNumTableCount;
    	/*
    	 * {
    	 * 		u2 start_pc  字节码位置   
    	 *      u2 line_number   源代码位置
    	 * } []
    	 * */
    	private int lineNumTable[][];
		public int getLineNumTableCount() {
			return lineNumTableCount;
		}
		public int[][] getLineNumTable() {
			return lineNumTable;
		}
		public void setLineNumTableCount(int lineNumTableCount) {
			this.lineNumTableCount = lineNumTableCount;
		}
		public void setLineNumTable(int[][] lineNumTable) {
			this.lineNumTable = lineNumTable;
		}
    	
    }
    
    public static class LocalVarAttribute extends AttributeInfo {

    	public LocalVarAttribute(int nameIdx, int len, byte[] data) {
			super(nameIdx, len, data);
		}
		private int localVarTableCount;
    	/*
    	 * {
    	 * 		u2 start_pc  字节码位置   
    	 *      u2 length   源代码位置
    	 *      u2 name_index 
    	 *      u2 desc_index
    	 *      u2 index
    	 * } []
    	 * */
    	private int localVarTable[][];
		public int getLocalVarTableCount() {
			return localVarTableCount;
		}
		public int[][] getLocalVarTable() {
			return localVarTable;
		}
		public void setLocalVarTableCount(int localVarTableCount) {
			this.localVarTableCount = localVarTableCount;
		}
		public void setLocalVarTable(int[][] localVarTable) {
			this.localVarTable = localVarTable;
		}
    }
    
    public void read(Bytes bytes, ConstantInfo[] cpInfo) {
    	accessFlags = bytes.readInt16();
    	nameIndex = bytes.readInt16();
    	descriptorIndex = bytes.readInt16();
    	attributesCount = bytes.readInt16();
    	attributes = new AttributeInfo[attributesCount];
    	
    	this.name = ((ConstantUtf8) cpInfo[nameIndex]).getValue();
    	this.desc = ((ConstantUtf8) cpInfo[descriptorIndex]).getValue();
    	
    	readAttributes(bytes, attributes, cpInfo);
    }

    public void write(Bytes bytes) {
    	bytes.writeInt16(accessFlags);
    	bytes.writeInt16(nameIndex);
    	bytes.writeInt16(descriptorIndex);
    	bytes.writeInt16(attributesCount);
    	
    	writeAttributes(bytes, attributes);
    }
    
	private void readAttributes(Bytes input, AttributeInfo[] attributes, ConstantInfo[] cpInfo) {
		for (int j = 0; j < attributes.length; j++) {
    		int nameIndex = input.readInt16();
    		int length = input.readInt32();
            byte[] info = input.read(length);
            Bytes bytes = new Bytes(info);
    		String name = ((ConstantUtf8) cpInfo[nameIndex]).getValue();
    		if("Code".equals(name)) {
    			CodeAttribute attr = new CodeAttribute(nameIndex, length, info);
    			attr.setName(name);
    			
    			attr.setMaxStack(bytes.readInt16()); //2bytes
    			attr.setMaxLocals(bytes.readInt16()); //2bytes
    			int codeLength = bytes.readInt32();   //4bytes
    			attr.setCode(bytes.read(codeLength)); //length bytes
    			
    			int exceptionTableLength = bytes.readInt16();    // 2bytes
    			ExceptionTableEntry[] exceptions = new ExceptionTableEntry[exceptionTableLength];
                for (int i = 0; i < exceptionTableLength; i++) {
                    ExceptionTableEntry e = new ExceptionTableEntry();
                    e.startPc = bytes.readInt16();
                    e.endPc = bytes.readInt16();
                    e.handlerPc = bytes.readInt16();
                    e.catchType = bytes.readInt16();
                    exceptions[i] = e;
                }
                
    			int attrCount = bytes.readInt16(); //2bytes
    			attr.setAttributes(new AttributeInfo[attrCount]);
    			readAttributes(bytes, attr.attributes, cpInfo);
    			
    			attributes[j] = attr;
    		} else if("LineNumberTable".equals(name)) {
    			LineNumAttribute attr = new LineNumAttribute(nameIndex, length, info);
    			attr.setName(name);
    			
    			attr.setLineNumTableCount(bytes.readInt16());
    			int[][] lineNumTable = new int[attr.lineNumTableCount][2];
    			attr.setLineNumTable(lineNumTable);
    			for(int i = 0; i < attr.lineNumTableCount; i++) {
    				lineNumTable[i][0] = bytes.readInt16();
    				lineNumTable[i][1] = bytes.readInt16();
    			}
    			attributes[j] = attr;
    		} else if("LocalVariableTable".equals(name)) {
    			LocalVarAttribute attr = new LocalVarAttribute(nameIndex, length, info);
    			attr.setName(name);
    			
    			attr.setLocalVarTableCount(bytes.readInt16());
    			int[][] localVarTable = new int[attr.localVarTableCount][5];
    			attr.setLocalVarTable(localVarTable);
    			for(int i = 0; i < attr.localVarTableCount; i++) {
    				localVarTable[i][0] = bytes.readInt16();
    				localVarTable[i][1] = bytes.readInt16();
    				localVarTable[i][2] = bytes.readInt16();
    				localVarTable[i][3] = bytes.readInt16();
    				localVarTable[i][4] = bytes.readInt16();
    			}
    			attributes[j] = attr;
    		} else {
    			AttributeInfo attr = new AttributeInfo(nameIndex, length, info);
    			attr.setName(name);
    			attributes[j] = attr;
    		}
        }
	}
	
	private void writeAttributes(Bytes bytes, AttributeInfo[] attrs) {
		for (int j = 0; j < attrs.length; j++) {
			AttributeInfo attr = attrs[j];
			bytes.writeInt16(attr.nameIndex);
			bytes.writeInt32(attr.length);
			bytes.write(attr.info);
        }
	}
	
}
