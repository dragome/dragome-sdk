package com.dragome.commons.compiler;


public interface BytecodeToJavascriptCompiler
{
	public void compile();
	void configure(BytecodeToJavascriptCompilerConfiguration compilerConfiguration);
}