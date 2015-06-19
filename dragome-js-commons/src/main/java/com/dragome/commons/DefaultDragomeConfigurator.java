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

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import com.dragome.commons.compiler.BytecodeTransformer;
import com.dragome.commons.compiler.annotations.CompilerType;

@DragomeConfiguratorImplementor
public class DefaultDragomeConfigurator implements DragomeConfigurator
{
	private CompilerType defaultCompilerType= CompilerType.Standard;
	private FileFilter classpathFilter;

	public ClassLoader getNewClassloaderInstance(ClassLoader parent, ClassLoader current)
	{
		return current;
	}

	public BytecodeTransformer getBytecodeTransformer()
	{
		return new BytecodeTransformer()
		{
			public byte[] transform(String className, byte[] bytecode)
			{
				return bytecode;
			}

			public boolean requiresTransformation(String className)
			{
				return false;
			}
		};
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
				classpathEntry.contains("dragome-js-jre-") || //
				classpathEntry.contains("dragome-js-commons-") || //
				classpathEntry.contains("dragome-core-") || //
				classpathEntry.contains("dragome-guia-") || //
				classpathEntry.contains("dragome-form-bindings-") || //
				classpathEntry.contains("dragome-method-logger-") || //
				classpathEntry.contains("dragome-web-");

		return include;
	}

	public CompilerType getDefaultCompilerType()
	{
		return defaultCompilerType;
	}

	public void setDefaultCompilerType(CompilerType defaultCompilerType)
	{
		this.defaultCompilerType= defaultCompilerType;
	}

	public FileFilter getClasspathFilter()
	{
		return classpathFilter;
	}

	public void setClasspathFilter(FileFilter classpathFilter)
	{
		this.classpathFilter= classpathFilter;
	}

	public boolean isCheckingCast()
	{
		return true;
	}

	public List<File> getExtraClasspath(String classpath)
	{
		return new ArrayList<File>();
	}
}
