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
import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

import org.xmlvm.Log;
import org.xmlvm.util.universalfile.UniversalFile;

/**
 * Utility class for loading red/green/reflection class lists
 */
public class ClassListLoader
{
	public static Set<String> loadGreenlist(UniversalFile file)
	{
		return loadList(file, "C");
	}

	public static Set<String> loadReflectionClassList(UniversalFile file)
	{
		return loadList(file, "R");
	}

	public static Set<String> loadRedlist(UniversalFile file)
	{
		return loadList(file, null);
	}

	private static Set<String> loadList(UniversalFile file, String prefix)
	{
		try
		{
			Set<String> result= new HashSet<String>();

			if (file != null)
			{
				BufferedReader reader;
				reader= new BufferedReader(new StringReader(file.getFileAsString()));
				String line;
				while ((line= reader.readLine()) != null)
				{
					if (line.contains(":"))
					{
						if (prefix != null && line.startsWith(prefix + ":"))
						{
							result.add(line.replaceFirst(prefix + ":", "").trim());
						}
					}
					else
					{
						result.add(line.trim());
					}
				}
			}
			return result;
		}
		catch (IOException e)
		{
			Log.error(ClassListLoader.class.getSimpleName(), "Problem reading class list file: " + file.getAbsolutePath() + ": " + e.getMessage());
		}
		return null;

	}
}
