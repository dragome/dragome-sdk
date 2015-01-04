/*******************************************************************************
 * Copyright (c) 2011-2014 Fernando Petrola
 * 
 * This file is part of Dragome SDK.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/

// Copyright 2011 The j2js Authors. All Rights Reserved.
//
// This file is part of j2js.
//
// j2js is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// j2js is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with j2js. If not, see <http://www.gnu.org/licenses/>.

package com.dragome.compiler.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import com.dragome.compiler.DragomeJsCompiler;

public class FileManager
{
	private List<Object> path= new ArrayList<Object>();
	private FileFilter classpathFilter;

	public FileManager(List<File> classPath, FileFilter classpathFilter, List<File> extraClasspath)
	{
		this.classpathFilter= classpathFilter;
		
		classPath.addAll(extraClasspath);
		
		Log.getLogger().debug("Resolving class path " + classPath);

		for (File file : classPath)
		{
			if (!file.exists())
			{
				DragomeJsCompiler.errorCount++;
				Log.getLogger().error("Cannot find resource on class path: " + file.getAbsolutePath());
				continue;
			}

			if (file.getName().endsWith(".jar"))
			{
				try
				{
					path.add(new JarFile(file));
				}
				catch (IOException e)
				{
					throw new RuntimeException(e);
				}
			}
			else
			{
				path.add(file);
			}
		}
	}

	public FileObject getFileForInput(String relativeName)
	{
		for (Object o : path)
		{
			if (o instanceof JarFile)
			{
				JarFile jarFile= (JarFile) o;
				JarEntry entry= jarFile.getJarEntry(relativeName);
				if (entry != null)
				{
					return new FileObject(jarFile, entry);
				}
			}
			else
			{
				File file= new File(((File) o), relativeName);
				if (file.exists())
				{
					return new FileObject(file);
				}
			}
		}

		throw new RuntimeException("Could not find " + relativeName + " on class path");
	}

	private static List<String> findClassesInJar(JarFile jarFile)
	{
		ArrayList<String> result= new ArrayList<String>();

		final Enumeration<JarEntry> entries= jarFile.entries();
		while (entries.hasMoreElements())
		{
			final JarEntry entry= entries.nextElement();
			final String entryName= entry.getName();
			if (entryName.endsWith(".class"))
				result.add(entryName.replace('/', File.separatorChar).replace(".class", ""));
		}

		return result;
	}
	public Collection<String> getAllFilesInClasspath()
	{
		Collection<String> files= new ArrayList<String>();
		for (Object o : path)
		{
			if (o instanceof JarFile)
			{
				JarFile jarFile= (JarFile) o;

				List<String> classesInJar= findClassesInJar(jarFile);

				for (String file : classesInJar)
				{
					if (classpathFilter == null || classpathFilter.accept(new File(file)))
					{
						files.add(file);
					}
				}
			}
			else
			{
				File folder= (File) o;
				Collection<File> listFiles= FileUtils.listFiles(folder, new WildcardFileFilter("*.class"), DirectoryFileFilter.DIRECTORY);
				for (File file : listFiles)
				{
					if (classpathFilter == null || classpathFilter.accept(file))
					{
						String substring= file.toString().substring(folder.toString().length() + 1);
						files.add(substring.replace(".class", ""));
					}
				}
			}
		}
		return files;
	}
}
