package com.dragome.commons.compiler.classpath;

import java.util.List;
import java.util.jar.JarOutputStream;

public interface ClasspathEntry
{
	ClasspathFile getClasspathFileOf(String relativeName);
	List<String> getAllFilesNamesFiltering(ClasspathFileFilter classpathFilter);
	void copyFilesToJar(JarOutputStream jos);
	String getName();
}