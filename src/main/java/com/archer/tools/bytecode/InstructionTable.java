package com.archer.tools.bytecode;

import com.archer.tools.java.Pair;

public class InstructionTable {
	private static final String[] TABLE = {
		    "nop",
		    "aconst_null",
		    "iconst_m1",
		    "iconst_0",
		    "iconst_1",
		    "iconst_2",
		    "iconst_3",
		    "iconst_4",
		    "iconst_5",
		    "lconst_0",
		    "lconst_1",
		    "fconst_0",
		    "fconst_1",
		    "fconst_2",
		    "dconst_0",
		    "dconst_1",
		    "bipush",
		    "sipush",
		    "ldc",
		    "ldc_w",
		    "ldc2_w",
		    "iload",
		    "lload",
		    "fload",
		    "dload",
		    "aload",
		    "iload_0",
		    "iload_1",
		    "iload_2",
		    "iload_3",
		    "lload_0",
		    "lload_1",
		    "lload_2",
		    "lload_3",
		    "fload_0",
		    "fload_1",
		    "fload_2",
		    "fload_3",
		    "dload_0",
		    "dload_1",
		    "dload_2",
		    "dload_3",
		    "aload_0",
		    "aload_1",
		    "aload_2",
		    "aload_3",
		    "iaload",
		    "laload",
		    "faload",
		    "daload",
		    "aaload",
		    "baload",
		    "caload",
		    "saload",
		    "istore",
		    "lstore",
		    "fstore",
		    "dstore",
		    "astore",
		    "istore_0",
		    "istore_1",
		    "istore_2",
		    "istore_3",
		    "lstore_0",
		    "lstore_1",
		    "lstore_2",
		    "lstore_3",
		    "fstore_0",
		    "fstore_1",
		    "fstore_2",
		    "fstore_3",
		    "dstore_0",
		    "dstore_1",
		    "dstore_2",
		    "dstore_3",
		    "astore_0",
		    "astore_1",
		    "astore_2",
		    "astore_3",
		    "iastore",
		    "lastore",
		    "fastore",
		    "dastore",
		    "aastore",
		    "bastore",
		    "castore",
		    "sastore",
		    "pop",
		    "pop2",
		    "dup",
		    "dup_x1",
		    "dup_x2",
		    "dup2",
		    "dup2_x1",
		    "dup2_x2",
		    "swap",
		    "iadd",
		    "ladd",
		    "fadd",
		    "dadd",
		    "isub",
		    "lsub",
		    "fsub",
		    "dsub",
		    "imul",
		    "lmul",
		    "fmul",
		    "dmul",
		    "idiv",
		    "ldiv",
		    "fdiv",
		    "ddiv",
		    "irem",
		    "lrem",
		    "frem",
		    "drem",
		    "ineg",
		    "lneg",
		    "fneg",
		    "dneg",
		    "ishl",
		    "lshl",
		    "ishr",
		    "lshr",
		    "iushr",
		    "lushr",
		    "iand",
		    "land",
		    "ior",
		    "lor",
		    "ixor",
		    "lxor",
		    "iinc",
		    "i2l",
		    "i2f",
		    "i2d",
		    "l2i",
		    "l2f",
		    "l2d",
		    "f2i",
		    "f2l",
		    "f2d",
		    "d2i",
		    "d2l",
		    "d2f",
		    "i2b",
		    "i2c",
		    "i2s",
		    "lcmp",
		    "fcmpl",
		    "fcmpg",
		    "dcmpl",
		    "dcmpg",
		    "ifeq",
		    "ifne",
		    "iflt",
		    "ifge",
		    "ifgt",
		    "ifle",
		    "if_icmpeq",
		    "if_icmpne",
		    "if_icmplt",
		    "if_icmpge",
		    "if_icmpgt",
		    "if_icmple",
		    "if_acmpeq",
		    "if_acmpne",
		    "goto",
		    "jsr",
		    "ret",
		    "tableswitch",
		    "lookupswitch",
		    "ireturn",
		    "lreturn",
		    "freturn",
		    "dreturn",
		    "areturn",
		    "return",
		    "getstatic",
		    "putstatic",
		    "getfield",
		    "putfield",
		    "invokevirtual",
		    "invokespecial",
		    "invokestatic",
		    "invokeinterface",
		    "--",
		    "new",
		    "newarray",
		    "anewarray",
		    "arraylength",
		    "athrow",
		    "checkcast",
		    "instanceof",
		    "monitorenter",
		    "monitorexit",
		    "wide",
		    "multianewarray",
		    "ifnull",
		    "ifnonnull",
		    "goto_w",
		    "jsr_w"
		   };

