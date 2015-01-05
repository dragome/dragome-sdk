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
