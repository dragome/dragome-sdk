package com.dragome.commons.compiler;

import java.io.IOException;
import java.io.InputStream;

public abstract class AbstractClasspathFile implements ClasspathFile
{
	protected long lastModified;
	protected InputStream inputStream;
	protected String path;

	public AbstractClasspathFile(String path)
	{
		this.path= path;
	}

	public InputStream openInputStream() throws IOException
	{
		return inputStream;
	}

	public long getLastModified()
	{
		return lastModified;
	}

	public void close()
	{
		try
		{
			inputStream.close();
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	public String getPath()
	{
		return path;
	}
}