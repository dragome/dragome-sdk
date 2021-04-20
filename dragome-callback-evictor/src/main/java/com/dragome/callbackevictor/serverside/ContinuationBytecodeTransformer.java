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
package com.dragome.callbackevictor.serverside;

import java.util.Set;

import com.dragome.callbackevictor.serverside.javaflow.providers.asmx.AsmxResourceTransformationFactory;
import com.dragome.callbackevictor.serverside.javaflow.spi.ClasspathResourceLoader;
import com.dragome.callbackevictor.serverside.javaflow.spi.ResourceTransformer;
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
			ResourceTransformer asmClassTransformer= new AsmxResourceTransformationFactory().createTransformer(new ClasspathResourceLoader(getClass().getClassLoader()));
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
