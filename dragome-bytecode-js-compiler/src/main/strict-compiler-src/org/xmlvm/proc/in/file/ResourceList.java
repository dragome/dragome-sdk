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
 * An {@link XFile} class for resource list files.
 */
public class ResourceList extends XFile
{
	public static final String RESOURCE_ENDING= ".resources";

	public ResourceList(UniversalFile path)
	{
		super(path);
	}

	/**
	 * Returns whether the input is a resource list file, thus need to be
	 * ignored when creating xml output.
	 */
	public static boolean isResourceList(UniversalFile file)
	{
		return file != null && file.isFile() && file.getAbsolutePath().toLowerCase().endsWith(RESOURCE_ENDING);
	}

}
