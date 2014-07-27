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

package org.xmlvm.proc;

import java.util.Collection;
import java.util.Map;

import org.xmlvm.proc.out.OutputFile;

/**
 * The interface of {@link CompilationBundle} that is used during the second
 * phase.
 */
public interface BundlePhase2
{

	/**
	 * This adds a new resource to the bundle.
	 * <p>
	 * <b>IMPORTANT:</b> Only use this in cases where the resource does not need
	 * to be part of the global view as only processes coming after this one
	 * will be able to see the newly added resource. If the resource needs to be
	 * part of the global view, you need to add it in the first phase.
	 */
	public void addAdditionalResource(XmlvmResource resource);

	/**
	 * Returns all {@link XmlvmResource}s that have been added to the bundle so
	 * far.
	 */
	public Collection<XmlvmResource> getResources();

	/**
	 * Returns a read-only map of the resources, where the key is the resource's
	 * full name.
	 */
	public Map<String, XmlvmResource> getResourceMap();

	/**
	 * Adds a new {@link OutputFile} to this bundle.
	 */
	public void addOutputFile(OutputFile file);

	/**
	 * Adds a bunch of {@link OutputFile}s to this bundle.
	 */
	public void addOutputFiles(Collection<OutputFile> files);

	/**
	 * Returns all {@link OutputFile}s that have been added to the bundle so
	 * far.
	 */
	public Collection<OutputFile> getOutputFiles();

	/**
	 * Removes the given {@link OutputFile} from this bundle.
	 */
	public void removeOutputFile(OutputFile file);

	/**
	 * Removes all {@link OutputFile}s from this bundle.
	 */
	public void removeAllOutputFiles();
}
