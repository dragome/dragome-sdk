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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

import org.xmlvm.Log;

/**
 * A {@link UniversalFile} based on a {@link JarFile}.
 */
public class UniversalFileFromJarFile extends UniversalFile
{
	private static final String TAG= "UniversalFileFromJarFile";

	private String absoluteName;
	private JarInputStream jarStream;
	private UniversalFileDirectory directory;
	private long lastModified;

	UniversalFileFromJarFile(String absoluteName, JarInputStream jarStream)
	{
		this.absoluteName= absoluteName;
		this.jarStream= jarStream;
		this.lastModified= calcLastModified();
	}

	private long calcLastModified()
	{
		File file= new File(absoluteName);
		if (file.exists())
		{
			return file.lastModified();
		}
		else
		{
			return System.currentTimeMillis();
		}
	}

	public String getAbsolutePath()
	{
		return absoluteName;
	}

	public byte[] getFileAsBytes()
	{
		return null;
	}

	public String getFileAsString()
	{
		return null;
	}

	public boolean isDirectory()
	{
		return true;
	}

	public boolean isFile()
	{
		return false;
	}

	public boolean exists()
	{
		return true;
	}

	public synchronized UniversalFile[] listFiles()
	{
		if (directory == null)
		{
			directory= initialize();
		}
		return directory.listFiles();
	}

	private synchronized UniversalFileDirectory initialize()
	{
		UniversalFileDirectory result= new UniversalFileDirectory(absoluteName, lastModified);
		JarEntry entry;
		try
		{
			byte data[]= new byte[4096];
			while ((entry= jarStream.getNextJarEntry()) != null)
			{
				ByteArrayOutputStream outputStream= new ByteArrayOutputStream();
				int len= 0;
				while ((len= jarStream.read(data)) != -1)
				{
					outputStream.write(data, 0, len);
				}
				put(result, entry.getName(), new ByteArrayInputStream(outputStream.toByteArray()), entry.getTime());
			}
		}
		catch (IOException e)
		{
			Log.error(TAG, "Error reading JAR file: " + absoluteName);
		}
		return result;
	}

	private synchronized void put(UniversalFileDirectory addToDir, String name, InputStream stream, long entryLastModified)
	{
		int index;

		// A JAR file is a compressed ZIP file that only gives back a list of
		// all files contained in it. It doesn't allow hierarchical requests.
		// Therefore, each file is here put into its correct hierarchical path.
		while ((index= name.indexOf("/")) != -1)
		{
			String subDirName= name.substring(0, index);
			if (!subDirName.isEmpty())
			{
				UniversalFileDirectory subDirectory= addToDir.getDirectory(subDirName);
				if (subDirectory == null)
				{
					subDirectory= new UniversalFileDirectory(addToDir.getAbsolutePath() + File.separator + subDirName, entryLastModified);
					addToDir.add(subDirectory);
				}
				addToDir= subDirectory;
			}
			name= name.substring(index + 1);
		}

		// If this entry is a directory, we don't need to add it.
		if (!name.isEmpty())
		{
			addToDir.add(new UniversalFileFromStreamResource(addToDir.getAbsolutePath() + File.separator + name, stream, entryLastModified));
		}
	}

	public long getLastModified()
	{
		return this.lastModified;
	}
}
