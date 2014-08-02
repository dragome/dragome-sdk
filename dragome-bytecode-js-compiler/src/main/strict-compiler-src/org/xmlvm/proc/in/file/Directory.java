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

package org.xmlvm.proc.in.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.xmlvm.util.FileSet;

/**
 * An {@link XFile} class for a directory.
 */
public class Directory extends XFile
{
	List<File> result;

	public Directory(String path)
	{
		super(new File(path));
		init();
	}

	/**
	 * Returns whether the given input is a directory.
	 */
	public static boolean isDirectoryInput(String path)
	{
		return (path.contains("*") || (new File(path)).isDirectory());
	}

	/**
	 * Returns a list of files that match the given input string.
	 */
	public List<File> getAllMatchingFiles()
	{
		return result;
	}

	/**
	 * Returns whether the directory given is the same as the one as described
	 * by this {@link Directory} instance.
	 */
	public boolean equals(File file)
	{
		return this.file.equals(file);
	}

	protected void init()
	{
		result= new ArrayList<File>();
		FileSet fileSet= new FileSet(file.getAbsolutePath());
		for (File file : fileSet)
		{
			// Only return actual files that match this directory request. Files
			// in sub-directories are covered if they are matched the input
			// statement.
			if (!file.isDirectory())
			{
				result.add(file);
			}
		}
	}
}
