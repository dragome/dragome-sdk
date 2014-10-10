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