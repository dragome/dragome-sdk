package com.dragome.commons.compiler.classpath;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public abstract class AbstractClasspathFile implements ClasspathFile
{
	protected long lastModified;
	protected InputStream inputStream;
	protected String path;
	protected byte[] bytes;

	public AbstractClasspathFile(String path)
	{
		this.path= path;
	}

	public long getLastModified()
	{
		return lastModified;
	}

	public void close()
	{
		try
		{
			if (inputStream != null)
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

	public long getCRC()
	{
		return lastModified;
	}
	
	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}
	
	public InputStream openInputStream()
	{
		return bytes != null ? new ByteArrayInputStream(bytes) : null;
	}
}