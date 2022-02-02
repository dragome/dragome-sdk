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
package com.dragome.methodlogger;

import java.util.Arrays;
import java.util.function.Predicate;

import com.dragome.commons.InstrumentationDragomeConfigurator;
import com.dragome.commons.compiler.BytecodeTransformer;
import com.dragome.compiler.invokedynamic.serverside.InvokeDynamicBackporter;

public class InvokeDynamicBackporterConfigurator extends InstrumentationDragomeConfigurator
{
	private Predicate<String> requiredChecker;

	public InvokeDynamicBackporterConfigurator(Predicate<String> requiredChecker)
	{
		this.requiredChecker = requiredChecker;
	}

	public InvokeDynamicBackporterConfigurator(String... includedPaths)
	{
		this(s-> true);
		this.includedPaths.addAll(Arrays.asList(includedPaths));
	}

	public BytecodeTransformer getBytecodeTransformer()
	{
		return new InvokeDynamicBackporter(requiredChecker);
	}

	public boolean filterClassPath(String classpathEntry)
	{
		return super.filterClassPath(classpathEntry) || classpathEntry.contains("dragome-callback-evictor-");
	}
}
