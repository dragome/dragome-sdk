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
package com.dragome.methodlogger;

import java.util.Arrays;

import com.dragome.commons.InstrumentationDragomeConfigurator;
import com.dragome.commons.compiler.BytecodeTransformer;
import com.dragome.methodlogger.serverside.MethodLoggerBytecodeTransformer;

public class MethodLoggerConfigurator extends InstrumentationDragomeConfigurator
{
	public MethodLoggerConfigurator()
	{
		loadedFromParent.addAll(Arrays.asList("org.atmosphere", "com.dragome.commons.ProxyRelatedInvocationHandler", "java.", "javax.", "net.sf.saxon"));
	}

	public MethodLoggerConfigurator(String... includedPaths)
	{
		this();
		this.includedPaths.addAll(Arrays.asList(includedPaths));
	}

	public BytecodeTransformer getBytecodeTransformer()
	{
		return new MethodLoggerBytecodeTransformer(includedPaths, enabled);
	}

	public boolean filterClassPath(String classpathEntry)
	{
		return super.filterClassPath(classpathEntry) || classpathEntry.contains("dragome-method-logger-" + dragomeVersion + ".jar");
	}
}
