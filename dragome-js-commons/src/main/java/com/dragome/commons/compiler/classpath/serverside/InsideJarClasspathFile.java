package com.dragome.commons.compiler.classpath.serverside;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.dragome.commons.compiler.classpath.AbstractClasspathFile;

public class InsideJarClasspathFile extends AbstractClasspathFile 
{
	private JarEntry entry;
	private JarFile jarFile;

	public String getFilename()
	{
		return entry.getName();
	}

	public InsideJarClasspathFile(JarFile jarFile, JarEntry entry, String path)
	{
		super(path);
		this.jarFile= jarFile;

		this.entry= entry;

		lastModified= entry.getTime();
	}

	public InputStream openInputStream()
	{
		InputStream openInputStream = super.openInputStream();
		if (openInputStream != null)
			return openInputStream;
		else
			try {
				return inputStream = jarFile.getInputStream(entry);
			} catch (IOException e) {
				throw new RuntimeException(e);
		}
	}

	public long getCRC()
	{
		return entry.getCrc();
	}
}
