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

import java.io.FileFilter;

import com.dragome.commons.compiler.annotations.CompilerType;

public class BytecodeToJavascriptCompilerConfiguration
{
	public String classPath;
	public String targetDir;
	public String mainClassName;
	public CompilerType compilerType;
	public BytecodeTransformer bytecodeTransformer;
	public FileFilter classpathFilter;

	public BytecodeToJavascriptCompilerConfiguration(String classPath, String targetDir, String mainClassName, CompilerType compilerType, BytecodeTransformer bytecodeTransformer, FileFilter classpathFilter)
	{
		this.classPath= classPath;
		this.targetDir= targetDir;
		this.mainClassName= mainClassName;
		this.compilerType= compilerType;
		this.bytecodeTransformer= bytecodeTransformer;
		this.classpathFilter= classpathFilter;
	}

	public String getClassPath()
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

	public FileFilter getClasspathFilter()
	{
		return classpathFilter;
	}
}