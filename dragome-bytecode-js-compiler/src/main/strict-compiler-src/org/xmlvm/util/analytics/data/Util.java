/*******************************************************************************
 * Copyright (c) 2011-2014 Fernando Petrola
 * 
 *  This file is part of Dragome SDK.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
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

package org.xmlvm.util.analytics.data;

import org.xmlvm.util.universalfile.UniversalFile;

/**
 * Common functionality used for analytical puproses.
 */
public class Util
{

	public static class JarFileData
	{
		public final UniversalFile[] classes;
		public final String basePath;

		public JarFileData(UniversalFile[] classes, String basePath)
		{
			this.classes= classes;
			this.basePath= basePath;
		}
	}

	/**
	 * A little helper class that contains package- and class name.
	 */
	public static class PackagePlusClassName
	{
		public String packageName= "";
		public String className= "";

		public PackagePlusClassName(String className)
		{
			this.className= className;
		}

		public PackagePlusClassName(String packageName, String className)
		{
			this.packageName= packageName;
			this.className= className;
		}

		public String toString()
		{
			if (packageName.isEmpty())
			{
				return className;
			}
			else
			{
				return packageName.replace('/', '.') + "." + className;
			}
		}
	}

	private Util()
	{
	}

	/**
	 * Converts a class name in the form of a/b/C into a
	 * {@link PackagePlusClassName} object.
	 */
	public static PackagePlusClassName parseClassName(String packagePlusClassName)
	{
		int lastSlash= packagePlusClassName.lastIndexOf('/');
		if (lastSlash == -1)
		{
			return new PackagePlusClassName(packagePlusClassName);
		}

		String className= packagePlusClassName.substring(lastSlash + 1).replace('/', '.');
		String packageName= packagePlusClassName.substring(0, lastSlash);

		return new PackagePlusClassName(packageName, className);
	}
}
