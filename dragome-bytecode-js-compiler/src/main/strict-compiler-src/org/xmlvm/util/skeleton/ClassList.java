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

package org.xmlvm.util.skeleton;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class ClassList extends ArrayList<Class>
{

	private String pkg;
	private String canonical_pkg;

	public ClassList(String pkg)
	{
		this.pkg= pkg.replace(".", "/");
		canonical_pkg= pkg.replace("/", ".") + ".";
		updateList();
	}

	private void updateList()
	{
		String classpath= System.getProperty("java.class.path");
		StringTokenizer tok= new StringTokenizer(classpath, File.pathSeparator);

		while (tok.hasMoreTokens())
		{
			File file= new File(tok.nextToken());
			if (file.isFile())
				browseJarFile(file);
			else if (file.isDirectory())
				browseLocalDir(file, "");
		}
	}

	private void browseJarFile(File f)
	{
		JarInputStream libFiles= null;
		int pkglen= pkg.length();
		try
		{
			libFiles= new JarInputStream(new FileInputStream(f));
			JarEntry file= null;
			while ((file= libFiles.getNextJarEntry()) != null)
			{
				String name= file.getName();
				int lastslash= name.lastIndexOf("/");
				if (lastslash == pkglen && name.startsWith(pkg))
					checkClassName(name.substring(lastslash + 1, name.length()));
			}
		}
		catch (IOException ex)
		{
		}
		finally
		{
			try
			{
				libFiles.close();
			}
			catch (IOException ex)
			{
			}
		}
	}

	private void browseLocalDir(File d, String currentpkg)
	{
		if (currentpkg.equals(pkg))
		{
			File[] list= d.listFiles();
			for (File entry : list)
				if (entry.isFile())
					checkClassName(entry.getName());
			return;
		}
		if (currentpkg.length() >= pkg.length())
			return;
		if (!pkg.startsWith(currentpkg))
			return;
		File[] list= d.listFiles();
		for (File entry : list)
			if (entry.isDirectory())
				browseLocalDir(entry, currentpkg + ((currentpkg.length() == 0) ? "" : "/") + entry.getName());
	}

	private void checkClassName(String name)
	{
		if (name.endsWith(".class") && name.indexOf('$') < 0)
		{
			try
			{
				add(Class.forName(canonical_pkg + name.substring(0, name.length() - ".class".length())));
			}
			catch (ClassNotFoundException ex)
			{
				System.out.println("Class reference " + name + " found but unable to initialize.");
			}
		}
	}
}
