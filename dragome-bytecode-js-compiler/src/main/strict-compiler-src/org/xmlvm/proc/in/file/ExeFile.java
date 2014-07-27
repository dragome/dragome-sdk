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

import org.xmlvm.util.universalfile.UniversalFile;

/**
 * An {@link XFile} class for exe files.
 */
public class ExeFile extends XFile
{
	public static final String EXE_ENDING= ".exe";

	public ExeFile(UniversalFile path)
	{
		super(path);
	}

	/**
	 * Returns whether the input is an EXE file.
	 */
	public static boolean isExeInput(UniversalFile file)
	{
		return file != null && file.isFile() && file.getAbsolutePath().toLowerCase().endsWith(EXE_ENDING);
	}

}
