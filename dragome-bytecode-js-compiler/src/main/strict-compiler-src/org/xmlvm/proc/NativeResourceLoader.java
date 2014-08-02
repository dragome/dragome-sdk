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

package org.xmlvm.proc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.xmlvm.Log;
import org.xmlvm.util.universalfile.UniversalFile;

/**
 * Loads native methods for certain types.
 * <p>
 * Background: We can cross compile the byte code of classes, but some classes
 * have native methods, which are implemented elsewhere. For these, we generate
 * native skeletons first and then implement those native methods manually.
 * Whenever a type with native methods is loaded, the file that contains its
 * native methods also needs to be loaded. This class simply looks up whether a
 * given type has a native part. If so, it loads the file.
 */
public class NativeResourceLoader
{
	private static final String TAG= NativeResourceLoader.class.getSimpleName();
	private static final String NATIVE_PREFIX= "native_";
	private final UniversalFile basePath;
	private final String fileSuffix;

	private Map<String, UniversalFile> cache;

	/**
	 * Instantiates the native resource loader.
	 * 
	 * @param basePath
	 *            The path in which the native files can be found.
	 * @param fileSuffix
	 *            The suffix of the native files (e.g. 'c').
	 */
	public NativeResourceLoader(UniversalFile basePath, String fileSuffix)
	{
		this.basePath= basePath;
		this.fileSuffix= fileSuffix;
	}

	/**
	 * Returns a list with native implementations of all the types that do have
	 * native parts.
	 * 
	 * @param typeNames
	 *            a type name like "java.lang.Object" or "java_lang_Object".
	 */
	public List<UniversalFile> load(Set<String> typeNames)
	{
		List<UniversalFile> result= new ArrayList<UniversalFile>();

		if (!basePath.exists())
		{
			Log.error(TAG, "Could not find path: " + basePath.getAbsolutePath());
			return result;
		}
		maybeInitialize();

		for (String typeName : typeNames)
		{
			String fileName= typeNameToNativeFileName(typeName);
			if (cache.containsKey(fileName))
			{
				result.add(cache.get(fileName));
			}
		}
		return result;
	}

	/**
	 * Reads the files from the {@link #basePath} and makes them accessible in
	 * {@link #cache}. Does nothing, if {@link #cache} already exists.
	 */
	private void maybeInitialize()
	{
		if (cache != null)
		{
			return;
		}
		cache= new HashMap<String, UniversalFile>();

		for (UniversalFile file : basePath.listFiles())
		{
			cache.put(file.getName(), file);
		}
	}

	/**
	 * Returns the file name for the given type.
	 * 
	 * @param typeName
	 *            Type name like "java.lang.Object".
	 * @return File name like "native_java_lang_Object.c".
	 */
	private String typeNameToNativeFileName(String typeName)
	{
		typeName= typeName.replace('.', '_').replace('$', '_');
		return NATIVE_PREFIX + typeName + "." + fileSuffix;
	}
}
