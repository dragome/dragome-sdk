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

import java.util.concurrent.Executor;

import com.dragome.commons.compiler.BytecodeTransformer;
import com.dragome.commons.compiler.annotations.CompilerType;

@DragomeConfiguratorImplementor
public class DefaultDragomeConfigurator implements DragomeConfigurator
{
	protected String dragomeVersion="0.95.1-beta1";
	private CompilerType defaultCompilerType= CompilerType.Standard;

	public ClassLoader getNewClassloaderInstance(ClassLoader parent, ClassLoader current)
	{
		return current;
	}

	public BytecodeTransformer getBytecodeTransformer()
	{
		return null;
	}

	public ExecutionHandler getExecutionHandler()
	{
		return new ExecutionHandler()
		{
			public void suspendExecution()
			{
			}

			public boolean canSuspend()
			{
				return false;
			}

			public Executor getExecutor()
			{
				return new Executor()
				{
					public void execute(Runnable command)
					{
						command.run();
					}
				};
			}

			public void continueExecution()
			{
			}
		};
	}

	public boolean filterClassPath(String classpathEntry)
	{
		boolean include= false || //
		        classpathEntry.contains("dragome-js-jre-"+dragomeVersion+".jar") || //
		        classpathEntry.contains("dragome-js-commons-"+dragomeVersion+".jar") || //
		        classpathEntry.contains("dragome-core-"+dragomeVersion+".jar") || //
		        classpathEntry.contains("dragome-guia-"+dragomeVersion+".jar") || //
		        classpathEntry.contains("dragome-form-bindings-"+dragomeVersion+".jar") || //
		        classpathEntry.contains("dragome-method-logger-"+dragomeVersion+".jar") //
		;

		return include;
	}

	public CompilerType getDefaultCompilerType()
	{
	    return defaultCompilerType;
	}

	public void setDefaultCompilerType(CompilerType defaultCompilerType)
	{
	    this.defaultCompilerType = defaultCompilerType;
	}
}
