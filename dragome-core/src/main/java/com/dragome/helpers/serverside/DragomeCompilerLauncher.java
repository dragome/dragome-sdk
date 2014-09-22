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
import com.dragome.compiler.DragomeJsCompiler;
import com.dragome.compiler.Project;
import com.dragome.compiler.writer.Assembly;
import com.dragome.debugging.execution.DragomeVisualActivity;
import com.dragome.services.ServiceLocator;

public class DragomeCompilerLauncher
{
	static Class<DragomeVisualActivity> mainClass= DragomeVisualActivity.class;

	public static void compileWithMainClass(String classPath, String target)
	{
		try
		{
			Project.singleton= null;

			String classpathElements= classPath;
			Assembly assembly= new Assembly();
			assembly.setEntryPointClassName(mainClass.getName());
			assembly.setTargetLocation(new File(target));

			DragomeConfigurator configurator= ServiceLocator.getInstance().getConfigurator();

			DragomeJsCompiler compiler= new DragomeJsCompiler(configurator.getDefaultCompilerType());
			//	    compiler.setBasedir(basedir);
			compiler.addClasspathElements(classpathElements);
			compiler.addClasspathFilter(new FileFilter()
			{
				public boolean accept(File pathname)
				{
					return !pathname.toString().contains(File.separator + "serverside");
				}
			});

			if (configurator != null)
				compiler.setBytecodeTransformer(configurator.getBytecodeTransformer());

			compiler.addAssembly(assembly);
			compiler.setGenerateLineNumbers(false);
			compiler.setCompression(false);
			compiler.execute();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