	public static String get(int index) {
		return TABLE[index];
	}

	public static String get(byte opcode) {
		int idx = (int)opcode;
		return TABLE[idx < 0 ? idx + 256 : idx];
	}

	public static byte getInstructionCode(String instruction) {
		for(int i = 0; i < TABLE.length; i++) {
			if(TABLE[i].equals(instruction)) {
				return (byte) i;
			}
		}
		throw new IllegalArgumentException("invalid instruction " + instruction);
	}

	public static Pair<Integer, Integer> calculateMaxs(byte[] code) {
		int maxStack = 0, maxLocals = 0, cur = 0;
		for(int i = 0; i < code.length; i++) {
			int delta = 0, len = 0;
			int op = code[i] & 0xFF;
			switch(op) {
			    case 0:        //nop
			        break;
			    case 1:        //aconst_null
			    case 2:        //iconst_m1
			    case 3:        //iconst_0
			    case 4:        //iconst_1
			    case 5:        //iconst_2
			    case 6:        //iconst_3
			    case 7:        //iconst_4
			    case 8:        //iconst_5
			    case 11:       //fconst_0
			    case 12:       //fconst_1
			    case 13:       //fconst_2
			    	delta = 1;
			    	break;
			    case 9:        //lconst_0
			    case 10:       //lconst_1
			    case 14:       //dconst_0
			    case 15:       //dconst_1
			    	delta = 2;
			    	break;
			    case 16:       //bipush
			    	len = 1;
			    	delta = 1;
			        break;
			    case 17:       //sipush
			    	len = 2;
			    	delta = 1;
			        break;
			    case 18:       //ldc
			    	len = 1;
			    	delta = 1;
			        break;
			    case 19:       //ldc_w
			    	len = 2;
			    	delta = 1;
			        break;
			    case 20:       //ldc2_w
			    	len = 2;
			    	delta = 2;
			        break;
			    case 21:       //iload
			    case 23:       //fload
			    case 25:       //aload
			    	len = 1;
			    	delta = 1;
			    	maxLocals = Math.max(maxLocals, (code[i + 1] & 0xFF) + 1);
			        break;
			    case 22:       //lload
			    case 24:       //dload
			    	len = 1;
			    	delta = 2;
			    	maxLocals = Math.max(maxLocals, (code[i + 1] & 0xFF) + 2);
			        break;
			    case 26: case 27: case 28: case 29:        //iload_0..3
			    	delta = 1;
			    	maxLocals = Math.max(maxLocals, op - 25);
			        break;
			    case 30: case 31: case 32: case 33:        //lload_0..3
			    	delta = 2;
			    	maxLocals = Math.max(maxLocals, op - 28);
			        break;
			    case 34: case 35: case 36: case 37:        //fload_0..3
			    	delta = 1;
			    	maxLocals = Math.max(maxLocals, op - 33);
			        break;
			    case 38: case 39: case 40: case 41:        //dload_0..3
			    	delta = 2;
			    	maxLocals = Math.max(maxLocals, op - 36);
			        break;
			    case 42: case 43: case 44: case 45:        //aload_0..3
			    	delta = 1;
			    	maxLocals = Math.max(maxLocals, op - 41);
			        break;
			    case 46:       //iaload
			    case 48:       //faload
			    case 50:       //aaload
			    case 51:       //baload
			    case 52:       //caload
			    case 53:       //saload
			    	delta = -1;
			        break;
			    case 47:       //laload
			    case 49:       //daload
			        break;
			    case 54:       //istore
			    case 56:       //fstore
			    case 58:       //astore
			    	len = 1;
			    	delta = -1;
			    	maxLocals = Math.max(maxLocals, (code[i + 1] & 0xFF) + 1);
			    	break;
			    case 55:       //lstore
			    case 57:       //dstore
			    	len = 1;
			    	delta = -2;
			    	maxLocals = Math.max(maxLocals, (code[i + 1] & 0xFF) + 2);
			    	break;
			    case 59: case 60: case 61: case 62:        //istore_0..3
			    	delta = -1;
			    	maxLocals = Math.max(maxLocals, op - 58);
			    	break;
			    case 63: case 64: case 65: case 66:        //lstore_0..3
			    	delta = -2;
			    	maxLocals = Math.max(maxLocals, op - 61);
			    	break;
			    case 67: case 68: case 69: case 70:        //fstore_0..3
			    	delta = -1;
			    	maxLocals = Math.max(maxLocals, op - 66);
			    	break;
			    case 71: case 72: case 73: case 74:        //dstore_0..3
			    	delta = -2;
			    	maxLocals = Math.max(maxLocals, op - 69);
			    	break;
			    case 75: case 76: case 77: case 78:        //astore_0..3
			    	delta = -1;
			    	maxLocals = Math.max(maxLocals, op - 74);
			    	break;
			    case 79:       //iastore
			    case 81:       //fastore
			    case 83:       //aastore
			    case 84:       //bastore
			    case 85:       //castore
			    case 86:       //sastore
			    	delta = -3;
			    	break;
			    case 80:       //lastore
			    case 82:       //dastore
			    	delta = -4;
			    	break;
			    case 87:       //pop
			    	delta = -1;
			    	break;
			    case 88:       //pop2
			    	delta = -2;
			    	break;
			    case 89:       //dup
			    case 90:       //dup_x1
			    case 91:       //dup_x2
			    	delta = 1;
			        break;
			    case 92:       //dup2
			    case 93:       //dup2_x1
			    case 94:       //dup2_x2
			    	delta = 2;
			        break;
			    case 95:       //swap
			    case 116:      //ineg
			    case 117:      //lneg
			    case 118:      //fneg
			    case 119:      //dneg
			    case 134:      //i2f
			    case 138:      //l2d
			    case 139:      //f2i
			    case 143:      //d2l
			    case 145:      //i2b
			    case 146:      //i2c
			    case 147:      //i2s
			        break;
			    case 96: case 98: case 100: case 102:      //iadd fadd isub fsub
			    case 104: case 106: case 108: case 110:    //imul fmul idiv fdiv
			    case 112: case 114:                        //irem frem
			    case 120: case 121: case 122:              //ishl lshl ishr
			    case 123: case 124: case 125:              //lshr iushr lushr
			    case 126: case 128: case 130:              //iand ior ixor
			    case 149: case 150:                        //fcmpl fcmpg
			    	delta = -1;
			        break;
			    case 97: case 99: case 101: case 103:      //ladd dadd lsub dsub
			    case 105: case 107: case 109: case 111:    //lmul dmul ldiv ddiv
			    case 113: case 115:                        //lrem drem
			    case 127: case 129: case 131:              //land lor lxor
			    	delta = -2;
			        break;
			    case 132:      //iinc
			    	len = 2;
			    	maxLocals = Math.max(maxLocals, (code[i + 1] & 0xFF) + 1);
			        break;
			    case 133:      //i2l
			    case 135:      //i2d
			    case 140:      //f2l
			    case 141:      //f2d
			    	delta = 1;
			        break;
			    case 136:      //l2i
			    case 137:      //l2f
			    case 142:      //d2i
			    case 144:      //d2f
			    	delta = -1;
			        break;
			    case 148:      //lcmp
			    case 151:      //dcmpl
			    case 152:      //dcmpg
			    	delta = -3;
			        break;
			    case 153: case 154: case 155:              //ifeq ifne iflt
			    case 156: case 157: case 158:              //ifge ifgt ifle
			    case 198: case 199:                        //ifnull ifnonnull
			    	len = 2;
			    	delta = -1;
			        break;
			    case 159: case 160: case 161:              //if_icmpeq if_icmpne if_icmplt
			    case 162: case 163: case 164:              //if_icmpge if_icmpgt if_icmple
			    case 165: case 166:                        //if_acmpeq if_acmpne
			    	len = 2;
			    	delta = -2;
			        break;
			    case 167:      //goto
			    	len = 2;
			        break;
			    case 168:      //jsr
			    	len = 2;
			    	delta = 1;
			        break;
			    case 169:      //ret
			    	len = 1;
			    	maxLocals = Math.max(maxLocals, (code[i + 1] & 0xFF) + 1);
			        break;
			    case 170: {    //tableswitch
			    	int pad = (4 - ((i + 1) & 3)) & 3;
			    	int low = readInt32(code, i + pad + 5);
			    	int high = readInt32(code, i + pad + 9);
			    	len = pad + 12 + 4 * (high - low + 1);
			    	delta = -1;
			        break;
			    }
			    case 171: {    //lookupswitch
			    	int pad = (4 - ((i + 1) & 3)) & 3;
			    	int npairs = readInt32(code, i + pad + 5);
			    	len = pad + 8 + 8 * npairs;
			    	delta = -1;
			        break;
			    }
			    case 172:      //ireturn
			    case 174:      //freturn
			    case 176:      //areturn
			    	delta = -1;
			        break;
			    case 173:      //lreturn
			    case 175:      //dreturn
			    	delta = -2;
			        break;
			    case 177:      //return
			    	break;
			    //字段/方法相关指令的栈深度变化取决于常量池描述符，此处按双槽上界保守估计，
			    //保证声明的 max_stack 不小于真实峰值
			    case 178:      //getstatic
			    case 180:      //getfield
			    case 182:      //invokevirtual
			    case 183:      //invokespecial
			    case 184:      //invokestatic
			    	len = 2;
			    	delta = 2;
			        break;
			    case 179:      //putstatic
			    case 181:      //putfield
			    	len = 2;
			        break;
			    case 185:      //invokeinterface
			    case 186:      //invokedynamic
			    	len = 4;
			    	delta = 2;
			        break;
			    case 187:      //new
			    	len = 2;
			    	delta = 1;
			        break;
			    case 188:      //newarray
			    	len = 1;
			        break;
			    case 189:      //anewarray
			    	len = 2;
			        break;
			    case 190:      //arraylength
			        break;
			    case 191:      //athrow
			    case 194:      //monitorenter
			    case 195:      //monitorexit
			    	delta = -1;
			        break;
			    case 192:      //checkcast
			    case 193:      //instanceof
			    	len = 2;
			        break;
			    case 196: {    //wide
			    	int sub = code[i + 1] & 0xFF;
			    	int idx = ((code[i + 2] & 0xFF) << 8) | (code[i + 3] & 0xFF);
			    	if(sub == 132) {           //wide iinc
			    		len = 5;
			    		maxLocals = Math.max(maxLocals, idx + 1);
			    	} else {
			    		len = 3;
			    		switch(sub) {
			    		    case 21: case 23: case 25:       //iload fload aload
			    		    	delta = 1;
			    		    	maxLocals = Math.max(maxLocals, idx + 1);
			    		    	break;
			    		    case 22: case 24:                //lload dload
			    		    	delta = 2;
			    		    	maxLocals = Math.max(maxLocals, idx + 2);
			    		    	break;
			    		    case 54: case 56: case 58:       //istore fstore astore
			    		    	delta = -1;
			    		    	maxLocals = Math.max(maxLocals, idx + 1);
			    		    	break;
			    		    case 55: case 57:                //lstore dstore
			    		    	delta = -2;
			    		    	maxLocals = Math.max(maxLocals, idx + 2);
			    		    	break;
			    		    default:                         //ret
			    		    	maxLocals = Math.max(maxLocals, idx + 1);
			    		}
			    	}
			        break;
			    }
			    case 197:      //multianewarray
			    	len = 3;
			    	delta = 1 - (code[i + 3] & 0xFF);
			        break;
			    case 200:      //goto_w
			    	len = 4;
			        break;
			    case 201:      //jsr_w
			    	len = 4;
			    	delta = 1;
			        break;
			    default:
			    	throw new BytecodeException("Invalid code instruction " + op);
			}
			cur += delta;
			if(cur < 0) {
				cur = 0;
			}
			if(cur > maxStack) {
				maxStack = cur;
			}
			i += len;
		}
		return new Pair<>(maxStack, maxLocals);
	}

	private static int readInt32(byte[] code, int off) {
		return ((code[off] & 0xFF) << 24) | ((code[off + 1] & 0xFF) << 16)
				| ((code[off + 2] & 0xFF) << 8) | (code[off + 3] & 0xFF);
	}
}
