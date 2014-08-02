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

package org.xmlvm.util.universalfile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.xmlvm.Log;
import org.xmlvm.util.FileUtil;

/**
 * A {@link UniversalFile} that is based on a real file on the file system.
 */
public class UniversalFileFromFileSystemFile extends UniversalFile
{

	private File file;

	UniversalFileFromFileSystemFile(File file)
	{
		this.file= file;
	}

	public String getAbsolutePath()
	{
		return file.getAbsolutePath();
	}

	public byte[] getFileAsBytes()
	{
		return readFileAsBytes(file);
	}

	public String getFileAsString()
	{
		return readFileAsString(file);
	}

	public boolean isDirectory()
	{
		return false;
	}

	public boolean isFile()
	{
		return true;
	}

	public boolean exists()
	{
		return file.exists();
	}

	public UniversalFile[] listFiles()
	{
		return new UniversalFile[0];
	}

	public long getLastModified()
	{
		return file.lastModified();
	}

	/**
	 * Read the content of a file as bytes.
	 * 
	 * @param file
	 *            the file to read
	 * @return The content of the file.
	 */
	public static byte[] readFileAsBytes(File file)
	{
		try
		{
			FileInputStream stream= new FileInputStream(file);
			byte[] fromStream= FileUtil.readBytesFromStream(stream);
			try
			{
				stream.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			return fromStream;
		}
		catch (FileNotFoundException e)
		{
			Log.error("Could not read file: " + file.getAbsolutePath() + " ( " + e.getMessage() + " )");
			return new byte[0];
		}
	}

	/**
	 * Read the content of a file as String.
	 * 
	 * @param file
	 *            the file to read.
	 */
	public static String readFileAsString(File file)
	{
		try
		{
			FileInputStream stream= new FileInputStream(file);
			String fromStream= FileUtil.readStringFromStream(stream);
			try
			{
				stream.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			return fromStream;
		}
		catch (FileNotFoundException e)
		{
			Log.error("Could not read file: " + file.getAbsolutePath() + " ( " + e.getMessage() + " )");
			return "";
		}
	}
}
