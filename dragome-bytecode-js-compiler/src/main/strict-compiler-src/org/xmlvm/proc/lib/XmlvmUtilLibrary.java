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
 * This is a collection of utility classes that can be used by cross-compiled
 * code. This library should be kept target-independent.
 */
public class XmlvmUtilLibrary extends Library
{

	public boolean isMonolithic()
	{
		return true;
	}

	protected UniversalFile[] getLibraryUncached()
	{
		// TODO(Sascha): We might want to compile bin-util ourselves at some
		// point, as we are getting into some classpath issues in Eclipse
		// with this approach.
		return new UniversalFile[] { UniversalFileCreator.createDirectory("/lib/xmlvm-util-java.jar", "bin-util") };
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
