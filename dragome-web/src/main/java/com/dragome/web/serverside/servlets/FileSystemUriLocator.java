package com.dragome.web.serverside.servlets;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import ro.isdc.wro.model.resource.locator.UriLocator;

public class FileSystemUriLocator implements UriLocator
{
	private File baseFolder;

	public FileSystemUriLocator(File baseFolder)
	{
		this.baseFolder= baseFolder;
	}

	public InputStream locate(String uri) throws IOException
	{
		File file= new File(baseFolder, uri);
		return new FileInputStream(file);
	}

	public boolean accept(String uri)
	{
		return new File(baseFolder, uri).exists();
	}

}
