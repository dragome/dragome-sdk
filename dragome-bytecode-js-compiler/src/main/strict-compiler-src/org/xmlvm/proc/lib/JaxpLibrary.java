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

import java.util.List;

import org.xmlvm.main.Targets;
import org.xmlvm.util.universalfile.UniversalFile;
import org.xmlvm.util.universalfile.UniversalFileCreator;

/**
 * The JAXP library.
 */
public class JaxpLibrary extends Library
{
	private static final String ONE_JAR_LOCATION= "/lib/jaxp.jar";
	private static final String FILE_SYSTEM_LOCATION= "lib/jaxp.jar";

	public boolean isMonolithic()
	{
		return false;
	}

	protected UniversalFile[] getLibraryUncached()
	{
		return new UniversalFile[] { UniversalFileCreator.createDirectory(ONE_JAR_LOCATION, FILE_SYSTEM_LOCATION) };
	}

	protected List<Targets> includedTargets()
	{
		return null;
	}

	protected List<Targets> excludedTargets()
	{
		return null;
	}
}
