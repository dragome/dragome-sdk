/*******************************************************************************
 * Copyright (c) 2011-2014 Fernando Petrola
 * 
 *  This file is part of Dragome SDK.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
/* Copyright (c) 2002-2011 by XMLVM.org
 *
 * Project Info:  http://www.xmlvm.org
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 */

package org.xmlvm.proc.out;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.bcel.Constants;
import org.apache.bcel.generic.AALOAD;
import org.apache.bcel.generic.AASTORE;
import org.apache.bcel.generic.ACONST_NULL;
import org.apache.bcel.generic.ANEWARRAY;
import org.apache.bcel.generic.ARETURN;
import org.apache.bcel.generic.ARRAYLENGTH;
import org.apache.bcel.generic.ASTORE;
import org.apache.bcel.generic.ArrayType;
import org.apache.bcel.generic.BIPUSH;
import org.apache.bcel.generic.BranchInstruction;
import org.apache.bcel.generic.CHECKCAST;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.CompoundInstruction;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.D2F;
import org.apache.bcel.generic.D2I;
import org.apache.bcel.generic.DADD;
import org.apache.bcel.generic.DDIV;
import org.apache.bcel.generic.DLOAD;
import org.apache.bcel.generic.DMUL;
import org.apache.bcel.generic.DSTORE;
import org.apache.bcel.generic.DSUB;
import org.apache.bcel.generic.DUP;
import org.apache.bcel.generic.F2I;
import org.apache.bcel.generic.FADD;
import org.apache.bcel.generic.FCMPG;
import org.apache.bcel.generic.FCMPL;
import org.apache.bcel.generic.FLOAD;
import org.apache.bcel.generic.FMUL;
import org.apache.bcel.generic.FSTORE;
import org.apache.bcel.generic.FSUB;
import org.apache.bcel.generic.FieldGen;
import org.apache.bcel.generic.GOTO;
import org.apache.bcel.generic.I2B;
import org.apache.bcel.generic.I2F;
import org.apache.bcel.generic.I2L;
import org.apache.bcel.generic.IADD;
import org.apache.bcel.generic.ICONST;
import org.apache.bcel.generic.IDIV;
import org.apache.bcel.generic.IFEQ;
import org.apache.bcel.generic.IFNE;
import org.apache.bcel.generic.IF_ACMPNE;
import org.apache.bcel.generic.IF_ICMPGE;
import org.apache.bcel.generic.IF_ICMPGT;
import org.apache.bcel.generic.IF_ICMPLE;
import org.apache.bcel.generic.IF_ICMPLT;
import org.apache.bcel.generic.IF_ICMPNE;
import org.apache.bcel.generic.IINC;
import org.apache.bcel.generic.ILOAD;
import org.apache.bcel.generic.IMUL;
import org.apache.bcel.generic.IREM;
import org.apache.bcel.generic.ISTORE;
import org.apache.bcel.generic.ISUB;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.L2I;
import org.apache.bcel.generic.LADD;
import org.apache.bcel.generic.LLOAD;
import org.apache.bcel.generic.LSTORE;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.NOP;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.POP;
import org.apache.bcel.generic.PUSH;
import org.apache.bcel.generic.Type;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.xmlvm.IllegalXMLVMException;
import org.xmlvm.Log;
import org.xmlvm.main.Arguments;
import org.xmlvm.proc.BundlePhase1;
import org.xmlvm.proc.BundlePhase2;
import org.xmlvm.proc.XmlvmProcessImpl;
import org.xmlvm.proc.XmlvmResource;
import org.xmlvm.proc.in.file.ClassFile;

/**
 * This process takes XMLVM and turns it into Java ByteCode.
 */
public class JavaByteCodeOutputProcess extends XmlvmProcessImpl
{

	private static final class InstructionHandlerManager
	{

		private Map<Integer, InstructionHandle> mapID2InstructionHandle;
		private Map<Integer, List<BranchInstruction>> mapID2BranchInstructions;
		private ArrayList<Integer> currentIds;

		public InstructionHandlerManager()
		{
			mapID2InstructionHandle= new HashMap<Integer, InstructionHandle>();
			mapID2BranchInstructions= new HashMap<Integer, List<BranchInstruction>>();
			currentIds= new ArrayList<Integer>();
		}

