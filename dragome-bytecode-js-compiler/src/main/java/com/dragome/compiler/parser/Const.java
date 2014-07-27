package com.dragome.compiler.parser;

import com.dragome.compiler.parser.Form.Value;

public class Const
{

	public static InstructionType[] instructionTypes= new InstructionType[202];

	public static final int AALOAD= 50;

	public static final int AASTORE= 83;

	public static final int ACONST_NULL= 1;

	public static final int ALOAD= 25;

	public static final int ALOAD_0= 42;

	public static final int ALOAD_1= 43;

	public static final int ALOAD_2= 44;

	public static final int ALOAD_3= 45;

	public static final int ANEWARRAY= 189;

	public static final int ARETURN= 176;

	public static final int ARRAYLENGTH= 190;

	public static final int ASTORE= 58;

	public static final int ASTORE_0= 75;

	public static final int ASTORE_1= 76;

	public static final int ASTORE_2= 77;

	public static final int ASTORE_3= 78;

	public static final int ATHROW= 191;

	public static final int BALOAD= 51;

	public static final int BASTORE= 84;

	public static final int BIPUSH= 16;

	public static final int NEW= 187;

	public static final int NEWARRAY= 188;

	public static final int NOP= 0;

	public static final int POP= 87;

	public static final int POP2= 88;

	public static final int PUTFIELD= 181;

	public static final int PUTSTATIC= 179;

	public static final int RET= 169;

	public static final int RETURN= 177;

	public static final int SALOAD= 53;

	public static final int SASTORE= 86;

	public static final int SIPUSH= 17;

	public static final int SWAP= 95;

	public static final int TABLESWITCH= 170;

	public static final int WIDE= 196;

	public static final int CALOAD= 52;

	public static final int CASTORE= 85;

	public static final int CHECKCAST= 192;

	public static final int D2F= 144;

	public static final int D2I= 142;

	public static final int D2L= 143;

	public static final int DADD= 99;

	public static final int DALOAD= 49;

	public static final int DASTORE= 82;

	public static final int DCMPL= 151;

	public static final int DCMPG= 152;

	public static final int DCONST_0= 14;

	public static final int DCONST_1= 15;

	public static final int DDIV= 111;

	public static final int DLOAD= 24;

	public static final int DLOAD_0= 38;

	public static final int DLOAD_1= 39;

	public static final int DLOAD_2= 40;

	public static final int DLOAD_3= 41;

	public static final int DMUL= 107;

	public static final int DNEG= 119;

	public static final int DREM= 115;

	public static final int DRETURN= 175;

	public static final int DSTORE= 57;

	public static final int DSTORE_0= 71;

	public static final int DSTORE_1= 72;

	public static final int DSTORE_2= 73;

	public static final int DSTORE_3= 74;

	public static final int DSUB= 103;

	public static final int DUP= 89;

	public static final int DUP_X1= 90;

	public static final int DUP_X2= 91;

	public static final int DUP2= 92;

	public static final int DUP2_X1= 93;

	public static final int DUP2_X2= 94;

	public static final int F2D= 141;

	public static final int F2I= 139;

	public static final int F2L= 140;

	public static final int FADD= 98;

	public static final int FALOAD= 48;

	public static final int FASTORE= 81;

	public static final int FCMPL= 149;

	public static final int FCMPG= 150;

	public static final int FCONST_0= 11;

	public static final int FCONST_1= 12;

	public static final int FCONST_2= 13;

	public static final int FDIV= 110;

	public static final int FLOAD= 23;

	public static final int FLOAD_0= 34;

	public static final int FLOAD_1= 35;

	public static final int FLOAD_2= 36;

	public static final int FLOAD_3= 37;

	public static final int FMUL= 106;

	public static final int FNEG= 118;

	public static final int FREM= 114;

	public static final int FRETURN= 174;

	public static final int FSTORE= 56;

	public static final int FSTORE_0= 67;

	public static final int FSTORE_1= 68;

	public static final int FSTORE_2= 69;

	public static final int FSTORE_3= 70;

	public static final int FSUB= 102;

	public static final int GETFIELD= 180;

	public static final int GETSTATIC= 178;

	public static final int GOTO= 167;

	public static final int GOTO_W= 200;

	public static final int I2B= 145;

	public static final int I2C= 146;

	public static final int I2D= 135;

	public static final int I2F= 134;

	public static final int I2L= 133;

	public static final int I2S= 147;

	public static final int IADD= 96;

	public static final int IALOAD= 46;

	public static final int IAND= 126;

	public static final int IASTORE= 79;

	public static final int ICONST_M1= 2;

	public static final int ICONST_0= 3;

	public static final int ICONST_1= 4;

	public static final int ICONST_2= 5;

	public static final int ICONST_3= 6;

	public static final int ICONST_4= 7;

	public static final int ICONST_5= 8;

	public static final int IDIV= 108;

	public static final int IF_ACMPEQ= 165;

	public static final int IF_ACMPNE= 166;

	public static final int IF_ICMPEQ= 159;

	public static final int IF_ICMPNE= 160;

	public static final int IF_ICMPLT= 161;

	public static final int IF_ICMPGE= 162;

	public static final int IF_ICMPGT= 163;

	public static final int IF_ICMPLE= 164;

	public static final int IFEQ= 153;

	public static final int IFNE= 154;

	public static final int IFLT= 155;

	public static final int IFGE= 156;

	public static final int IFGT= 157;

	public static final int IFLE= 158;

	public static final int IFNONNULL= 199;

	public static final int IFNULL= 198;

	public static final int IINC= 132;

	public static final int ILOAD= 21;

	public static final int ILOAD_0= 26;

	public static final int ILOAD_1= 27;

	public static final int ILOAD_2= 28;

	public static final int ILOAD_3= 29;

	public static final int IMUL= 104;

	public static final int INEG= 116;

	public static final int INSTANCEOF= 193;

	public static final int INVOKEINTERFACE= 185;

	public static final int INVOKESPECIAL= 183;

	public static final int INVOKESTATIC= 184;

	public static final int INVOKEVIRTUAL= 182;

	public static final int IOR= 128;

	public static final int IREM= 112;

	public static final int IRETURN= 172;

	public static final int ISHL= 120;

	public static final int ISHR= 122;

	public static final int ISTORE= 54;

	public static final int ISTORE_0= 59;

	public static final int ISTORE_1= 60;

