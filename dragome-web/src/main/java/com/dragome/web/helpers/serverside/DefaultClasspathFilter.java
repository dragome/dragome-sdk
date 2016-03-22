package com.dragome.web.helpers.serverside;

import java.io.File;

import com.dragome.commons.compiler.ClasspathFileFilter;
import com.dragome.commons.compiler.CompilerMode;

public class DefaultClasspathFilter implements ClasspathFileFilter
{
	public boolean accept(File pathname, File folder)
	{
		String string= pathname.toString();
		boolean isServerSideOnly= string.contains(File.separator + "serverside");
		boolean isDebuggingPackage= string.contains(File.separator + "debugging");
		if (!CompilerMode.Production.toString().equals(System.getProperty("dragome-compile-mode")))
			isDebuggingPackage= false;

		return !(isServerSideOnly || isDebuggingPackage);
	}
}