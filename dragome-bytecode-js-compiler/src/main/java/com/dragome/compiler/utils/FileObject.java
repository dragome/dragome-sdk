package com.dragome.compiler.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class FileObject
{

	private long lastModified;

	private InputStream in;

	private File file;

	public File getFile()
	{
		return file;
	}

	public void setFile(File file)
	{
		this.file= file;
	}

	public FileObject(JarFile jarFile, JarEntry entry)
	{
		try
		{
			in= jarFile.getInputStream(entry);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
		lastModified= entry.getTime();
	}

	public FileObject(File file)
	{
		this.file= file;
		try
		{
			in= new FileInputStream(file);
		}
		catch (FileNotFoundException e)
		{
			throw new RuntimeException(e);
		}
		lastModified= file.lastModified();
	}

	public InputStream openInputStream() throws IOException
	{
		return in;
	}

	public long getLastModified()
	{
		return lastModified;
	}

	public void close()
	{
		try
		{
			in.close();
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

}
