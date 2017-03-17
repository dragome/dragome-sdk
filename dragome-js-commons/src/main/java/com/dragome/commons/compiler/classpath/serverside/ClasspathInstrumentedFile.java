package com.dragome.commons.compiler.classpath.serverside;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import com.dragome.commons.compiler.BytecodeTransformer;
import com.dragome.commons.compiler.classpath.ClasspathFile;

public class ClasspathInstrumentedFile implements ClasspathFile
{
	private ClasspathFile classpathFile;
	private BytecodeTransformer bytecodeTransformer;

	public ClasspathInstrumentedFile(ClasspathFile classpathFile, BytecodeTransformer bytecodeTransformer)
	{
		this.classpathFile= classpathFile;
		this.bytecodeTransformer= bytecodeTransformer;
	}

	public String getFilename()
	{
		return classpathFile.getFilename();
	}

	public InputStream openInputStream()
	{
		byte[] bytecode;
		try
		{
			bytecode= IOUtils.toByteArray(classpathFile.openInputStream());
			bytecode= bytecodeTransformer.transform(getPath().replace("/", ".").replace(".class", ""), bytecode);

			return new ByteArrayInputStream(bytecode);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	public long getLastModified()
	{
		return classpathFile.getLastModified();
	}

	public void close()
	{
		classpathFile.close();
	}

	public String getPath()
	{
		return classpathFile.getPath();
	}

	public long getCRC()
	{
		return classpathFile.getCRC();
	}
}
