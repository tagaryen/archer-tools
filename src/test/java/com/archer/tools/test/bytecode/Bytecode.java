package com.archer.tools.test.bytecode;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Bytecode {

    private ByteReader reader;
    private List<ConstantInfo> constantPool;
    private int majorVersion;
    private int minorVersion;
    private int accessFlags;
    private int thisClass;
    private int superClass;
    private List<Integer> interfaces;
    private List<FieldInfo> fields;
    private List<MethodInfo> methods;
    private List<AttributeInfo> attributes;

    // 指令助记符映射表（常用指令）
    private static final Map<Integer, String> OPCODE_MAP = new HashMap<>();

    static {
        // 加载常用指令
        String[][] opcodes = {
            { "0x00", "nop" }, { "0x01", "aconst_null" }, { "0x02", "iconst_m1" },
            { "0x03", "iconst_0" }, { "0x04", "iconst_1" }, { "0x05", "iconst_2" },
            { "0x06", "iconst_3" }, { "0x07", "iconst_4" }, { "0x08", "iconst_5" },
            { "0x09", "lconst_0" }, { "0x0a", "lconst_1" }, { "0x0b", "fconst_0" },
            { "0x0c", "fconst_1" }, { "0x0d", "fconst_2" }, { "0x0e", "dconst_0" },
            { "0x0f", "dconst_1" }, { "0x10", "bipush" }, { "0x11", "sipush" },
            { "0x12", "ldc" }, { "0x13", "ldc_w" }, { "0x14", "ldc2_w" },
            { "0x15", "iload" }, { "0x16", "lload" }, { "0x17", "fload" },
            { "0x18", "dload" }, { "0x19", "aload" }, { "0x1a", "iload_0" },
            { "0x1b", "iload_1" }, { "0x1c", "iload_2" }, { "0x1d", "iload_3" },
            { "0x1e", "lload_0" }, { "0x1f", "lload_1" }, { "0x20", "lload_2" },
            { "0x21", "lload_3" }, { "0x22", "fload_0" }, { "0x23", "fload_1" },
            { "0x24", "fload_2" }, { "0x25", "fload_3" }, { "0x26", "dload_0" },
            { "0x27", "dload_1" }, { "0x28", "dload_2" }, { "0x29", "dload_3" },
            { "0x2a", "aload_0" }, { "0x2b", "aload_1" }, { "0x2c", "aload_2" },
            { "0x2d", "aload_3" }, { "0x2e", "iaload" }, { "0x2f", "laload" },
            { "0x30", "faload" }, { "0x31", "daload" }, { "0x32", "aaload" },
            { "0x33", "baload" }, { "0x34", "caload" }, { "0x35", "saload" },
            { "0x36", "istore" }, { "0x37", "lstore" }, { "0x38", "fstore" },
            { "0x39", "dstore" }, { "0x3a", "astore" }, { "0x3b", "istore_0" },
            { "0x3c", "istore_1" }, { "0x3d", "istore_2" }, { "0x3e", "istore_3" },
            { "0x3f", "lstore_0" }, { "0x40", "lstore_1" }, { "0x41", "lstore_2" },
            { "0x42", "lstore_3" }, { "0x43", "fstore_0" }, { "0x44", "fstore_1" },
            { "0x45", "fstore_2" }, { "0x46", "fstore_3" }, { "0x47", "dstore_0" },
            { "0x48", "dstore_1" }, { "0x49", "dstore_2" }, { "0x4a", "dstore_3" },
            { "0x4b", "astore_0" }, { "0x4c", "astore_1" }, { "0x4d", "astore_2" },
            { "0x4e", "astore_3" }, { "0x4f", "iastore" }, { "0x50", "lastore" },
            { "0x51", "fastore" }, { "0x52", "dastore" }, { "0x53", "aastore" },
            { "0x54", "bastore" }, { "0x55", "castore" }, { "0x56", "sastore" },
            { "0x57", "pop" }, { "0x58", "pop2" }, { "0x59", "dup" }, { "0x5a", "dup_x1" },
            { "0x5b", "dup_x2" }, { "0x5c", "dup2" }, { "0x5d", "dup2_x1" },
            { "0x5e", "dup2_x2" }, { "0x5f", "swap" }, { "0x60", "iadd" }, { "0x61", "ladd" },
            { "0x62", "fadd" }, { "0x63", "dadd" }, { "0x64", "isub" }, { "0x65", "lsub" },
            { "0x66", "fsub" }, { "0x67", "dsub" }, { "0x68", "imul" }, { "0x69", "lmul" },
            { "0x6a", "fmul" }, { "0x6b", "dmul" }, { "0x6c", "idiv" }, { "0x6d", "ldiv" },
            { "0x6e", "fdiv" }, { "0x6f", "ddiv" }, { "0x70", "irem" }, { "0x71", "lrem" },
            { "0x72", "frem" }, { "0x73", "drem" }, { "0x74", "ineg" }, { "0x75", "lneg" },
            { "0x76", "fneg" }, { "0x77", "dneg" }, { "0x78", "ishl" }, { "0x79", "lshl" },
            { "0x7a", "ishr" }, { "0x7b", "lshr" }, { "0x7c", "iushr" }, { "0x7d", "lushr" },
            { "0x7e", "iand" }, { "0x7f", "land" }, { "0x80", "ior" }, { "0x81", "lor" },
            { "0x82", "ixor" }, { "0x83", "lxor" }, { "0x84", "iinc" }, { "0x85", "i2l" },
            { "0x86", "i2f" }, { "0x87", "i2d" }, { "0x88", "l2i" }, { "0x89", "l2f" },
            { "0x8a", "l2d" }, { "0x8b", "f2i" }, { "0x8c", "f2l" }, { "0x8d", "f2d" },
            { "0x8e", "d2i" }, { "0x8f", "d2l" }, { "0x90", "d2f" }, { "0x91", "i2b" },
            { "0x92", "i2c" }, { "0x93", "i2s" }, { "0x94", "lcmp" }, { "0x95", "fcmpl" },
            { "0x96", "fcmpg" }, { "0x97", "dcmpl" }, { "0x98", "dcmpg" }, { "0x99", "ifeq" },
            { "0x9a", "ifne" }, { "0x9b", "iflt" }, { "0x9c", "ifge" }, { "0x9d", "ifgt" },
            { "0x9e", "ifle" }, { "0x9f", "if_icmpeq" }, { "0xa0", "if_icmpne" },
            { "0xa1", "if_icmplt" }, { "0xa2", "if_icmpge" }, { "0xa3", "if_icmpgt" },
            { "0xa4", "if_icmple" }, { "0xa5", "if_acmpeq" }, { "0xa6", "if_acmpne" },
            { "0xa7", "goto" }, { "0xa8", "jsr" }, { "0xa9", "ret" }, { "0xaa", "tableswitch" },
            { "0xab", "lookupswitch" }, { "0xac", "ireturn" }, { "0xad", "lreturn" },
            { "0xae", "freturn" }, { "0xaf", "dreturn" }, { "0xb0", "areturn" },
            { "0xb1", "return" }, { "0xb2", "getstatic" }, { "0xb3", "putstatic" },
            { "0xb4", "getfield" }, { "0xb5", "putfield" }, { "0xb6", "invokevirtual" },
            { "0xb7", "invokespecial" }, { "0xb8", "invokestatic" }, { "0xb9", "invokeinterface" },
            { "0xba", "invokedynamic" }, { "0xbb", "new" }, { "0xbc", "newarray" },
            { "0xbd", "anewarray" }, { "0xbe", "arraylength" }, { "0xbf", "athrow" },
            { "0xc0", "checkcast" }, { "0xc1", "instanceof" }, { "0xc2", "monitorenter" },
            { "0xc3", "monitorexit" }, { "0xc4", "wide" }, { "0xc5", "multianewarray" },
            { "0xc6", "ifnull" }, { "0xc7", "ifnonnull" }, { "0xc8", "goto_w" },
            { "0xc9", "jsr_w" }
        };
        for (String[] pair : opcodes) {
            int op = Integer.decode(pair[0]);
            OPCODE_MAP.put(op, pair[1]);
        }
    }

    // 常量池信息基类
    interface ConstantInfo {
        void print(int index);
    }

    static class ConstantUtf8 implements ConstantInfo {
        String value;
        ConstantUtf8(String v) { value = v; }
        public void print(int index) {
            System.out.printf("  #%d = Utf8          \"%s\"\n", index, value);
        }
    }

    static class ConstantInteger implements ConstantInfo {
        int bytes;
        ConstantInteger(int b) { bytes = b; }
        public void print(int index) {
            System.out.printf("  #%d = Integer        %d\n", index, bytes);
        }
    }

    static class ConstantFloat implements ConstantInfo {
        float value;
        ConstantFloat(float v) { value = v; }
        public void print(int index) {
            System.out.printf("  #%d = Float          %f\n", index, value);
        }
    }

    static class ConstantLong implements ConstantInfo {
        long value;
        ConstantLong(long v) { value = v; }
        public void print(int index) {
            System.out.printf("  #%d = Long           %d\n", index, value);
        }
    }

    static class ConstantDouble implements ConstantInfo {
        double value;
        ConstantDouble(double v) { value = v; }
        public void print(int index) {
            System.out.printf("  #%d = Double         %f\n", index, value);
        }
    }

    static class ConstantClass implements ConstantInfo {
        int nameIndex;
        ConstantClass(int idx) { nameIndex = idx; }
        public void print(int index) {
            System.out.printf("  #%d = Class          #%d\n", index, nameIndex);
        }
    }

    static class ConstantString implements ConstantInfo {
        int stringIndex;
        ConstantString(int idx) { stringIndex = idx; }
        public void print(int index) {
            System.out.printf("  #%d = String         #%d\n", index, stringIndex);
        }
    }

    static class ConstantFieldref implements ConstantInfo {
        int classIndex, nameAndTypeIndex;
        ConstantFieldref(int c, int nt) { classIndex = c; nameAndTypeIndex = nt; }
        public void print(int index) {
            System.out.printf("  #%d = Fieldref       #%d.#%d\n", index, classIndex, nameAndTypeIndex);
        }
    }

    static class ConstantMethodref implements ConstantInfo {
        int classIndex, nameAndTypeIndex;
        ConstantMethodref(int c, int nt) { classIndex = c; nameAndTypeIndex = nt; }
        public void print(int index) {
            System.out.printf("  #%d = Methodref      #%d.#%d\n", index, classIndex, nameAndTypeIndex);
        }
    }

    static class ConstantInterfaceMethodref implements ConstantInfo {
        int classIndex, nameAndTypeIndex;
        ConstantInterfaceMethodref(int c, int nt) { classIndex = c; nameAndTypeIndex = nt; }
        public void print(int index) {
            System.out.printf("  #%d = InterfaceMethodref #%d.#%d\n", index, classIndex, nameAndTypeIndex);
        }
    }

    static class ConstantNameAndType implements ConstantInfo {
        int nameIndex, descriptorIndex;
        ConstantNameAndType(int n, int d) { nameIndex = n; descriptorIndex = d; }
        public void print(int index) {
            System.out.printf("  #%d = NameAndType    #%d:#%d\n", index, nameIndex, descriptorIndex);
        }
    }

    // 字段信息
    static class FieldInfo {
        int accessFlags;
        int nameIndex;
        int descriptorIndex;
        List<AttributeInfo> attributes;

        void print(Bytecode parser) {
            System.out.printf("  Field: flags=0x%04x, name=%s, descriptor=%s\n",
                    accessFlags, parser.getUtf8(nameIndex), parser.getUtf8(descriptorIndex));
            for (AttributeInfo attr : attributes) {
                attr.print(parser);
            }
        }
    }

    // 方法信息
    static class MethodInfo {
        int accessFlags;
        int nameIndex;
        int descriptorIndex;
        List<AttributeInfo> attributes;

        void print(Bytecode parser) {
            System.out.printf("  Method: flags=0x%04x, name=%s, descriptor=%s\n",
                    accessFlags, parser.getUtf8(nameIndex), parser.getUtf8(descriptorIndex));
            for (AttributeInfo attr : attributes) {
                attr.print(parser);
            }
        }
    }

    // 属性信息基类
    static class AttributeInfo {
        int nameIndex;
        byte[] info;
        AttributeInfo(int nameIdx, byte[] data) { nameIndex = nameIdx; info = data; }
        void print(Bytecode parser) {
            System.out.printf("    Attribute: %s, length=%d\n", parser.getUtf8(nameIndex), info.length);
        }
    }

    // Code属性专用解析
    static class CodeAttribute extends AttributeInfo {
        int maxStack, maxLocals;
        byte[] code;
        List<ExceptionTableEntry> exceptionTable;
        List<AttributeInfo> attributes;

        static class ExceptionTableEntry {
            int startPc, endPc, handlerPc, catchType;
        }

        CodeAttribute(int nameIdx, byte[] data, ByteReader reader, Bytecode parser) {
            super(nameIdx, data);
            reader = new ByteReader(data);
            maxStack = reader.readU2();
            maxLocals = reader.readU2();
            int codeLength = reader.readU4();
            code = reader.readBytes(codeLength);
            int exceptionTableLength = reader.readU2();
            exceptionTable = new ArrayList<>();
            for (int i = 0; i < exceptionTableLength; i++) {
                ExceptionTableEntry e = new ExceptionTableEntry();
                e.startPc = reader.readU2();
                e.endPc = reader.readU2();
                e.handlerPc = reader.readU2();
                e.catchType = reader.readU2();
                exceptionTable.add(e);
            }
            int attrCount = reader.readU2();
            attributes = new ArrayList<>();
            for (int i = 0; i < attrCount; i++) {
                int attrNameIdx = reader.readU2();
                int attrLen = reader.readU4();
                byte[] attrInfo = reader.readBytes(attrLen);
                attributes.add(new AttributeInfo(attrNameIdx, attrInfo));
            }
        }

        @Override
        void print(Bytecode parser) {
            System.out.printf("    Code: max_stack=%d, max_locals=%d, code_length=%d\n", maxStack, maxLocals, code.length);
            // 打印字节码指令
            int pc = 0;
            while (pc < code.length) {
                int op = code[pc] & 0xFF;
                String mnemonic = OPCODE_MAP.getOrDefault(op, "unknown");
                System.out.printf("      %03d: %s", pc, mnemonic);
                // 处理操作数（简单处理常见格式）
                if (op >= 0x10 && op <= 0x11) { // bipush, sipush
                    if (op == 0x10) {
                        int operand = code[pc+1] & 0xFF;
                        System.out.printf(" %d", operand);
                        pc += 2;
                    } else {
                        int operand = ((code[pc+1] & 0xFF) << 8) | (code[pc+2] & 0xFF);
                        System.out.printf(" %d", operand);
                        pc += 3;
                    }
                } else if ((op >= 0x12 && op <= 0x13) || op == 0x15 || op == 0x16 || op == 0x17 || op == 0x18 || op == 0x19 ||
                           op == 0x36 || op == 0x37 || op == 0x38 || op == 0x39 || op == 0x3a) {
                    int idx = code[pc+1] & 0xFF;
                    System.out.printf(" %d", idx);
                    pc += 2;
                } else if (op == 0x14) { // ldc2_w
                    int idx = ((code[pc+1] & 0xFF) << 8) | (code[pc+2] & 0xFF);
                    System.out.printf(" %d", idx);
                    pc += 3;
                } else if (op == 0x84) { // iinc
                    int idx = code[pc+1] & 0xFF;
                    int inc = code[pc+2] & 0xFF;
                    System.out.printf(" %d, %d", idx, inc);
                    pc += 3;
                } else if (op == 0x99 || op == 0x9a || op == 0x9b || op == 0x9c || op == 0x9d || op == 0x9e ||
                           op == 0x9f || op == 0xa0 || op == 0xa1 || op == 0xa2 || op == 0xa3 || op == 0xa4 ||
                           op == 0xa5 || op == 0xa6 || op == 0xa7 || op == 0xa8 || op == 0xc6 || op == 0xc7) {
                    int offset = ((code[pc+1] & 0xFF) << 8) | (code[pc+2] & 0xFF);
                    System.out.printf(" %d", offset);
                    pc += 3;
                } else if (op == 0xc8 || op == 0xc9) { // goto_w, jsr_w
                    int offset = ((code[pc+1] & 0xFF) << 24) | ((code[pc+2] & 0xFF) << 16) |
                                 ((code[pc+3] & 0xFF) << 8) | (code[pc+4] & 0xFF);
                    System.out.printf(" %d", offset);
                    pc += 5;
                } else if (op == 0xb2 || op == 0xb3 || op == 0xb4 || op == 0xb5 || op == 0xb6 || op == 0xb7 || op == 0xb8 ||
                           op == 0xbb || op == 0xbd || op == 0xc0 || op == 0xc1) {
                    int index = ((code[pc+1] & 0xFF) << 8) | (code[pc+2] & 0xFF);
                    System.out.printf(" #%d", index);
                    pc += 3;
                } else if (op == 0xb9) { // invokeinterface
                    int index = ((code[pc+1] & 0xFF) << 8) | (code[pc+2] & 0xFF);
                    int count = code[pc+3] & 0xFF;
                    int zero = code[pc+4] & 0xFF;
                    System.out.printf(" #%d, %d, %d", index, count, zero);
                    pc += 5;
                } else if (op == 0xba) { // invokedynamic
                    int index = ((code[pc+1] & 0xFF) << 8) | (code[pc+2] & 0xFF);
                    int zero1 = code[pc+3] & 0xFF;
                    int zero2 = code[pc+4] & 0xFF;
                    System.out.printf(" #%d, %d, %d", index, zero1, zero2);
                    pc += 5;
                } else if (op == 0xc5) { // multianewarray
                    int index = ((code[pc+1] & 0xFF) << 8) | (code[pc+2] & 0xFF);
                    int dim = code[pc+3] & 0xFF;
                    System.out.printf(" #%d, %d", index, dim);
                    pc += 4;
                } else if (op == 0xbc) { // newarray
                    int atype = code[pc+1] & 0xFF;
                    System.out.printf(" %d", atype);
                    pc += 2;
                } else if (op == 0xaa || op == 0xab) {
                    // tableswitch/lookupswitch 复杂，跳过剩余字节到对齐，简单跳过指令
                    int padding = 3 - (pc % 4);
                    pc += 1 + padding;
                    // 简单的跳过，不解析具体分支
                    System.out.printf(" (complex, skipped)");
                    pc = code.length; // 暂不详细解析，避免越界
                } else {
                    pc += 1;
                }
                System.out.println();
            }
            // 打印异常表
            if (!exceptionTable.isEmpty()) {
                System.out.println("      Exception table:");
                for (ExceptionTableEntry e : exceptionTable) {
                    System.out.printf("        start=%d, end=%d, handler=%d, catch_type=%d\n",
                            e.startPc, e.endPc, e.handlerPc, e.catchType);
                }
            }
        }
    }

    // 字节读取辅助类
    static class ByteReader {
        private byte[] data;
        private int pos;
        ByteReader(byte[] data) { this.data = data; pos = 0; }
        int readU1() { return data[pos++] & 0xFF; }
        int readU2() { return (readU1() << 8) | readU1(); }
        int readU4() { return (readU2() << 16) | readU2(); }
        long readU8() {
            long high = ((long)readU4() & 0xFFFFFFFFL) << 32;
            long low = readU4() & 0xFFFFFFFFL;
            return high | low;
        }
        byte[] readBytes(int len) {
            byte[] b = new byte[len];
            System.arraycopy(data, pos, b, 0, len);
            pos += len;
            return b;
        }
        void skip(int n) { pos += n; }
        int getPos() { return pos; }
    }

    public Bytecode(byte[] classData) {
        reader = new ByteReader(classData);
        constantPool = new ArrayList<>();
        interfaces = new ArrayList<>();
        fields = new ArrayList<>();
        methods = new ArrayList<>();
        attributes = new ArrayList<>();
        parse();
    }

    private void parse() {
        // 魔数
        int magic = reader.readU4();
        if (magic != 0xCAFEBABE) {
            throw new RuntimeException("Invalid magic number: " + Integer.toHexString(magic));
        }
        minorVersion = reader.readU2();
        majorVersion = reader.readU2();
        // 常量池
        int cpCount = reader.readU2();
        constantPool.add(null); // 索引0 不用
        for (int i = 1; i < cpCount; i++) {
            int tag = reader.readU1();
            switch (tag) {
                case 1: { // Utf8
                    int len = reader.readU2();
                    byte[] bytes = reader.readBytes(len);
                    String value = new String(bytes, StandardCharsets.UTF_8);
                    constantPool.add(new ConstantUtf8(value));
                    break;
                }
                case 3: { // Integer
                    int bytes = reader.readU4();
                    constantPool.add(new ConstantInteger(bytes));
                    break;
                }
                case 4: { // Float
                    int bits = reader.readU4();
                    float value = Float.intBitsToFloat(bits);
                    constantPool.add(new ConstantFloat(value));
                    break;
                }
                case 5: { // Long
                    long value = reader.readU8();
                    constantPool.add(new ConstantLong(value));
                    i++; // Long占两个条目
                    constantPool.add(null);
                    break;
                }
                case 6: { // Double
                    long bits = reader.readU8();
                    double value = Double.longBitsToDouble(bits);
                    constantPool.add(new ConstantDouble(value));
                    i++;
                    constantPool.add(null);
                    break;
                }
                case 7: { // Class
                    int nameIdx = reader.readU2();
                    constantPool.add(new ConstantClass(nameIdx));
                    break;
                }
                case 8: { // String
                    int stringIdx = reader.readU2();
                    constantPool.add(new ConstantString(stringIdx));
                    break;
                }
                case 9: { // Fieldref
                    int classIdx = reader.readU2();
                    int ntIdx = reader.readU2();
                    constantPool.add(new ConstantFieldref(classIdx, ntIdx));
                    break;
                }
                case 10: { // Methodref
                    int classIdx = reader.readU2();
                    int ntIdx = reader.readU2();
                    constantPool.add(new ConstantMethodref(classIdx, ntIdx));
                    break;
                }
                case 11: { // InterfaceMethodref
                    int classIdx = reader.readU2();
                    int ntIdx = reader.readU2();
                    constantPool.add(new ConstantInterfaceMethodref(classIdx, ntIdx));
                    break;
                }
                case 12: { // NameAndType
                    int nameIdx = reader.readU2();
                    int descIdx = reader.readU2();
                    constantPool.add(new ConstantNameAndType(nameIdx, descIdx));
                    break;
                }
                default:
                    System.err.println("Unsupported tag: " + tag);
                    // 跳过未知结构
                    // 简单处理，实际需要根据tag长度跳过，此处为了演示直接抛异常
                    throw new RuntimeException("Unknown tag: " + tag);
            }
        }

        accessFlags = reader.readU2();
        thisClass = reader.readU2();
        superClass = reader.readU2();
        int interfacesCount = reader.readU2();
        for (int i = 0; i < interfacesCount; i++) {
            interfaces.add(reader.readU2());
        }

        // 字段
        int fieldsCount = reader.readU2();
        for (int i = 0; i < fieldsCount; i++) {
            FieldInfo field = new FieldInfo();
            field.accessFlags = reader.readU2();
            field.nameIndex = reader.readU2();
            field.descriptorIndex = reader.readU2();
            int attrCount = reader.readU2();
            field.attributes = parseAttributes(attrCount);
            fields.add(field);
        }

        // 方法
        int methodsCount = reader.readU2();
        for (int i = 0; i < methodsCount; i++) {
            MethodInfo method = new MethodInfo();
            method.accessFlags = reader.readU2();
            method.nameIndex = reader.readU2();
            method.descriptorIndex = reader.readU2();
            int attrCount = reader.readU2();
            method.attributes = parseAttributes(attrCount);
            methods.add(method);
        }

        // 类属性
        int attrCount = reader.readU2();
        attributes = parseAttributes(attrCount);
    }

    private List<AttributeInfo> parseAttributes(int count) {
        List<AttributeInfo> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            int nameIdx = reader.readU2();
            int length = reader.readU4();
            byte[] info = reader.readBytes(length);
            String attrName = getUtf8(nameIdx);
            if ("Code".equals(attrName)) {
                // 重新构建 CodeAttribute
                CodeAttribute codeAttr = new CodeAttribute(nameIdx, info, null, this);
                list.add(codeAttr);
            } else {
                list.add(new AttributeInfo(nameIdx, info));
            }
        }
        return list;
    }

    public String getUtf8(int index) {
        ConstantInfo ci = constantPool.get(index);
        if (ci instanceof ConstantUtf8) {
            return ((ConstantUtf8) ci).value;
        }
        return "#" + index;
    }

    public void print() {
        System.out.println("Class file version: " + majorVersion + "." + minorVersion);
        System.out.println("Constant pool:");
        for (int i = 1; i < constantPool.size(); i++) {
            ConstantInfo ci = constantPool.get(i);
            if (ci != null) ci.print(i);
        }
        System.out.println("Access flags: 0x" + Integer.toHexString(accessFlags));
        System.out.println("This class: #" + thisClass + " (" + getUtf8(((ConstantClass)constantPool.get(thisClass)).nameIndex) + ")");
        System.out.println("Super class: #" + superClass + " (" + getUtf8(((ConstantClass)constantPool.get(superClass)).nameIndex) + ")");
        System.out.println("Interfaces: " + interfaces);
        System.out.println("Fields: " + fields.size());
        for (FieldInfo f : fields) f.print(this);
        System.out.println("Methods: " + methods.size());
        for (MethodInfo m : methods) m.print(this);
        System.out.println("Class attributes: " + attributes.size());
        for (AttributeInfo a : attributes) a.print(this);
    }

    // 示例：解析指定的 class 文件
    public static void main(String[] args) throws IOException {
        // 如果没有参数，则解析当前目录下的某个class，可修改路径
        String classFilePath = args.length > 0 ? args[0] : "Example.class";
        // 这里为了演示，可以指定一个实际存在的class文件路径
        // 如果文件不存在，可以创建一个简单的 test 类编译后使用
        // 为了示例，这里改为读取自身类文件（需要先编译本文件）
        // 注意：运行时需要先编译，然后指定 class 路径，如果找不到可以修改
        File file = new File(classFilePath);
        if (!file.exists()) {
            System.err.println("File not found: " + classFilePath);
            System.err.println("Usage: java Bytecode <class file>");
            return;
        }
        byte[] data = new byte[0];
        Bytecode parser = new Bytecode(data);
        parser.print();
    }
}