		public void setLabelID(int id) throws IllegalXMLVMException
		{
			currentIds.add(id);
		}

		public void registerInstructionHandle(InstructionHandle ih)
		{
			if (currentIds.size() == 0)
				return;

			for (Integer currentID : currentIds)
			{
				mapID2InstructionHandle.put(currentID, ih);
				List<BranchInstruction> l= mapID2BranchInstructions.get(currentID);
				if (l != null)
				{
					// We encountered some branch instructions earlier before we
					// registered this instruction handle
					for (BranchInstruction bi : l)
					{
						bi.setTarget(ih);
					}
					mapID2BranchInstructions.remove(currentID);
				}

			}
			currentIds.clear();

		}

		public void registerBranchInstruction(BranchInstruction g, int id)
		{
			InstructionHandle ih= mapID2InstructionHandle.get(id);
			if (ih != null)
			{
				// Instruction handle was registered before
				g.setTarget(ih);
				return;
			}
			// We haven't seen the instruction handle yet. Remember this branch
			// instruction
			List<BranchInstruction> l= mapID2BranchInstructions.get(id);
			if (l == null)
				l= new ArrayList<BranchInstruction>();
			l.add(g);
			mapID2BranchInstructions.put(id, l);
		}

		public void checkConsistency()
		{
			// At the end of processing the byte code of a method,
			// mapID2BranchInstructions
			// should be empty.
			if (mapID2BranchInstructions.size() != 0)
			{
				System.err.println("Following label IDs could not be resolved: " + mapID2BranchInstructions.keySet());
				;
				System.exit(-1);
			}
		}
	}

	private static final Namespace nsXMLVM= Namespace.getNamespace("vm", "http://xmlvm.org");

	private InstructionFactory factory;
	private InstructionList instructionList;
	private ConstantPoolGen constantPoolGen;
	private ClassGen classGen;
	private InstructionHandlerManager instructionHandlerManager;
	private String fullQualifiedClassName;

	public JavaByteCodeOutputProcess(Arguments arguments)
	{
		super(arguments);
		addAllXmlvmEmittingProcessesAsInput();
	}

	public boolean processPhase1(BundlePhase1 bundle)
	{
		return true;
	}

	public boolean processPhase2(BundlePhase2 bundle)
	{
		for (XmlvmResource xmlvmResource : bundle.getResources())
		{
			try
			{
				bundle.addOutputFiles(createBytecode(xmlvmResource.getXmlvmDocument(), arguments.option_out()));
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
				Log.error("Could not create class file for: " + xmlvmResource.getName());
				return false;
			}
			catch (IllegalXMLVMException ex)
			{
				ex.printStackTrace();
				Log.error("Could not create class file for: " + xmlvmResource.getName());
				return false;
			}
		}
		return true;
	}

	/**
	 * Creates Java binary files from the org.jdom.Document that is associated
	 * with this object. The location for the binary files is determined by the
	 * given string path.
	 */
	private List<OutputFile> createBytecode(Document document, String path) throws IllegalXMLVMException, IOException
	{
		List<OutputFile> result= new ArrayList<OutputFile>();
		Element root= document.getRootElement();
		if (!root.getName().equals("xmlvm"))
			throw new IllegalXMLVMException("Root element needs to be <xmlvm>");
		@SuppressWarnings("unchecked")
		List<Element> clazzes= root.getChildren("class", nsXMLVM);

		for (Element clazz : clazzes)
		{
			if (clazz == null)
				throw new IllegalXMLVMException("XMLVM contains no class");
			createClass(clazz);
			for (Object o : clazz.getChildren())
			{
				Element decl= (Element) o;
				String tag= decl.getName();
				if (tag.equals("method"))
					createMethod(decl);
				else if (tag.equals("field"))
					createField(decl);
				else
					throw new IllegalXMLVMException("Unknown class declaration '" + tag + "'");
			}

			String packageName= clazz.getAttributeValue("package");

			// If there is a package name, then create necessary
			// folders for that package.
			if (packageName != null && packageName != "")
			{
				packageName= File.separatorChar + packageName.replace('.', File.separatorChar);
				new File(path + File.separatorChar + packageName).mkdirs();
			}
			else
			{
				packageName= "";
			}

			String className= clazz.getAttributeValue("name");

			OutputFile outputFile= new OutputFile(classGen.getJavaClass().getBytes());
			outputFile.setFileName(className + ClassFile.CLASS_ENDING);
			outputFile.setLocation(path + File.separatorChar + packageName);
			result.add(outputFile);
		}
		return result;
	}

