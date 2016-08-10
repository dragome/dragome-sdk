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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import com.dragome.commons.compiler.classpath.Classpath;
import com.dragome.commons.compiler.classpath.ClasspathEntry;
import com.dragome.commons.compiler.classpath.ClasspathFile;
import com.dragome.commons.compiler.classpath.ClasspathFileFilter;

public class FileManager
{
	private ClasspathFileFilter classpathFilter;
	private Classpath classPath;

	public FileManager(Classpath classPath, ClasspathFileFilter classpathFilter)
	{
		this.classPath= classPath;
		this.classpathFilter= classpathFilter;
	}

	public ClasspathFile getFileForInput(String relativeName)
	{
		for (ClasspathEntry classpathEntry : classPath.getEntries())
		{
			ClasspathFile classpathFile= classpathEntry.getClasspathFileOf(relativeName);
			if (classpathFile != null)
				return classpathFile;
		}

		throw new RuntimeException("Could not find " + relativeName + " on class path");
	}

	public Collection<ClasspathFile> getAllFilesInClasspath()
	{
		Collection<ClasspathFile> files= new ArrayList<ClasspathFile>();

		for (ClasspathEntry classpathEntry : classPath.getEntries())
		{
			List<ClasspathFile> classpathFiles= classpathEntry.getClasspathFilesFiltering(classpathFilter);
			files.addAll(classpathFiles);
		}

		return files;
	}
}
