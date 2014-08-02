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
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link UniversalFile} type representing a directory. The contents
 * are given directly.
 */
public class UniversalFileDirectory extends UniversalFile
{
	private String absoluteName;
	private List<UniversalFile> files= new ArrayList<UniversalFile>();
	private long lastModified;

	UniversalFileDirectory(String absoluteName, long lastModified)
	{
		this.absoluteName= absoluteName;
		this.lastModified= lastModified;
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

	public UniversalFile[] listFiles()
	{
		return files.toArray(new UniversalFile[0]);
	}

	/**
	 * Adds a universal file as a child.
	 */
	public void add(UniversalFile file)
	{
		files.add(file);
		this.lastModified= System.currentTimeMillis();
	}

	/**
	 * Returns the sub-directory with the given name or {@code null}, if not
	 * found.
	 * 
	 * @param name
	 *            the name of the sub-directory.
	 * @return The instance or{@code null}.
	 */
	public UniversalFileDirectory getDirectory(String name)
	{
		for (UniversalFile file : files)
		{
			if (file.isDirectory() && (file.getAbsolutePath().endsWith(File.separator + name)))
			{
				return (UniversalFileDirectory) file;
			}
		}
		return null;
	}

	public long getLastModified()
	{
		return lastModified;
	}
}
