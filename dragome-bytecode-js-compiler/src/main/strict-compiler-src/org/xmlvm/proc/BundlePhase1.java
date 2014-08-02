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
 * The interface of {@link CompilationBundle} that is used during the first
 * phase.
 */
public interface BundlePhase1
{

	/**
	 * Adds the given {@link XmlvmResource} to this bundle.
	 */
	public void addResource(XmlvmResource resource);

	/**
	 * Adds the given {@link XmlvmResource}s to this bundle
	 */
	public void addResources(Collection<XmlvmResource> resources);

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
