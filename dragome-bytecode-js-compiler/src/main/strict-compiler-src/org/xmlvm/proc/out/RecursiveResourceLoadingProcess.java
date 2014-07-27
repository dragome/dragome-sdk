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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.xmlvm.Log;
import org.xmlvm.main.Arguments;
import org.xmlvm.proc.BundlePhase1;
import org.xmlvm.proc.BundlePhase2;
import org.xmlvm.proc.XmlvmProcessImpl;
import org.xmlvm.proc.XmlvmResource;
import org.xmlvm.proc.lib.LibraryLoader;
import org.xmlvm.util.ClassListLoader;
import org.xmlvm.util.universalfile.UniversalFile;
import org.xmlvm.util.universalfile.UniversalFileCreator;

/**
 * Makes sure that all referenced resources from the input resources are loaded.
 * This is done recursively.
 */
public class RecursiveResourceLoadingProcess extends XmlvmProcessImpl
{

	public RecursiveResourceLoadingProcess(Arguments arguments)
	{
		super(arguments);
		addAllXmlvmEmittingProcessesAsInput();
	}

	public boolean processPhase1(BundlePhase1 bundle)
	{
		// We create a map that maps type name to the resource.
		Map<String, XmlvmResource> xmlvmResources= new HashMap<String, XmlvmResource>();
		for (XmlvmResource resource : bundle.getResources())
		{
			xmlvmResources.put(resource.getFullName(), resource);
		}
		if (arguments.option_load_dependencies() && !arguments.option_disable_load_dependencies())
		{
			if (arguments.option_greenlist() != null)
			{ // Force loading of all greenlist classes
				preloadGreenlistFiles(arguments.option_greenlist(), xmlvmResources);
			}

			LibraryLoader libraryLoader= new LibraryLoader(arguments);

			// Add all required resources, that will not be referenced to the
			// usual way.
			for (XmlvmResource requiredResource : libraryLoader.loadMonolithicLibraries())
			{
				xmlvmResources.put(requiredResource.getFullName(), requiredResource);
			}

			// Make sure we have all types that are referenced loaded.
			libraryLoader.loadAllReferencedTypes(xmlvmResources);
		}
		bundle.addResources(xmlvmResources.values());
		return true;
	}

	public boolean processPhase2(BundlePhase2 bundle)
	{
		return true;
	}

	private void preloadGreenlistFiles(String greenlistFile, Map<String, XmlvmResource> resources)
	{
		UniversalFile greenList= UniversalFileCreator.createFile(new File(greenlistFile));
		LibraryLoader libraryLoader= new LibraryLoader(arguments);
		UniversalFile defaultGreenList= UniversalFileCreator.createFile("/lib/greenlist.txt", "lib/greenlist.txt");
		for (UniversalFile file : new UniversalFile[] { greenList, defaultGreenList })
		{
			Set<String> classes= ClassListLoader.loadGreenlist(file);
			if (classes != null)
			{ // If any green list exists, force inclusion of its contents
				for (String className : classes)
				{
					if (!resources.containsKey(className))
					{
						XmlvmResource resource= libraryLoader.load(className);
						if (resource != null)
						{
							resources.put(resource.getFullName(), resource);
						}
					}
				}
			}
		}
	}
}
