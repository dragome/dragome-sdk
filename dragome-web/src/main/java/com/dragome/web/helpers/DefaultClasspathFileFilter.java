package com.dragome.web.helpers;

import java.io.File;

import com.dragome.commons.compiler.CompilerMode;
import com.dragome.commons.compiler.classpath.ClasspathFile;
import com.dragome.commons.compiler.classpath.ClasspathFileFilter;

public class DefaultClasspathFileFilter implements ClasspathFileFilter
{
	public boolean accept(ClasspathFile classpathFile)
	{
		String string= classpathFile.getPath();
		boolean isServerSideOnly= string.contains(File.separator + "serverside");
		boolean isDebuggingPackage= string.contains(File.separator + "debugging");
		if (!CompilerMode.Production.toString().equals(System.getProperty("dragome-compile-mode")))
			isDebuggingPackage= false;

		return !(isServerSideOnly || isDebuggingPackage);
	}
}