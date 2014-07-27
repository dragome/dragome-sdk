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

package org.xmlvm.proc.out.build;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collection;

import org.xmlvm.main.Arguments;
import org.xmlvm.proc.out.OutputFile;

/**
 * Create a file responsible to "build" the selected target (i.e. a Makefile or
 * a Xcode file)
 */
public abstract class BuildFile
{

	protected final static String BUILDFILE_LOCATION= File.separator + "dist" + File.separator;

	/**
	 * Get a list of filenames from a OutputFile list, which are in accordance
	 * with some criteria.
	 * 
	 * @param fileList
	 *            The list of OutputFile.
	 * @param filter
	 *            Filename criteria.
	 * @return List of filenames with valid files.
	 */
	protected static ArrayList<String> getFileNames(Collection<OutputFile> fileList, FileFilter filter)
	{
		ArrayList<String> result= new ArrayList<String>();

		for (OutputFile fileBundle : fileList)
		{
			for (OutputFile outfile : fileBundle.getAffectedSourceFiles())
			{
				File fileEntry= new File(outfile.getFullPath());
				if (filter.accept(fileEntry))
					result.add(fileEntry.getName());
			}
		}
		return result;
	}

	/**
	 * Create build files for this target.
	 * 
	 * @param arguments
	 *            XMLVM command line arguments.
	 * @return The produced build-file.
	 */
	public abstract OutputFile composeBuildFiles(Arguments arguments);
}
