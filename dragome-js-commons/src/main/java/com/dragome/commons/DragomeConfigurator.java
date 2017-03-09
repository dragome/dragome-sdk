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

import java.net.URL;
import java.util.List;

import com.dragome.commons.compiler.BytecodeTransformer;
import com.dragome.commons.compiler.annotations.CompilerType;
import com.dragome.commons.compiler.classpath.Classpath;
import com.dragome.commons.compiler.classpath.ClasspathEntry;
import com.dragome.commons.compiler.classpath.ClasspathFileFilter;

public interface DragomeConfigurator
{
	public ClassLoader getNewClassloaderInstance(ClassLoader parent, ClassLoader current);
	public BytecodeTransformer getBytecodeTransformer();
	public ExecutionHandler getExecutionHandler();
	public boolean filterClassPath(String classpathEntry);
	public CompilerType getDefaultCompilerType();
	void setDefaultCompilerType(CompilerType defaultCompilerType);
	public ClasspathFileFilter getClasspathFilter();
	public boolean isCheckingCast();
	List<ClasspathEntry> getExtraClasspath(Classpath classPath);
	boolean isRemoveUnusedCode();
	boolean isObfuscateCode();
	boolean isCaching();
	String getCompiledPath();
	public void sortClassPath(Classpath classPath);
	List<URL> getAdditionalCodeKeepConfigFile();
	List<URL> getAdditionalObfuscateCodeKeepConfigFile();
	public boolean isFailOnError();
}
