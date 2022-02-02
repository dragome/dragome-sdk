/*******************************************************************************
 * Copyright (c) 2011-2014 Fernando Petrola
 * 
 * This file is part of Dragome SDK.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/

package com.dragome.compiler.invokedynamic.serverside;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;

import java.util.function.Predicate;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;

import com.dragome.commons.compiler.BytecodeTransformer;

public class InvokeDynamicBackporter implements BytecodeTransformer {
	private Predicate<String> requiredChecker;

	public InvokeDynamicBackporter(Predicate<String> requiredChecker) {
		this.requiredChecker = requiredChecker;
	}

	public static byte[] transform(byte[] bytecode) {
		ClassNode classNode = new ClassNode(Opcodes.ASM5);
		InvokeDynamicConverter invokeDynamicConverter = new InvokeDynamicConverter(classNode);
		new ClassReader(bytecode).accept(invokeDynamicConverter, 0);
		ClassWriter cw = new ClassWriterExtension(ClassWriter.COMPUTE_MAXS);
		classNode.accept(cw);
		return cw.toByteArray();
	}

	private static class InvokeDynamicConverter extends ClassVisitor {
		private int classAccess;
		private String className;

		public InvokeDynamicConverter(ClassVisitor next) {
			super(Opcodes.ASM5, next);
		}

		public void visit(int version, int access, String name, String signature, String superName,
				String[] interfaces) {
			super.visit(version, access, name, signature, superName, interfaces);
			this.classAccess = access;
			this.className = name;
		}

		public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
			if (isBridgeMethodOnInterface(access)) {
				return null;
			}
			if (isNonAbstractMethodOnInterface(access) && !isClassInitializerMethod(name, desc, access)) {
				// System.out.println("WARNING: Method '" + name + "' of interface '" +
				// className + "' is non-abstract! " + "This will probably fail to run on Java 7
				// and below. " + "If you get this warning _without_ using Java 8's default
				// methods, " + "please report a bug at
				// https://github.com/orfjackal/retrolambda/issues " + "together with an SSCCE
				// (http://www.sscce.org/)");
			}
			MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
			return new InvokeDynamicInsnConvertingMethodVisitor(api, mv, className);
		}

		private boolean isBridgeMethodOnInterface(int methodAccess) {
			return Flags.hasFlag(classAccess, Opcodes.ACC_INTERFACE) && Flags.hasFlag(methodAccess, Opcodes.ACC_BRIDGE);
		}

		private boolean isNonAbstractMethodOnInterface(int methodAccess) {
			return Flags.hasFlag(classAccess, Opcodes.ACC_INTERFACE)
					&& !Flags.hasFlag(methodAccess, Opcodes.ACC_ABSTRACT);
		}

		private static boolean isClassInitializerMethod(String name, String desc, int methodAccess) {
			return name.equals("<clinit>") && desc.equals("()V") && Flags.hasFlag(methodAccess, Opcodes.ACC_STATIC);
		}
	}

	private static class InvokeDynamicInsnConvertingMethodVisitor extends MethodVisitor {
		private final String myClassName;

		public InvokeDynamicInsnConvertingMethodVisitor(int api, MethodVisitor mv, String myClassName) {
			super(api, mv);
			this.myClassName = myClassName;
		}

		public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs) {
			backportLambda(name, Type.getType(desc), bsm, bsmArgs);
		}

		private void backportLambda(String invokedName, Type invokedType, Handle bsm, Object[] bsmArgs) {
			Type[] argumentTypes = Type.getArgumentTypes(invokedType.toString());
			Type returnType = Type.getReturnType(invokedType.toString());
			String returnTypeName = returnType.getClassName();

			int length = argumentTypes.length;

			createArrayWithParameters(length, argumentTypes);

			this.visitLdcInsn(myClassName);
			this.visitLdcInsn(invokedName);
			this.visitLdcInsn(returnTypeName);
			this.visitLdcInsn(invokedType.toString());
			this.visitLdcInsn(bsmArgs[1].toString());

			this.visitVarInsn(Opcodes.ALOAD, 20);
			this.visitLdcInsn(bsm.getTag() == 5 ? "virtual" : "static");

			String runnableSignature = "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/Object;";
			this.visitMethodInsn(INVOKESTATIC, "com/dragome/utils/DragomeCallsiteFactory", "create", runnableSignature,
					false);
		}

		private void createArrayWithParameters(int parametersCount, Type[] argumentTypes) {
			this.visitIntInsn(Opcodes.BIPUSH, parametersCount);
			this.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/Object");
			this.visitVarInsn(Opcodes.ASTORE, 20);

			for (int i = parametersCount - 1; i >= 0; i--) {
				convertPrimitive(argumentTypes[i], i);
				this.visitVarInsn(Opcodes.ASTORE, 21);
				this.visitVarInsn(Opcodes.ALOAD, 20);
				this.visitIntInsn(Opcodes.BIPUSH, i);
				this.visitVarInsn(Opcodes.ALOAD, 21);
				this.visitInsn(Opcodes.AASTORE);
			}
		}

		private void convertPrimitive(Object tp, int i) {
			if (tp.equals(Type.BOOLEAN_TYPE)) {
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;",
						false);
			} else if (tp.equals(Type.BYTE_TYPE)) {
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;", false);
			} else if (tp.equals(Type.CHAR_TYPE)) {
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;",
						false);
			} else if (tp.equals(Type.SHORT_TYPE)) {
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;", false);
			} else if (tp.equals(Type.INT_TYPE)) {
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;",
						false);
			} else if (tp.equals(Type.LONG_TYPE)) {
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
				i++;
			} else if (tp.equals(Type.FLOAT_TYPE)) {
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
			} else if (tp.equals(Type.DOUBLE_TYPE)) {
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
				i++;
			}
			// else
			// mv.visitVarInsn(Opcodes.ALOAD, i);
		}
	}

	public byte[] transform(String className, byte[] bytecode) {
		return transform(bytecode);
	}

	public boolean requiresTransformation(String className) {
		if (System.getProperty("invokeDynamicBackporter") != null)
			return true;
		else
			return requiredChecker.test(className);
	}
}
