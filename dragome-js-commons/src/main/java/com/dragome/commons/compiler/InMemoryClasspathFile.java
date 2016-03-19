package com.dragome.commons.compiler;

import java.io.ByteArrayInputStream;

public class InMemoryClasspathFile extends AbstractClasspathFile
{
	public String getFilename()
	{
		return path;
	}

	public InMemoryClasspathFile(String classname, byte[] bytecode)
	{
		super(classnameToPath(classname));
		inputStream= new ByteArrayInputStream(bytecode);
	}

	private static String classnameToPath(String classname)
	{
		return classname.replace(".", "/") + ".class";
	}
}
