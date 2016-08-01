package com.dragome.commons.compiler.classpath;

import java.util.List;
import java.util.jar.JarOutputStream;

import com.dragome.commons.compiler.ClasspathEntryFilter;

public interface ClasspathEntry
{
	ClasspathFile getClasspathFileOf(String relativeName);
	List<String> getAllFilesNamesFiltering(ClasspathFileFilter classpathFilter);
	String getName();
	void copyFilesToJar(JarOutputStream jos, ClasspathEntryFilter classpathEntryFilter);
}