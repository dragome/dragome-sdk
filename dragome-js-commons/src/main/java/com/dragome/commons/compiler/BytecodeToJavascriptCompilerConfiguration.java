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
package com.dragome.commons.compiler;

import com.dragome.commons.compiler.annotations.CompilerType;
import com.dragome.commons.compiler.classpath.Classpath;
import com.dragome.commons.compiler.classpath.ClasspathFileFilter;

public class BytecodeToJavascriptCompilerConfiguration
{
	private Classpath classPath;
	private String targetDir;
	private String mainClassName;
	private CompilerType compilerType;
	private BytecodeTransformer bytecodeTransformer;
	private ClasspathFileFilter classpathFilter;
	private boolean checkingCast;
	private boolean isCaching;
	private boolean failOnError;

	public BytecodeToJavascriptCompilerConfiguration(Classpath classPath, String targetDir, String mainClassName, CompilerType compilerType, BytecodeTransformer bytecodeTransformer, ClasspathFileFilter classpathFilter, boolean isCheckingCast, boolean isCaching, boolean failOnError)
	{
		this.classPath= classPath;
		this.targetDir= targetDir;
		this.mainClassName= mainClassName;
		this.compilerType= compilerType;
		this.bytecodeTransformer= bytecodeTransformer;
		this.classpathFilter= classpathFilter;
		this.checkingCast= isCheckingCast;
		this.isCaching = isCaching;
		this.failOnError= failOnError;
	}

	public Classpath getClasspath()
	{
		return classPath;
	}

	public String getTargetDir()
	{
		return targetDir;
	}

	public String getMainClassName()
	{
		return mainClassName;
	}

	public CompilerType getCompilerType()
	{
		return compilerType;
	}

	public BytecodeTransformer getBytecodeTransformer()
	{
		return bytecodeTransformer;
	}

	public ClasspathFileFilter getClasspathFilter()
	{
		return classpathFilter;
	}

	public boolean isCheckingCast()
	{
		return checkingCast;
	}
	
	public boolean isCaching()
	{
		return isCaching;
	}

	public boolean isFailOnError()
	{
		return failOnError;
	}
}