	private void createClass(Element clazz)
	{
		String packageName= clazz.getAttributeValue("package");
		packageName= packageName == null ? "" : packageName;
		String clazzName= clazz.getAttributeValue("name");
		String fileName= clazzName + ".java";
		fullQualifiedClassName= packageName.equals("") ? clazzName : packageName + "." + clazzName;
		String baseClazz= clazz.getAttributeValue("extends");
		// TODO Read interfaces
		String[] interfaces= new String[] {};
		short accessFlags= getAccessFlags(clazz);
		classGen= new ClassGen(fullQualifiedClassName, baseClazz, fileName, accessFlags, interfaces);
		constantPoolGen= classGen.getConstantPool();
		factory= new InstructionFactory(classGen, constantPoolGen);
	}

	private void createMethod(Element method) throws IllegalXMLVMException
	{
		instructionList= new InstructionList();
		instructionHandlerManager= new InstructionHandlerManager();
		String methodName= method.getAttributeValue("name");

		Element signature= method.getChild("signature", nsXMLVM);
		Type retType= collectReturnType(signature);
		Type[] argTypes= collectArgumentTypes(signature);
		short accessFlags= getAccessFlags(method);

		if (methodName.equals(".cctor")) // Same concept, different names in
		// .net/JVM. Note we are doing init of
		// statics for a class
		{
			methodName= "<clinit>";
			accessFlags= 0x8; // static
		}

		MethodGen m= new MethodGen(accessFlags, retType, argTypes, null, methodName, fullQualifiedClassName, instructionList, constantPoolGen);
		Element code= method.getChild("code", nsXMLVM);
		createCode(code);
		instructionHandlerManager.checkConsistency();
		m.setMaxLocals();
		m.setMaxStack();
		classGen.addMethod(m.getMethod());
		instructionList.dispose();
	}

	private void createField(Element field) throws IllegalXMLVMException
	{
		String name= field.getAttributeValue("name");
		Type t= parseTypeString(field.getAttributeValue("type"));
		short flags= getAccessFlags(field);
		FieldGen f= new FieldGen(flags, t, name, constantPoolGen);
		classGen.addField(f.getField());
	}

	private Type collectReturnType(Element signature) throws IllegalXMLVMException
	{
		Element ret= signature.getChild("return", nsXMLVM);
		String t= ret.getAttributeValue("type");
		if (t.equals("void"))
			return Type.VOID;
		return parseTypeString(t);
	}

	private Type[] collectArgumentTypes(Element signature) throws IllegalXMLVMException
	{
		List<Element> params= signature.getChildren("parameter", nsXMLVM);
		if (params.isEmpty())
			return Type.NO_ARGS;
		List<Type> argTypes= new ArrayList<Type>();
		for (Element p : params)
		{
			String type= p.getAttributeValue("type");
			argTypes.add(parseTypeString(type));
		}
		return argTypes.toArray(new Type[0]);
	}

	private Type parseTypeString(String type) throws IllegalXMLVMException
	{
		int arrayDimension= 0;
		while (type.endsWith("[]"))
		{
			arrayDimension++;
			type= type.substring(0, type.length() - 2);
		}
		Type baseType= null;
		if (type.equals("java.lang.String"))
			baseType= Type.STRING;
		if (type.equals("boolean"))
			baseType= Type.BOOLEAN;
		if (type.equals("byte"))
			baseType= Type.BYTE;
		if (type.equals("short"))
			baseType= Type.SHORT;
		if (type.equals("int"))
			baseType= Type.INT;
		if (type.equals("long"))
			baseType= Type.LONG;
		if (type.equals("float"))
			baseType= Type.FLOAT;
		if (type.equals("double"))
			baseType= Type.DOUBLE;
		if (type.equals("char"))
			baseType= Type.CHAR;
		if (type.equals("java.lang.Object"))
			baseType= Type.OBJECT;
		if (baseType == null)
			baseType= new ObjectType(type);
		if (arrayDimension == 0)
			return baseType;
		else
			return new ArrayType(baseType, arrayDimension);
	}

