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

import org.xmlvm.util.FileUtil;

/**
 * This output file is responsible to copy resources to a project
 */
public class VerbatimOutputFile extends OutputFile
{

	private String sourcePath= "";

	/**
	 * Create a new resource file
	 * 
	 * @param from
	 *            Source directory
	 * @param to
	 *            Destination directory
	 * @param fname
	 *            Filename to copy
	 */
	public VerbatimOutputFile(String from, String to, String fname)
	{
		setFileName(fname);
		setLocation(to);
		this.sourcePath= from;
	}

	/**
	 * Get the source directory
	 * 
	 * @return the source directory
	 */
	public String getSourcePath()
	{
		return sourcePath;
	}

	/**
	 * Set the source directory
	 * 
	 * @param sourcePath
	 *            the source directory
	 */
	public void setSourcePath(String sourcePath)
	{
		this.sourcePath= sourcePath;
	}

	/**
	 * Perform the actual action of this VerbatimOutputFile (i.e. write file to
	 * disk)
	 * 
	 * @return true, if no errors exist
	 */

	public boolean write()
	{
		return FileUtil.copyFile(new File(getSourcePath(), getFileName()), new File(getLocation(), getFileName()));
	}
}
