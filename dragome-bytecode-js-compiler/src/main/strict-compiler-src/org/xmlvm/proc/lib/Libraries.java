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

package org.xmlvm.proc.lib;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.xmlvm.Log;
import org.xmlvm.main.Arguments;
import org.xmlvm.util.universalfile.UniversalFile;
import org.xmlvm.util.universalfile.UniversalFileCreator;

/**
 * This is a registry for all the accessible libraries. Libraries get registered
 * here so processes can use them for their own purposes.
 */
public class Libraries
{
	private static final String TAG= Libraries.class.getSimpleName();
	private final static List<Library> libraries= new ArrayList<Library>();
	private final Arguments arguments;

	static
	{
		// Add all libraries here. First entry has highest priority.
		libraries.add(new JaxpLibrary());
		libraries.add(new JdkLibrary());
		libraries.add(new CocoaJavaLibrary());
		libraries.add(new IOSLibrary());
		libraries.add(new XmlvmUtilLibrary());
		libraries.add(new AndroidIPhoneLibrary());
		libraries.add(new WP7Library());
		libraries.add(new AndroidWP7Library());
		libraries.add(new SDLLibrary());
		libraries.add(new AndroidSDLLibrary());
	}

	/**
	 * Adds a new additional library to the top of the library stack.
	 * 
	 * @param locationStr
	 *            the location of the library. Can be a JAR/ZIP file or a
	 *            directory.
	 * @return Whether the library was added successfully. Return false, the if
	 *         the library could not be found.
	 */
	public static boolean addLibrary(String locationStr)
	{
		File location= new File(locationStr);
		if (!location.exists())
		{
			Log.error(TAG, "Could not find additional library at: " + locationStr);
			return false;
		}

		UniversalFile file= UniversalFileCreator.createDirectory(location.getAbsolutePath());
		libraries.add(0, new AdditionalLibrary(file));
		return true;
	}

	public Libraries(Arguments arguments)
	{
		this.arguments= arguments;
	}

	/**
	 * Returns the most recent lastModified date of all the libraries.
	 * <p>
	 * Note: The required the libraries to be loaded, to determine the
	 * last-modified timestamp.
	 */
	public long getLastModified()
	{
		long mostRecentLastModified= 0;

		for (Library library : libraries)
		{
			if (library.isEnabled(arguments))
			{
				for (UniversalFile lib : library.getLibrary())
				{
					if (lib.getLastModified() > mostRecentLastModified)
					{
						mostRecentLastModified= lib.getLastModified();
					}
				}
			}
		}
		return mostRecentLastModified;
	}

	/**
	 * Returns the libraries that can be loaded as they are needed.
	 */
	public List<UniversalFile> getLibraryFiles()
	{
		List<UniversalFile> result= new ArrayList<UniversalFile>();
		for (Library library : libraries)
		{
			if (library.isEnabled(arguments) && !library.isMonolithic())
			{
				result.addAll(Arrays.asList(library.getLibrary()));
			}
		}
		return result;
	}

	/**
	 * Required libraries that should be completely added to the input resources
	 * set.
	 */
	public List<UniversalFile> getMonolithicLibraryFiles()
	{
		List<UniversalFile> result= new ArrayList<UniversalFile>();
		for (Library library : libraries)
		{
			if (library.isEnabled(arguments) && library.isMonolithic())
			{
				result.addAll(Arrays.asList(library.getLibrary()));
			}
		}
		return result;
	}
}
