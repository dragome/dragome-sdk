package com.dragome.commons.compiler.classpath;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;

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
	}

	private static String classnameToPath(String classname)
	{
		return classname.replace(".", "/") + ".class";
	}

	public InputStream openInputStream()
	{
		return inputStream= new ByteArrayInputStream(bytecode);
	}

	public long getCRC()
	{
		return Arrays.hashCode(bytecode);
	}
}
