/*******************************************************************************
 * Copyright (c) 2011-2014 Fernando Petrola
 * 
 *  This file is part of Dragome SDK.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
/* Copyright (c) 2002-2011 by XMLVM.org
 *
 * Project Info:  http://www.xmlvm.org
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 */

package org.xmlvm.proc.out;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.xmlvm.Log;
import org.xmlvm.util.FileUtil;
import org.xmlvm.util.universalfile.UniversalFile;
import org.xmlvm.util.universalfile.UniversalFileCreator;

/**
 * This class represents a file and its content, read to be written out to the
 * file system.
 */
public class OutputFile
{
	public static final String TAG_LIB_NAME= "lib-name";

	/**
	 * Defines a data provider that can be used to de-couple the creation of the
	 * OutputFile from the time the data is produced. getData() is only called
	 * when the data is actually needed. It is also guaranteed that the method
	 * is only called once inside OutputFile.
	 */
	public static interface DelayedDataProvider
	{
		public UniversalFile getData();
	}

	private UniversalFile data;
	private DelayedDataProvider provider;
	private String location= "";
	private String fileName= "";
	private String origin= null;
	private Map<String, String> tags= new HashMap<String, String>();

	/**
	 * Create an empty output file. It is required to set data later
	 */
	public OutputFile()
	{
	}

	public OutputFile(DelayedDataProvider provider)
	{
		this.provider= provider;
	}

	/**
	 * Create a new file with the given string content.
	 */
	public OutputFile(String data)
	{
		setData(data);
	}

	/**
	 * Create a new file with the given string content.
	 */
	public OutputFile(byte[] data)
	{
		setData(data, System.currentTimeMillis());
	}

	/**
	 * Create a new file with the given string content.
	 */
	public OutputFile(byte[] data, long lastModified)
	{
		setData(data, lastModified);
	}

	public OutputFile(UniversalFile file)
	{
		this.data= file;
	}

	/**
	 * Returns the contents of this file.
	 */
	public UniversalFile getData()
	{
		maybeLoadDelayedData();
		if (data == null)
		{
			return null;
		}
		return data;
	}

	/**
	 * Returns the data as a byte array.
	 * <p>
	 * Same as getData().getFileAsBytes();
	 */
	public byte[] getDataAsBytes()
	{
		maybeLoadDelayedData();
		return data.getFileAsBytes();
	}

	/**
	 * Returns the data as a string.
	 * <p>
	 * Same as getData().getFileAsString();
	 */
	public String getDataAsString()
	{
		maybeLoadDelayedData();
		return data.getFileAsString();
	}

	/**
	 * Sets the content of this file.
	 */
	public final void setData(String data)
	{
		setData(data, System.currentTimeMillis());
	}

	/**
	 * Sets the content of this file.
	 */
	public final void setData(String data, long lastModified)
	{
		if (data == null)
		{
			this.data= null;
		}
		else
		{
			try
			{
				setData(data.getBytes("UTF-8"), lastModified);
			}
			catch (UnsupportedEncodingException ex)
			{
				Log.error(ex.getMessage());
			}
		}
	}

	/**
	 * Sets the content of this file.
	 */
	public final void setData(byte[] data, long lastModified)
	{
		this.data= UniversalFileCreator.createFile("", data, lastModified);
	}

	/**
	 * Sets the content of this file from an Input stream
	 * 
	 * @param stream
	 *            The InputStream to use - only UTF-8 streams are supported
	 * @return true, if everything was successful
	 */
	public boolean setDataFromStream(InputStream stream, long lastModified)
	{
		if (stream == null)
		{
			return false;
		}
		this.data= UniversalFileCreator.createFile("", stream, lastModified);
		return true;
	}

	/**
	 * Sets the content of this file from a BufferReader
	 * 
	 * @param in
	 *            The BufferReader to use as input
	 * @return true, if everything was successful
	 */
	public boolean setDataFromReader(BufferedReader in)
	{
		if (in == null)
		{
			return false;
		}
		StringWriter out= new StringWriter();
		if (FileUtil.copyReaders(in, out))
		{
			setData(out.toString());
		}
		return false;
	}

	/**
	 * Returns the output location of this file.
	 */
	public String getLocation()
	{
		return location;
	}

	/**
	 * Sets the output location of this file.
	 */
	public void setLocation(String location)
	{
		this.location= (new File(location)).getAbsolutePath();
	}

	/**
	 * Returns the name of this file (excluding the path).
	 */
	public String getFileName()
	{
		return fileName;
	}

	/**
	 * Sets the name of this file (excluding the path).
	 */
	public void setFileName(String fileName)
	{
		this.fileName= fileName;
	}

