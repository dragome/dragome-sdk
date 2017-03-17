package com.dragome.commons.compiler.classpath;

import java.util.List;
import java.util.jar.JarOutputStream;

import com.dragome.commons.compiler.BytecodeTransformer;

public interface ClasspathEntry
{
	ClasspathFile getClasspathFileOf(String relativeName);
	List<ClasspathFile> getClasspathFilesFiltering(ClasspathFileFilter classpathFilter);
	String getName();
	void copyFilesToJar(JarOutputStream jos, BytecodeTransformer bytecodeTransformer, ClasspathFileFilter classpathFileFilter);
}