package com.dragome.commons.compiler.classpath;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class JavaFileClasspathFile extends AbstractClasspathFile
{
	private File file;
	public String getFilename()
	{
		return file != null ? file.getName() : "";
	}

	public void setFile(File file)
	{
		this.file= file;
	}

	public JavaFileClasspathFile(File file, String path)
	{
		super(path);
		this.file= file;
		try
		{
			inputStream= new FileInputStream(file);
		}
		catch (FileNotFoundException e)
		{
			throw new RuntimeException(e);
		}
		lastModified= file.lastModified();
	}
}
