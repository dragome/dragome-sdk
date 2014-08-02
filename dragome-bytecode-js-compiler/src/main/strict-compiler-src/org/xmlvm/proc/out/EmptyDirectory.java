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
import org.xmlvm.Log;

/**
 * This output file is responsible to create empty directories, especially while
 * creating templates.
 */
public class EmptyDirectory extends OutputFile
{

	/**
	 * Create a new empty directory.
	 * 
	 * @param outdir
	 *            the empty directory name
	 */
	public EmptyDirectory(String outdir)
	{
		setLocation(outdir);
	}

	/**
	 * Perform the actual action of this EmptyDirectory (i.e. create empty
	 * directory)
	 * 
	 * @return true, if no errors exist
	 */

	public boolean write()
	{
		Log.debug("Creating directory " + getLocation());
		return new File(getLocation()).mkdirs();
	}
}
