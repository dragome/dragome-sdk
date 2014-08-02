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

import java.util.ArrayList;
import java.util.List;

import org.xmlvm.main.Targets;
import org.xmlvm.util.universalfile.UniversalFile;
import org.xmlvm.util.universalfile.UniversalFileCreator;

/**
 * Android compatibility library, which is mostly developed against the
 * Java-based cocoa library and is for use in iphone targets only.
 */
public class WP7Library extends Library
{
	private static final String ONE_JAR_LOCATION= "/lib/silverlight-java.jar";
	private static final String FILE_SYSTEM_LOCATION= "bin-csharp";

	public boolean isMonolithic()
	{
		return false;
	}

	protected UniversalFile[] getLibraryUncached()
	{
		UniversalFile result= UniversalFileCreator.createDirectory(ONE_JAR_LOCATION, prepareTempJar(FILE_SYSTEM_LOCATION, ""));
		return new UniversalFile[] { result };
	}

	protected List<Targets> includedTargets()
	{
		List<Targets> included= new ArrayList<Targets>();
		included.add(Targets.WP7);
		included.add(Targets.WP7ANDROID);
		return included;
	}

	protected List<Targets> excludedTargets()
	{
		return null;
	}
}
