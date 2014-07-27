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

package org.xmlvm.proc.in;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.xmlvm.Log;
import org.xmlvm.main.Arguments;
import org.xmlvm.proc.in.InputProcess.ClassInputProcess;
import org.xmlvm.proc.in.InputProcess.EmptyInputProcess;
import org.xmlvm.proc.in.InputProcess.ExeInputProcess;
import org.xmlvm.proc.in.InputProcess.XmlvmInputProcess;
import org.xmlvm.proc.in.file.ClassFile;
import org.xmlvm.proc.in.file.Directory;
import org.xmlvm.proc.in.file.ExeFile;
import org.xmlvm.proc.in.file.ResourceList;
import org.xmlvm.proc.in.file.XmlvmFile;
import org.xmlvm.util.universalfile.UniversalFile;
import org.xmlvm.util.universalfile.UniversalFileCreator;

/**
 * Used for creating input processes.
 */
public class InputProcessFactory
{
	private static final String TAG= InputProcessFactory.class.getSimpleName();

	/**
	 * The arguments that should be given to the created processes.
	 */
	private Arguments arguments;

	public InputProcessFactory(Arguments arguments)
	{
		this.arguments= arguments;
	}

	/**
	 * Creates a list of {@link InputProcess} instances. One for each user
	 * provided input.
	 * 
	 * @param inputElements
	 *            A list of inputs specified by the user.
	 * @return A list of InputProcesses.
	 */
	public List<InputProcess<?>> createInputProcesses(List<String> inputElements)
	{
		List<InputProcess<?>> processes= new ArrayList<InputProcess<?>>();

		// If there is no input specified, we create an empty input process.
		// This is used for processes that just create files, but don't take
		// input. One such example is creating empty project skeletons.
		if (inputElements.isEmpty())
		{
			processes.add(new EmptyInputProcess());
			return processes;
		}

		for (String inputElement : inputElements)
		{
			// If this input element is a directory, we add all the children
			// elements that are applicable.
			if (Directory.isDirectoryInput(inputElement))
			{
				processes.addAll(createInputProcessesForDirectory(new Directory(inputElement)));
			}
			else if (ZipArchiveExtractor.isZipArchive(inputElement))
			{
				for (UniversalFile file : ZipArchiveExtractor.createFilesForArchive(inputElement))
				{
					processes.add(createInputProcess(file));
				}
			}
			else
			{
				UniversalFile file= UniversalFileCreator.createFile(new File(inputElement));
				if (file != null)
				{
					processes.add(createInputProcess(file));
				}
				else
				{
					Log.error(TAG, "Could not find input resource: " + inputElement);
				}
			}
		}
		return processes;
	}

	/**
	 * Creates a list of {@link InputProcess} instances for each provided input.
	 */
	public List<InputProcess<?>> createInputProcessesFromFiles(List<UniversalFile> inputFiles)
	{
		List<InputProcess<?>> processes= new ArrayList<InputProcess<?>>();
		for (UniversalFile inputFile : inputFiles)
		{
			InputProcess<?> inputProcess= createInputProcess(inputFile);
			if (inputProcess != null)
			{
				processes.add(inputProcess);
			}
		}
		return processes;
	}

	/**
	 * This method decides which concrete subclass of InputProcesses should be
	 * instantiated, depending on the input given.
	 * 
	 * @param input
	 *            This could be a directory path, a directory path with wildcard
	 *            or a path to a single file.
	 * @return A {@link InputProcess} that is able to process the given input or
	 *         null, if no process was found for the given input.
	 */
	protected InputProcess<?> createInputProcess(UniversalFile input)
	{
		if (ClassFile.isClassInput(input))
		{
			// CLASS files.
			return new ClassInputProcess(arguments, new ClassFile(input));
		}
		else if (ExeFile.isExeInput(input))
		{
			// EXE files.
			return new ExeInputProcess(arguments, new ExeFile(input));
		}
		else if (XmlvmFile.isXmlvmInput(input))
		{
			// XMLVM files.
			return new XmlvmInputProcess(arguments, new XmlvmFile(input));
		}
		else if (ResourceList.isResourceList(input))
		{
			// Do nothing: ignore resource list files
		}
		else
			Log.warn("Unable to create InputProcesses for input: " + input);
		return null;
	}

	/**
	 * Create InputProcesses for all applicable elements for this directory.
	 * 
	 * @param input
	 *            The directory to process.
	 * @return All InputProcesses for the applicable elements.
	 */
	protected List<InputProcess<?>> createInputProcessesForDirectory(Directory input)
	{
		List<InputProcess<?>> result= new ArrayList<InputProcess<?>>();
		List<File> files= input.getAllMatchingFiles();
		for (File file : files)
		{
			// We don't want to process ourself.
			if (!input.equals(file))
			{
				// Add process to the processor.
				result.add(createInputProcess(UniversalFileCreator.createFile(file)));
			}
		}
		return result;
	}
}
