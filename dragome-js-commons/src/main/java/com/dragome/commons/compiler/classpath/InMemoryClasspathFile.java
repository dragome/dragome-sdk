package com.dragome.commons.compiler.classpath;

import java.io.ByteArrayInputStream;

public class InMemoryClasspathFile extends AbstractClasspathFile
{
	private byte[] bytecode;
	private String classname;

	public byte[] getBytecode()
	{
		return bytecode;
	}

	public String getClassname()
	{
		return classname;
	}

	public String getFilename()
	{
		return path;
	}

	public InMemoryClasspathFile(String classname, byte[] bytecode)
	{
		super(classnameToPath(classname));
		this.classname= classname;
		this.bytecode= bytecode;
		inputStream= new ByteArrayInputStream(bytecode);
	}

	private static String classnameToPath(String classname)
	{
		return classname.replace(".", "/") + ".class";
	}
}
