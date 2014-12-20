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
package com.dragome.commons;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.dragome.commons.compiler.BytecodeTransformer;

public class ChainedInstrumentationDragomeConfigurator extends InstrumentationDragomeConfigurator
{
	protected List<InstrumentationDragomeConfigurator> configurators= new ArrayList<InstrumentationDragomeConfigurator>();

	public ChainedInstrumentationDragomeConfigurator()
	{
	}

	public void init(InstrumentationDragomeConfigurator... configurators)
	{
		this.configurators= Arrays.asList(configurators);

		for (InstrumentationDragomeConfigurator configurator : this.configurators)
			if (configurator.isEnabled())
			{
				includedPaths.addAll(configurator.getIncludedPaths());
				loadedFromParent.addAll(configurator.getLoadedFromParent());
			}
	}

	public BytecodeTransformer getBytecodeTransformer()
	{
		return new BytecodeTransformer()
		{
			public byte[] transform(String className, byte[] bytecode)
			{
				byte[] transformedBytecode= bytecode;

				for (InstrumentationDragomeConfigurator configurator : configurators)
					if (configurator.isEnabled())
						transformedBytecode= configurator.getBytecodeTransformer().transform(className, transformedBytecode);

				return transformedBytecode;
			}

			public boolean requiresTransformation(String className)
			{
				boolean transform= false;
				for (InstrumentationDragomeConfigurator configurator : configurators)
					if (configurator.isEnabled())
						transform|= configurator.getBytecodeTransformer().requiresTransformation(className);

				return transform;
			}
		};
	}

	public boolean filterClassPath(String classpathEntry)
	{
		boolean result= false;
		for (InstrumentationDragomeConfigurator configurator : configurators)
			result|= configurator.filterClassPath(classpathEntry);

		return result;
	}
}
