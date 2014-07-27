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

import org.xmlvm.Log;
import org.xmlvm.main.Arguments;
import org.xmlvm.proc.BundlePhase1;
import org.xmlvm.proc.BundlePhase2;
import org.xmlvm.proc.XmlvmProcessImpl;
import org.xmlvm.proc.in.file.ClassFile;
import org.xmlvm.proc.in.file.ExeFile;
import org.xmlvm.proc.in.file.XFile;
import org.xmlvm.proc.in.file.XmlvmFile;
import org.xmlvm.proc.out.OutputFile;

/**
 * An input process is created of every input file or resource.
 * 
 * @param <T>
 *            the concrete type of the file that this process is reading
 */
public abstract class InputProcess<T extends XFile> extends XmlvmProcessImpl
{

	public static class EmptyInputProcess extends InputProcess<ClassFile>
	{
		/**
		 * The signature of this input process. If a target requires this string
		 * as input, means that actually no input is required
		 */
		public final static String EMPTY_INPUT_IN_ARGUMENT= "<<EMPTY_INPUT>>";

		public EmptyInputProcess()
		{
			super(null, null);
		}
	}

	/**
	 * An implementation of {@link InputProcess} that reads class files.
	 */
	public static class ClassInputProcess extends InputProcess<ClassFile>
	{
		public ClassInputProcess(Arguments arguments, ClassFile input)
		{
			super(arguments, input);
		}
	}

	/**
	 * An implementation of {@link InputProcess} that reads exe files.
	 */
	public static class ExeInputProcess extends InputProcess<ExeFile>
	{
		public ExeInputProcess(Arguments arguments, ExeFile input)
		{
			super(arguments, input);
		}
	}

	/**
	 * An implementation of {@link InputProcess} that reads XMLVM files.
	 */
	public static class XmlvmInputProcess extends InputProcess<XmlvmFile>
	{
		public XmlvmInputProcess(Arguments arguments, XmlvmFile input)
		{
			super(arguments, input);
		}
	}

	protected T input;

	public InputProcess(Arguments arguments, T input)
	{
		super(arguments);
		this.input= input;
		//Log.debug("Instantiated: " + this.getClass().getName() + " for \"" + input + "\"");
	}

	/**
	 * Returns the input file of this process.
	 */
	public T getInputFile()
	{
		return input;
	}

	public boolean isActive()
	{
		return true;
	}

	/**
	 * If the input is a valid file, this process add the file to the given
	 * resources.
	 */

	public boolean processPhase1(BundlePhase1 bundle)
	{
		if (input == null || input.getFile() == null)
		{
			Log.warn("InputProcess.getOutputFiles(): Input File is null.");
			return false;
		}
		if (!input.getFile().exists() || !input.getFile().isFile())
		{
			Log.warn("InputProcess.getOutputFiles(): Input File " + input.getFile() + "does not exist or is not a file.");
			return false;
		}

		OutputFile outputFile= new OutputFile(input.getFile());
		outputFile.setOrigin(input.getFile().getAbsolutePath());
		outputFile.setLocation(arguments.option_out());
		outputFile.setFileName(input.getFile().getName());
		bundle.addOutputFile(outputFile);
		return true;
	}

	/**
	 * InputProcesses don't do anything in the second phase.
	 */

	public boolean processPhase2(BundlePhase2 bundle)
	{
		return true;
	}
}
