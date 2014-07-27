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
package com.dragome.callbackevictor.serverside;

import java.util.Set;

import com.dragome.callbackevictor.serverside.bytecode.transformation.asm.AsmClassTransformer;
import com.dragome.commons.compiler.BytecodeTransformer;

public class ContinuationBytecodeTransformer implements BytecodeTransformer
{
	protected Set<String> includePathsList;
	private boolean enabled;

	public ContinuationBytecodeTransformer(Set<String> includePathsList, boolean enabled)
    {
	    this.includePathsList= includePathsList;
		this.enabled= enabled;
    }

	public byte[] transform(String className, byte[] originalByteArray)
	{
		byte[] transformedArray;

		if (requiresTransformation(className))
		{
			AsmClassTransformer asmClassTransformer= new AsmClassTransformer();
			transformedArray= asmClassTransformer.transform(originalByteArray);
		}
		else
		{
			transformedArray= originalByteArray;
		}

		return transformedArray;
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
}
