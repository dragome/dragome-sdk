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

package org.xmlvm.proc.out;

import java.io.File;
import java.util.Collection;

import org.xmlvm.Log;

/**
 * Writes out instances of {@link OutputFile}s.
 */
public class OutputFileWriter
{

	private Collection<OutputFile> outputFiles;

	public OutputFileWriter(Collection<OutputFile> outputFiles)
	{
		this.outputFiles= outputFiles;
	}

	/**
	 * Writes the files given in the constructor to the file system.
	 * 
	 * @return whether all files were written successfully
	 */
	public boolean writeFiles()
	{
		for (OutputFile outputFile : outputFiles)
		{
			if (!createOutputDirectory(outputFile))
				Log.error("Could not create directory for file: " + outputFile.getFileName());
			outputFile.write();
		}
		return true;
	}

	/**
	 * Make sure that the directory, this file is written to, exists or is
	 * created.
	 * 
	 * @return whether the directory exists or could be created
	 */
	private boolean createOutputDirectory(OutputFile outputFile)
	{
		File location= new File(outputFile.getLocation());
		if (location.exists())
		{
			if (location.isDirectory())
			{
				return true;
			}
			else
			{
				Log.error("Location is not a directory: " + outputFile.getLocation());
				return false;
			}
		}
		if (!location.mkdirs())
		{
			Log.error("Directory could not be created: " + outputFile.getLocation());
			return false;
		}
		return true;
	}
}