	/**
	 * Returns the path from which this resource originated from. Returns
	 * <code>null</code>, if no origin can be assigned.
	 * <p>
	 * The origin is used to cache resources generated from the original
	 * resource.
	 */
	public String getOrigin()
	{
		return origin;
	}

	/**
	 * Sets the origin of this file. This is typically a path to the original
	 * file from which this resource has been generated from and is typically
	 * used for caching.
	 */
	public void setOrigin(String origin)
	{
		this.origin= origin;
	}

	/**
	 * Get a list of the files affected by this OutputFile class
	 * <p>
	 * If this OutputFile only contains a file, it will return itself as the
	 * only array element.
	 * 
	 * @return Array of affected files.
	 */
	public ArrayList<OutputFile> getAffectedSourceFiles()
	{
		maybeLoadDelayedData();
		ArrayList<OutputFile> result= new ArrayList<OutputFile>();
		if (data == null || data.isFile())
		{
			result.add(this);
		}
		else
		{
			UniversalFile[] files= data.listFilesRecursively();
			int dataPathLength= data.getAbsolutePath().length();
			for (UniversalFile file : files)
			{
				String relativePath= file.getAbsolutePath().substring(dataPathLength + 1);
				OutputFile outputFile= new OutputFile(file);
				String path= getFullPath();
				if (!relativePath.isEmpty())
				{
					path+= relativePath;
				}

				String filelocation= path.substring(0, path.length() - file.getName().length());
				outputFile.setLocation(filelocation);
				outputFile.setFileName(file.getName());

				result.add(outputFile);
			}
		}
		return result;
	}

	/**
	 * Get the fill pathname of the output file
	 * 
	 * @return The full pathname as String
	 */
	public String getFullPath()
	{
		return location + (location.endsWith(File.separator) ? "" : File.separator) + fileName;
	}

	/**
	 * Returns the path of this file relative to the given one.
	 * <p>
	 * <p>
	 * Example:
	 * <p>
	 * Path of this file: /foo/bar/tar/file.txt
	 * <p>
	 * Given basePath: /foo/bar
	 * <p>
	 * Result: tar/file.txt
	 * 
	 * @param basePath
	 * @return
	 */
	public String getRelativePath(String basePath)
	{
		String fullPath= getFullPath();

		if (!fullPath.startsWith(basePath))
		{
			Log.error("'" + basePath + "' is not a base path of '" + fullPath);
			return null;
		}
		String result= fullPath.substring(basePath.length());
		if (result.startsWith(File.separator))
		{
			result= result.substring(1);
		}
		return result;
	}

	/**
	 * Write the given file to disk.
	 * 
	 * @return Whether file was written successfully.
	 */
	public boolean write()
	{
		if (location.isEmpty())
		{
			Log.warn("Cannot write OutputFile with no location: " + getFullPath());
			return false;
		}
		maybeLoadDelayedData();
		if (isEmpty())
		{
			Log.warn("Ignoring empty or non-existent file: " + getFullPath());
			return false;
		}
		String pathAndName= getFullPath();
		return data.saveAs(pathAndName);
	}

	/**
	 * Returns whether the contents of this file is different from the
	 * destination. If the destination doesn't exist, this method returns
	 * <code>true</code>.
	 */
	public boolean isDifferentFromExisting()
	{
		UniversalFile destination= UniversalFileCreator.createFile(new File(getFullPath()));
		if (!destination.exists() || !destination.isFile())
		{
			return true;
		}

		maybeLoadDelayedData();
		return data.isDifferentFromExisting(getFullPath());
	}

	/**
	 * Determines if the file is empty.
	 * 
	 * @return Whether the file empty or not present.
	 */
	public boolean isEmpty()
	{
		maybeLoadDelayedData();
		return data == null || data.isEmpty();
	}

	private void maybeLoadDelayedData()
	{
		if (provider != null)
		{
			data= provider.getData();
			provider= null;
		}
	}

	/**
	 * Returns the time stamp when the contents of this file have been changed
	 * the last time.
	 */
	public long getLastModified()
	{
		return data.getLastModified();
	}

	/**
	 * Sets a new or overrides an existing tag value.
	 * 
	 * @param tagName
	 *            The name of the tag.
	 * @param value
	 *            The value of the tag.
	 */
	public void setTag(String tagName, String value)
	{
		tags.put(tagName, value);
	}

	/**
	 * Returns whether the tag with the given name exists on this file.
	 */
	public boolean hasTag(String tagName)
	{
		return tags.containsKey(tagName);
	}

	/**
	 * Returns the value of the tag with the given name.
	 */
	public String getTag(String tagName)
	{
		return tags.get(tagName);
	}
}
