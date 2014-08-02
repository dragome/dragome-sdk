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
 * A {@link UniversalFile} that is based on a file system directory.
 */
public class UniversalFileFromFileSystemDirectory extends UniversalFile
{

	private File directory;

	UniversalFileFromFileSystemDirectory(File directory)
	{
		this.directory= directory;
	}

	public String getAbsolutePath()
	{
		return directory.getAbsolutePath();
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
		return directory.exists();
	}

	public UniversalFile[] listFiles()
	{
		List<UniversalFile> result= new ArrayList<UniversalFile>();
		File files[]= directory.listFiles();
		for (File file : files)
		{
			if (file.isDirectory())
			{
				result.add(new UniversalFileFromFileSystemDirectory(file));
			}
			else
			{
				result.add(new UniversalFileFromFileSystemFile(file));
			}
		}
		return result.toArray(new UniversalFile[0]);
	}

	public long getLastModified()
	{
		return directory.lastModified();
	}
}