	private void createCode(Element code) throws IllegalXMLVMException
	{
		List<Element> instructions= code.getChildren();
		for (Element inst : instructions)
		{
			String name= inst.getName();
			String opcMethodName= "createInstruction" + name.substring(0, 1).toUpperCase() + name.substring(1);
			Class<?> appClazz;
			Method opcMeth;
			Class<?>[] paramTypes= { Element.class };
			Object[] params= { inst };
			appClazz= this.getClass();
			Object newInstr= null;
			try
			{
				opcMeth= appClazz.getDeclaredMethod(opcMethodName, paramTypes);
				newInstr= opcMeth.invoke(this, params);
			}
			catch (NoSuchMethodException ex)
			{
				throw new IllegalXMLVMException("Illegal instruction 1, unable to find method " + opcMethodName + " for '" + name + "'");
			}
			catch (InvocationTargetException ex)
			{
				ex.printStackTrace();
				throw new IllegalXMLVMException("Illegal instruction 2 '" + name + "'");
			}
			catch (IllegalAccessException ex)
			{
				throw new IllegalXMLVMException("Illegal instruction 3 '" + name + "'");
			}
			if (newInstr != null)
			{
				InstructionHandle ih= null;
				if (newInstr instanceof BranchInstruction)
					ih= instructionList.append((BranchInstruction) newInstr);
				else if (newInstr instanceof CompoundInstruction)
					ih= instructionList.append((CompoundInstruction) newInstr);
				else if (newInstr instanceof Instruction)
					ih= instructionList.append((Instruction) newInstr);
				instructionHandlerManager.registerInstructionHandle(ih);

			}
		}
	}

	private short getAccessFlags(Element elem)
	{
		short af= 0;
		af|= checkAccessFlag(elem, "isPublic", Constants.ACC_PUBLIC);
		af|= checkAccessFlag(elem, "isPrivate", Constants.ACC_PRIVATE);
		af|= checkAccessFlag(elem, "isProtected", Constants.ACC_PROTECTED);
		af|= checkAccessFlag(elem, "isSynchronized", Constants.ACC_SYNCHRONIZED);
		af|= checkAccessFlag(elem, "isStatic", Constants.ACC_STATIC);
		return af;
	}

	private short checkAccessFlag(Element elem, String flag, short jvmFlag)
	{
		String val= elem.getAttributeValue(flag);
		if (val == null)
			return 0;
		return val.equals("true") ? jvmFlag : 0;
	}

	@SuppressWarnings("unused")
	// Called using reflection
	private Instruction createInstructionVar(Element inst)
	{
		return null;
	}

	@SuppressWarnings("unused")
	// Called using reflection
	private Instruction createInstructionCheckcast(Element inst) throws IllegalXMLVMException
	{
		String classType= inst.getAttributeValue("type");
		return new CHECKCAST(constantPoolGen.addClass(classType));
	}

	@SuppressWarnings("unused")
	// Called using reflection
	private Instruction createInstructionF2i(Element inst)
	{
		return new F2I();
	}

	@SuppressWarnings("unused")
	// Called using reflection
	private Instruction createInstructionFmul(Element inst)
	{
		return new FMUL();
	}

	@SuppressWarnings("unused")
	// Called using reflection
	private Instruction createInstructionFcmpl(Element inst)
	{
		return new FCMPL();
	}

	@SuppressWarnings("unused")
	// Called using reflection
	private Instruction createInstructionFcmpg(Element inst)
	{
		return new FCMPG();
	}

	@SuppressWarnings("unused")
	// Called using reflection
	private Instruction createInstructionFsub(Element inst)
	{
		return new FSUB();
	}

