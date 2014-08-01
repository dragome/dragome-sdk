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
