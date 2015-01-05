package com.dragome.helpers.serverside;

import java.io.File;
import java.io.FileFilter;

import com.dragome.commons.compiler.CompilerMode;

public class DefaultClasspathFilter implements FileFilter
{
	public boolean accept(File pathname)
	{
		String string= pathname.toString();
		boolean isServerSideOnly= string.contains(File.separator + "serverside");
		boolean isDebuggingPackage= string.contains(File.separator + "debugging");
		if (!CompilerMode.Production.toString().equals(System.getProperty("dragome-compile-mode")))
			isDebuggingPackage= false;

		return !(isServerSideOnly || isDebuggingPackage);
	}
}