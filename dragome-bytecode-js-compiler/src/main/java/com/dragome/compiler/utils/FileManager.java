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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import com.dragome.commons.compiler.ClasspathFile;
import com.dragome.commons.compiler.ClasspathFileFilter;
import com.dragome.compiler.DragomeJsCompiler;

public class FileManager
{
	private static Logger LOGGER= Logger.getLogger(FileManager.class.getName());

	private List<Object> path= new ArrayList<Object>();
	private ClasspathFileFilter classpathFilter;
	private List<ClasspathFile> extraClasspathFiles;

	public FileManager(List<File> classPath, ClasspathFileFilter classpathFilter, List<ClasspathFile> extraCompilableFiles)
	{
		this.classpathFilter= classpathFilter;
		this.extraClasspathFiles= extraCompilableFiles;

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

	public ClasspathFile getFileForInput(String relativeName)
	{
		for (ClasspathFile compilableFile : extraClasspathFiles)
			if (compilableFile.getPath().equals(relativeName))
				return compilableFile;

		for (Object o : path)
		{
			if (o instanceof JarFile)
			{
				JarFile jarFile= (JarFile) o;
				JarEntry entry= jarFile.getJarEntry(relativeName);
				if (entry != null)
				{
					return new InsideJarClasspathFile(jarFile, entry, relativeName);
				}
			}
			else
			{
				File file= new File(((File) o), relativeName);
				if (file.exists())
				{
					return new JavaFileClasspathFile(file, relativeName);
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
			try
			{
				final JarEntry entry= entries.nextElement();
				final String entryName= entry.getName();
				if (entryName.endsWith(".class"))
					result.add(entryName.replace('/', File.separatorChar).replace(".class", ""));
			}
			catch (Exception e)
			{
				LOGGER.warning("There is an invalid jar entry: " + e.getMessage());
			}
		}

		return result;
	}
	public Collection<String> getAllFilesInClasspath()
	{
		Collection<String> files= new ArrayList<String>();

		for (ClasspathFile classpathFile : extraClasspathFiles)
		{
			File file= new File(classpathFile.getPath());
			if (classpathFilter == null || classpathFilter.accept(file, new File(".")))
				files.add(classpathFile.getPath().replace(".class", ""));
		}

		for (Object o : path)
		{
			if (o instanceof JarFile)
			{
				JarFile jarFile= (JarFile) o;

				List<String> classesInJar= findClassesInJar(jarFile);

				for (String file : classesInJar)
				{
					if (classpathFilter == null || classpathFilter.accept(new File(file), new File(jarFile.getName())))
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
					String substring= file.toString().substring(folder.toString().length() + 1);

					if (classpathFilter == null || classpathFilter.accept(file, folder))
					{
						files.add(substring.replace(".class", ""));
					}
				}
			}
		}
		return files;
	}

	//	public static void main(String[] args) throws Exception
	//	{
	//		String className= args[0];
	//		DragomeJsCompiler.compiler= new DragomeJsCompiler(CompilerType.Standard);
	//
	//		Project.createSingleton(null);
	//		ClassUnit classUnit= new ClassUnit(Project.singleton, Project.singleton.getSignature(className));
	//		classUnit.setClassFile(new JavaFileCompilableFile(new File(args[1])));
	//		Parser parser= new Parser(classUnit);
	//		TypeDeclaration typeDecl= parser.parse();
	//		DragomeJavaScriptGenerator dragomeJavaScriptGenerator= new DragomeJavaScriptGenerator(Project.singleton);
	//		dragomeJavaScriptGenerator.setOutputStream(new PrintStream(new FileOutputStream(new File("/tmp/webapp.js"))));
	//		dragomeJavaScriptGenerator.visit(typeDecl);
	//
	//		FileInputStream fis= new FileInputStream(new File("/tmp/webapp.js"));
	//		String md5= org.apache.commons.codec.digest.DigestUtils.md5Hex(fis);
	//		System.out.println(md5);
	//	}
}