	@SuppressWarnings("unused")
	// Called using reflection
	private Instruction createInstructionDup(Element inst)
	{
		return new DUP();
	}

	@SuppressWarnings("unused")
	// Called using reflection
	private Instruction createInstructionLabel(Element inst) throws IllegalXMLVMException
	{
		int id= Integer.parseInt(inst.getAttributeValue("id"));
		instructionHandlerManager.setLabelID(id);
		return null;
	}

	@SuppressWarnings("unused")
	// Called using reflection
	private Instruction createInstructionGoto(Element inst)
	{
		int id= Integer.parseInt(inst.getAttributeValue("label"));
		BranchInstruction bi= new GOTO(null);
		instructionHandlerManager.registerBranchInstruction(bi, id);
		return bi;
	}

	@SuppressWarnings("unused")
	// Called using reflection
	private Instruction createInstructionD2f(Element inst)
	{
		return new D2F();
	}

	@SuppressWarnings("unused")
	// Called using reflection
	private Instruction createInstructionI2f(Element inst)
	{
		return new I2F();
	}

	@SuppressWarnings("unused")
	// Called using reflection
	private Instruction createInstructionD2i(Element inst)
	{
		return new D2I();
	}

	@SuppressWarnings("unused")
	// Called using reflection
	private Instruction createInstructionArraylength(Element inst)
	{
		return new ARRAYLENGTH();
	}

	@SuppressWarnings("unused")
	// Called using reflection
	private Instruction createInstructionIf_acmpne(Element inst)
	{
		int id= Integer.parseInt(inst.getAttributeValue("label"));
		BranchInstruction bi= new IF_ACMPNE(null);
		instructionHandlerManager.registerBranchInstruction(bi, id);
		return bi;
	}

	@SuppressWarnings("unused")
	// Called using reflection
	private Instruction createInstructionIf_icmplt(Element inst)
	{
		int id= Integer.parseInt(inst.getAttributeValue("label"));
		BranchInstruction bi= new IF_ICMPLT(null);
		instructionHandlerManager.registerBranchInstruction(bi, id);
		return bi;
	}

	@SuppressWarnings("unused")
	// Called using reflection
	private Instruction createInstructionIf_icmpgt(Element inst)
	{
		int id= Integer.parseInt(inst.getAttributeValue("label"));
		BranchInstruction bi= new IF_ICMPGT(null);
		instructionHandlerManager.registerBranchInstruction(bi, id);
		return bi;
	}

	@SuppressWarnings("unused")
	// Called using reflection
	private Instruction createInstructionIf_icmpge(Element inst)
	{
		int id= Integer.parseInt(inst.getAttributeValue("label"));
		BranchInstruction bi= new IF_ICMPGE(null);
		instructionHandlerManager.registerBranchInstruction(bi, id);
		return bi;
	}

	@SuppressWarnings("unused")
	// Called using reflection
	private Instruction createInstructionIf_icmple(Element inst)
	{
		int id= Integer.parseInt(inst.getAttributeValue("label"));
		BranchInstruction bi= new IF_ICMPLE(null);
		instructionHandlerManager.registerBranchInstruction(bi, id);
		return bi;
	}

	@SuppressWarnings("unused")
	// Called using reflection
	private Instruction createInstructionIf_icmpne(Element inst)
	{
		int id= Integer.parseInt(inst.getAttributeValue("label"));
		BranchInstruction bi= new IF_ICMPNE(null);
		instructionHandlerManager.registerBranchInstruction(bi, id);
		return bi;
	}

	@SuppressWarnings("unused")
	// Called using reflection
	private Instruction createInstructionIfeq(Element inst)
	{
		int id= Integer.parseInt(inst.getAttributeValue("label"));
		BranchInstruction bi= new IFEQ(null);
		instructionHandlerManager.registerBranchInstruction(bi, id);
		return bi;
	}

	@SuppressWarnings("unused")
	// Called using reflection
	private Instruction createInstructionIfne(Element inst)
	{
		int id= Integer.parseInt(inst.getAttributeValue("label"));
		BranchInstruction bi= new IFNE(null);
		instructionHandlerManager.registerBranchInstruction(bi, id);
		return bi;
	}

