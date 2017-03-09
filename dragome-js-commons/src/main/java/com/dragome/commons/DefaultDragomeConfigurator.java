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
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;

import com.dragome.commons.compiler.BytecodeTransformer;
import com.dragome.commons.compiler.annotations.CompilerType;
import com.dragome.commons.compiler.classpath.Classpath;
import com.dragome.commons.compiler.classpath.ClasspathEntry;
import com.dragome.commons.compiler.classpath.ClasspathFileFilter;

@DragomeConfiguratorImplementor
public class DefaultDragomeConfigurator implements DragomeConfigurator
{
	private CompilerType defaultCompilerType= CompilerType.Standard;
	private ClasspathFileFilter classpathFilter;
	private boolean removeUnusedCode;
	private boolean obfuscateCode;

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
		boolean include= false;
		include|= classpathEntry.contains("WEB-INF" + File.separatorChar + "classes");
		include|= addClassesFolder(classpathEntry, "dragome-js-commons");
		include|= addClassesFolder(classpathEntry, "dragome-w3c-standards");
		include|= addClassesFolder(classpathEntry, "dragome-js-jre");
		include|= addClassesFolder(classpathEntry, "dragome-core");
		include|= addClassesFolder(classpathEntry, "dragome-web");
		include|= addClassesFolder(classpathEntry, "dragome-guia");
		include|= addClassesFolder(classpathEntry, "dragome-form-bindings");
		include|= addClassesFolder(classpathEntry, "dragome-method-logger");
		return include;
	}

	private boolean addClassesFolder(String classpathEntry, String module)
	{
		boolean include= false;
		if (classpathEntry.contains(module))
		{
			include|= classpathEntry.contains(module + "-");
			include|= classpathEntry.contains(File.separatorChar + "bin");
			include|= classpathEntry.contains(File.separatorChar + "classes");
		}
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

	public ClasspathFileFilter getClasspathFilter()
	{
		return classpathFilter;
	}

	public void setClasspathFilter(ClasspathFileFilter classpathFilter)
	{
		this.classpathFilter= classpathFilter;
	}

	public boolean isCheckingCast()
	{
		return true;
	}

	public List<ClasspathEntry> getExtraClasspath(Classpath classpath)
	{
		return new ArrayList<ClasspathEntry>();
	}

	public boolean isRemoveUnusedCode()
	{
		return removeUnusedCode;
	}

	public boolean isObfuscateCode()
	{
		return obfuscateCode;
	}

	public void sortClassPath(Classpath classPath)
	{
	}

	public List<URL> getAdditionalCodeKeepConfigFile()
	{
		return new ArrayList<URL>();
	}

	public List<URL> getAdditionalObfuscateCodeKeepConfigFile()
	{
		return new ArrayList<URL>();
	}

	public boolean isCaching() {
		return true;
	}

	public String getCompiledPath()
	{
		return null;
	}

	public boolean isFailOnError()
	{
		return false;
	}
}