	public static final int ISTORE_2= 61;

	public static final int ISTORE_3= 62;

	public static final int ISUB= 100;

	public static final int IUSHR= 124;

	public static final int IXOR= 130;

	public static final int JSR= 168;

	public static final int JSR_W= 201;

	public static final int L2D= 138;

	public static final int L2F= 137;

	public static final int L2I= 136;

	public static final int LADD= 97;

	public static final int LALOAD= 47;

	public static final int LAND= 127;

	public static final int LASTORE= 80;

	public static final int LCMP= 148;

	public static final int LCONST_0= 9;

	public static final int LCONST_1= 10;

	public static final int LDC= 18;

	public static final int LDC_W= 19;

	public static final int LDC2_W= 20;

	public static final int LDIV= 109;

	public static final int LLOAD= 22;

	public static final int LLOAD_0= 30;

	public static final int LLOAD_1= 31;

	public static final int LLOAD_2= 32;

	public static final int LLOAD_3= 33;

	public static final int LMUL= 105;

	public static final int LNEG= 117;

	public static final int LOOKUPSWITCH= 171;

	public static final int LOR= 129;

	public static final int LREM= 113;

	public static final int LRETURN= 173;

	public static final int LSHL= 121;

	public static final int LSHR= 123;

	public static final int LSTORE= 55;

	public static final int LSTORE_0= 63;

	public static final int LSTORE_1= 64;

	public static final int LSTORE_2= 65;

	public static final int LSTORE_3= 66;

	public static final int LSUB= 101;

	public static final int LUSHR= 125;

	public static final int LXOR= 131;

	public static final int MONITORENTER= 194;

	public static final int MONITOREXIT= 195;

	public static final int MULTIANEWARRAY= 197;

	public static final int XXXUNUSEDXXX= 186;

