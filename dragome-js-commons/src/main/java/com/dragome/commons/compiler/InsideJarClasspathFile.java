package com.dragome.commons.compiler;

import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.dragome.commons.compiler.classpath.AbstractClasspathFile;
import com.dragome.commons.compiler.classpath.ClasspathFile;

public class InsideJarClasspathFile extends AbstractClasspathFile implements ClasspathFile
{
	private JarEntry entry;

	public String getFilename()
	{
		return entry.getName();
	}

	public InsideJarClasspathFile(JarFile jarFile, JarEntry entry, String path)
	{
		super(path);

		this.entry= entry;
		try
		{
			inputStream= jarFile.getInputStream(entry);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
		lastModified= entry.getTime();
	}
}