	@SuppressWarnings("unused")
	// Called using reflection
	private Instruction createInstructionAconst_null(Element inst) throws IllegalXMLVMException
	{
		return new ACONST_NULL();
	}

	@SuppressWarnings("unused")
	// Called using reflection
	private Instruction createInstructionAload(Element inst) throws IllegalXMLVMException
	{
		String t= inst.getAttributeValue("type");
		Type type= parseTypeString(t);
		int idx= Integer.parseInt(inst.getAttributeValue("index"));
		return factory.createLoad(type, idx);
	}

	@SuppressWarnings("unused")
	// Called using reflection
	private Instruction createInstructionInvokespecial(Element inst) throws IllegalXMLVMException
	{
		return createInvokeInstruction(inst, Constants.INVOKESPECIAL);
	}

	@SuppressWarnings("unused")
	// Called using reflection
	private Instruction createInstructionInvokevirtual(Element inst) throws IllegalXMLVMException
	{
		return createInvokeInstruction(inst, Constants.INVOKEVIRTUAL);
	}

	@SuppressWarnings("unused")
	// Called using reflection
	private Instruction createInstructionInvokestatic(Element inst) throws IllegalXMLVMException
	{
		return createInvokeInstruction(inst, Constants.INVOKESTATIC);
	}

	private Instruction createInvokeInstruction(Element inst, short kind) throws IllegalXMLVMException
	{
		String classType= inst.getAttributeValue("class-type");
		String methodName= inst.getAttributeValue("method");
		Element signature= inst.getChild("signature", nsXMLVM);
		Type retType= collectReturnType(signature);
		Type[] argTypes= collectArgumentTypes(signature);
		return factory.createInvoke(classType, methodName, retType, argTypes, kind);
	}

	@SuppressWarnings("unused")
	// Called using reflection
	private Instruction createInstructionReturn(Element inst)
	{
		return factory.createReturn(Type.VOID);
	}

	@SuppressWarnings("unused")
	// Called using reflection
	private Instruction createInstructionIreturn(Element inst)
	{
		return factory.createReturn(Type.INT);
	}

	@SuppressWarnings("unused")
	// Called using reflection
	private Instruction createInstructionDreturn(Element inst)
	{
		return factory.createReturn(Type.DOUBLE);
	}

	@SuppressWarnings("unused")
	// Called using reflection
	private Instruction createInstructionFreturn(Element inst)
	{
		return factory.createReturn(Type.FLOAT);
	}

	@SuppressWarnings("unused")
	// Called using reflection
	private Instruction createInstructionLreturn(Element inst)
	{
		return factory.createReturn(Type.LONG);
	}

	@SuppressWarnings("unused")
	// Called using reflection
	private Instruction createInstructionGetstatic(Element inst) throws IllegalXMLVMException
	{
		String classType= inst.getAttributeValue("class-type");
		String field= inst.getAttributeValue("field");
		Type type= parseTypeString(inst.getAttributeValue("type"));
		return factory.createFieldAccess(classType, field, type, Constants.GETSTATIC);
	}

	@SuppressWarnings("unused")
	// Called using reflection
	private CompoundInstruction createInstructionLdc(Element inst) throws IllegalXMLVMException
	{
		return createInstructionPush(inst);
	}

	@SuppressWarnings("unused")
	// Called using reflection
	private CompoundInstruction createInstructionLdc2_w(Element inst) throws IllegalXMLVMException
	{
		return createInstructionPush(inst);
	}

	private CompoundInstruction createInstructionPush(Element inst) throws IllegalXMLVMException
	{
		String t= inst.getAttributeValue("type");
		Type type= parseTypeString(t);
		String value= inst.getAttributeValue("value");
		if (type == Type.STRING)
			return new PUSH(constantPoolGen, value);
		else if (type == Type.INT)
			return new PUSH(constantPoolGen, Integer.parseInt(value));
		else if (type == Type.FLOAT)
			return new PUSH(constantPoolGen, Float.parseFloat(value));
		else if (type == Type.DOUBLE)
			return new PUSH(constantPoolGen, Double.parseDouble(value));
		else if (type == Type.LONG)
			return new PUSH(constantPoolGen, Long.parseLong(value));
		else
			throw new IllegalXMLVMException(inst.getName() + " with bad type '" + t + "'");
	}