	static
	{
		InstructionType i;
		Form f;

		i= new InstructionType((short) 0, "nop", 1);

		f= new Form();
		f.setIns(new Form.Value[] {});
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[0]= i;

		i= new InstructionType((short) 1, "aconst_null", 1);

		f= new Form();
		f.setIns(new Form.Value[] {});
		f.setOuts(new Form.Value[] { new Form.Value("object", "null"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[1]= i;

		i= new InstructionType((short) 2, "iconst_m1", 1);

		f= new Form();
		f.setIns(new Form.Value[] {});
		f.setOuts(new Form.Value[] { new Form.Value("int", "i"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[2]= i;

		i= new InstructionType((short) 3, "iconst_0", 1);

		f= new Form();
		f.setIns(new Form.Value[] {});
		f.setOuts(new Form.Value[] { new Form.Value("int", "i"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[3]= i;

		i= new InstructionType((short) 4, "iconst_1", 1);

		f= new Form();
		f.setIns(new Form.Value[] {});
		f.setOuts(new Form.Value[] { new Form.Value("int", "i"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[4]= i;

		i= new InstructionType((short) 5, "iconst_2", 1);

		f= new Form();
		f.setIns(new Form.Value[] {});
		f.setOuts(new Form.Value[] { new Form.Value("int", "i"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[5]= i;

		i= new InstructionType((short) 6, "iconst_3", 1);

		f= new Form();
		f.setIns(new Form.Value[] {});
		f.setOuts(new Form.Value[] { new Form.Value("int", "i"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[6]= i;

		i= new InstructionType((short) 7, "iconst_4", 1);

		f= new Form();
		f.setIns(new Form.Value[] {});
		f.setOuts(new Form.Value[] { new Form.Value("int", "i"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[7]= i;

		i= new InstructionType((short) 8, "iconst_5", 1);

		f= new Form();
		f.setIns(new Form.Value[] {});
		f.setOuts(new Form.Value[] { new Form.Value("int", "i"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[8]= i;

		i= new InstructionType((short) 9, "lconst_0", 1);

		f= new Form();
		f.setIns(new Form.Value[] {});
		f.setOuts(new Form.Value[] { new Form.Value("long", "l"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[9]= i;

		i= new InstructionType((short) 10, "lconst_1", 1);

		f= new Form();
		f.setIns(new Form.Value[] {});
		f.setOuts(new Form.Value[] { new Form.Value("long", "l"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[10]= i;

		i= new InstructionType((short) 11, "fconst_0", 1);

		f= new Form();
		f.setIns(new Form.Value[] {});
		f.setOuts(new Form.Value[] { new Form.Value("float", "f"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[11]= i;

		i= new InstructionType((short) 12, "fconst_1", 1);

		f= new Form();
		f.setIns(new Form.Value[] {});
		f.setOuts(new Form.Value[] { new Form.Value("float", "f"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[12]= i;

		i= new InstructionType((short) 13, "fconst_2", 1);

		f= new Form();
		f.setIns(new Form.Value[] {});
		f.setOuts(new Form.Value[] { new Form.Value("float", "f"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[13]= i;

		i= new InstructionType((short) 14, "dconst_0", 1);

		f= new Form();
		f.setIns(new Form.Value[] {});
		f.setOuts(new Form.Value[] { new Form.Value("double", "d"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[14]= i;

		i= new InstructionType((short) 15, "dconst_1", 1);

		f= new Form();
		f.setIns(new Form.Value[] {});
		f.setOuts(new Form.Value[] { new Form.Value("double", "d"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[15]= i;

		i= new InstructionType((short) 16, "bipush", 1);

		f= new Form();
		f.setIns(new Form.Value[] {});
		f.setOuts(new Form.Value[] { new Form.Value("int", "value"), });
		f.setOperands(new Form.Value[] { new Form.Value("byte", "byte"), });
		i.setForm(f, 0);

		instructionTypes[16]= i;

		i= new InstructionType((short) 17, "sipush", 1);

		f= new Form();
		f.setIns(new Form.Value[] {});
		f.setOuts(new Form.Value[] { new Form.Value("short", "value"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[17]= i;

		i= new InstructionType((short) 18, "ldc", 1);

		f= new Form();
		f.setIns(new Form.Value[] {});
		f.setOuts(new Form.Value[] { new Form.Value("cat1", "value"), });
		f.setOperands(new Form.Value[] { new Form.Value("byte", "index"), });
		i.setForm(f, 0);

		instructionTypes[18]= i;

		i= new InstructionType((short) 19, "ldc_w", 1);

		f= new Form();
		f.setIns(new Form.Value[] {});
		f.setOuts(new Form.Value[] { new Form.Value("cat1", "value"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[19]= i;

		i= new InstructionType((short) 20, "ldc2_w", 1);

		f= new Form();
		f.setIns(new Form.Value[] {});
		f.setOuts(new Form.Value[] { new Form.Value("cat1", "value"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[20]= i;

		i= new InstructionType((short) 21, "iload", 1);

		f= new Form();
		f.setIns(new Form.Value[] {});
		f.setOuts(new Form.Value[] { new Form.Value("int", "value"), });
		f.setOperands(new Form.Value[] { new Form.Value("byte", "index"), });
		i.setForm(f, 0);

		instructionTypes[21]= i;

		i= new InstructionType((short) 22, "lload", 1);

		f= new Form();
		f.setIns(new Form.Value[] {});
		f.setOuts(new Form.Value[] { new Form.Value("long", "value"), });
		f.setOperands(new Form.Value[] { new Form.Value("byte", "index"), });
		i.setForm(f, 0);

		instructionTypes[22]= i;

		i= new InstructionType((short) 23, "fload", 1);

		f= new Form();
		f.setIns(new Form.Value[] {});
		f.setOuts(new Form.Value[] { new Form.Value("float", "value"), });
		f.setOperands(new Form.Value[] { new Form.Value("byte", "index"), });
		i.setForm(f, 0);

		instructionTypes[23]= i;

		i= new InstructionType((short) 24, "dload", 1);

		f= new Form();
		f.setIns(new Form.Value[] {});
		f.setOuts(new Form.Value[] { new Form.Value("double", "value"), });
		f.setOperands(new Form.Value[] { new Form.Value("byte", "index"), });
		i.setForm(f, 0);

		instructionTypes[24]= i;

		i= new InstructionType((short) 25, "aload", 1);

		f= new Form();
		f.setIns(new Form.Value[] {});
		f.setOuts(new Form.Value[] { new Form.Value("object", "objectref"), });
		f.setOperands(new Form.Value[] { new Form.Value("byte", "index"), });
		i.setForm(f, 0);

		instructionTypes[25]= i;

		i= new InstructionType((short) 26, "iload_0", 1);

		f= new Form();
		f.setIns(new Form.Value[] {});
		f.setOuts(new Form.Value[] { new Form.Value("int", "value"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[26]= i;

		i= new InstructionType((short) 27, "iload_1", 1);

		f= new Form();
		f.setIns(new Form.Value[] {});
		f.setOuts(new Form.Value[] { new Form.Value("int", "value"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[27]= i;

		i= new InstructionType((short) 28, "iload_2", 1);

		f= new Form();
		f.setIns(new Form.Value[] {});
		f.setOuts(new Form.Value[] { new Form.Value("int", "value"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[28]= i;

		i= new InstructionType((short) 29, "iload_3", 1);

		f= new Form();
		f.setIns(new Form.Value[] {});
		f.setOuts(new Form.Value[] { new Form.Value("int", "value"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[29]= i;

		i= new InstructionType((short) 30, "lload_0", 1);

		f= new Form();
		f.setIns(new Form.Value[] {});
		f.setOuts(new Form.Value[] { new Form.Value("long", "value"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[30]= i;

		i= new InstructionType((short) 31, "lload_1", 1);

		f= new Form();
		f.setIns(new Form.Value[] {});
		f.setOuts(new Form.Value[] { new Form.Value("long", "value"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[31]= i;

		i= new InstructionType((short) 32, "lload_2", 1);

		f= new Form();
		f.setIns(new Form.Value[] {});
		f.setOuts(new Form.Value[] { new Form.Value("long", "value"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[32]= i;

		i= new InstructionType((short) 33, "lload_3", 1);

		f= new Form();
		f.setIns(new Form.Value[] {});
		f.setOuts(new Form.Value[] { new Form.Value("long", "value"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[33]= i;

		i= new InstructionType((short) 34, "fload_0", 1);

		f= new Form();
		f.setIns(new Form.Value[] {});
		f.setOuts(new Form.Value[] { new Form.Value("float", "value"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[34]= i;

		i= new InstructionType((short) 35, "fload_1", 1);

		f= new Form();
		f.setIns(new Form.Value[] {});
		f.setOuts(new Form.Value[] { new Form.Value("float", "value"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[35]= i;

		i= new InstructionType((short) 36, "fload_2", 1);

		f= new Form();
		f.setIns(new Form.Value[] {});
		f.setOuts(new Form.Value[] { new Form.Value("float", "value"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[36]= i;

		i= new InstructionType((short) 37, "fload_3", 1);

		f= new Form();
		f.setIns(new Form.Value[] {});
		f.setOuts(new Form.Value[] { new Form.Value("float", "value"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[37]= i;

		i= new InstructionType((short) 38, "dload_0", 1);

		f= new Form();
		f.setIns(new Form.Value[] {});
		f.setOuts(new Form.Value[] { new Form.Value("double", "value"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[38]= i;

		i= new InstructionType((short) 39, "dload_1", 1);

		f= new Form();
		f.setIns(new Form.Value[] {});
		f.setOuts(new Form.Value[] { new Form.Value("double", "value"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[39]= i;

		i= new InstructionType((short) 40, "dload_2", 1);

		f= new Form();
		f.setIns(new Form.Value[] {});
		f.setOuts(new Form.Value[] { new Form.Value("double", "value"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[40]= i;

		i= new InstructionType((short) 41, "dload_3", 1);

		f= new Form();
		f.setIns(new Form.Value[] {});
		f.setOuts(new Form.Value[] { new Form.Value("double", "value"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[41]= i;

		i= new InstructionType((short) 42, "aload_0", 1);

		f= new Form();
		f.setIns(new Form.Value[] {});
		f.setOuts(new Form.Value[] { new Form.Value("object", "objectref"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[42]= i;

		i= new InstructionType((short) 43, "aload_1", 1);

		f= new Form();
		f.setIns(new Form.Value[] {});
		f.setOuts(new Form.Value[] { new Form.Value("object", "objectref"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[43]= i;

		i= new InstructionType((short) 44, "aload_2", 1);

		f= new Form();
		f.setIns(new Form.Value[] {});
		f.setOuts(new Form.Value[] { new Form.Value("object", "objectref"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[44]= i;

		i= new InstructionType((short) 45, "aload_3", 1);

		f= new Form();
		f.setIns(new Form.Value[] {});
		f.setOuts(new Form.Value[] { new Form.Value("object", "objectref"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[45]= i;

		i= new InstructionType((short) 46, "iaload", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "arrayref"), new Form.Value("", "index"), });
		f.setOuts(new Form.Value[] { new Form.Value("int", "value"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[46]= i;

		i= new InstructionType((short) 47, "laload", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "arrayref"), new Form.Value("", "index"), });
		f.setOuts(new Form.Value[] { new Form.Value("long", "value"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[47]= i;

		i= new InstructionType((short) 48, "faload", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "arrayref"), new Form.Value("", "index"), });
		f.setOuts(new Form.Value[] { new Form.Value("float", "value"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[48]= i;

		i= new InstructionType((short) 49, "daload", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "arrayref"), new Form.Value("", "index"), });
		f.setOuts(new Form.Value[] { new Form.Value("double", "value"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[49]= i;

		i= new InstructionType((short) 50, "aaload", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "arrayref"), new Form.Value("", "index"), });
		f.setOuts(new Form.Value[] { new Form.Value("object", "objectref"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[50]= i;

		i= new InstructionType((short) 51, "baload", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "arrayref"), new Form.Value("", "index"), });
		f.setOuts(new Form.Value[] { new Form.Value("int", "value"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[51]= i;

		i= new InstructionType((short) 52, "caload", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "arrayref"), new Form.Value("", "index"), });
		f.setOuts(new Form.Value[] { new Form.Value("int", "value"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[52]= i;

		i= new InstructionType((short) 53, "saload", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "arrayref"), new Form.Value("", "index"), });
		f.setOuts(new Form.Value[] { new Form.Value("short", "value"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[53]= i;

		i= new InstructionType((short) 54, "istore", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value"), });
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] { new Form.Value("byte", "index"), });
		i.setForm(f, 0);

		instructionTypes[54]= i;

		i= new InstructionType((short) 55, "lstore", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value"), });
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] { new Form.Value("byte", "index"), });
		i.setForm(f, 0);

		instructionTypes[55]= i;

		i= new InstructionType((short) 56, "fstore", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value"), });
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] { new Form.Value("byte", "index"), });
		i.setForm(f, 0);

		instructionTypes[56]= i;

		i= new InstructionType((short) 57, "dstore", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("double", "value"), });
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] { new Form.Value("byte", "index"), });
		i.setForm(f, 0);

		instructionTypes[57]= i;

		i= new InstructionType((short) 58, "astore", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "objectref"), });
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] { new Form.Value("byte", "index"), });
		i.setForm(f, 0);

		instructionTypes[58]= i;

		i= new InstructionType((short) 59, "istore_0", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value"), });
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[59]= i;

		i= new InstructionType((short) 60, "istore_1", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value"), });
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[60]= i;

		i= new InstructionType((short) 61, "istore_2", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value"), });
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[61]= i;

		i= new InstructionType((short) 62, "istore_3", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value"), });
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[62]= i;

		i= new InstructionType((short) 63, "lstore_0", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value"), });
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[63]= i;

		i= new InstructionType((short) 64, "lstore_1", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value"), });
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[64]= i;

		i= new InstructionType((short) 65, "lstore_2", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value"), });
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[65]= i;

		i= new InstructionType((short) 66, "lstore_3", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value"), });
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[66]= i;

		i= new InstructionType((short) 67, "fstore_0", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("float", "value"), });
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[67]= i;

		i= new InstructionType((short) 68, "fstore_1", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("float", "value"), });
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[68]= i;

		i= new InstructionType((short) 69, "fstore_2", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("float", "value"), });
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[69]= i;

		i= new InstructionType((short) 70, "fstore_3", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("float", "value"), });
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[70]= i;

		i= new InstructionType((short) 71, "dstore_0", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("double", "value"), });
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[71]= i;

		i= new InstructionType((short) 72, "dstore_1", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("double", "value"), });
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[72]= i;

		i= new InstructionType((short) 73, "dstore_2", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("double", "value"), });
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[73]= i;

		i= new InstructionType((short) 74, "dstore_3", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("double", "value"), });
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[74]= i;

		i= new InstructionType((short) 75, "astore_0", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "objectref"), });
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[75]= i;

		i= new InstructionType((short) 76, "astore_1", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "objectref"), });
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[76]= i;

		i= new InstructionType((short) 77, "astore_2", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "objectref"), });
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[77]= i;

		i= new InstructionType((short) 78, "astore_3", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "objectref"), });
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[78]= i;

		i= new InstructionType((short) 79, "iastore", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "arrayref"), new Form.Value("", "index"), new Form.Value("", "value"), });
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[79]= i;

		i= new InstructionType((short) 80, "lastore", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "arrayref"), new Form.Value("", "index"), new Form.Value("", "value"), });
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[80]= i;

		i= new InstructionType((short) 81, "fastore", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "arrayref"), new Form.Value("", "index"), new Form.Value("", "value"), });
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[81]= i;

		i= new InstructionType((short) 82, "dastore", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "arrayref"), new Form.Value("", "index"), new Form.Value("", "value"), });
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[82]= i;

		i= new InstructionType((short) 83, "aastore", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "arrayref"), new Form.Value("", "index"), new Form.Value("", "value"), });
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[83]= i;

		i= new InstructionType((short) 84, "bastore", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "arrayref"), new Form.Value("", "index"), new Form.Value("", "value"), });
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[84]= i;

		i= new InstructionType((short) 85, "castore", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "arrayref"), new Form.Value("", "index"), new Form.Value("", "value"), });
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[85]= i;

		i= new InstructionType((short) 86, "sastore", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "array"), new Form.Value("", "index"), new Form.Value("", "value"), });
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[86]= i;

		i= new InstructionType((short) 87, "pop", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("cat1", "value"), });
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[87]= i;

		i= new InstructionType((short) 88, "pop2", 2);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("cat2", "value"), });
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("cat1", "value2"), new Form.Value("cat1", "value1"), });
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 1);

		instructionTypes[88]= i;

		i= new InstructionType((short) 89, "dup", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("cat1", "value"), });
		f.setOuts(new Form.Value[] { new Form.Value("cat1", "value"), new Form.Value("cat1", "value"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[89]= i;

		i= new InstructionType((short) 90, "dup_x1", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("cat1", "value2"), new Form.Value("cat1", "value1"), });
		f.setOuts(new Form.Value[] { new Form.Value("cat1", "value1"), new Form.Value("cat1", "value2"), new Form.Value("cat1", "value1"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[90]= i;

		i= new InstructionType((short) 91, "dup_x2", 2);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("cat1", "value3"), new Form.Value("cat1", "value2"), new Form.Value("cat1", "value1"), });
		f.setOuts(new Form.Value[] { new Form.Value("cat1", "value1"), new Form.Value("cat1", "value3"), new Form.Value("cat1", "value2"), new Form.Value("cat1", "value1"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("cat2", "value2"), new Form.Value("cat1", "value1"), });
		f.setOuts(new Form.Value[] { new Form.Value("cat1", "value1"), new Form.Value("cat2", "value2"), new Form.Value("cat1", "value1"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 1);

		instructionTypes[91]= i;

		i= new InstructionType((short) 92, "dup2", 2);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("cat1", "value2"), new Form.Value("cat1", "value1"), });
		f.setOuts(new Form.Value[] { new Form.Value("cat1", "value2"), new Form.Value("cat1", "value1"), new Form.Value("cat1", "value2"), new Form.Value("cat1", "value1"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("cat2", "value"), });
		f.setOuts(new Form.Value[] { new Form.Value("cat2", "value"), new Form.Value("cat2", "value"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 1);

		instructionTypes[92]= i;

		i= new InstructionType((short) 93, "dup2_x1", 2);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("cat1", "value3"), new Form.Value("cat1", "value2"), new Form.Value("cat1", "value1"), });
		f.setOuts(new Form.Value[] { new Form.Value("cat1", "value2"), new Form.Value("cat1", "value1"), new Form.Value("cat1", "value3"), new Form.Value("cat1", "value2"), new Form.Value("cat1", "value1"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("cat1", "value2"), new Form.Value("cat2", "value1"), });
		f.setOuts(new Form.Value[] { new Form.Value("cat2", "value1"), new Form.Value("cat1", "value2"), new Form.Value("cat2", "value1"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 1);

		instructionTypes[93]= i;

		i= new InstructionType((short) 94, "dup2_x2", 4);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("cat1", "value4"), new Form.Value("cat1", "value3"), new Form.Value("cat1", "value2"), new Form.Value("cat1", "value1"), });
		f.setOuts(new Form.Value[] { new Form.Value("cat1", "value2"), new Form.Value("cat1", "value1"), new Form.Value("cat1", "value4"), new Form.Value("cat1", "value3"), new Form.Value("cat1", "value2"), new Form.Value("cat1", "value1"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("cat1", "value3"), new Form.Value("cat1", "value2"), new Form.Value("cat2", "value1"), });
		f.setOuts(new Form.Value[] { new Form.Value("cat2", "value1"), new Form.Value("cat1", "value3"), new Form.Value("cat1", "value2"), new Form.Value("cat2", "value1"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("cat2", "value3"), new Form.Value("cat1", "value2"), new Form.Value("cat1", "value1"), });
		f.setOuts(new Form.Value[] { new Form.Value("cat1", "value2"), new Form.Value("cat1", "value1"), new Form.Value("cat2", "value3"), new Form.Value("cat1", "value2"), new Form.Value("cat1", "value1"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 2);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("cat2", "value2"), new Form.Value("cat2", "value1"), });
		f.setOuts(new Form.Value[] { new Form.Value("cat2", "value1"), new Form.Value("cat2", "value2"), new Form.Value("cat2", "value1"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 3);

		instructionTypes[94]= i;

		i= new InstructionType((short) 95, "swap", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("cat1", "value2"), new Form.Value("cat1", "value1"), });
		f.setOuts(new Form.Value[] { new Form.Value("cat1", "value1"), new Form.Value("cat1", "value2"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[95]= i;

		i= new InstructionType((short) 96, "iadd", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value1"), new Form.Value("", "value2"), });
		f.setOuts(new Form.Value[] { new Form.Value("int", "result"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[96]= i;

		i= new InstructionType((short) 97, "ladd", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value1"), new Form.Value("", "value2"), });
		f.setOuts(new Form.Value[] { new Form.Value("long", "result"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[97]= i;

		i= new InstructionType((short) 98, "fadd", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value1"), new Form.Value("", "value2"), });
		f.setOuts(new Form.Value[] { new Form.Value("float", "result"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[98]= i;

		i= new InstructionType((short) 99, "dadd", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("double", "value1"), new Form.Value("double", "value2"), });
		f.setOuts(new Form.Value[] { new Form.Value("double", "result"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[99]= i;

		i= new InstructionType((short) 100, "isub", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value1"), new Form.Value("", "value2"), });
		f.setOuts(new Form.Value[] { new Form.Value("int", "result"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[100]= i;

		i= new InstructionType((short) 101, "lsub", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value1"), new Form.Value("", "value2"), });
		f.setOuts(new Form.Value[] { new Form.Value("long", "result"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[101]= i;

		i= new InstructionType((short) 102, "fsub", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value1"), new Form.Value("", "value2"), });
		f.setOuts(new Form.Value[] { new Form.Value("float", "result"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[102]= i;

		i= new InstructionType((short) 103, "dsub", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("double", "value1"), new Form.Value("double", "value2"), });
		f.setOuts(new Form.Value[] { new Form.Value("double", "result"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[103]= i;

		i= new InstructionType((short) 104, "imul", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value1"), new Form.Value("", "value2"), });
		f.setOuts(new Form.Value[] { new Form.Value("int", "result"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[104]= i;

		i= new InstructionType((short) 105, "lmul", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value1"), new Form.Value("", "value2"), });
		f.setOuts(new Form.Value[] { new Form.Value("long", "result"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[105]= i;

		i= new InstructionType((short) 106, "fmul", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value1"), new Form.Value("", "value2"), });
		f.setOuts(new Form.Value[] { new Form.Value("float", "result"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[106]= i;

		i= new InstructionType((short) 107, "dmul", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value1"), new Form.Value("", "value2"), });
		f.setOuts(new Form.Value[] { new Form.Value("double", "result"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[107]= i;

		i= new InstructionType((short) 108, "idiv", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value1"), new Form.Value("", "value2"), });
		f.setOuts(new Form.Value[] { new Form.Value("int", "result"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[108]= i;

		i= new InstructionType((short) 109, "ldiv", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value1"), new Form.Value("", "value2"), });
		f.setOuts(new Form.Value[] { new Form.Value("long", "result"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[109]= i;

		i= new InstructionType((short) 110, "fdiv", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value1"), new Form.Value("", "value2"), });
		f.setOuts(new Form.Value[] { new Form.Value("float", "result"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[110]= i;

		i= new InstructionType((short) 111, "ddiv", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value1"), new Form.Value("", "value2"), });
		f.setOuts(new Form.Value[] { new Form.Value("double", "result"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[111]= i;

		i= new InstructionType((short) 112, "irem", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value1"), new Form.Value("", "value2"), });
		f.setOuts(new Form.Value[] { new Form.Value("int", "result"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[112]= i;

		i= new InstructionType((short) 113, "lrem", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value1"), new Form.Value("", "value2"), });
		f.setOuts(new Form.Value[] { new Form.Value("long", "result"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[113]= i;

		i= new InstructionType((short) 114, "frem", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value1"), new Form.Value("", "value2"), });
		f.setOuts(new Form.Value[] { new Form.Value("float", "result"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[114]= i;

		i= new InstructionType((short) 115, "drem", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value1"), new Form.Value("", "value2"), });
		f.setOuts(new Form.Value[] { new Form.Value("double", "result"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[115]= i;

		i= new InstructionType((short) 116, "ineg", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value"), });
		f.setOuts(new Form.Value[] { new Form.Value("int", "result"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[116]= i;

		i= new InstructionType((short) 117, "lneg", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value"), });
		f.setOuts(new Form.Value[] { new Form.Value("long", "result"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[117]= i;

		i= new InstructionType((short) 118, "fneg", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value"), });
		f.setOuts(new Form.Value[] { new Form.Value("float", "result"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[118]= i;

		i= new InstructionType((short) 119, "dneg", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value"), });
		f.setOuts(new Form.Value[] { new Form.Value("double", "result"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[119]= i;

		i= new InstructionType((short) 120, "ishl", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value1"), new Form.Value("", "value2"), });
		f.setOuts(new Form.Value[] { new Form.Value("int", "result"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[120]= i;

		i= new InstructionType((short) 121, "lshl", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value1"), new Form.Value("", "value2"), });
		f.setOuts(new Form.Value[] { new Form.Value("long", "result"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[121]= i;

		i= new InstructionType((short) 122, "ishr", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value1"), new Form.Value("", "value2"), });
		f.setOuts(new Form.Value[] { new Form.Value("int", "result"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[122]= i;

		i= new InstructionType((short) 123, "lshr", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value1"), new Form.Value("", "value2"), });
		f.setOuts(new Form.Value[] { new Form.Value("long", "result"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[123]= i;

		i= new InstructionType((short) 124, "iushr", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value1"), new Form.Value("", "value2"), });
		f.setOuts(new Form.Value[] { new Form.Value("int", "result"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[124]= i;

		i= new InstructionType((short) 125, "lushr", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value1"), new Form.Value("", "value2"), });
		f.setOuts(new Form.Value[] { new Form.Value("long", "result"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[125]= i;

		i= new InstructionType((short) 126, "iand", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value1"), new Form.Value("", "value2"), });
		f.setOuts(new Form.Value[] { new Form.Value("int", "result"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[126]= i;

		i= new InstructionType((short) 127, "land", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value1"), new Form.Value("", "value2"), });
		f.setOuts(new Form.Value[] { new Form.Value("long", "result"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[127]= i;

		i= new InstructionType((short) 128, "ior", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value1"), new Form.Value("", "value2"), });
		f.setOuts(new Form.Value[] { new Form.Value("int", "result"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[128]= i;

		i= new InstructionType((short) 129, "lor", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value1"), new Form.Value("", "value2"), });
		f.setOuts(new Form.Value[] { new Form.Value("long", "result"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[129]= i;

		i= new InstructionType((short) 130, "ixor", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value1"), new Form.Value("", "value2"), });
		f.setOuts(new Form.Value[] { new Form.Value("int", "result"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[130]= i;

		i= new InstructionType((short) 131, "lxor", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value1"), new Form.Value("", "value2"), });
		f.setOuts(new Form.Value[] { new Form.Value("long", "result"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[131]= i;

		i= new InstructionType((short) 132, "iinc", 1);

		f= new Form();
		f.setIns(new Form.Value[] {});
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] { new Form.Value("byte", "index"), new Form.Value("int", "const"), });
		i.setForm(f, 0);

		instructionTypes[132]= i;

		i= new InstructionType((short) 133, "i2l", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value"), });
		f.setOuts(new Form.Value[] { new Form.Value("long", "result"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[133]= i;

		i= new InstructionType((short) 134, "i2f", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value"), });
		f.setOuts(new Form.Value[] { new Form.Value("float", "result"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[134]= i;

		i= new InstructionType((short) 135, "i2d", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value"), });
		f.setOuts(new Form.Value[] { new Form.Value("double", "result"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[135]= i;

		i= new InstructionType((short) 136, "l2i", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value"), });
		f.setOuts(new Form.Value[] { new Form.Value("int", "result"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[136]= i;

		i= new InstructionType((short) 137, "l2f", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value"), });
		f.setOuts(new Form.Value[] { new Form.Value("float", "result"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[137]= i;

		i= new InstructionType((short) 138, "l2d", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value"), });
		f.setOuts(new Form.Value[] { new Form.Value("double", "result"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[138]= i;

		i= new InstructionType((short) 139, "f2i", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("float", "value"), });
		f.setOuts(new Form.Value[] { new Form.Value("int", "result"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[139]= i;

		i= new InstructionType((short) 140, "f2l", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value"), });
		f.setOuts(new Form.Value[] { new Form.Value("long", "result"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[140]= i;

		i= new InstructionType((short) 141, "f2d", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value"), });
		f.setOuts(new Form.Value[] { new Form.Value("double", "result"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[141]= i;

		i= new InstructionType((short) 142, "d2i", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value"), });
		f.setOuts(new Form.Value[] { new Form.Value("int", "result"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[142]= i;

		i= new InstructionType((short) 143, "d2l", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value"), });
		f.setOuts(new Form.Value[] { new Form.Value("long", "result"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[143]= i;

		i= new InstructionType((short) 144, "d2f", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value"), });
		f.setOuts(new Form.Value[] { new Form.Value("float", "result"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[144]= i;

		i= new InstructionType((short) 145, "i2b", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value"), });
		f.setOuts(new Form.Value[] { new Form.Value("byte", "result"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[145]= i;

		i= new InstructionType((short) 146, "i2c", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value"), });
		f.setOuts(new Form.Value[] { new Form.Value("byte", "result"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[146]= i;

		i= new InstructionType((short) 147, "i2s", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value"), });
		f.setOuts(new Form.Value[] { new Form.Value("short", "result"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[147]= i;

		i= new InstructionType((short) 148, "lcmp", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value1"), new Form.Value("", "value2"), });
		f.setOuts(new Form.Value[] { new Form.Value("int", "result"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[148]= i;

		i= new InstructionType((short) 149, "fcmpl", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value1"), new Form.Value("", "value2"), });
		f.setOuts(new Form.Value[] { new Form.Value("int", "result"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[149]= i;

		i= new InstructionType((short) 150, "fcmpg", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value1"), new Form.Value("", "value2"), });
		f.setOuts(new Form.Value[] { new Form.Value("int", "result"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[150]= i;

		i= new InstructionType((short) 151, "dcmpl", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value1"), new Form.Value("", "value2"), });
		f.setOuts(new Form.Value[] { new Form.Value("int", "result"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[151]= i;

		i= new InstructionType((short) 152, "dcmpg", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value1"), new Form.Value("", "value2"), });
		f.setOuts(new Form.Value[] { new Form.Value("int", "result"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[152]= i;

		i= new InstructionType((short) 153, "ifeq", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value"), });
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] { new Form.Value("short", "branch"), });
		i.setForm(f, 0);

		instructionTypes[153]= i;

		i= new InstructionType((short) 154, "ifne", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value"), });
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] { new Form.Value("short", "branch"), });
		i.setForm(f, 0);

		instructionTypes[154]= i;

		i= new InstructionType((short) 155, "iflt", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value"), });
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] { new Form.Value("short", "branch"), });
		i.setForm(f, 0);

		instructionTypes[155]= i;

		i= new InstructionType((short) 156, "ifge", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value"), });
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] { new Form.Value("short", "branch"), });
		i.setForm(f, 0);

		instructionTypes[156]= i;

		i= new InstructionType((short) 157, "ifgt", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value"), });
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] { new Form.Value("short", "branch"), });
		i.setForm(f, 0);

		instructionTypes[157]= i;

		i= new InstructionType((short) 158, "ifle", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value"), });
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] { new Form.Value("short", "branch"), });
		i.setForm(f, 0);

		instructionTypes[158]= i;

		i= new InstructionType((short) 159, "if_icmpeq", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value1"), new Form.Value("", "value2"), });
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] { new Form.Value("short", "branch"), });
		i.setForm(f, 0);

		instructionTypes[159]= i;

		i= new InstructionType((short) 160, "if_icmpne", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value1"), new Form.Value("", "value2"), });
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] { new Form.Value("short", "branch"), });
		i.setForm(f, 0);

		instructionTypes[160]= i;

		i= new InstructionType((short) 161, "if_icmplt", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value1"), new Form.Value("", "value2"), });
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] { new Form.Value("short", "branch"), });
		i.setForm(f, 0);

		instructionTypes[161]= i;

		i= new InstructionType((short) 162, "if_icmpge", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value1"), new Form.Value("", "value2"), });
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] { new Form.Value("short", "branch"), });
		i.setForm(f, 0);

		instructionTypes[162]= i;

		i= new InstructionType((short) 163, "if_icmpgt", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value1"), new Form.Value("", "value2"), });
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] { new Form.Value("short", "branch"), });
		i.setForm(f, 0);

		instructionTypes[163]= i;

		i= new InstructionType((short) 164, "if_icmple", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value1"), new Form.Value("", "value2"), });
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] { new Form.Value("short", "branch"), });
		i.setForm(f, 0);

		instructionTypes[164]= i;

		i= new InstructionType((short) 165, "if_acmpeq", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value1"), new Form.Value("", "value2"), });
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] { new Form.Value("short", "branch"), });
		i.setForm(f, 0);

		instructionTypes[165]= i;

		i= new InstructionType((short) 166, "if_acmpne", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value1"), new Form.Value("", "value2"), });
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] { new Form.Value("short", "branch"), });
		i.setForm(f, 0);

		instructionTypes[166]= i;

		i= new InstructionType((short) 167, "goto", 1);

		f= new Form();
		f.setIns(new Form.Value[] {});
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] { new Form.Value("short", "branch"), });
		i.setForm(f, 0);

		instructionTypes[167]= i;

		i= new InstructionType((short) 168, "jsr", 1);

		f= new Form();
		f.setIns(new Form.Value[] {});
		f.setOuts(new Form.Value[] { new Form.Value("returnAddress", "address"), });
		f.setOperands(new Form.Value[] { new Form.Value("short", "branch"), });
		i.setForm(f, 0);

		instructionTypes[168]= i;

		i= new InstructionType((short) 169, "ret", 1);

		f= new Form();
		f.setIns(new Form.Value[] {});
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] { new Form.Value("byte", "index"), });
		i.setForm(f, 0);

		instructionTypes[169]= i;

		i= new InstructionType((short) 170, "tableswitch", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("int", "index"), });
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] { new Form.Value("byte", "padding"), new Form.Value("byte", "padding"), new Form.Value("byte", "padding"), new Form.Value("int", "default"), new Form.Value("int", "low"), new Form.Value("int", "high"), });
		i.setForm(f, 0);

		instructionTypes[170]= i;

		i= new InstructionType((short) 171, "lookupswitch", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "key"), });
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] { new Form.Value("byte", "padding"), new Form.Value("byte", "padding"), new Form.Value("byte", "padding"), new Form.Value("int", "default"), new Form.Value("int", "npairs"), });
		i.setForm(f, 0);

		instructionTypes[171]= i;

		i= new InstructionType((short) 172, "ireturn", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value"), });
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[172]= i;

		i= new InstructionType((short) 173, "lreturn", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value"), });
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[173]= i;

		i= new InstructionType((short) 174, "freturn", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value"), });
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[174]= i;

		i= new InstructionType((short) 175, "dreturn", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("double", "value"), });
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[175]= i;

		i= new InstructionType((short) 176, "areturn", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "objectref"), });
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[176]= i;

		i= new InstructionType((short) 177, "return", 1);

		f= new Form();
		f.setIns(new Form.Value[] {});
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[177]= i;

		i= new InstructionType((short) 178, "getstatic", 1);

		f= new Form();
		f.setIns(new Form.Value[] {});
		f.setOuts(new Form.Value[] { new Form.Value("", "value"), });
		f.setOperands(new Form.Value[] { new Form.Value("short", "index"), });
		i.setForm(f, 0);

		instructionTypes[178]= i;

		i= new InstructionType((short) 179, "putstatic", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value"), });
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] { new Form.Value("short", "index"), });
		i.setForm(f, 0);

		instructionTypes[179]= i;

		i= new InstructionType((short) 180, "getfield", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "objectref"), });
		f.setOuts(new Form.Value[] { new Form.Value("", "value"), });
		f.setOperands(new Form.Value[] { new Form.Value("short", "index"), });
		i.setForm(f, 0);

		instructionTypes[180]= i;

		i= new InstructionType((short) 181, "putfield", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "objectref"), new Form.Value("", "value"), });
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] { new Form.Value("short", "index"), });
		i.setForm(f, 0);

		instructionTypes[181]= i;

		i= new InstructionType((short) 182, "invokevirtual", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "objectref"), new Form.Value("", "arg1"), new Form.Value("", "..."), new Form.Value("", "argN"), });
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[182]= i;

		i= new InstructionType((short) 183, "invokespecial", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "objectref"), new Form.Value("", "arg1"), new Form.Value("", "..."), new Form.Value("", "argN"), });
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[183]= i;

		i= new InstructionType((short) 184, "invokestatic", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "arg1"), new Form.Value("", "..."), new Form.Value("", "argN"), });
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] { new Form.Value("short", "index"), });
		i.setForm(f, 0);

		instructionTypes[184]= i;

		i= new InstructionType((short) 185, "invokeinterface", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "objectref"), new Form.Value("", "arg1"), new Form.Value("", "..."), new Form.Value("", "argN"), });
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] { new Form.Value("short", "index"), new Form.Value("byte", "count"), new Form.Value("byte", "0"), });
		i.setForm(f, 0);

		instructionTypes[185]= i;

		i= new InstructionType((short) 186, "xxxunusedxxx", 1);

		f= new Form();
		f.setIns(new Form.Value[] {});
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[186]= i;

		i= new InstructionType((short) 187, "new", 1);

		f= new Form();
		f.setIns(new Form.Value[] {});
		f.setOuts(new Form.Value[] { new Form.Value("object", "objectref"), });
		f.setOperands(new Form.Value[] { new Form.Value("short", "index"), });
		i.setForm(f, 0);

		instructionTypes[187]= i;

		i= new InstructionType((short) 188, "newarray", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("int", "count"), });
		f.setOuts(new Form.Value[] { new Form.Value("object", "arrayref"), });
		f.setOperands(new Form.Value[] { new Form.Value("byte", "atype"), });
		i.setForm(f, 0);

		instructionTypes[188]= i;

		i= new InstructionType((short) 189, "anewarray", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("int", "count"), });
		f.setOuts(new Form.Value[] { new Form.Value("object", "arrayref"), });
		f.setOperands(new Form.Value[] { new Form.Value("short", "index"), });
		i.setForm(f, 0);

		instructionTypes[189]= i;

		i= new InstructionType((short) 190, "arraylength", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "arrayref"), });
		f.setOuts(new Form.Value[] { new Form.Value("int", "length"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[190]= i;

		i= new InstructionType((short) 191, "athrow", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "objectref"), });
		f.setOuts(new Form.Value[] { new Form.Value("object", "objectref"), });
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[191]= i;

		i= new InstructionType((short) 192, "checkcast", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "objectref"), });
		f.setOuts(new Form.Value[] { new Form.Value("object", "objectref"), });
		f.setOperands(new Form.Value[] { new Form.Value("short", "index"), });
		i.setForm(f, 0);

		instructionTypes[192]= i;

		i= new InstructionType((short) 193, "instanceof", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "objectref"), });
		f.setOuts(new Form.Value[] { new Form.Value("int", "result"), });
		f.setOperands(new Form.Value[] { new Form.Value("short", "index"), });
		i.setForm(f, 0);

		instructionTypes[193]= i;

		i= new InstructionType((short) 194, "monitorenter", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "objectref"), });
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[194]= i;

		i= new InstructionType((short) 195, "monitorexit", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "objectref"), });
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[195]= i;

		i= new InstructionType((short) 196, "wide", 1);

		f= new Form();
		f.setIns(new Form.Value[] {});
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] {});
		i.setForm(f, 0);

		instructionTypes[196]= i;

		i= new InstructionType((short) 197, "multianewarray", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "count1"), new Form.Value("", "..."), new Form.Value("", "countN"), });
		f.setOuts(new Form.Value[] { new Form.Value("object", "arrayref"), });
		f.setOperands(new Form.Value[] { new Form.Value("short", "index"), new Form.Value("byte", "dimension N"), });
		i.setForm(f, 0);

		instructionTypes[197]= i;

		i= new InstructionType((short) 198, "ifnull", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value"), });
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] { new Form.Value("short", "branch"), });
		i.setForm(f, 0);

		instructionTypes[198]= i;

		i= new InstructionType((short) 199, "ifnonnull", 1);

		f= new Form();
		f.setIns(new Form.Value[] { new Form.Value("", "value"), });
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] { new Form.Value("short", "branch"), });
		i.setForm(f, 0);

		instructionTypes[199]= i;

		i= new InstructionType((short) 200, "goto_w", 1);

		f= new Form();
		f.setIns(new Form.Value[] {});
		f.setOuts(new Form.Value[] {});
		f.setOperands(new Form.Value[] { new Form.Value("int", "branch"), });
		i.setForm(f, 0);

		instructionTypes[200]= i;

		i= new InstructionType((short) 201, "jsr_w", 1);

		f= new Form();
		f.setIns(new Form.Value[] {});
		f.setOuts(new Form.Value[] { new Form.Value("returnAddress", "address"), });
		f.setOperands(new Form.Value[] { new Form.Value("int", "branch"), });
		i.setForm(f, 0);

		instructionTypes[201]= i;

	}
}
