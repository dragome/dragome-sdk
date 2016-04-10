package com.dragome.commons.compiler.classpath;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

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

	public JavaFileClasspathFile(File folder, String path)
	{
		super(path);
		this.file= new File(folder, path);

		lastModified= file.lastModified();
	}

	public InputStream openInputStream()
	{
		try
		{
			return inputStream= new FileInputStream(file);
		}
		catch (FileNotFoundException e)
		{
			throw new RuntimeException(e);
		}
	}
}