	@SuppressWarnings("unused")
	// Called using reflection
	private Instruction createInstructionIconst(Element inst)
	{
		int value= Integer.parseInt(inst.getAttributeValue("value"));
		return new ICONST(value);
	}

	@SuppressWarnings("unused")
	// Called using reflection
	private Instruction createInstructionIinc(Element inst)
	{
		int index= Integer.parseInt(inst.getAttributeValue("index"));
		int incr= Integer.parseInt(inst.getAttributeValue("incr"));
		return new IINC(index, incr);
	}

	@SuppressWarnings("unused")
	// Called using reflection
	private Instruction createInstructionBipush(Element inst)
	{
		byte val= (byte) Integer.parseInt(inst.getAttributeValue("value"));
		return new BIPUSH(val);
	}

	@SuppressWarnings("unused")
	// Called using reflection
	private Instruction createInstructionIstore(Element inst) throws IllegalXMLVMException
	{
		int idx= Integer.parseInt(inst.getAttributeValue("index"));
		return new ISTORE(idx);
	}

	@SuppressWarnings("unused")
	// Called using reflection
	private Instruction createInstructionLstore(Element inst) throws IllegalXMLVMException
	{
		// BCEL's LSTORE creation instruction only takes an integer!!!
		int idx= Integer.parseInt(inst.getAttributeValue("index"));
		return new LSTORE(idx);
	}

	@SuppressWarnings("unused")
	// Called using reflection
	private Instruction createInstructionIload(Element inst) throws IllegalXMLVMException
	{
		int idx= Integer.parseInt(inst.getAttributeValue("index"));
		return new ILOAD(idx);
	}

	@SuppressWarnings("unused")
	// Called using reflection
	private Instruction createInstructionLload(Element inst) throws IllegalXMLVMException
	{
		int idx= Integer.parseInt(inst.getAttributeValue("index"));
		return new LLOAD(idx);
	}

	@SuppressWarnings("unused")
	// Called using reflection
	private Instruction createInstructionAstore(Element inst)
	{
		int idx= Integer.parseInt(inst.getAttributeValue("index"));
		return new ASTORE(idx);
	}

	@SuppressWarnings("unused")
	// Called using reflection
	private Instruction createInstructionFstore(Element inst) throws IllegalXMLVMException
	{
		int idx= Integer.parseInt(inst.getAttributeValue("index"));
		return new FSTORE(idx);
	}

	@SuppressWarnings("unused")
	// Called using reflection
	private Instruction createInstructionFload(Element inst) throws IllegalXMLVMException
	{
		int idx= Integer.parseInt(inst.getAttributeValue("index"));
		return new FLOAD(idx);
	}

	@SuppressWarnings("unused")
	// Called using reflection
	private Instruction createInstructionDstore(Element inst) throws IllegalXMLVMException
	{
		int idx= Integer.parseInt(inst.getAttributeValue("index"));
		return new DSTORE(idx);
	}

	@SuppressWarnings("unused")
	// Called using reflection
	private Instruction createInstructionDload(Element inst) throws IllegalXMLVMException
	{
		int idx= Integer.parseInt(inst.getAttributeValue("index"));
		return new DLOAD(idx);
	}

	@SuppressWarnings("unused")
	// Called using reflection
	private Instruction createInstructionIadd(Element inst) throws IllegalXMLVMException
	{
		return new IADD();
	}

	@SuppressWarnings("unused")
	// Called using reflection
	private Instruction createInstructionLadd(Element inst) throws IllegalXMLVMException
	{
		return new LADD();
	}

	@SuppressWarnings("unused")
	// Called using reflection
	private Instruction createInstructionIsub(Element inst) throws IllegalXMLVMException
	{
		return new ISUB();
	}

	@SuppressWarnings("unused")
	// Called using reflection
	private Instruction createInstructionDsub(Element inst) throws IllegalXMLVMException
	{
		return new DSUB();
	}

