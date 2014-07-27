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

package org.xmlvm.proc.in.file;

import java.io.File;

import org.xmlvm.proc.in.InputProcess;
import org.xmlvm.util.universalfile.UniversalFile;
import org.xmlvm.util.universalfile.UniversalFileCreator;

/**
 * A file used as an input for the {@link InputProcess} instances.
 */
public class XFile
{
	protected UniversalFile file;

	/**
	 * Creates an {@link XFile} instance from a file system location.
	 */
	public XFile(File location)
	{
		file= UniversalFileCreator.createFile(location);
	}

	/**
	 * Creates an {@link XFile} from a {@link UniversalFile} resource.
	 */
	public XFile(UniversalFile file)
	{
		this.file= file;
	}

	/**
	 * Returns the file as {@link File}.
	 */
	public UniversalFile getFile()
	{
		return file;
	}

	/**
	 * Returns the input of this file.
	 */
	public String getPath()
	{
		return file.getAbsolutePath();
	}

	/**
	 * Returns a String representation of this file. Currently this is the same
	 * as {@link #getPath}
	 */

	public String toString()
	{
		return getPath();
	}

	/**
	 * Returns whether the given input is a file.
	 */
	public static boolean isFile(String input)
	{
		return (new File(input)).isFile();
	}
}
