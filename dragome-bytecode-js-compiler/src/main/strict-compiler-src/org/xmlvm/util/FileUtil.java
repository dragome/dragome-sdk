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

package org.xmlvm.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.xmlvm.Log;
import org.xmlvm.util.universalfile.UniversalFile;

/**
 * Various utilities around handling files.
 */
public class FileUtil
{
	private static final String TAG= "FileUtil";

	/**
	 * Copies the given source directory to the given destination.
	 * 
	 * @param source
	 *            the source directory to copy from
	 * @param destination
	 *            where to copy the files to
	 * @param recursive
	 *            whether the files should be copied recursively
	 * @return Whether the operation was successful.
	 */
	public static boolean copyDirectory(UniversalFile source, String destination, boolean recursive)
	{
		if (!source.isDirectory())
		{
			Log.error(TAG, "CopyDirectory: Source is not a directory: " + source);
			return false;
		}

		if (destination.endsWith(File.separator))
		{
			destination= destination.substring(0, destination.length() - 1);
		}

		for (UniversalFile file : source.listFiles())
		{
			if (file.isDirectory() && recursive)
			{
				String subPath= destination + file.getAbsolutePath().substring(source.getAbsolutePath().length());
				copyDirectory(file, subPath, recursive);
			}
			else if (file.isFile())
			{
				file.saveAs(destination + File.separator + file.getName());
			}
		}
		return true;
	}

	/**
	 * Copies a single file from source to destination.
	 * 
	 * @param source
	 *            The source file to be copied.
	 * @param destination
	 *            The destination of the copied file.
	 * @return Whether the operation was successful.
	 */
	public static boolean copyFile(File source, File destination)
	{
		try
		{
			if (source.equals(destination))
			{
				Log.debug("Ignoring copying of file " + source.getPath() + ": destination is same as source.");
				return true;
			}
			Log.debug("Copying " + source.getPath() + " to " + destination.getPath());
			return copyStreams(new FileInputStream(source), new FileOutputStream(destination));
		}
		catch (FileNotFoundException ex)
		{
		}
		return false;
	}

	/**
	 * The actual procedure of copying Streams (like binary files)
	 * 
	 * @param in
	 *            The stream to read data from
	 * @param out
	 *            The stream to write data to
	 * @return true, if everything is ok
	 */
	public static boolean copyStreams(InputStream in, OutputStream out)
	{
		if (in == null || out == null)
			return false;
		try
		{
			byte[] buf= new byte[4096];
			int len;
			while ((len= in.read(buf)) > 0)
				out.write(buf, 0, len);
			in.close();
			out.close();
			return true;
		}
		catch (IOException e)
		{
			try
			{
				in.close();
			}
			catch (IOException ex1)
			{
			}
			try
			{
				out.close();
			}
			catch (IOException ex1)
			{
			}
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * The actual procedure of copying writers (like text files)
	 * 
	 * @param in
	 *            The stream to read data from
	 * @param out
	 *            The stream to write data to
	 * @return true, if everything is ok
	 */
	public static boolean copyReaders(BufferedReader in, Writer out)
	{
		if (in == null || out == null)
			return false;
		try
		{
			String line;
			while ((line= in.readLine()) != null)
				out.append(line).append('\n');
			in.close();
			out.close();
			return true;
		}
		catch (IOException e)
		{
			try
			{
				in.close();
			}
			catch (IOException ex1)
			{
			}
			try
			{
				out.close();
			}
			catch (IOException ex1)
			{
			}
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Returns the path to the bin directory, where Eclipse typically stores
	 * compiled class files.
	 */
	public static String getBinDirectory()
	{
		return (new File("bin")).getAbsolutePath();
	}

	/**
	 * Reads a file and returns it contents as a byte array.
	 * 
	 * @param file
	 *            The file to read.
	 */
	public static byte[] readBytesFromStream(InputStream stream)
	{
		if (stream == null)
		{
			return new byte[0];
		}

		ByteArrayOutputStream byteArrayStream= new ByteArrayOutputStream();

		final int READ_BUFFER= 4096;
		byte b[]= new byte[READ_BUFFER];
		int l= 0;
		try
		{
			while ((l= stream.read(b)) > 0)
			{
				byteArrayStream.write(b, 0, l);
			}
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
			return new byte[0];
		}

		return byteArrayStream.toByteArray();
	}

	/**
	 * Read the content of an {@link InputStream} as String.
	 * 
	 * @param stream
	 *            The stream to read from.
	 * @return The content of the stream or an empty string, if an error occurs.
	 */
	public static String readStringFromStream(InputStream stream)
	{
		final int READ_BUFFER= 4096;
		StringBuilder buffer= new StringBuilder();
		byte b[]= new byte[READ_BUFFER];
		int l= 0;
		try
		{
			if (stream == null)
			{
				return "";
			}
			else
			{
				while ((l= stream.read(b)) > 0)
				{
					buffer.append(new String(b, 0, l));
				}
			}
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
			return "";
		}
		return buffer.toString();
	}

	/**
	 * Writes a string to a file.
	 * 
	 * @param file
	 *            The file to write to.
	 * @param content
	 *            The content to write to the file.
	 * @return Whether the writing was successful.
	 */
	public static boolean writeStringToFile(File file, String content)
	{
		try
		{
			OutputStreamWriter stageAssistantWriter= new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
			stageAssistantWriter.write(content);
			stageAssistantWriter.close();
		}
		catch (IOException e)
		{
			Log.error("Could not write to " + file.getAbsolutePath());
			return false;
		}
		return true;
	}

	/**
	 * Recursively deletes the given directory.
	 * 
	 * @param path
	 *            The directory to delete.
	 * @return Whether the process was successful.
	 */
	public static boolean deleteDirectory(File path)
	{
		if (path.exists())
		{
			File[] files= path.listFiles();
			for (int i= 0; i < files.length; i++)
			{
				if (files[i].isDirectory())
				{
					deleteDirectory(files[i]);
				}
				else
				{
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}
}