	@SuppressWarnings("unused")
	// Called using reflection
	private Instruction createInstructionAaload(Element inst) throws IllegalXMLVMException
	{
		return new AALOAD();
	}

	@SuppressWarnings("unused")
	// Called using reflection
	private Instruction createInstructionDadd(Element inst) throws IllegalXMLVMException
	{
		return new DADD();
	}

	@SuppressWarnings("unused")
	// Called using reflection
	private Instruction createInstructionFadd(Element inst) throws IllegalXMLVMException
	{
		return new FADD();
	}

	@SuppressWarnings("unused")
	// Called using reflection
	private Instruction createInstructionImul(Element inst) throws IllegalXMLVMException
	{
		return new IMUL();
	}

	@SuppressWarnings("unused")
	// Called using reflection
	private Instruction createInstructionDmul(Element inst) throws IllegalXMLVMException
	{
		return new DMUL();
	}

	@SuppressWarnings("unused")
	// Called using reflection
	private Instruction createInstructionIdiv(Element inst) throws IllegalXMLVMException
	{
		return new IDIV();
	}

	@SuppressWarnings("unused")
	// Called using reflection
	private Instruction createInstructionDdiv(Element inst) throws IllegalXMLVMException
	{
		return new DDIV();
	}

	@SuppressWarnings("unused")
	// Called using reflection
	private Instruction createInstructionIrem(Element inst) throws IllegalXMLVMException
	{
		return new IREM();
	}

	@SuppressWarnings("unused")
	// Called using reflection
	private Instruction createInstructionNew(Element inst)
	{
		String t= inst.getAttributeValue("type");
		return factory.createNew(t);
	}

	@SuppressWarnings("unused")
	// Called using reflection
	private Instruction createInstructionPutfield(Element inst) throws IllegalXMLVMException
	{
		String classType= inst.getAttributeValue("class-type");
		String field= inst.getAttributeValue("field");
		String type= inst.getAttributeValue("type");
		Type t= parseTypeString(type);
		return factory.createPutField(classType, field, t);
	}

	@SuppressWarnings("unused")
	// Called using reflection
	private Instruction createInstructionPutstatic(Element inst) throws IllegalXMLVMException
	{
		String classType= inst.getAttributeValue("class-type");
		String field= inst.getAttributeValue("field");
		String type= inst.getAttributeValue("type");
		Type t= parseTypeString(type);
		return factory.createPutStatic(classType, field, t);
	}

	@SuppressWarnings("unused")
	// Called using reflection
	private Instruction createInstructionPop(Element inst) throws IllegalXMLVMException
	{
		return new POP();
	}

	@SuppressWarnings("unused")
	// Called using reflection
	private Instruction createInstructionGetfield(Element inst) throws IllegalXMLVMException
	{
		String classType= inst.getAttributeValue("class-type");
		String field= inst.getAttributeValue("field");
		String type= inst.getAttributeValue("type");
		Type t= parseTypeString(type);
		return factory.createGetField(classType, field, t);
	}

	@SuppressWarnings("unused")
	// Called using reflection
	private Instruction createInstructionAreturn(Element inst)
	{
		return new ARETURN();
	}

	@SuppressWarnings("unused")
	// Called using reflection
	private Instruction createInstructionAnewarray(Element inst)
	{
		String classType= inst.getAttributeValue("elementType");
		return new ANEWARRAY(constantPoolGen.addClass(classType));
	}

	@SuppressWarnings("unused")
	// Called using reflection
	private Instruction createInstructionAastore(Element inst)
	{
		return new AASTORE();
	}

	@SuppressWarnings("unused")
	// Called using reflection
	private Instruction createInstructionNop(Element inst)
	{
		return new NOP();
	}

	@SuppressWarnings("unused")
	// Called using reflection
	private Instruction createInstructionI2b(Element inst)
	{
		return new I2B();
	}

	@SuppressWarnings("unused")
	// Called using reflection
	private Instruction createInstructionI2l(Element inst)
	{
		return new I2L();
	}

	@SuppressWarnings("unused")
	// Called using reflection
	private Instruction createInstructionL2i(Element inst)
	{
		return new L2I();
	}
}
