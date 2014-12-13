/*
 * Copyright (c) 2011-2014 Fernando Petrola
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dragome.methodlogger.serverside;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;

public class MethodLoggerAdapter extends ClassVisitor
{
	private String className;

	public MethodLoggerAdapter(ClassVisitor cv)
	{
		super(Opcodes.ASM5, cv);
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
	{
		MethodVisitor mv;
		mv= cv.visitMethod(access, name, desc, signature, exceptions);
		mv= new MethodReturnAdapter(Opcodes.ASM5, className, access, name, desc, mv);
		return mv;
	}

	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces)
	{
		className= name;
		super.visit(version, access, name, signature, superName, interfaces);
	}

}

class MethodReturnAdapter extends AdviceAdapter
{
	private String name;
	private String owner;
	protected Label l0;

	public MethodReturnAdapter(int api, String owner, int access, String name, String desc, MethodVisitor mv)
	{
		super(Opcodes.ASM5, mv, access, name, desc);
		this.owner= owner;
		this.name= name;
	}

	public MethodReturnAdapter(MethodVisitor mv, int access, String name, String desc)
	{
		super(Opcodes.ASM5, mv, access, name, desc);
		this.name= name;
	}

	protected void onMethodEnter()
	{
		if (!isStatic())
		{
			l0= new Label();
			super.visitLabel(l0);

			super.visitVarInsn(Opcodes.ALOAD, 0);
			super.visitLdcInsn(name);
			super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/dragome/methodlogger/enhancers/MethodInvocationLogger", "onMethodEnter", "(Ljava/lang/Object;Ljava/lang/String;)V", false);
		}
		super.onMethodEnter();
	}

	public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index)
	{
		if (!isStatic())
			start= l0;
		
		super.visitLocalVariable(name, desc, signature, start, end, index);
	}

	private boolean isStatic()
	{
		return (methodAccess & Opcodes.ACC_STATIC) != 0 || name.equals("<init>");
	}

	protected void onMethodExit(int opcode)
	{
		if (!isStatic())
		{
			super.visitVarInsn(Opcodes.ALOAD, 0);
			super.visitLdcInsn(name);
			super.visitMethodInsn(Opcodes.INVOKESTATIC, "com/dragome/methodlogger/enhancers/MethodInvocationLogger", "onMethodExit", "(Ljava/lang/Object;Ljava/lang/String;)V", false);
		}
	}
}