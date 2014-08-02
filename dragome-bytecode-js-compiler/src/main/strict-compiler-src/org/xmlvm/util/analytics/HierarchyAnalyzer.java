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

package org.xmlvm.util.analytics;

import org.xmlvm.util.analytics.data.TypeHierarchy;
import org.xmlvm.util.analytics.data.Util;
import org.xmlvm.util.universalfile.UniversalFile;
import org.xmlvm.util.universalfile.UniversalFileCreator;
import org.xmlvm.util.universalfile.UniversalFileFilter;

import com.android.dx.cf.direct.DirectClassFile;
import com.android.dx.cf.direct.StdAttributeFactory;
import com.android.dx.cf.iface.ParseException;
import com.android.dx.rop.type.TypeList;

/**
 * The HierarchyAnalyzer takes a set of classes and analyzes their hierarchy. It
 * creates a {@link TypeHierarchy} object which allows for a comprehensive
 * analysis of type hierarchies in a library.
 */
public class HierarchyAnalyzer
{
	private final String libraryPath;

	public static void main(String[] args)
	{
		// The library JAR or root directory should be given
		if (args.length != 1)
		{
			System.err.println("Invalid usage.\nExpected: HierarchyAnalyer <filename.jar|directory>");
			System.exit(-1);
		}

		HierarchyAnalyzer analyzer= new HierarchyAnalyzer(args[0]);
		TypeHierarchy hierarchy= analyzer.analyze();

		System.out.println("Type hierarchy:");
		System.out.println(hierarchy.toString());
	}

	/**
	 * Initialized the {@link HierarchyAnalyzer} with the given JAR file.
	 * 
	 * @param libraryPath
	 *            The JAR file or root directory of the library to analyze.
	 */
	public HierarchyAnalyzer(String libraryPath)
	{
		this.libraryPath= libraryPath;
	}

	/**
	 * Performs the hierarchy analysis and returns the result.
	 */
	public TypeHierarchy analyze()
	{
		// Extract all class files.
		UniversalFile library= UniversalFileCreator.createDirectory(null, libraryPath);
		UniversalFile[] classes= library.listFilesRecursively(new UniversalFileFilter()
		{

			public boolean accept(UniversalFile file)
			{
				return file.getName().toLowerCase().endsWith(".class");
			}
		});
		System.out.println("Getting type hierarchy for " + classes.length + " classes.");

		TypeHierarchy result= new TypeHierarchy();
		final String basePath= library.getAbsolutePath();
		for (UniversalFile clazz : classes)
		{
			String fileName= clazz.getRelativePath(basePath).replace('\\', '.');
			DirectClassFile classFile= new DirectClassFile(clazz.getFileAsBytes(), fileName, false);
			classFile.setAttributeFactory(StdAttributeFactory.THE_ONE);
			try
			{
				classFile.getMagic();
			}
			catch (ParseException ex)
			{
				continue;
			}

			final int DOT_CLASS_LENGTH= ".class".length();
			String className= fileName.substring(0, fileName.length() - DOT_CLASS_LENGTH).replace('/', '.');

			// Super-class.
			if (classFile.getSuperclass() != null)
			{
				String superClassName= Util.parseClassName(classFile.getSuperclass().getClassType().getClassName()).toString();
				result.addDirectSubType(className, superClassName);
			}

			// Interfaces
			TypeList interfaces= classFile.getInterfaces();
			if (interfaces != null)
			{
				for (int i= 0; i < interfaces.size(); i++)
				{
					String interfaceName= Util.parseClassName(interfaces.getType(i).getClassName()).toString();
					result.addDirectSubType(className, interfaceName);
				}
			}
		}
		return result;
	}
}
