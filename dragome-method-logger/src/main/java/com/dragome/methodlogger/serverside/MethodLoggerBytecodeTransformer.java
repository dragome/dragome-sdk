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
package com.dragome.methodlogger.serverside;

import java.util.Set;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import com.dragome.commons.compiler.BytecodeTransformer;

public class MethodLoggerBytecodeTransformer implements BytecodeTransformer
{
	protected Set<String> includePathsList;
	private boolean enabled;

	public MethodLoggerBytecodeTransformer(Set<String> includePathsList, boolean enabled)
	{
		this.includePathsList= includePathsList;
		this.enabled= enabled;
	}

	public boolean requiresTransformation(String className)
	{
		if (!enabled)
			return false;

		for (String path : includePathsList)
			if (className.startsWith(path))
				return true;

		return false;
	}

	public byte[] transform(String className, byte[] bytecode)
	{
		if (requiresTransformation(className))
		{
			ClassNode classNode= new ClassNode(Opcodes.ASM5);
			MethodLoggerAdapter invokeDynamicConverter= new MethodLoggerAdapter(classNode);
			new ClassReader(bytecode).accept(invokeDynamicConverter, ClassReader.EXPAND_FRAMES);
			ClassWriter cw= new ClassWriter(ClassWriter.COMPUTE_MAXS);
			classNode.accept(cw);
			return cw.toByteArray();
		}
		else
			return bytecode;
	}
}
