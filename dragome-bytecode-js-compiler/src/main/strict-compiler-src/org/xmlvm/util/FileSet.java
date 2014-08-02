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

import java.io.File;
import java.io.FileFilter;
import java.util.Iterator;
import java.util.Stack;
import java.util.regex.Pattern;

public class FileSet implements Iterable<File>, Iterator<File>
{

	private File[] fileList;
	private final Stack<File> paths= new Stack<File>();
	private final Pattern wildcard;
	private boolean recurseFolders;
	private Iterator<File> currentIterator;

	public FileSet(String filePattern)
	{
		File f= new File(filePattern);
		if (f.isDirectory())
			f= new File(filePattern + File.separator + "*");
		File path= f.getParentFile() == null ? new File(".") : f.getParentFile();
		paths.push(path);
		String regex= "";
		recurseFolders= true;
		String fname= f.getName();
		for (int i= 0; i < fname.length(); i++)
		{
			switch (fname.charAt(i))
			{
				case '.':
					regex+= "\\.";
					break;
				case '?':
					regex+= '.';
					recurseFolders= true;
					break;
				case '*':
					regex+= ".*";
					recurseFolders= true;
					break;
				default:
					regex+= fname.charAt(i);
			}
		}
		wildcard= Pattern.compile(regex);
		fillUpFileList();
	}

	private void fillUpFileList()
	{
		if (paths.empty())
		{
			fileList= null;
			currentIterator= new Iterator<File>()
			{
				public boolean hasNext()
				{
					return false;
				}

				public void remove()
				{
				}

				public File next()
				{
					return null;
				}
			};
			return;
		}
		File folder= paths.pop();
		fileList= folder.listFiles(new FileFilter()
		{
			public boolean accept(File pathname)
			{
				if (recurseFolders && pathname.isDirectory())
					paths.push(pathname);
				return wildcard.matcher(pathname.getName()).matches();
			}
		});
		currentIterator= new Iterator<File>()
		{
			int i= 0;

			public boolean hasNext()
			{
				return i < fileList.length;
			}

			public File next()
			{
				return fileList[i++];
			}

			public void remove()
			{
			}
		};
	}

	public void remove()
	{
	}

	public Iterator<File> iterator()
	{
		return this;
	}

	public boolean hasNext()
	{
		while (!currentIterator.hasNext() && !paths.empty())
			fillUpFileList();
		return currentIterator.hasNext();
	}

	public File next()
	{
		if (!hasNext())
			return null;
		return currentIterator.next();
	}
}
