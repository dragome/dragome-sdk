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
package com.dragome.helpers.serverside;

import java.io.File;
import java.io.FileFilter;

import com.dragome.commons.DragomeConfigurator;
import com.dragome.commons.compiler.BytecodeToJavascriptCompiler;
import com.dragome.commons.compiler.BytecodeToJavascriptCompilerConfiguration;
import com.dragome.commons.compiler.BytecodeTransformer;
import com.dragome.commons.compiler.annotations.CompilerType;
import com.dragome.debugging.execution.VisualActivity;
import com.dragome.services.ServiceLocator;

public class DragomeCompilerLauncher
{
	public static void compileWithMainClass(String classPath, String target)
	{
		DragomeConfigurator configurator= ServiceLocator.getInstance().getConfigurator();
		String mainClassName= VisualActivity.class.getName();
		CompilerType defaultCompilerType= configurator.getDefaultCompilerType();
		BytecodeTransformer bytecodeTransformer= configurator.getBytecodeTransformer();
		FileFilter classpathFilter= new FileFilter()
		{
			public boolean accept(File pathname)
			{
				return !pathname.toString().contains(File.separator + "serverside");
			}
		};
		
		BytecodeToJavascriptCompiler bytecodeToJavascriptCompiler= ServiceLocator.getInstance().getBytecodeToJavascriptCompiler();
		
		BytecodeToJavascriptCompilerConfiguration compilerConfiguration= new BytecodeToJavascriptCompilerConfiguration(classPath, target, mainClassName, defaultCompilerType, bytecodeTransformer, classpathFilter);
		bytecodeToJavascriptCompiler.configure(compilerConfiguration);
		bytecodeToJavascriptCompiler.compile();
	}
}
