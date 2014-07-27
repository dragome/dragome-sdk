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

package org.xmlvm.proc.lib;

import java.io.File;
import java.util.List;

import org.xmlvm.Log;
import org.xmlvm.main.Arguments;
import org.xmlvm.main.Targets;
import org.xmlvm.util.universalfile.UniversalFile;
import org.xmlvm.util.universalfile.UniversalFileCreator;

/**
 * A library that can included into the compilation process.
 */
public abstract class Library
{
	private static final String TAG= Library.class.getSimpleName();
	private static final String CACHE_PATH= ".cache/";

	private UniversalFile[] resource= null;

	/**
	 * Returns whether this library should be used in the current process.
	 */
	public boolean isEnabled(Arguments arguments)
	{
		// Don't enable, if the target is amongst the excluded ones.
		List<Targets> excludedTargets= excludedTargets();
		if (excludedTargets != null)
		{
			if (excludedTargets.contains(arguments.option_target()))
			{
				return false;
			}
		}

		// If there are no included targets, the library is enabled for all
		// targets. Otherwise it's only enabled, if the target is amonst the
		// included ones.
		List<Targets> includedTargets= includedTargets();
		if (includedTargets != null)
		{
			return includedTargets.contains(arguments.option_target());
		}
		else
		{
			return true;
		}
	}

	/**
	 * Returns the library as a {@link UniversalFile} resource.
	 */
	public UniversalFile[] getLibrary()
	{
		if (resource == null)
		{
			resource= getLibraryUncached();
		}
		return resource;
	}

	/**
	 * This determines whether a library should be copied over completely, or
	 * whether it should be offered for the dependency analysis.
	 * <p>
	 * If this returns <code>true</code>, the complete library will be copied.
	 * <p>
	 * If this returns <code>false</code>, the library will be included in the
	 * dynamic dependency loading process.
	 */
	public abstract boolean isMonolithic();

	/**
	 * Returns the library resource. The implementation doesn't need to cache,
	 * as this method is only called once.
	 */
	protected abstract UniversalFile[] getLibraryUncached();

	/**
	 * Returns the targets for which this library should be activated. Returns
	 * <code>null</code>, if this library should be included in all targets.
	 */
	protected abstract List<Targets> includedTargets();

	/**
	 * Returns a list of targets for which this library should not be included.
	 */
	protected abstract List<Targets> excludedTargets();

	/**
	 * In order to easily load libraries that are already compiled, we put them
	 * in a JAR file. This method makes sure that the files in the given path
	 * are in the given destination JAR file.
	 * <p>
	 * TODO: The JAR file Should be stored alongside with timestamp information
	 * so that the JAR file will be re-created, if one of the source files
	 * changes. Right now it is re-created every time.
	 * 
	 * @param path
	 *            the directory that contains the resources to be archived
	 * @param pathPrefix
	 *            the path inside the archive, where the files are put into
	 * @return The path to the JAR file or null, if and error occurred or the
	 *         path was not found.
	 */
	protected String prepareTempJar(String path, String pathPrefix)
	{

		UniversalFile source= UniversalFileCreator.createDirectory(path);
		if (source == null || !source.exists())
		{
			Log.debug(TAG, "Couldn't find library path: " + path);
			return null;
		}
		long lastModified= lastModifiedRecursive(source);
		String tempFileName= createTempFileName(path, lastModified);

		// Check whether the temp jar already exists with the given time stamp.
		if (exists(tempFileName))
		{
			return tempFileName;
		}

		Log.debug(TAG, "Preparing temp JAR for '" + path + "' at '" + tempFileName + "'.");
		source.archiveTo(tempFileName, pathPrefix);
		return tempFileName;
	}

	private long lastModifiedRecursive(UniversalFile directory)
	{
		long lastModified= 0;
		for (UniversalFile file : directory.listFilesRecursively())
		{
			if (file.getLastModified() > lastModified)
			{
				lastModified= file.getLastModified();
			}
		}
		return lastModified;
	}

	private static String createTempFileName(String path, long lastModified)
	{
		return CACHE_PATH + path.replace("/", "_").replace("\\", "_") + "." + lastModified + ".jar";
	}

	private static boolean exists(String fileName)
	{
		return (new File(fileName)).exists();
	}
}
