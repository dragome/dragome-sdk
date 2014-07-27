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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.xmlvm.Log;
import org.xmlvm.main.Arguments;
import org.xmlvm.proc.out.OutputFile;
import org.xmlvm.util.universalfile.UniversalFile;
import org.xmlvm.util.universalfile.UniversalFileCreator;

/**
 * Supports direct copying of data resources needed for build. This includes both 
 * internal resources, as well as those specified at run-time.
 */
public class DataResources
{
	private static final String INTERNAL_DATAFILE_JAR_LOCATION= "/__PLATFORM__/data/";
	private static final String INTERNAL_DATAFILE_LOCATION= "var/__PLATFORM__/data/";
	private static final String DATAFILE_LOCATION= File.separator + "data" + File.separator;

	private Collection<UniversalFile> internalDataFiles;

	public DataResources(String platform, String... resources)
	{
		internalDataFiles= new ArrayList<UniversalFile>();
		for (String resource : resources)
		{
			String dataInJarResource= INTERNAL_DATAFILE_JAR_LOCATION.replace("__PLATFORM__", platform) + resource;
			String dataPath= INTERNAL_DATAFILE_LOCATION.replace("__PLATFORM__", platform) + resource;
			UniversalFile resourceFile= UniversalFileCreator.createFile(dataInJarResource, dataPath);
			if (resourceFile != null && resourceFile.exists())
			{
				internalDataFiles.add(resourceFile);
			}
		}
	}

	public DataResources(UniversalFile... internal)
	{
		internalDataFiles= new ArrayList<UniversalFile>();
		internalDataFiles.addAll(Arrays.asList(internal));
	}

	/**
	 * Get a description of all data files which need to be copied. This includes 
	 * both internal data as well as data specified at run-time via arguments.
	 */
	public Collection<OutputFile> composeResourceFiles(Arguments arguments)
	{
		List<OutputFile> outputFiles= new ArrayList<OutputFile>();
		List<UniversalFile> inputFiles= new ArrayList<UniversalFile>();

		// Include all internal data files for copying
		inputFiles.addAll(internalDataFiles);

		// Add all files specified by the user as resources
		for (String resourceName : arguments.option_resource())
		{
			File resource= new File(resourceName);
			if (resource.exists())
			{
				inputFiles.add(UniversalFileCreator.createFile(resource));
			}
			else
			{
				Log.warn("Could not find specified resource " + resource + "; this will be omitted.");
			}
		}

		// Generate output files for all data to be explicitly copied
		for (UniversalFile inputFile : inputFiles)
		{
			OutputFile outputFile= new OutputFile(inputFile);
			outputFile.setLocation(arguments.option_out() + DATAFILE_LOCATION);
			outputFile.setFileName(inputFile.getName());
			outputFiles.add(outputFile);
		}

		return outputFiles;
	}

}
